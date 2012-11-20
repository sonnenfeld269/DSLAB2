/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

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
    private static Registry registry=null;
    private Logger logger=null;
    
    public AnalyticsServer ()
    {
        
    }
    
    
    
    public static boolean startRegistry(String propertyFile) throws RemoteException {
        
        boolean b=true;
        try{
            FileReader reader = new FileReader(propertyFile);
            Properties prop = new Properties();
            prop.load( reader );
            
            int port = Integer.parseInt(prop.getProperty("registry.port", "1099"));

            registry = java.rmi.registry.LocateRegistry.createRegistry(port);
            
        }catch(RemoteException ex)
        {
            b=false;
            
        }catch(FileNotFoundException ex)
        {
            //default value will be used
        
        }catch(IOException ex)
        {
             b=false;
             
        }finally
        {
          if(registry!=null)
          {
              registry=null;
          }
            
        }
        
        return b;
    }

    
    
    public static void registerObject(String name, Remote remoteObj) throws RemoteException, AlreadyBoundException {
        //  Registry registry = LocateRegistry.getRegistry();
        registry.bind(name, remoteObj);
        System.out.println("Registered: " + name + " -> " + remoteObj.getClass().getName() + "[" + remoteObj + "]");
    }

    public void run()  {
        try {
            //startRegistry();
            registerObject(AnalyticsServerInterface.class.getSimpleName(), new AnalyticsServerInterfaceImpl());
        
        } catch (RemoteException ex) {
            Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex);
        }catch (AlreadyBoundException ex) {
            Logger.getLogger(AnalyticsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
