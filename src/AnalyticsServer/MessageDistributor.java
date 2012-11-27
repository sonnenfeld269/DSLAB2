/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import Event.AnalyticsControllEvent;
import Event.Event;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Marko
 */
public class MessageDistributor implements Runnable{
    private static long counter=0;
    private LinkedBlockingQueue<Event> incomingchannel=null;   
    private ConcurrentHashMap<Long,LinkedBlockingQueue<Event>> outcomingdistributor=null;
    private Logger logger=null;
    public MessageDistributor(LinkedBlockingQueue<Event> incomingchannel)
    {   
        logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME+"."+MessageDistributor.class.getSimpleName());
        this.incomingchannel=incomingchannel;
        outcomingdistributor=new ConcurrentHashMap<Long,LinkedBlockingQueue<Event>>();
    }
    
    public void run()
    {
        logger.entry();
        Event event=null;
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                
               event=incomingchannel.take();
               if(event.getType().contains(AnalyticsControllEvent.AnalyticsControllEventType.CLOSE_DISTRIBUTOR.name()))
               {
                   Thread.currentThread().interrupt();
                   break;
               }
               if(!this.outcomingdistributor.isEmpty())
               {
                   
                   Iterator<Map.Entry<Long,LinkedBlockingQueue<Event>>> iter = outcomingdistributor.entrySet().iterator();
                   while(iter.hasNext())
                   {
                        Map.Entry<Long,LinkedBlockingQueue<Event>> entry = iter.next(); 
                        entry.getValue().offer(event);
                   }
                   logger.debug("Distributed event: "+event.getType()+" to all MClientHandler.");
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
   
    public  Long registerOutcomingMember(long channelID,LinkedBlockingQueue<Event> lbq)throws MessageDistributorException
    {
        logger.entry();
        //ClientID=FILTERID=ChannelID
        Long id=new Long(channelID);
        this.outcomingdistributor.put(id, lbq);
        logger.debug("Registered a new channel in the distributor List with Channel ID"+channelID);
        if(!this.outcomingdistributor.containsKey(id))
        {
            logger.error("Hashmap registration unsuccesfull.");
            throw new MessageDistributorException("Hashmap registration unsuccesfull.");
        }
        logger.trace("Added new queue to outcomingdistributor.");
        logger.exit();
        return id;
        
        
    }
    
    public  void deregisterOutcomingMember(Long id)
    {
        logger.entry();
        if(outcomingdistributor.containsKey(id))
        {
            LinkedBlockingQueue<Event> out=this.outcomingdistributor.remove(id);
            out.offer(new AnalyticsControllEvent(id.longValue(),
                    AnalyticsControllEvent.AnalyticsControllEventType.CLOSE_FILTER));
            logger.debug("Removed queue from outcomingdistributor "
                    +"and send close message to Handler.");
        }
        logger.exit();
    }
    
   
    
    private Long getID()
    {
        logger.entry();
        logger.exit();
        return new Long(counter++);
    }
    
    
    public void close()
    {
        logger.debug("Closing MessageDistributor and all ressources.");
        if(!this.outcomingdistributor.isEmpty())
        {
            Event event=null;    
           Iterator<Map.Entry<Long,LinkedBlockingQueue<Event>>> iter = outcomingdistributor.entrySet().iterator();
           while(iter.hasNext())
           {
                Map.Entry<Long,LinkedBlockingQueue<Event>> entry = iter.next(); 
                event=new AnalyticsControllEvent(entry.getKey().longValue(),
                    AnalyticsControllEvent.AnalyticsControllEventType.CLOSE_FILTER);
                entry.getValue().offer(event);
           }
           logger.debug("Send kill message to al MCClientHandler");
        }
       
        
         this.outcomingdistributor.clear();
         this.incomingchannel=null;
         this.outcomingdistributor=null;
         
          logger.trace("Closing  MessageDistributor finished.");
         
    }
}