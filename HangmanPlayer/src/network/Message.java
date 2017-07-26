package network;

import java.io.Serializable;

/**
 * Abstract class defining structure for all messages sent within the system
 * @author Jack L. Clements
 */
public abstract class Message implements Serializable{
    static final long serialVersionUID = 101;
    
    //Header describing the message intent
    String header;
    
    /**
     * default constructor
     * @param header 
     */
    protected Message(String header){
        this.header = header;
    }
    
    /**
     * Accessor method
     * @return header as string
     */
    public String getHeader(){
        return this.header;
    }
    
    //instanceOf can tell classes above, serializing to send/read
    //One thread per connection - allows for implementation of multicast, threads allow for easy send/recieve 
    //Own facade for class - no need for anything other for sending by calling class, connection class handles io logic
    
}
