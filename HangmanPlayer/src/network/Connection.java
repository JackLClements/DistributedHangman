//Idea is to decouple network code from program logic
package network;

import hangmanplayer.Player;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * For each individual connection, allows program logic to run independent of
 * network logic
 *
 * @author dha13jyu
 */
public class Connection implements Runnable {

    //private String name;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean active;
    //Table object, set by connection manager, has references to connection objects a-la doubly linked list, 
    Player player;

    public Connection() {
    }

    public Connection(Socket socket) throws IOException { //may be worth handling elsewhere? maybe
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream()); //can also use buffered reader
        in = new ObjectInputStream(socket.getInputStream());
        active = true;
    }

    public void send(Message msg) {
        try {
            out.writeObject(msg);
        } catch (IOException ex) {
            System.out.println("Cannot send message");
            //ex.printStackTrace(System.out);
        }
    }
    
    /**
     * Close socket and server
     * @throws IOException 
     */
    public void close() throws IOException {
        active = false;
        out.flush();
        out.close();
        in.close();
        socket.close();
    }
    
    /**
     * Recieve a message from the server
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public Message recieve() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }
    
    //Accessor methods & Overrides
    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        //recieving messages from client, done regularly
        //loop to deal with input
        //where to send input and cast?
        while (active) {
            Message msg;
            try {
                msg = (Message) in.readObject(); //needs a reference to a handler or set at later date
                //instanceof is then used at e.g. table/dealer and handled
                //passed to table, cast then handled
                player.processMessage(msg);
            } catch (Exception e) {
                System.out.println("Unexpected server disconnect");
                //e.printStackTrace();
            }
        }
    }

}
