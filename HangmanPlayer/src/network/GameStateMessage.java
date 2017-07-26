/*
 */
package network;

/**
 * Represents current state of game being played
 * @author Jack L. Clements
 */
public class GameStateMessage extends Message{
    static final long serialVersionUID = 110;
    private char [] word; //note, spaces left \u0000 are blank
    private int score;
    
    public GameStateMessage(String header) {
        super(header);
    }
    
    public GameStateMessage(String header, char[] word, int score){
        super(header);
        this.word = word;
        this. score = score;
    }
    
    public int getScore(){
        return this.score;
    }
    
    public char [] getWord(){
        return this.word;
    }
    
}
