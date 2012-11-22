/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BillingServer;

import RMI.BillingServerSecureInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class BillingServerSecureInterfaceImpl extends UnicastRemoteObject implements BillingServerSecureInterface{

    Logger logger = LogManager.getLogger(BillingServerSecureInterfaceImpl.class);

    public BillingServerSecureInterfaceImpl() throws RemoteException {
        super();
        logger.info("BillingServerSecureInterface started!");
    }

    @Override
    public String getPriceSteps() {
        logger.debug("EXECUTED BILLING SERVER SECURE PRICE STEPS");
        return "YES";
    }
}
