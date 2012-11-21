/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import RMI.AnalyticsServerInterface;
import Event.Event;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Dave
 */
public class AnalyticsServerInterfaceImpl  extends UnicastRemoteObject implements AnalyticsServerInterface {
    
    public AnalyticsServerInterfaceImpl() throws RemoteException {
        super();
    }


    @Override
    public void processEvents(Event e) throws RemoteException {
        System.out.println("Event was passed:\n" + e.toString());
    }

    @Override
    public void subscribe() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unsubscribe() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
