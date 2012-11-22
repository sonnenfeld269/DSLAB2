package BillingServer;

import java.util.concurrent.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class BillingServerSecure {
    private Logger logger = null;
    private BillingServerSecureInterfaceImpl bssi = null;
    private Executor pool = null;
    
    public BillingServerSecure() {
           try {
            logger = LogManager.getLogger(BillingsServer.class);
           logger.debug("SillingServer Secure started!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
         

}
