/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BillingServer;

import AnalyticsServer.*;
import Event.Event;
import RMI.BillingServerInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Dave
 */
public class BillingServerInterfaceImpl  extends UnicastRemoteObject implements BillingServerInterface {
    
    public BillingServerInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public BillingServerSecure login(String username, String password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
