/*
 */
package hangmandealer;

import java.io.IOException;
import network.ConnectionManager;

/**
 *
 * @author Jack L. Clements
 */
public class HangmanDealer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        ConnectionManager manager = new ConnectionManager();
        manager.run();
    }
    
}
