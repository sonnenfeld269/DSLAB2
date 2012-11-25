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
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author Dave
 */
public class AnalyticsServerInterfaceImpl  extends UnicastRemoteObject implements AnalyticsServerInterface {
    
    private static long counter=0;
    private LinkedBlockingQueue<Task> outgoingchannel=null;
    LinkedBlockingQueue<Event> distributorchannel=null;
    private Logger logger=null;
    
    public AnalyticsServerInterfaceImpl() throws RemoteException {
        super();
    }

    public void initialize(LinkedBlockingQueue<Task> amschannel,LinkedBlockingQueue<Event> distributorchannel)
    {
        logger=Logger.getLogger(AnalyticsServerInterfaceImpl.class);
        this.outgoingchannel=amschannel;
        this.distributorchannel=distributorchannel;
    }

    @Override
    public void processEvents(Event e) throws RemoteException {
        logger.debug("RMI send an Event to the distributorChannel.");
        this.distributorchannel.offer(e);
    }

    @Override
    public long subscribe(ManagementClientCallBackInterface mclient,String filter) throws RemoteException {
        //throw new UnsupportedOperationException("Not supported yet.");
        long id=this.getNewSubscribtionID();
        
        Task.SUBSCRIBER subscriber=new Task.SUBSCRIBER(mclient,filter);
        Task task = new Task(subscriber);
        logger.debug("RMI send an Suscriber Task to the AnalyticsManagementSystem.");
        this.outgoingchannel.offer(task);
        
        return id;
        
    }

    @Override
    public void unsubscribe(long subsciptionID) throws RemoteException {
        
        Task.UNSUBSCRIBER unsubscriber = new Task.UNSUBSCRIBER(subsciptionID);
        Task task = new Task(unsubscriber);
        logger.debug("RMI send an UnSuscriber Task to the AnalyticsManagementSystem.");
        this.outgoingchannel.offer(task);
        
    }
    
    
    private synchronized long getNewSubscribtionID()
    {
      return AnalyticsServerInterfaceImpl.counter++;  
    }
   
    
}