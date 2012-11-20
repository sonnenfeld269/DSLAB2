/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;


import RMI.AnalyticsServerInterface;
import RMI.RMIRegistry;
import RMI.RMIRegistryException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
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
    private Executor pool=null;
    
    public AnalyticsServer (String propertyFile)
    {
        try {
            logger = LogManager.getLogger(RMIRegistry.class.getSimpleName());
            /**
             * Get or start Registry(Naming Server)
             **/
            registry=new RMIRegistry(propertyFile);
            if(registry.getRegistry()==null)
                registry.startRegistry();
            /**
             * Register all neccessary RemoteObjects to the registry
             **/
            //register AnalyticsServerInterfaceImpl
            registry.registerObject(AnalyticsServerInterface.class.getSimpleName(),
                    new AnalyticsServerInterfaceImpl());
        }catch (RemoteException ex) {
           // logger.e;
            
        }catch (RMIRegistryException ex) {
           // logger.e;
            
        }
        
    }
   
    

    public void run()
    {
            
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
