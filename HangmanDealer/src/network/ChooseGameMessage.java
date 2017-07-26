/*
 */
package network;

/**
 * Sent when a new game is established or a running game is chosen
 * @author Jack L. Clements
 */
public class ChooseGameMessage extends Message{
    static final long serialVersionUID = 120;
    private boolean newRoom;
    private int roomNo;
    
    public ChooseGameMessage(String header) {
        super(header);
    }
    
    public ChooseGameMessage(String header, boolean newRoom) {
        super(header);
        this.newRoom = newRoom;
    }
    
    public ChooseGameMessage(String header, boolean newRoom, int roomNo) {
        super(header);
        this.newRoom = newRoom;
        this.roomNo = roomNo;
    }
    
    public boolean isNewRoom(){
        return newRoom;
    }
    
    public int getRoomNo(){
        return roomNo; 
   }
    
}
