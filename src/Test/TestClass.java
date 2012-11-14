/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import AnalyticsServer.AnalyticsServerInterface;
import Event.Event;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestClass {

    private static Logger logger = LogManager.getLogger(TestClass.class.getName());
    
    private static final String HOST = "localhost";
    private static final int PORT = 1099;
    private static Registry registry;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        logger.debug("Test Class main started.");
        registry = LocateRegistry.getRegistry(HOST, PORT);
        AnalyticsServerInterface asi = (AnalyticsServerInterface) registry.lookup(AnalyticsServerInterface.class.getSimpleName());
        
        Event e = new Event("TEST");
        asi.processEvents(e);
        logger.fatal("process event was called");
    }
}
