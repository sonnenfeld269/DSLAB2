/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BillingServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class BillingServerSecureImpl extends UnicastRemoteObject implements BillingServerSecure {

    static final Logger logger = LogManager.getLogger(BillingServerSecureImpl.class);

    public BillingServerSecureImpl() throws RemoteException {
        super();
        logger.info("BillingServerSecureImpl started!");
    }

    @Override
    public String getPriceSteps() {
        logger.debug("Inside getPriceSteps");
        InitPriceStep ips = new InitPriceStep();
        return ips.toString();
    }
}
