/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BillingServer;

import RMI.BillingServerSecure;
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
    PriceSteps ips = new PriceSteps();

    public BillingServerSecureImpl() throws RemoteException {
        super();
        logger.info("BillingServerSecureImpl started!");
    }

    @Override
    public PriceSteps getPriceSteps() {
        logger.debug("Inside getPriceSteps");
        return ips;
    }

    @Override
    public void createPriceStep(double min_value, double max_value, double fee_fixed, double fee_variable) {
        logger.debug("Inside createPriceStep");
        ips.createPriceStep(min_value, max_value, fee_fixed, fee_variable);
        logger.info("PriceStep added successfully");
    }

    @Override
    public void deletePriceStep(double min_value, double max_value) {
        logger.debug("Inside deletePriceStep");
        ips.deletePriceStep(min_value, max_value);
    }
    
   
}
