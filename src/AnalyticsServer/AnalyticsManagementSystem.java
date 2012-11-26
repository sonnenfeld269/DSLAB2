/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import Event.Event;
import RMI.ManagementClientCallBackInterface;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sanker
 */
public class AnalyticsManagementSystem implements Runnable {
    
    private ExecutorService pool=null;
    private Logger logger=null;
    /*maps*/
    private ConcurrentHashMap<Long,Filter> mclient_map=null;
    private ConcurrentHashMap<Long,Filter> subscription_map=null;
    /*message objects*/
    private LinkedBlockingQueue<Task> rmitoamsincomechannel=null;
    private LinkedBlockingQueue<Task.RESULT> amstormisoutcomechannel=null;
    private LinkedBlockingQueue<Event> distributorincomechannel=null;
    /*Handler*/
    private MessageDistributor MD =null;
    
    public AnalyticsManagementSystem(ExecutorService pool,
            LinkedBlockingQueue<Task> rmitoamsincomechannel,
            LinkedBlockingQueue<Task.RESULT> amstormisoutcomechannel,
            LinkedBlockingQueue<Event> distributorincomechannel)
    {
        this.logger=LogManager.getLogger(LogManager.ROOT_LOGGER_NAME+
                "."+AnalyticsManagementSystem.class.getSimpleName());
       
        this.pool=pool;
        //ManagementClient ID and filter
        this.mclient_map=new ConcurrentHashMap<Long,Filter>();
        this.subscription_map=new ConcurrentHashMap<Long,Filter>();
        this.rmitoamsincomechannel=rmitoamsincomechannel;
        this.amstormisoutcomechannel=amstormisoutcomechannel;
        this.distributorincomechannel=distributorincomechannel;
        
        MD=new MessageDistributor(this.distributorincomechannel);
        
    }
    
    public void run()
    {
        //start Distributor
        pool.execute(MD);
        
        
        Task task;
        while(!Thread.currentThread().isInterrupted())
        {
            Task.RESULT result=null;
            long subscriptionID=0;
            try {
               
                task=this.rmitoamsincomechannel.take();
               
                if(task.isSubscriber())
                {
                   Long id=null;
                   try{ 
                       logger.debug("Taken Subscriber Task from RMI.");
                       Filter filter=null;
                       Task.SUBSCRIBER subscriber=task.getSubscriber();
                       id=new Long(subscriber.mccbi.getManagementClientID());

                       //find handler for this subscription or create on
                       if(this.mclient_map.containsKey(new Long(id)))
                       {
                           filter=this.mclient_map.get(id);
                           subscription_map.put(subscriber.subscribeID, filter);
                           filter.subscribeRegex(subscriber.subscribeID,subscriber.regex);

                       }else
                       {
                           filter=new Filter(id.longValue());
                           filter.subscribeRegex(subscriber.subscribeID,subscriber.regex);
                           subscription_map.put(subscriber.subscribeID, filter);
                           mclient_map.put(new Long(id), filter);
                           LinkedBlockingQueue<Event> lbq=new LinkedBlockingQueue<Event>();
                           this.MD.registerOutcomingMember(lbq);
                           MClientHandler handler=new MClientHandler(id.longValue()
                                   ,filter
                                   ,subscriber.mccbi
                                   ,lbq);
                           pool.execute(handler);
                       }
                      result = new Task.RESULT(true,subscriptionID);
                   }catch(Exception ex)
                   {
                       logger.error("Failure in Subcription of ID "+id+" :Exception:"+ex.getMessage());
                       result = new Task.RESULT(false,subscriptionID);
                       
                   }
                   
                   this.amstormisoutcomechannel.offer(result);
                   
                   
                }else
                {
                   Long subscribtion_id=null;
                   try{
                       logger.debug("Taken Unsubscriber Task from RMI."); 
                       Task.UNSUBSCRIBER unsubscriber=task.getUnSubscriber(); 
                       Filter filter=null;                   
                       subscribtion_id=new Long(unsubscriber.subscriptionID);
                       if(subscription_map.containsKey(subscribtion_id))
                       {
                          
                          filter=subscription_map.remove(subscribtion_id);
                          Long filter_id=new Long(filter.getFilterID());
                          if(subscription_map.containsKey(filter_id))
                              throw new Exception("Subscription ID was not properly deleted.");
                          if(!mclient_map.containsKey(filter_id))
                              throw new Exception("No Filter exists for "
                                      +"propper Subcsription ID "
                                      +subscribtion_id
                                      +" .");
                          filter.unsubscribeRegex(subscribtion_id);
                          if(filter.getSubscriberSize()==0)
                          {
                             mclient_map.remove(filter_id);
                             MD.deregisterOutcomingMember(filter_id);
                             
                             
                          
                          }
                          result = new Task.RESULT(true,subscriptionID);
                       }else{
                          result = new Task.RESULT(false,subscriptionID); 
                       }
                       
                       
                       
                       
                   }catch(Exception ex)
                   {
                       logger.error("Failure in Unsubscription of ID "+subscribtion_id+" :Exception:"+ex.getMessage());
                       result = new Task.RESULT(false,subscriptionID);
                   
                   }
                    
                    this.amstormisoutcomechannel.offer(result);
                   
                   
                   
                   
                }
                                        
              
                
                 
                
                
                
                
                
                this.amstormisoutcomechannel.offer(result);
            } catch (InterruptedException ex) {
               Thread.currentThread().interrupt();
              logger.catching(ex);
              Thread.currentThread().interrupt();
            } catch (Exception ex) {
              logger.catching(ex);
              Thread.currentThread().interrupt();
            }       
            
        }
        
    
    }
    
     public void close()
     {
         //close all resources
         
         
         
         
         
     }
   
}