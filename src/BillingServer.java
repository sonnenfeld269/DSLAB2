
import BillingServer.BillingsServer;

/**
 * This is just a temporary test Server class. For lookup of processEvents. Just
 * for RMI Demonstration. This can be for example the AuctionServer or
 * BillingServer.
 *
 * @author Dave
 */
public class BillingServer {

    public static void main(String[] args) {
        BillingsServer billingserver = new BillingsServer("./src/registry.properties");
        billingserver.run();
        System.exit(0);
    }
}
