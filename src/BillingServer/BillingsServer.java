package BillingServer;

import RMI.BillingServerInterface;
import RMI.RMIRegistry;
import RMI.RMIRegistryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class BillingsServer implements Runnable {

    private Logger logger = null;
    private RMIRegistry registry = null;
    private BillingServerInterface bsi = null;
    private Executor pool = null;
    static PriceSteps ips = new PriceSteps();
    static Bill bill = new Bill();

    public BillingsServer(String propertyFile) {
        try {
            logger = LogManager.getLogger(BillingsServer.class);
            registry = new RMIRegistry(propertyFile);
            if (registry.getRegistry() == null) {
                registry.startRegistry();
                logger.info("Registry successfully started by BillingsServer");
            }
            registry.registerObject(BillingServerInterface.class.getSimpleName(), (new BillingServerInterfaceImpl()));
            logger.info("BillingServerInterface was successfully registered to the Registry");
        } catch (RemoteException ex) { // java 7 wird bei der abgabe nicht unterstützt
            logger.catching(ex);
        } catch (RMIRegistryException ex) {
            logger.catching(ex);
        }
    }

    public void run() {
        logger.info("BillingServer started...");
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (!Thread.currentThread().isInterrupted()) {
            try {
                while ((line = in.readLine()) != null) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
