package BillingServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Dave
 */
public interface BillingServerSecure extends Remote{

    public String getPriceSteps() throws RemoteException;
}
