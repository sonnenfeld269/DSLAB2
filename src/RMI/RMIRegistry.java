/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RMI;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import utils.EasyProperties;

/**
 *
 * @author sanker
 */
public class RMIRegistry {
    
    
     private Logger logger = null;
     private Registry registry=null;
     private String properties=null;
     private String registryHost=null;
     private int registryPort;
    
    public RMIRegistry(String propertyFile)
    {
        logger = LogManager.getLogger(RMIRegistry.class.getSimpleName());
       
        this.properties = propertyFile;
        this.registryHost = EasyProperties.getProperty(propertyFile,"registry.host");
        this.registryPort = Integer.parseInt(EasyProperties.getProperty(propertyFile,"registry.port"));
        try {
            this.registry = LocateRegistry.getRegistry(this.registryHost,this.registryPort);
        } catch (RemoteException ex) {
            this.registry=null;
            java.util.logging.Logger.getLogger(RMIRegistry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }
    
    public Registry startRegistry() throws RMIRegistryException {
        logger.entry();
        
        
        try{
           
            int port = Integer.parseInt(EasyProperties.getProperty(this.properties,"registry.port"));
            registry = java.rmi.registry.LocateRegistry.createRegistry(port);
            
        }catch(RemoteException ex)
        {
             logger.catching(ex);
             throw new RMIRegistryException("RemoteException:",ex);
         
        }catch(IOException ex)
        {
            logger.catching(ex);
            throw new RMIRegistryException("IOException:",ex);
        }catch(Exception ex)
        {
            logger.catching(ex);
             throw new RMIRegistryException("Exception:",ex);
        }
        logger.exit();
        return registry;
    }
    
    public Registry getRegistry()
    {
        return this.registry;
    }

    
    public  void registerObject(String name, Remote remoteObj) throws RMIRegistryException  {
        
        try{   
            if(registry==null)
                throw new RMIRegistryException("No registry located.");
            registry.rebind(name, remoteObj);
            //System.out.println("Registered: " + name + " -> " + remoteObj.getClass().getName() + "[" + remoteObj + "]");
            String msg = "RMIRegistry:RegisterObject: " + name + " -> " + remoteObj.getClass().getName() + "[" + remoteObj + "]";
            logger.info(msg);
        }catch(RemoteException ex)
        {
            throw new RMIRegistryException("RemoteException:",ex);
        }
        
    }
    
    public AnalyticsServerInterface getAnalyticsInterface() throws RMIRegistryException 
    {
        if(registry==null)
                throw new RMIRegistryException("No registry located.");
        
        AnalyticsServerInterface analytic=null;
        try {

            analytic=(AnalyticsServerInterface) registry.lookup(AnalyticsServerInterface.class.getSimpleName());
       
        } catch (RemoteException ex) {
            java.util.logging.Logger.getLogger(RMIRegistry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            java.util.logging.Logger.getLogger(RMIRegistry.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } 
        
        return analytic;
    }
    
}
