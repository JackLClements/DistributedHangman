/*
 */
package hangmandealer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import network.*;

/**
 * Controls game logic/game state
 *
 * @author Jack L. Clements
 */
public class Dealer {

    private enum state {
        PLAYING, SOLVED, FAILED
    }; //use of methods here not needed, with safety could be int instead
    //game variables
    //may be able to optimise this by removing solved words but is in essence the same thing rn

    private char[] word;
    private boolean[] solved;
    private state gameState;
    private int guessLimit;
    private int guesses;
    private int gameNo;
    private static int noOfGames = 0;
    //player info
    private ArrayList<Connection> players;
    
    
    //dealer log
    private static File log; //static for out of class access
    private PrintWriter pw; //local
    private FileOutputStream fw;
    
    
    //constructors
    /**
     * Default constructor
     * Generates new word and sets up class fields
     * @throws IOException 
     */
    public Dealer() throws IOException {
        //set up log
        log = new File("Log.txt");
        if(log.exists()){
            fw = new FileOutputStream(log, true);
        }
        else{
            fw = new FileOutputStream(log);
        }
        pw = new PrintWriter(fw);
        
        String newWord = generateNewWord();
        noOfGames++;
        gameNo = noOfGames;
        pw.write("New game " + gameNo + ". Word to guess - " + newWord + "\n");
        word = newWord.toCharArray();
        solved = new boolean[word.length];
        gameState = state.PLAYING;
        players = new ArrayList<>();
        guessLimit = 15;
        guesses = 0;
    }

    //separate guess letter and w/e into things
    //boolean?
    public synchronized boolean guessLetter(char guess) {
        boolean letterFound = false;
        if (gameState == state.PLAYING) {
            for (int i = 0; i < word.length; i++) {
                if (word[i] == guess) { //if letter is correct and not already solved - solutions inc. letter buffer? think more on this later
                    solved[i] = true;
                    letterFound = true;
                    System.out.println("Found!");
                }
            }
            if (!letterFound) {
                guesses++;
                if (guesses > guessLimit) {
                    this.gameState = state.FAILED;
                    pw.write("Game " + gameNo + "ended - game failed" + "\n");
                    pw.flush();
                    pw.close();
                    sendToAll(new GameEndMessage("You Lose! Answer - " + String.copyValueOf(word), false));
                }
            }
            if (isSolved()) { //program logic is messy and it'll all be void once separated out
                System.out.println("Solved");
                pw.write("Game " + gameNo + " ended - game won" + "\n");
                pw.flush();
                pw.close();
                this.gameState = state.SOLVED;
                sendToAll(new GameEndMessage("You Win! Answer - " + String.copyValueOf(word), true));
                ConnectionManager.removeDealer(this);
            }
            GameStateMessage msg = new GameStateMessage("Game State - ", getProgress(), guesses);
            sendToAll(msg);
        }
        return letterFound;
    }
    
    /**
     * Checks solved characters to see whether the hangman has been completed
     * @return true if all characters have been guessed, otherwise false
     */
    public boolean isSolved() {
        boolean isSolved = true;
        for (boolean b : solved) {
            if (!b) {
                isSolved = false;
            }
        }
        return isSolved;
    }
    
    /**
     * Generates a new word from the inluded .txt file
     * @return A new word as a String
     * @throws FileNotFoundException if the file cannot be located
     * @throws IOException if a problem occurs during reading the file
     */
    public String generateNewWord() throws FileNotFoundException, IOException { //use try-catch later
        Random rng = new Random();
        File file = new File("dictionary.txt");
        
        //size is 122294, easier than re-scanning file on setup every time, may do that later though
        RandomAccessFile reader = new RandomAccessFile("dictionary.txt", "r");
        long random = (long) rng.nextInt(122293);
        System.out.println(random);
        reader.seek(random);
        reader.readLine(); //flush what is most likely a partial word
        //slight stopgap solution in that word 0 will never appear
        //however it can be padded, it's just "A" anyway
        String word = reader.readLine().toLowerCase();
        System.out.println(word);
        return word;
    }
    /**
     * Adds a player to the currently running game
     * @param con Connection object
     */
    public void addPlayer(Connection con) {
        if (gameState == state.PLAYING) {
            players.add(con);
        }
        con.send(new GameStateMessage("Game State", getProgress(), guesses));
    }
    
    /**
     * Returns all players in ArrayList data structure
     * @return Connection objects in list
     */
    public ArrayList<Connection> getPlayers() {
        return players;
    }
    
    /**
     * Returns the current characters solved in the word.
     * Characters yet to be solved are represented using the char null \u0000
     * @return 
     */
    public char[] getProgress() {
        int wordlength = word.length;
        char[] progress = new char[wordlength];
        for (int i = 0; i < wordlength; i++) {
            if (solved[i]) {
                progress[i] = word[i];
            } else {
                progress[i] = '\u0000';
            }
        }
        return progress;
    }
    
    //Accessor methods
    public int getGuesses() {
        return this.guesses;
    }

    public void setGuesses(int guesses) {
        this.guesses = guesses;
    }

    public int getGuessLimit() {
        return this.guessLimit;
    }

    public void setGuessLimit(int guessLimit) {
        this.guessLimit = guessLimit;
    }

    public char[] getWord() {
        return this.word;
    }

    public void setWord(char[] word) {
        this.word = word;
    }

    /**
     * Send message to player with ID id
     *
     * @param msg message to send
     * @param id ID of player
     */
    public void sendMessage(Message msg, int id) {
        boolean found = false;
        int index = 0;
        while (!found && index < players.size()) {
            if (players.get(index).getID() == id) {
                found = true;
                players.get(index).send(msg);
            } else {
                index++;
            }
        }
    }
    /**
     * Send message to all players currently connected
     * @param msg 
     */
    public void sendToAll(Message msg) {
        for (Connection con : players) {
            con.send(msg);
        }
    }
    
    /**
     * Process a message received regardless of who sent it
     * @param msg 
     */
    public void processMessage(Message msg) {
        if (msg instanceof GuessMessage) {
            boolean guess = guessLetter(((GuessMessage) msg).getGuessChar());
            pw.write("Game " + gameNo + " Guess made - " + ((GuessMessage) msg).getGuessChar() + "\n");
            System.out.println("Guess message - " + ((GuessMessage) msg).getGuessChar());
        }
    }
    
    /**
     * Process a message received with regard to whoever sent it
     * @param msg the message object, in parent class before being defined by subclass
     * @param senderID sender ID no.
     */
    public void processMessage(Message msg, int senderID) {
        if (msg instanceof GameEndMessage) {
            pw.write("Player " + senderID + " disconnected "+ "\n");
            System.out.println("Player " + senderID + " has left the game.");
            boolean playerFound = false;
            int playerPointer = 0;
            //done to exit early, removes player with ID from game
            while (playerFound == false && playerPointer < players.size()) {
                if (players.get(playerPointer).getID() == senderID) {
                    players.get(playerPointer).close();
                    playerFound = true;
                } else {
                    playerPointer++;
                }
            }
        }
        if (msg instanceof GuessMessage) {
            boolean guess = guessLetter(((GuessMessage) msg).getGuessChar());
            pw.write("Game " + gameNo + ": Player " + senderID + " guessed " + ((GuessMessage) msg).getGuessChar() + "\n");
            pw.write("Guess " + ((GuessMessage) msg).getGuessChar() + " was " + guess + "\n");
            System.out.println("Guess message - " + ((GuessMessage) msg).getGuessChar());
            sendMessage(new CorrectMessage("Your guess was ", guess), senderID);
        }

    }
}
