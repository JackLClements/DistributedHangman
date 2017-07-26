/*
 */
package hangmanplayer;

import java.util.Scanner;
import network.Connection;
import network.CorrectMessage;
import network.GameEndMessage;
import network.GameStateMessage;
import network.GuessMessage;
import network.Message;

/**
 *
 * @author Jack L. Clements
 */
public class Player implements Runnable {
    
    private boolean gameOver;
    private Connection con;
    
    public Player(){
        gameOver = false;
    }
    
    public void addConnection(Connection con){
        this.con = con;
    }
    
    public void processMessage(Message msg){
        if(msg instanceof CorrectMessage){
            System.out.println(((CorrectMessage) msg).getCorrect());
        }
        if(msg instanceof GameStateMessage){
            String thing = String.copyValueOf(((GameStateMessage) msg).getWord());
            System.out.println("Current word - " + thing);
        }
        if(msg instanceof GameEndMessage){
            gameOver = true;
            con.send(new GameEndMessage("Disconnect", true)); //class doubles as a d/c
            con.close();   
        }
    }

    @Override
    public void run() {
        while(!gameOver){ //need more robust way of doing this so I don't get
            System.out.println("Guess a letter - ");
            Scanner scan = new Scanner(System.in);
            char guess = scan.next().toLowerCase().charAt(0);
            GuessMessage msg = new GuessMessage("Guess - ", guess);
            con.send(msg);
        }
    }
    
}
