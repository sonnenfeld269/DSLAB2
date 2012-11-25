package BillingServer;

import RMI.BillingServerInterface;
import RMI.BillingServerSecureCallbackInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class BillingServerInterfaceImpl  extends UnicastRemoteObject implements BillingServerInterface {
    
    static final Logger logger = LogManager.getLogger(BillingServerInterfaceImpl.class);
    
    public BillingServerInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public BillingServerSecure login(String username, String password) throws RemoteException{
        logger.debug("BillingServerInterface Login Method started!");
        BillingServerSecure bss = null;
        try {
        bss =  new BillingServerSecureImpl();
        logger.debug(this.getClass().getName() + " BillingServerSecureImpl can be returned!");
        return bss;
        } catch (Exception e) {
            logger.error("BillingServerSecure could not be returned!");
            return null;
        }
    }

}
