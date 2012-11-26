package RMI;

import BillingServer.PriceSteps;
import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 *
 * @author Dave
 */
public interface BillingServerSecure extends Remote{

    public PriceSteps getPriceSteps() throws RemoteException;
    public void createPriceStep(double min_value, double max_value, double fee_fixed, double fee_variable) throws RemoteException;
    public void deletePriceStep(double min_value, double max_value) throws RemoteException;
    
}
