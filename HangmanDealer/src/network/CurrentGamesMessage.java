/*
 */
package network;
/**
 * Sends no. of currently running games
 * @author Jack L. Clements
 */
public class CurrentGamesMessage extends Message{
    static final long serialVersionUID = 104;
    private int games; //not really sure how to ID games here, other than via ID or obj. clone
    
    public CurrentGamesMessage(String header) {
        super(header);
    }
    
    public CurrentGamesMessage(String header, int games){
        super(header);
        this.games = games;
    }
    
    public int getGames(){
        return games;
    }
    
}
