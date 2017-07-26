/*
 */
package network;

/**
 * Returns if a msg is correct
 * @author Jack L. Clements
 */
public class CorrectMessage extends Message{
    static final long serialVersionUID = 105;
    private boolean correct;
    
    public CorrectMessage(String header) {
        super(header);
    }
    
    public CorrectMessage(String header, boolean correct){
        super(header);
        this.correct = correct;
    }
    
    public boolean getCorrect(){
       return this.correct;
    }
    
    public void setCorrect(boolean correct){
        this.correct = correct;
    }
    
}
