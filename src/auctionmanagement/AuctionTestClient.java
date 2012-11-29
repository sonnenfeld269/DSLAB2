/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionmanagement;

import MyLogger.Log;
import communication.Client;
import communication.ClientException;
import communication.Operation;
import communication.OperationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author sanker
 */

public class AuctionTestClient { 
    
    private int id;
    private final ExecutorService pool;
    private AuctionTCPHandlerForAuctiontest handleTCP=null;
    private Client clientTCP=null;
    private Log errorlog=null;
    private LinkedBlockingQueue<String> messaging=null;
    /******/
   private String bindingName;
   private int auctionsPerMin;
   private int auctionDuration;
   private int updateIntervalSec;
   private int bidsPerMin ;
    
    private ClientStatus userstatus=null;
    
    
    
    public AuctionTestClient(int id,String host,
            int tcpPort,
            String bindingName,
            AuctionTest.Properties properties,
            ExecutorService pool,
            Log logger) throws AuctionTestClientException 
    {

        userstatus=new ClientStatus("none");
        try {
            //Initializations
            this.id=id;
            this.pool=pool;
            this.clientTCP=new Client(host,tcpPort);
            this.messaging=new LinkedBlockingQueue<String>();
            this.handleTCP=new AuctionTCPHandlerForAuctiontest(this.clientTCP,this.messaging,logger);
            pool.execute(handleTCP);
            
        } catch (ClientException e) {
           throw (new AuctionTestClientException("id:"+id+":ClientException:",e));
      }  
    }
    
    public void run()
    {
        
        
   
        
    }
    
    public void close()
    {
        
        try {
            this.clientTCP.closeSocket();
        } catch (ClientException e) {
            this.errorlog.output("ActionClientThread:close():"+e.getMessage());
        }
        
    }
    

    private class ClientStatus{
        private String user=null;
        private String reset=null;
        
        public ClientStatus(String name)
        {
            this.reset=name;
            this.user=name;
        }
        
        public String getUser()
        {
            return this.user;
        }
        
        public void setUser(String user)
        {
             this.user=user;
        }
        
        public void resetUser()
        {
            this.user=this.reset;
        }
        
        public boolean sameUser(String user1,String user2)
        {
            return user1.contains(user2);
        }
        
        public boolean noUser()
        {
            return this.user.contains(this.reset);
        }
        
    
    }
    
     
    
    
     public class AuctionTCPHandlerForAuctiontest implements Runnable {
        
        private Log out=null;
        private Client client=null;
        private LinkedBlockingQueue<String> messaging=null;
        
        public AuctionTCPHandlerForAuctiontest(Client client,LinkedBlockingQueue<String> messaging,Log out)
        {
                this.out=out;
                this.client=client;
                this.messaging=messaging;
                out.output("Constructor:Create AuctionClientTCPHandler", 3);
        }
        
        

        public void run()
        {
            String msg=null;
            
            Operation op=null;
            
            while(!Thread.currentThread().isInterrupted())
            {
                out.output("AuctionClientTCPHandlerThread is running..", 3);
                try {
                    
                    op  = new Operation(this.client);
                    msg = op.readString();
                    messaging.add(msg);
                    
                } catch (OperationException ex) {
                   out.output("AuctionClientTCPHandlerThread: OperationException",2);
                   Thread.currentThread().interrupt();
                  
                }
            }
            out.output("AuctionClientTCPHandlerThread finished..", 3);
            
        } 
    }

    
    

}
