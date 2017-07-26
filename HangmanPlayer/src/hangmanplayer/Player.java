/*
 */
package hangmanplayer;

import java.io.IOException;
import java.util.Scanner;
import network.*;

/**
 *
 * @author Jack L. Clements
 */
public class Player implements Runnable{

    private boolean gameOver;
    private Connection con;

    public Player() {
        gameOver = false;
    }

    public void addConnection(Connection con) {
        this.con = con;
    }

    public void processMessage(Message msg) throws IOException {
        if (msg instanceof GameEndMessage) {
            gameOver = true;
            con.send(new GameEndMessage("Disconnect", true)); //class doubles as a d/c
            con.close();
            System.out.print(msg.getHeader());
        }
        if (msg instanceof GameStateMessage) {
            String thing = String.copyValueOf(((GameStateMessage) msg).getWord());
            System.out.println("Current word - " + thing);
            //getGuess();
        }
        if (msg instanceof CorrectMessage) {
            System.out.println(((CorrectMessage) msg).getCorrect());
        }
        if (msg instanceof CurrentGamesMessage) {
            System.out.println("Choose room no. 1-" + ((CurrentGamesMessage) msg).getGames() + "or press 0 to join new room");
            Scanner scan = new Scanner(System.in);
            int choice = scan.nextInt();
            boolean newRoom = false;
            if (choice == 0 || choice > ((CurrentGamesMessage) msg).getGames()) {
                newRoom = true;
            }
            con.send(new ChooseGameMessage("Room chosen", newRoom, choice));
            //
            new Thread(this).start();
        }
    }
    
    //For non-threaded solution, blocks so cannot recieve updates, classed as not-efficient
    @Deprecated 
    public void getGuess() {
        System.out.println("Guess a letter - "); //need more robust way
        Scanner scan = new Scanner(System.in);
        char guess = scan.next().toLowerCase().charAt(0);
        GuessMessage msg = new GuessMessage("Guess - ", guess);
        con.send(msg);
    }
    
    /**
     * Runs the user IO logic, although due to blocking, it may wait for input after connection has closed
     * This issue is avoided via catching exception in connection class
     */
    @Override
    public void run(){
        while(!gameOver){
            System.out.println("Guess a letter - "); //need more robust way
            Scanner scan = new Scanner(System.in);
            char guess = scan.next().toLowerCase().charAt(0);
            GuessMessage msg = new GuessMessage("Guess - ", guess);
            con.send(msg);
        }
    }

}
