package ManagementClients;

import RMI.AnalyticsServerInterface;
import RMI.BillingServerInterface;
import RMI.BillingServerSecure;
import RMI.RMIRegistry;
import RMI.RMIRegistryException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class ManagementClient implements Runnable {
    
    private static long counter = 0;
    private Logger logger = null;
    private RMIRegistry registry = null;
    private ManagementClientCallBackInterfaceImpl mccbi = null;
    private AnalyticsServerInterface analytic = null;
    private BillingServerInterface billing = null;
    private BillingServerSecure bss = null;
    private boolean init = true;
    
    public ManagementClient(String propertyFile) {
        try {
            logger = LogManager.getLogger(ManagementClient.class.getSimpleName());
            /**
             * Get or start Registry(Naming Server)
             *
             */
            registry = new RMIRegistry(propertyFile);

            /**
             * ***AnalyticServer Part*******
             */
            analytic = registry.getAnalyticsInterface();
            mccbi = new ManagementClientCallBackInterfaceImpl();
            mccbi.initializeManagementClient(this.getID(), logger);
            /**
             * ***AnalyticServer Part*******
             */
            /**
             * ***BillingServer initialization Part*******
             */
            billing = registry.getBillingInterface();
            /**
             * ***BillingServer Part*******
             */
        } catch (RMIRegistryException ex) {
            this.logger.error("ManagementClient:Constructor:RMIRegistryException" + ex.getMessage());
            init = false;
        } catch (Exception ex) {
            this.logger.error("ManagementClient:Constructor:Exception:" + ex.getMessage());
            init = false;
        }
        
    }
    
    public void run() {
        try {
            logger.info("ManagementClientHandle started...");
            String line = null;
            // System.out.print("\n>");

            
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (!Thread.currentThread().isInterrupted() && init) {
                /**
                 * ***Analytics und Billing Eingaben****
                 */
               
                System.out.print("\n>");
                
                try {
                    while ((line = in.readLine()) != null) {
                        System.out.print("\n>");
                        if (line.contains("!subscribe")) {
                            String[] s = line.split(" ");
                            if (s.length >= 2) {
                                long sub_id = 0;
                                int position1 = line.indexOf('\'', 0);
                                int position2 = line.indexOf('\'', position1 + 1);
                                String regex = line.substring(position1 + 1, position2);
                                try{
                                    sub_id = analytic.subscribe(mccbi, regex);
                                    System.out.print("Created subscription with ID " + sub_id
                                            + " for events using filter"
                                            + " '"
                                            + regex
                                            + "' ");
                                }catch(RemoteException ex)
                                {
                                    System.out.print("Error:managmentClient:"
                                            +"run:subscribe:RemoteException:"
                                            +ex.getMessage());
                                }
                            }
                            
                        } else if (line.contains("!unsubscribe")) {
                            
                            try {
                                String[] s = line.split(" ");
                                long id = Long.getLong(s[1]);
                                try{
                                    boolean b;
                                    b=analytic.unsubscribe(id);
                                    if(b)
                                    {
                                    System.out.print("subscription"
                                            + id
                                            + "terminated ");
                                    }else
                                    {
                                        System.out.print("Error:unsubscribe id " 
                                            + id
                                            + "was not successfull.");
                                    }
                                }catch(RemoteException ex)
                                {
                                    System.out.print("Error:managmentClient:"
                                            +"run:unsubscribe:RemoteException:"
                                            +ex.getMessage());
                                }
                                
                                
                            } catch (Exception e) {
                                System.out.print("ERROR:" + e.getMessage());
                            }
                            
                            
                            
                        } else if (line.contains("!print")) {
                            if (!mccbi.getMode()) {
                                mccbi.printBuffer();
                            }
                        } else if (line.contains("!hide")) {
                            mccbi.setEventPrintMode(false);
                            
                        } else if (line.contains("!auto")) {
                            mccbi.setEventPrintMode(true);
                            
                        } else if (line.contains("!end")) {
                            //provisorisch
                            Thread.currentThread().interrupt();
                            break;
                            
                        } else if (line.contains("bill")) {
                        } // .
                        // .
                        // .
                        /**
                         * ***Analytics und Billing Eingaben****
                         */
                        else if (line.contains("!login")) {
                            logger.info("INSIDE MANAGEMENT CLIENT - LOGIN METHOD");
                            String[] split = line.split(" ");
                            String login = split[1];
                            String pass = split[2];
                            if (billing.login(login, pass) != null) {
                                bss = billing.login(login, pass);
                                logger.info("USER " + login + " was sucessfully logged in!");
                            } else {
                                logger.info("USER " + login + " not logged in! Wrong username or password.");
                            }
                        } else if (line.contains("!steps")) {
                            System.out.println(bss.getPriceSteps().toString());
                        } else if (line.contains("!addStep")) {
                            String[] split = line.split(" ");
                            double min_price = Double.parseDouble(split[1]);
                            double max_price = Double.parseDouble(split[2]);
                            double fee_fix = Double.parseDouble(split[3]);
                            double fee_var = Double.parseDouble(split[4]);
                            bss.createPriceStep(min_price, max_price, fee_fix, fee_var);
                        } else if (line.contains("!removeStep")) {
                            String[] split = line.split(" ");
                            double min_price = Double.parseDouble(split[1]);
                            double max_price = Double.parseDouble(split[2]);
                            bss.deletePriceStep(min_price, max_price);
                        } else {
                            logger.info("Wrong command.");
                        }
                    }
                } catch (Exception e) {
                    logger.info("Inside exception");
                    logger.catching(e);
                    e.printStackTrace();
                }
            }
            
        } catch (Exception ex) {
            logger.catching(ex);
            System.out.print("ERROR:" + ex.getMessage());
        }
    }
    
    private synchronized long getID() {
        return counter++;
    }
}
