/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Event;
import java.io.Serializable;
import java.util.Date;


/**
 *
 * @author Marko
 */
public class Event implements Serializable{
   
    private String ID=null;
    private String type=null;
    long timestamp=0;
    
    public Event(String type)
    {
        
        Date date = new Date();
        timestamp= date.getTime();
        this.ID= type+timestamp;
        this.type=type;
    }
    
    public Event(Event e)
    {
        this.ID=e.getID();
        this.timestamp=e.timestamp;
        this.type=e.type;
    }
    
    public String getID()
    {
        return this.ID;
    }
    
    public long getTimeStamp()
    {
        return this.timestamp;
    }
    
    public String getType()
    {
        return this.type;
    }
    
    
}
