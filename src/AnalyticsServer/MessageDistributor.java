/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

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
    public MessageDistributor()
    {   
        logger = LogManager.getLogger(MessageDistributor.class.getSimpleName());
        incomingchannel= new LinkedBlockingQueue<Event>();
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
               if(!this.outcomingdistributor.isEmpty())
               {
                   
                   Iterator<Map.Entry<Long,LinkedBlockingQueue<Event>>> iter = outcomingdistributor.entrySet().iterator();
                   while(iter.hasNext())
                   {
                        Map.Entry<Long,LinkedBlockingQueue<Event>> entry = iter.next(); 
                        entry.getValue().offer(event);
                   }
               }
                                           
              
            } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
            }catch (Exception ex) {
              Thread.currentThread().interrupt();
            }             
        
        }
    
     logger.exit();
    }
   
    public  Long registerOutcomingMember(LinkedBlockingQueue<Event> lbq)throws MessageDistributorException
    {
        logger.entry();
        
        Long id=this.getID();
        this.outcomingdistributor.put(id, lbq);
        if(!this.outcomingdistributor.containsKey(id))
        {
            logger.error("Hashmap registration unsuccesfull.");
            throw new MessageDistributorException("Hashmap registration unsuccesfull.");
        }
        
        logger.exit();
        return id;
        
        
    }
    
    public  void deregisterOutcomingMember(Long id)
    {
        logger.entry();
        this.outcomingdistributor.remove(id);
        logger.exit();
    }
    
   
    
    private Long getID()
    {
        logger.entry();
        logger.exit();
        return new Long(counter++);
    }
}
