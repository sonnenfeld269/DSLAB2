/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import Event.AnalyticsControllEvent;
import Event.Event;
import RMI.ManagementClientCallBackInterface;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sanker
 */
public class MClientHandler implements Runnable{
    
    private Filter filter=null;
    private ManagementClientCallBackInterface mccbi=null;
    private LinkedBlockingQueue<Event> lbq=null;
    private Logger logger=null;
    
    public MClientHandler(Filter filter,ManagementClientCallBackInterface mccbi,LinkedBlockingQueue<Event> lbq)
    {
        this.filter=filter;
        this.mccbi=mccbi;
        this.lbq=lbq;
        logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME+"."+MClientHandler.class.getSimpleName());
    }
    
    public void run()
    {
        logger.entry();
        Event event=null;
        
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                
              event=lbq.take();
              if(event.getType().contains(AnalyticsControllEvent.AnalyticsControllEventType.CLOSE_FILTER.name()))
              {
                  Thread.currentThread().interrupt();
                  break;
                  
              }else  if(event.getType().contains(AnalyticsControllEvent.AnalyticsControllEventType.ERROR_SUBSCRIPTION.name()))
              {
                  mccbi.processEvent(event.toString()); 
                  logger.debug("Send Event: "+event.getType()+" to ManagementClient.");
                  
              }else  if(event.getType().contains(AnalyticsControllEvent.AnalyticsControllEventType.ERROR_UNSUBSCRIPTION.name()))
              {
                  
                  mccbi.processEvent(event.toString()); 
                  logger.debug("Send Event: "+event.getType()+" to ManagementClient.");
                  
              }else if(filter.checkMessage(event.getType()))
              {
                 
                 mccbi.processEvent(event.toString()); 
                 logger.debug("Send Event: "+event.getType()+" to ManagementClient.");
             
              }
                                           
              
            } catch (InterruptedException ex) {
              logger.catching(ex);
              Thread.currentThread().interrupt();
            }catch (Exception ex) {
              logger.catching(ex);
              Thread.currentThread().interrupt();
            }             
        
        }
    
     logger.exit();

    }
}
