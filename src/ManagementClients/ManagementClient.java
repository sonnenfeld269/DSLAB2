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
       // System.out.print("\n>");
        
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(!Thread.currentThread().isInterrupted()&&init)
        {
         /*****Analytics und Billing Eingaben*****/
            //System out analytic and billing
            //> or user>
            System.out.print("\n>");
            
            try {
                while((line=in.readLine())!=null)
                {
                    System.out.print("\n>");
                    if(line.contains("!subscribe"))
                    {
                        String[] s= line.split(" ");
                        if(s.length>=2)
                        {
                          long sub_id=0;
                          int position1 = line.indexOf('\'',0);
                          int position2 = line.indexOf('\'',position1+1);
                          String regex=line.substring(position1+1,position2);
                          sub_id= analytic.subscribe(mccbi, regex); 
                          System.out.print("Created subscription with ID "+sub_id
                                  +" for events using filter"
                                  +" '"
                                  +regex
                                  + "' ");                       }
                        
                    }else if(line.contains("!unsubscribe"))
                    {
                       
                        try{
                            String[] s= line.split(" ");
                            long id=Long.getLong(s[1]);
                            
                            boolean b=analytic.unsubscribe(id);
                            if(b)
                            {
                                System.out.print("subscription"
                                        + id
                                        +"terminated ");
                            }else{
                                System.out.print("ERROR:!unsubscribe not successfull.");
                            }
                        }catch(Exception e)
                        {
                           System.out.print("ERROR:"+e.getMessage()); 
                        }
                        
                            
                        
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
            } catch (Exception e) {
                System.out.print("ERROR:"+e.getMessage()); 
            }
        }  
    
       }catch(Exception ex)
       {
             System.out.print("ERROR:"+ex.getMessage()); 
       }
    }
}
