package BillingServer;

import RMI.BillingServerInterface;
import RMI.BillingServerSecureCallbackInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.EasyProperties;

/**
 *
 * @author Dave
 */
public class BillingServerInterfaceImpl extends UnicastRemoteObject implements BillingServerInterface {

    static Logger logger = LogManager.getLogger(BillingServerInterfaceImpl.class);

    public BillingServerInterfaceImpl() throws RemoteException {
        super();
    }

    @Override
    public BillingServerSecure login(String username, String password) throws RemoteException {
        logger.debug("BillingServerInterface Login Method started!");
        BillingServerSecure bss = null;

        if (EasyProperties.login("./src/user.properties", username, password)) {
            bss = new BillingServerSecureImpl();
            return bss;
        } else {
            return null;
        }
    }
}
