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
            Task.RESULT result=null;//used in subscriber or unsubscriber part
            long subscriptionID=0;//used in subscriber or unsubscriber part
            try {
               
                task=this.rmitoamsincomechannel.take();
               
               
                
                if(task.isSubscriber())
                {
                   /************SUBSCRIBER****************/
                   Long clientId=null;
                   Task.SUBSCRIBER subscriber=task.getSubscriber();
                   subscriptionID=subscriber.subscribeID;
                   try{ 
                       
                        //cannot subscribe with already defined ID
                        if(subscription_map.containsKey(subscriptionID))
                               throw new Exception("Subscription ID "
                                       +subscriber.subscribeID
                                       +"already defined.");
                       
                       logger.debug("Taken Subscriber Task from RMI.");
                       Filter filter=null;
                       
                       //get ClientID
                       clientId=new Long(subscriber.mccbi.getManagementClientID());

                       //find handler for ClientID to handle this subscription or create on
                       if(this.mclient_map.containsKey(clientId))
                       {
                           
                           //a handler already exist for this managementclient
                           logger.debug("Found Handler with ClientID "
                                   +clientId.longValue()
                                   +" for handling subscription ID "
                                   +subscriber.subscribeID);
                           

                           filter=this.mclient_map.get(clientId);
                           filter.subscribeRegex(subscriber.subscribeID,subscriber.regex);
                           subscription_map.put(subscriber.subscribeID, filter);
                           

                       }else
                       {
                            //a new handler must be created for this managementclient
                           //filter ID and Client ID are the same
                           //because only one filter exist for only one managemenclient
                           filter=new Filter(clientId.longValue());
                           filter.subscribeRegex(subscriber.subscribeID,subscriber.regex);
                           subscription_map.put(subscriber.subscribeID, filter);
                           mclient_map.put(new Long(clientId), filter);
                           //create incomingChannel for new ManagementClient Handler
                           LinkedBlockingQueue<Event> lbq=new LinkedBlockingQueue<Event>();
                           //the new incoming channel must be registered in the distributor
                           //all new events will be put on this new channel
                           this.MD.registerOutcomingMember(lbq);
                           MClientHandler handler=new MClientHandler(clientId.longValue()
                                   ,filter
                                   ,subscriber.mccbi
                                   ,lbq);
                           //execute handler
                           pool.execute(handler);
                       }
                      result = new Task.RESULT(true,subscriptionID);
                      /***********END SUBSCRIBER****************/  
                   }catch(Exception ex)
                   {
                       logger.error("Failure in Subcription of ID "+clientId+" :Exception:"+ex.getMessage());
                       result = new Task.RESULT(false,subscriptionID);
                       
                   }
                   
                   this.amstormisoutcomechannel.offer(result);
                   
                   
                }else
                {
                    /************UNSUBSCRIBER****************/
                   
                   try{
                       
                       Task.UNSUBSCRIBER unsubscriber=task.getUnSubscriber();
                       subscriptionID=unsubscriber.subscriptionID;
                       logger.debug("Taken Unsubscriber Task for Subcription ID"
                               +subscriptionID+" from RMI."); 
                                        
                      
                       if(!subscription_map.containsKey(subscriptionID))
                           throw new Exception("Subscription ID"
                                   +" does not exist.");
                      

                      Filter filter=subscription_map.remove(subscriptionID);
                      long filter_id=filter.getFilterID();
                      if(subscription_map.containsKey(filter_id))
                          throw new Exception("Subscription ID was not properly deleted.");
                      //filter_id=client_id
                      //always one filter exist for one management client
                      if(!mclient_map.containsKey(filter_id))
                          throw new Exception("No Filter exists for "
                                  +"propper Subcsription ID "
                                  +subscriptionID
                                  +" .");
                      filter.unsubscribeRegex(subscriptionID);
                      if(filter.getSubscriberSize()==0)
                      {
                         mclient_map.remove(filter_id);
                         
                         //will remove incomechannel from handler 
                         //and send the handler a kill message
                         MD.deregisterOutcomingMember(filter_id);



                      }
                      result = new Task.RESULT(true,subscriptionID);
                      /***********END UNSUBSCRIBER****************/  
                   }catch(Exception ex)
                   {
                       logger.error("Failure in Unsubscription of ID "+subscriptionID+" :Exception:"+ex.getMessage());
                       result = new Task.RESULT(false,subscriptionID);
                   
                   }
                    
                    this.amstormisoutcomechannel.offer(result);

                }

                //return succes status back to rmi
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