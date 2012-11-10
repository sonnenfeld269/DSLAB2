/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Dave
 */
public interface AnalyticsServerInterface extends Remote{
    
    public void subscribe() throws RemoteException;
    public void processEvents() throws RemoteException;
    public void unsubscribe() throws RemoteException;
}
