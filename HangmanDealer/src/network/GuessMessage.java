/*
 */
package network;

/**
 *
 * @author Jack L. Clements
 */
public class GuessMessage extends Message {
    static final long serialVersionUID = 102;
    private char guessChar;
    
    public GuessMessage(String header) {
        super(header);
    }
    
    public GuessMessage(String header, char guessChar){
        super(header);
        this.guessChar = guessChar;
    }
    
    public void setGuessChar(char guessChar){
        this.guessChar = guessChar;
    }
    
    public char getGuessChar(){
        return this.guessChar;
    }
    
}
