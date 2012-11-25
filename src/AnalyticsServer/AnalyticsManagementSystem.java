/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import RMI.ManagementClientCallBackInterface;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sanker
 */
public class AnalyticsManagementSystem implements Runnable {
    
    private ExecutorService pool=null;
    private Logger logger=null;
    private ConcurrentHashMap<Long,Filter> mclient_map=null;
    private LinkedBlockingQueue<Task> incomingchannel=null;
    
    public AnalyticsManagementSystem(ExecutorService pool,LinkedBlockingQueue<Task> incomingchannel)
    {
        this.pool=pool;
        mclient_map=new ConcurrentHashMap<Long,Filter>();
        this.incomingchannel=incomingchannel;
    }
    
    public void run()
    {
        Task task;
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                
                task=this.incomingchannel.take();
              
                  //pool.                        
              
            } catch (Exception ex) {
              logger.catching(ex);
              Thread.currentThread().interrupt();
            }       
            
        }
        
    
    }
    
    
   
}