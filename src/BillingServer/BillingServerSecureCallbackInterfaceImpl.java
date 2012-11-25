package BillingServer;

import RMI.BillingServerSecureCallbackInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class BillingServerSecureCallbackInterfaceImpl extends UnicastRemoteObject implements BillingServerSecureCallbackInterface {

    boolean init = false;
    // boolean logged=false;
    private Logger logger = null;

    public BillingServerSecureCallbackInterfaceImpl() throws RemoteException {
    }

    @Override
    public BillingServerSecure login(String username, String password) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void initalizeBillingServerSecure(Logger logger) {
        if (!init) {
            this.logger = logger;
            init = true;
        }
    }
}
