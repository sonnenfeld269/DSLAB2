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

    public BillingServerSecureImpl() throws RemoteException {
        super();
        logger.info("BillingServerSecureImpl started!");
    }

    @Override
    public PriceSteps getPriceSteps() {
        logger.debug("Inside getPriceSteps");
        return BillingsServer.ips;
    }

    @Override
    public void createPriceStep(double min_value, double max_value, double fee_fixed, double fee_variable) throws RemoteException {
        logger.debug("Inside createPriceStep");
        if (min_value < 0 || max_value < 0 || fee_fixed < 0 || fee_variable < 0) {
            throw new RemoteException("There is a negative number.");
        }
        if (!BillingsServer.ips.check(min_value, max_value)) {
            throw new RemoteException("There is an overlap.");
        }
        BillingsServer.ips.createPriceStep(min_value, max_value, fee_fixed, fee_variable);
        logger.info("PriceStep added successfully");

    }

    @Override
    public void deletePriceStep(double min_value, double max_value) throws RemoteException {
        logger.debug("Inside deletePriceStep");
        BillingsServer.ips.deletePriceStep(min_value, max_value);
    }

    @Override
    public void billAuction(String user, long auctionID, double price) throws RemoteException {
        logger.debug("Inside billAuction");

        if (BillingsServer.bill.getUserByName(user) != null) {
            logger.debug("The user list already contains the user " + user);
            User u = BillingsServer.bill.getUserByName(user);
            Auction a = new Auction(auctionID, price);
            u.getAuctions().add(a);
        } else {
            logger.debug("The user " + user + " is not in the list and will be created.");
            User u = new User(user);
            Auction a = new Auction(auctionID, price);
            u.getAuctions().add(a);
            BillingsServer.bill.getUsers().add(u);
        }

        logger.info("An ended Auction with id " + auctionID + "  was saved to the User " + BillingsServer.bill.getUserByName(user).getName());
    }

    @Override
    public Bill getBill(String user) throws RemoteException {
        logger.debug("Inside getBill(" + user + ")");
        BillingsServer.bill.createBill(user);
        return BillingsServer.bill;
    }
}
