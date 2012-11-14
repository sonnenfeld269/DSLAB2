/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * This is just a temporary test Server class. For lookup of processEvents. Just
 * for RMI Demonstration. This can be for example the AuctionServer or
 * BillingServer.
 *
 * @author Dave
 */
public class AnalyticsServer {

    private static final int PORT = 1099;
    private static Registry registry;

    public static void startRegistry() throws RemoteException {
        // create in server registry
        registry = java.rmi.registry.LocateRegistry.createRegistry(PORT);
    }

    public static void registerObject(String name, Remote remoteObj) throws RemoteException, AlreadyBoundException {
        //  Registry registry = LocateRegistry.getRegistry();
        registry.bind(name, remoteObj);
        System.out.println("Registered: " + name + " -> " + remoteObj.getClass().getName() + "[" + remoteObj + "]");
    }

    public static void main(String[] args) throws Exception {
        startRegistry();
        registerObject(AnalyticsServerInterface.class.getSimpleName(), new AnalyticsServerInterfaceImpl());
        //    Thread.sleep(5 * 60 * 1000);
    }
}
