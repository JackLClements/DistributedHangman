/*
 */
package network;

import hangmandealer.Dealer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Jack L. Clements
 */
public class ConnectionManager implements Runnable {

    private ServerSocket server;
    //you also need some sort of frontend...
    //server should also keep a copy of tasks
    private static ArrayList<Dealer> games = new ArrayList<>();

    

    public static void newDealer() throws IOException {
        games.add(new Dealer());
    }

    public static void addDealer(Dealer dealer) {
        games.add(dealer);
    }

    public static void removeDealer(Dealer dealer) {
        games.remove(dealer);
    }

    public static Dealer getDealerAt(int pos) {
        return games.get(pos - 1);
    }

    public static boolean isEmpty() {
        return games.isEmpty();
    }

    public ConnectionManager() throws IOException {
        server = new ServerSocket(55552, 1);
    }

    @Override
    public void run() {
        try {
            System.out.println("Creating server socket...");
            System.out.println("Waiting for connections...");
            System.out.println("Blocking for Connection");
            while (true) {

                Socket connection = server.accept();
                Connection con = new Connection(connection);

                //Send list of msgs
                con.send(new CurrentGamesMessage("Rooms - ", games.size()));
                new Thread(con).start();
                //Add to existing object.
                //dealer.addPlayer(con);
                //con.addDealer(dealer);
                //new Thread(con).start();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
