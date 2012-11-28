
import auctionmanagement.AuctionClientException;
import auctionmanagement.AuctionTest;
import utils.EasyProperties;

/**
 *
 * @author Dave
 */
public class LoadTest {

    static int clients = Integer.parseInt(EasyProperties.getProperty("./src/loadtest.properties", "clients"));
    static int auctionsPerMin = Integer.parseInt(EasyProperties.getProperty("./src/loadtest.properties", "auctionsPerMin"));
    static int auctionDuration = Integer.parseInt(EasyProperties.getProperty("./src/loadtest.properties", "auctionDuration"));
    static int updateIntervalSec = Integer.parseInt(EasyProperties.getProperty("./src/loadtest.properties", "updateIntervalSec"));
    static int bidsPerMin = Integer.parseInt(EasyProperties.getProperty("./src/loadtest.properties", "bidsPerMin"));

    public static String getString() {
        return "LoadTest{" + "clients=" + clients + ", auctionsPerMin=" + auctionsPerMin + ", auctionDuration=" + auctionDuration + ", updateIntervalSec=" + updateIntervalSec + ", bidsPerMin=" + bidsPerMin + '}';
    }

    public static void main(String[] args) throws AuctionClientException {
        System.out.println(getString());

        /*
         * 1. Clients instanzieren
         */

        
        for (int i = 0; i < clients; i++) {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            String bindingName = args[2];
            AuctionTest at = new AuctionTest(host,port,bindingName,auctionsPerMin,auctionDuration,updateIntervalSec,bidsPerMin);
            
            //ManagementTest at = new ManagementTest(args[0],args[1],args[2]);
            
            
        }
    }
}
