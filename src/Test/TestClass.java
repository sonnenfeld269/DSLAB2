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

/**
 * This is just a temporary test Server class. For lookup of processEvents. Just
 * for RMI Demonstration. This can be for example the AuctionServer or
 * BillingServer.
 *
 * @author Dave
 */
public class TestClass {

    private static final String HOST = "localhost";
    private static final int PORT = 1099;
    private static Registry registry;

    public static void main(String[] args) throws RemoteException, NotBoundException {
        registry = LocateRegistry.getRegistry(HOST, PORT);
        AnalyticsServerInterface asi = (AnalyticsServerInterface) registry.lookup(AnalyticsServerInterface.class.getSimpleName());


        Event e = new Event("TEST");
        asi.processEvents(e);
        System.out.println("process event was called");
    }
}
