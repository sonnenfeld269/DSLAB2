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
    private ConcurrentHashMap<Long,Filter> mclient_map=null;
    private LinkedBlockingQueue<Task> rmitoamsincomechannel=null;
    private LinkedBlockingQueue<Task.RESULT> amstormisoutcomechannel=null;
    private LinkedBlockingQueue<Event> distributorincomechannel=null;
    
    public AnalyticsManagementSystem(ExecutorService pool,
            LinkedBlockingQueue<Task> rmitoamsincomechannel,
            LinkedBlockingQueue<Task.RESULT> amstormisoutcomechannel,
            LinkedBlockingQueue<Event> distributorincomechannel)
    {
        this.logger=LogManager.getLogger(LogManager.ROOT_LOGGER_NAME+
                "."+AnalyticsManagementSystem.class.getSimpleName());
       
        this.pool=pool;
        this.mclient_map=new ConcurrentHashMap<Long,Filter>();
        this.rmitoamsincomechannel=rmitoamsincomechannel;
        this.amstormisoutcomechannel=amstormisoutcomechannel;
        this.distributorincomechannel=distributorincomechannel;
        
    }
    
    public void run()
    {
        Task task;
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                
                task=this.rmitoamsincomechannel.take();
              
                  //pool.                        
              
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
    
    
   
}