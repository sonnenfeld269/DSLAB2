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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is just a temporary test Server class. For lookup of processEvents. Just
 * for RMI Demonstration. This can be for example the AuctionServer or
 * BillingServer.
 *
 * @author Dave
 */
public class AnalyticsServer implements Runnable{

    //private static final int standardPORT = 1099;
    
    private Logger logger=null;
    private RMIRegistry registry=null;
    private AnalyticsServerInterface asi=null;
    private ExecutorService pool=null;
    private LinkedBlockingQueue<Task> rmitoamsincomechannel=null;
    private LinkedBlockingQueue<Task.RESULT> amstormisoutcomechannel=null;
    private LinkedBlockingQueue<Event> distributorincomechannel=null;
    private AnalyticsServerInterfaceImpl ASII=null;
    private AnalyticsManagementSystem AMS =null; 
    public AnalyticsServer (String propertyFile)
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
            rmitoamsincomechannel=new LinkedBlockingQueue<Task>();
            amstormisoutcomechannel=new LinkedBlockingQueue<Task.RESULT>();
            distributorincomechannel=new LinkedBlockingQueue<Event>();
            ASII=new AnalyticsServerInterfaceImpl();
            ASII.initialize(rmitoamsincomechannel,amstormisoutcomechannel,distributorincomechannel);
            pool= Executors.newCachedThreadPool();
            AMS = new AnalyticsManagementSystem(pool,rmitoamsincomechannel,amstormisoutcomechannel,distributorincomechannel);
            //start objects necessary for management
            pool.execute(AMS);
           
            
           
            /**
             * Register all neccessary RemoteObjects to the registry
             **/
            registry.registerObject(AnalyticsServerInterface.class.getSimpleName(),
                   ASII);
            
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
        

    }

}