/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import AnalyticsServer.*;
import BillingServer.BillingServerSecure;
import Event.Event;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Dave
 */
public interface BillingServerInterface extends Remote{
    
    public  BillingServerSecure login(String username, String password);
    
}
