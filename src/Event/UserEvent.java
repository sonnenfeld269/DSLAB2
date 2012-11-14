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

   
}

