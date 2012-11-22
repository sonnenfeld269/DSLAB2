/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ManagementClients;

import RMI.AnalyticsServerInterface;
import RMI.ManagementClientCallBackInterface;
import RMI.RMIRegistry;
import RMI.RMIRegistryException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Dave
 */
public class ManagementClient implements Runnable{
    
    
    private Logger logger=null;
    private RMIRegistry registry=null;
    private ManagementClientCallBackInterfaceImpl mccbi=null;
     private AnalyticsServerInterface analytic=null;
    //private Executor pool=null;
    private boolean init=true;
    
    
    
    
     public ManagementClient (String propertyFile)
    {
        try {
            logger = LogManager.getLogger(RMIRegistry.class.getSimpleName());
            /**
             * Get or start Registry(Naming Server)
             **/
            registry=new RMIRegistry(propertyFile);
            
            /*****AnalyticServer Part********/
            analytic=registry.getAnalyticsInterface();
            mccbi = new ManagementClientCallBackInterfaceImpl();
            mccbi.initializeManagementClient(logger);
            /*****AnalyticServer Part********/
            
            /*****BillingServer initialization Part********/
            
            
            
            
            
            
            /*****BillingServer Part********/        
            
            
            
            
            
            //test
            analytic.subscribe(mccbi, "(USER_.*)");  
            
        }catch (RMIRegistryException ex) {
            this.logger.error("ManagementClient:Constructor:RMIRegistryException"+ex.getMessage());
            init=false;
        }catch (Exception ex) {
           this.logger.error("ManagementClient:Constructor:Exception:"+ex.getMessage());
           init=false;
        }
        
    }
    
    
    
    public void run()
    {
       try{
        logger.info("ManagementClientHandle started...");  
        String line=null;
        System.out.print("\n>");
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(!Thread.currentThread().isInterrupted()&&init)
        {
         /*****Analytics und Billing Eingaben*****/
            System.out.print("\n>");
            try {
                while((line=in.readLine())!=null)
                {
                    if(line.contains("!subscribe"))
                    {
                        String[] s= line.split(" ");
                        if(s.length>2)
                        {
                            
                        }
                        
                    }else if(line.contains("!unsubscribe"))
                    {
                        String[] s= line.split(" ");
                        
                    }else if(line.contains("!print"))
                    {
                        if(!mccbi.getMode())
                        {
                            mccbi.printBuffer();
                        }
                    }else if(line.contains("!hide"))
                    {
                        mccbi.setEventPrintMode(false);
                       
                    }else if(line.contains("!auto"))
                    {
                        mccbi.setEventPrintMode(true);
         
                    }else if(line.contains("!end"))
                    {
                        //provisorisch
                        Thread.currentThread().interrupt();
                        break;
                    }else if(line.contains("bill"))
                    {
                        
                    }
                    // .
                    // .
                    // .
                    
                    /*****Analytics und Billing Eingaben*****/
                }
            } catch (IOException e) {
                
            }
        }  
    
       }catch(Exception ex)
       {
    
       }
    }
}
