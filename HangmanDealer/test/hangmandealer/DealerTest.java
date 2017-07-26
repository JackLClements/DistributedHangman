/*
 */
package hangmandealer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import network.Connection;
import network.ConnectionManager;
import network.Message;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Dealer class
 * Can be extended as functionality of Dealer increases
 * @author Jack L. Clements
 */
public class DealerTest {
    
    public DealerTest() {
    }

    /**
     * Test of guessLetter method, of class Dealer.
     */
    @Test
    public void testGuessLetter() throws IOException {
        System.out.println("guessLetter");
        Dealer instance = new Dealer();
        char [] newWord = {'a'};
        instance.setWord(newWord);
        boolean result = instance.guessLetter('a');
        if(!result){
            fail("Guess solution incorrect");
        }
        
    }

    /**
     * Test of isSolved method, of class Dealer.
     */
    @Test
    public void testIsSolved() throws IOException {
        System.out.println("isSolved");        
        Dealer instance = new Dealer();
        ConnectionManager.addDealer(instance);
        char [] word = instance.getWord();
        for(int i = 0; i < word.length; i++){
            instance.guessLetter(word[i]);
        }
        if(!ConnectionManager.isEmpty()){
            fail("Not solved");
        }
    }

    /**
     * Test of generateNewWord method, of class Dealer.
     */
    @Test
    public void testGenerateNewWord() throws Exception {
        System.out.println("generateNewWord");
        Dealer instance = new Dealer();
        String result = instance.generateNewWord();
        if(result.isEmpty()){
            fail("No word generated");
        }
    }

    /**
     * Test of addPlayer method, of class Dealer.
     */
    @Test
    public void testAddPlayer() throws IOException {
        System.out.println("addPlayer");
        ConnectionManager man = new ConnectionManager();
        new Thread(man).start();
        InetAddress server = InetAddress.getByName("localhost");
        Socket connection = new Socket(server, 55552);
        Connection con = new Connection(connection);
        Dealer instance = new Dealer();
        instance.addPlayer(con);
        if(instance.getPlayers().isEmpty()){
            fail("Player not added");
        }
        
    }

    /**
     * Test of getProgress method, of class Dealer.
     */
    @Test
    public void testGetProgress() throws IOException {
        System.out.println("getProgress");
        Dealer instance = new Dealer();
        Random rng = new Random();
        
        instance.setGuessLimit(100); //unreachable limit
        char guessChar = '\u0000'; //initialised null
        boolean guess = false; //used to specify whether a correct guess is made
        while(!guess){
            int guessVal = rng.nextInt(26);
            guessChar = (char) (guessVal+97);
            System.out.println("Guess - " + guessChar);
            guess = instance.guessLetter(guessChar);
        }
        
        char [] progress = instance.getProgress();
        guess = false; //reuse variable to check whether instance of guess is in progress
        for(int i = 0; i < progress.length; i++){
            if(progress[i] == guessChar){
                guess = true;
            }
        }
        
        if(!guess){
            fail("Progress not recorded");
        }
        
    }
    
}
