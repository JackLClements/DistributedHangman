/*
 */
package network;

/**
 *
 * @author Jack L. Clements
 */
public class GameEndMessage extends Message{
    static final long serialVersionUID = 103;
    private boolean win;
    
    
    public GameEndMessage(String header) {
        super(header);
    }
    
    public GameEndMessage(String header, boolean win){
        super(header);
        this.win = win;
    }
    
    public boolean getWin(){
        return win;
    }
    
}
