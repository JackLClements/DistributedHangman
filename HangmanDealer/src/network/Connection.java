//Idea is to decouple network code from program logic
package network;

import hangmandealer.Dealer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * For each individual connection, allows program logic to run independent of
 * network logic. Acts as facade to network-specific code, allowing other
 * classes to send/receive data without being concerned with the actual network
 * operations being performed. Increases cohesion and lowers coupling.
 *
 * @author Jack L. Clements
 */
public class Connection implements Runnable {

    //private String name
    private int personalID;
    private static int totalID; //this doesn't persist over only works server-side
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean active;
    //object to link to game logic, such as the hangman game object in progress
    //Table object, set by connection manager, has references to connection objects a-la doubly linked list, 
    private Dealer dealer;

    /**
     * Default constructor, sets up ID only.
     */
    public Connection() {
        incrementID();
        personalID = getTotalID();
    }

    /**
     * Constructor that takes socket, sets up all other fields.
     *
     * @param socket connection socket
     * @throws IOException if not connected
     */
    public Connection(Socket socket) throws IOException {
        incrementID();
        personalID = getTotalID();
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream()); //can also use buffered reader
        in = new ObjectInputStream(socket.getInputStream());
        active = true;
    }

    /**
     * Accessor, setter for outputstream
     *
     * @param out
     */
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    /**
     * Attaches socket to connection, creates new out/in streams
     *
     * @param socket socket to be set
     * @throws IOException if not found
     */
    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        active = true;
    }

    /**
     * Sends a message object over a the outputstream, polymorphism used to not
     * worry about implementation
     *
     * @param msg message (subclass) to be sent
     */
    public void send(Message msg) {
        try {
            System.out.println("Message sent: " + msg.getClass().getName());
            out.writeObject(msg); //if object has same reference 
            out.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for casting a recieved message
     *
     * @return Input cast to Message
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Message recieve() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    /**
     * Set new inputstream, accessor method
     *
     * @param in
     */
    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    /**
     * Closes connection alongside all relevant streams
     */
    public void close() {
        try {
            active = false;
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error closing connection object");
            e.printStackTrace();
        }

    }

    public void addDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    public Dealer getDealer() {
        return dealer;
    }

    /**
     * Returns the ID of the object so things can be addressed to a specifc
     * connection
     *
     * @return
     */
    public int getID() {
        return personalID;
    }

    /*
     * Returns the total no. of IDs created 
     * @return 
     */
    public static synchronized int getTotalID() {
        return totalID;
    }

    //ought to increment properly so we don't get two object twos for instance
    /*
     * increments ID, synchronized in order to stop two objects created at once 
     * not properly incr4ementing
     */
    public static synchronized void incrementID() {
        System.out.println("ID BEFORE - " + totalID);
        totalID++;
        System.out.println("AFTER - " + totalID);
    }

    /**
     * Thread that recieves messages, extracts it as a "message" object and then
     * passes onto the table. It is not concerned with the specific object type
     * nor its contents.
     */
    @Override
    public void run() {
        //recieving messages from client, done regularly
        //loop to deal with input
        //where to send input and cast?
        try {
            while (active) {
                Message msg;

                msg = (Message) in.readObject(); //needs a reference to a handler or set at later date
                //instanceof is then used at e.g. table/dealer and handled
                //passed to table, cast then handled
                if (msg instanceof ChooseGameMessage) {
                    System.out.println("Room chosen - " + ((ChooseGameMessage) msg).getRoomNo());
                    if (((ChooseGameMessage) msg).isNewRoom()) {
                        Dealer dealer = new Dealer();
                        this.dealer = dealer;
                        dealer.addPlayer(this);
                        ConnectionManager.addDealer(dealer);
                    } else {
                        System.out.println("Room at - " + ((ChooseGameMessage) msg).getRoomNo());
                        Dealer dealer = ConnectionManager.getDealerAt(((ChooseGameMessage) msg).getRoomNo());
                        this.dealer = dealer;
                        dealer.addPlayer(this);
                    }
                } else {
                    dealer.processMessage(msg, personalID);
                }
                //table.processMessage(msg, personalID);
                //if in playing state, send to table
                //waiting state for other players
                //possible waiting for player to join state, etc.
            }

        } catch (Exception e) {
            close();
            System.out.println("Error: Unexpected client disconnect.");
            //e.printStackTrace();
        }
    }

}
