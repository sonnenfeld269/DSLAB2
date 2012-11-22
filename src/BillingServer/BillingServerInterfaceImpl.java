/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BillingServer;

import RMI.BillingServerInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class BillingServerInterfaceImpl  extends UnicastRemoteObject implements BillingServerInterface {
    
    Logger logger = LogManager.getLogger(BillingServerInterfaceImpl.class);
    public BillingServerInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public BillingServerSecure login(String username, String password) throws RemoteException{
        logger.info("BillingServerInterface Login Method started!");
        return new BillingServerSecure();
    }

}
