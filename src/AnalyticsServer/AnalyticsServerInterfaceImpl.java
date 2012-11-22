/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import RMI.AnalyticsServerInterface;
import Event.Event;
import RMI.ManagementClientCallBackInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Dave
 */
public class AnalyticsServerInterfaceImpl  extends UnicastRemoteObject implements AnalyticsServerInterface {
    
    private static long counter=0;
    private ManagementClientCallBackInterface mclient=null;
    String filter =null;
    
    
    public AnalyticsServerInterfaceImpl() throws RemoteException {
        super();
    }


    @Override
    public void processEvents(Event e) throws RemoteException {
        System.out.println("Event was passed:\n" + e.toString());
        if(mclient!=null)
        {
            mclient.processEvent(e.toString());
        }
    }

    @Override
    public long subscribe(ManagementClientCallBackInterface mclient,String filter) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet.");
        long id=this.getNewAuctionID();
        
        this.mclient=mclient;
        this.filter=filter;
        
        System.out.println("A Management Client has done a subscription with filter:"+filter);
        
        
        return id;
        
    }

    @Override
    public void unsubscribe(int subsciptionID) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    
    private synchronized long getNewAuctionID()
    {
      return AnalyticsServerInterfaceImpl.counter++;  
    }

    
}
