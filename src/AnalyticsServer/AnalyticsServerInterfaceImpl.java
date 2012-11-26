/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import Event.Event;
import RMI.AnalyticsServerInterface;
import RMI.ManagementClientCallBackInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class AnalyticsServerInterfaceImpl  extends UnicastRemoteObject implements AnalyticsServerInterface {
    
    private static long counter=0;
    private LinkedBlockingQueue<Task> ToAmsChannel=null;
    private LinkedBlockingQueue<Task.RESULT> FromAmsChannel=null;
    LinkedBlockingQueue<Event> distributorchannel=null;
    private Logger logger=null;
    
    public AnalyticsServerInterfaceImpl() throws RemoteException {
        super();
    }

    public void initialize(LinkedBlockingQueue<Task> ToAmsChannel,
            LinkedBlockingQueue<Task.RESULT> FromAmsChannel,
            LinkedBlockingQueue<Event> distributorchannel)
    {
       
        logger=LogManager.getLogger(LogManager.ROOT_LOGGER_NAME+"."+AnalyticsServerInterfaceImpl.class.getSimpleName());
        this.ToAmsChannel=ToAmsChannel;
        this.FromAmsChannel=FromAmsChannel;
        this.distributorchannel=distributorchannel;

    }

    @Override
    public void processEvents(Event e) throws RemoteException {
        logger.debug("AnalyticsServerInterfaceImpl:RMI send an Event to the distributorChannel.");
        this.distributorchannel.offer(e);
    }

    @Override
    public long subscribe(ManagementClientCallBackInterface mclient,String filter) throws RemoteException {
        long id=this.getNewSubscribtionID();
        boolean b=false;
        Task.SUBSCRIBER subscriber=new Task.SUBSCRIBER(mclient,id,filter);
        Task task = new Task(subscriber);
        logger.debug("AnalyticsServerInterfaceImpl:RMI send an Suscriber Task to the AnalyticsManagementSystem.");
        this.ToAmsChannel.offer(task);
        try {
            b=getResultFromAMS(id);
        } catch (InterruptedException ex) {
            logger.catching(ex);
        }
        
        
        return id;
        
    }

    @Override
    public boolean unsubscribe(long subsciptionID) throws RemoteException {
        boolean b=false;
        Task.UNSUBSCRIBER unsubscriber = new Task.UNSUBSCRIBER(subsciptionID);
        Task task = new Task(unsubscriber);
        logger.debug("AnalyticsServerInterfaceImpl:RMI send an UnSuscriber Task to the AnalyticsManagementSystem.");
        this.ToAmsChannel.offer(task);
        try{
            b=getResultFromAMS(subsciptionID);
        }catch(InterruptedException ex)
        {
             logger.catching(ex);
        }
        
        return b;
    }
    
    
    private synchronized long getNewSubscribtionID()
    {
      return AnalyticsServerInterfaceImpl.counter++;  
    }
    
    private boolean getResultFromAMS(long subscriptionID) throws InterruptedException
    {
        boolean b=false;
        
        while(true)
        {
            Task.RESULT result = FromAmsChannel.take();
            if(result.subscriptionID!=subscriptionID)
            {
                FromAmsChannel.add(result);
            }
            else
            {
                b=result.success;
                break;
            }
                       
        }
        return b;
    
    } 
    
    
    
}