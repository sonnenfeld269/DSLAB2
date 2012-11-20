/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import RMI.AnalyticsServerInterface;
import RMI.RMIRegistry;
import RMI.RMIRegistryException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public AnalyticsServer (String propertyFile)
    {
        try {
            registry=new RMIRegistry(propertyFile);
            if(registry.getRegistry()==null)
                registry.startRegistry();
        } catch (RMIRegistryException ex) {
            Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
    }
   
    

    public void run()
    {


    }


}
