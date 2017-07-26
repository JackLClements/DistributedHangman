/*
 */
package network;

import hangmanplayer.Player;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Jack L. Clements
 */
public class ConnectionManager implements Runnable {
    private ServerSocket server;
    //you also need some sort of frontend...
    //server should also keep a copy of tasks
    
    
    public ConnectionManager(){
    }
    
    @Override
    public void run(){
        try{
            System.out.println("Starting connection attempt.");
            InetAddress server = InetAddress.getByName("localhost");
            Socket connection = new Socket(server, 55552);
            Connection con1 = new Connection(connection); //new abstraction that handles a player connection
            Player player = new Player();
            con1.setPlayer(player);
            player.addConnection(con1);
            new Thread(con1).start();
            //new Thread(player).start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
}
