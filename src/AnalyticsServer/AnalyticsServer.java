/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;


import Event.Event;
import RMI.AnalyticsServerInterface;
import RMI.RMIRegistry;
import RMI.RMIRegistryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is just a temporary test Server class. For lookup of processEvents. Just
 * for RMI Demonstration. This can be for example the AuctionServer or
 * BillingServer.
 *
 * @author Dave
 */
public class AnalyticsServer {

    //private static final int standardPORT = 1099;
    
    private Logger logger=null;
    private RMIRegistry registry=null;
    private AnalyticsServerInterface asi=null;
    private ExecutorService pool=null;
    private LinkedBlockingQueue<Task> toamsincomechannel=null;
    private LinkedBlockingQueue<Task.RESULT> amstormisoutcomechannel=null;
    private LinkedBlockingQueue<Event> distributorincomechannel=null;
    private LinkedBlockingQueue<Event> statisticincomechannel=null;
    private AnalyticsServerInterfaceImpl ASII=null;
    private AnalyticsManagementSystem AMS =null;
    
    public AnalyticsServer (String propertyFile, String analyticBindingName)
    {
        try {
           // logger = LogManager.getLogger(AnalyticsServer.class.getSimpleName());
            logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);
            /**
             * Get or start Registry(Naming Server)
             **/
            registry=new RMIRegistry(propertyFile);
            if(registry.getRegistry()==null){
                registry.startRegistry();
                logger.info("REGISTRY STARTED!");
            }
            
            //create objects necessary for management
            toamsincomechannel=new LinkedBlockingQueue<Task>();
            amstormisoutcomechannel=new LinkedBlockingQueue<Task.RESULT>();
            distributorincomechannel=new LinkedBlockingQueue<Event>();
            statisticincomechannel=new LinkedBlockingQueue<Event>();
            
            pool= Executors.newCachedThreadPool();
            AMS = new AnalyticsManagementSystem(pool,
                    toamsincomechannel,amstormisoutcomechannel,
                    distributorincomechannel,statisticincomechannel);
            //start objects necessary for management
            pool.execute(AMS);
            
            ASII=new AnalyticsServerInterfaceImpl();
            ASII.initialize(toamsincomechannel,amstormisoutcomechannel
                    ,distributorincomechannel,statisticincomechannel);

            /**
             * Register all neccessary RemoteObjects to the registry
             **/
            registry.registerObject(analyticBindingName,ASII);
            
        }catch ( RemoteException ex) {
            logger.catching(ex);
        }catch(RMIRegistryException ex)
        {
            logger.catching(ex);
        }
        
    }
   
    

    public void run()
    {
        logger.info("AnalyticServerHandle started...");  
        String line=null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                while((line=in.readLine())!=null)
                {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (IOException e) {
                
            }
        } 
        
        this.close();      
        logger.info("AnalyticServerHandle close...");  
    }
    
    
    
    public void close()
    {
        //send AuctionManagementSystem the kill message
        Task.CLOSE close=new Task.CLOSE();
        toamsincomechannel.offer(new Task(close));
        //this.AMS.close();
        if(!pool.isShutdown())
        pool.shutdown(); // Disable new tasks from being submitted
        try {
         // Wait a while for existing tasks to terminate
             if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
               pool.shutdownNow(); // Cancel currently executing tasks
               // Wait a while for tasks to respond to being cancelled

             }
         } catch (InterruptedException ie) {
         // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
         // Preserve interrupt status
            Thread.currentThread().interrupt();
         } 
        try {
            this.registry.deregisterObject(AnalyticsServerInterface.class.getSimpleName());
        } catch (RMIRegistryException ex) {
           this.logger.error("Unbind AnalyticServerInterface from Registry not successfull.");
        }
        logger.info("AnalyticServer has closed all ressources.");

    }

}