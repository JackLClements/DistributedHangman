/*
 */
package hangmanplayer;

import network.ConnectionManager;

/**
 * 
 * @author Jack L. Clements
 */
public class HangmanPlayer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConnectionManager manager = new ConnectionManager();
        manager.run();
    }
    
}
