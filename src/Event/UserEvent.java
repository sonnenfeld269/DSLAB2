/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Event;

/**
 *
 * @author Marko
 */


public class UserEvent extends  Event {
    private String userName=null; 
    
    public UserEvent(String userName,UserEvent.UserEventType type)
    {
        super(type.name());
        this.userName=userName;
       
        
    }
    public UserEvent(UserEvent e)
    {
        super(e);
        this.userName=e.userName;
    }
    
    public String getUserName()
    {
        return this.userName;
    }
    
    public static enum UserEventType
    {
        USER_LOGIN,
        USER_LOGOUT,
        USER_DISCONNECTED
    }

    public String toString()
    {
        String msg = null;
        if(super.getType().contains("USER_LOGIN"))
        {
            msg="user "+this.userName+" logged in."; 
        }else  if(super.getType().contains("USER_LOGOUT"))
        {
             msg="user "+this.userName+" logged out."; 
        }else  if(super.getType().contains("USER_DISCONNECTED"))
             msg="user "+this.userName+" is disconnected."; 
        
        return (super.toString() + msg );
               
               
    }
   
}

