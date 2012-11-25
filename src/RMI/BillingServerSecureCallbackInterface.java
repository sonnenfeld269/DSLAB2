/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import BillingServer.BillingServerSecure;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Dave
 */
public interface BillingServerSecureCallbackInterface extends Remote{
    
    public BillingServerSecure login(String username, String password) throws RemoteException;
    
}
