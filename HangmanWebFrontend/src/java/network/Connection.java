//Idea is to decouple network code from program logic
package network;

import Servlets.EntryUtilServlet;
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
    EntryUtilServlet servlet;

    public Connection() {
    }
    /**
     * Main constructor used, sets up streams 
     * @param socket
     * @throws IOException 
     */
    public Connection(Socket socket) throws IOException { //may be worth handling elsewhere? maybe
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream()); //can also use buffered reader
        in = new ObjectInputStream(socket.getInputStream());
        active = true;
    }
    
   
    /**
     * Sends message along socket to server
     * @param msg 
     */
    public void send(Message msg) {
        try {
            out.writeObject(msg);
        } catch (IOException ex) {
            System.out.println("Unable to send message");
            //ex.printStackTrace(System.out);
        }
    }
    
    /**
     * Recieves message to process
     * @return
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public Message recieve() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    
    /**
     * Closes socket and associated streams
     */
    public void close() {
        try {
            active = false;
            out.flush();
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Error closing connection object");
            //e.printStackTrace();
        }

    }
    
    //Accessor methods
    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public void setServlet(EntryUtilServlet servlet) {
        this.servlet = servlet;
    }
    
    public boolean isActive(){
        return this.active;
    }
    
     public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


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
                System.out.println(msg.getHeader());
                servlet.processMessage(msg);
                //player.processMessage(msg);

            }
        } catch (Exception e) {
            close();
            System.out.println("Error: Unexpected server disconnect.");
            //e.printStackTrace();
        }
    }

}
