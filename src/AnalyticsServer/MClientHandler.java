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
    
    private long ClientID;
    private Filter filter=null;
    private ManagementClientCallBackInterface mccbi=null;
    private LinkedBlockingQueue<Event> lbq=null;
    private Logger logger=null;
    
    public MClientHandler(long ClientID,Filter filter,ManagementClientCallBackInterface mccbi,LinkedBlockingQueue<Event> lbq)
    {
        this.ClientID=ClientID;
        this.filter=filter;
        this.mccbi=mccbi;
        this.lbq=lbq;
        logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME+"."+MClientHandler.class.getSimpleName());
    }
    
    public void run()
    {
        logger.entry();
        Event event=null;
        logger.debug("MClientHandler:A new ManagementClient handler is running with ClientID:"
                +ClientID
                +"and FilterID "
                +filter.getFilterID()
                );
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                
              event=lbq.take();
              logger.debug("MClientHandler:Event message type "+event.getType()+" taken.");
              if(event.getType().contains(AnalyticsControllEvent.AnalyticsControllEventType.CLOSE_FILTER.name()))
              {
                  AnalyticsControllEvent asce=(AnalyticsControllEvent)event;
                  if(asce.getIDNumber()==this.ClientID)
                  {
                    logger.debug("Event message leads to closing this Thread. ");
                    Thread.currentThread().interrupt();
                    break;
                  }
                  
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
     this.close();
     logger.exit();

    }
    
    
    private void close()
    {
        
        //close ressources if avaible
        logger.debug("MClientHandler with filter ID "+this.filter.getFilterID()
                +" is closing.");
    }
            
}
