/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionmanagement;

import communication.OperationException;
import java.io.IOException;
import java.io.PrintWriter;
import communication.Client;
import communication.ClientException;
import communication.Operation;
import communication.ServerUDP;
import communication.ServerUDPException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import MyLogger.Log;
import auctionmanagement.CheckRequest.checkAuctionAnswer;
import java.util.concurrent.TimeUnit;



/**
 *
 * @author sanker
 */
public class AuctionTest implements Runnable {
   
    private final ExecutorService pool;
    private ServerUDP serverUDP=null;
    private int udpPort;
    private AuctionClientUDPHandler handleUDP=null;
    private AuctionClientTCPHandler handleTCP=null;
    private Client clientTCP=null;
    private Log errorlog=null;
    
    private ClientStatus userstatus=null;
    
    
    
    public AuctionTest(String host,int tcpPort,String bindingName,int auctionsPerMin,int auctionDuration,int updateIntervalSec,int bidsPerMin) throws AuctionClientException
    {
        //this.errorlog=output;
        userstatus=new ClientStatus("none");
        try {
            
            this.clientTCP=new Client(host,tcpPort);
          
            this.handleTCP=new AuctionClientTCPHandler(this.clientTCP,null);
            //this.serverUDP=new ServerUDP(udpPort,handleUDP,output);
           // this.serverUDP.setErrorLog(output);
            pool = Executors.newCachedThreadPool();
           // pool.execute(serverUDP);
            pool.execute(handleTCP);
            
        } catch (ClientException e) {
           throw (new AuctionClientException(":ClientException:",e));
      }  /* catch (ServerUDPException e) {
           throw (new AuctionClientException(":ServerUDPException:",e));
        } */
    }
    
    public void run()
    {
        
        
   
        
    }
    
    public void close()
    {
        this.shutdownPool();
        try {
            this.clientTCP.closeSocket();
        } catch (ClientException e) {
            this.errorlog.output("ActionClientThread:close():"+e.getMessage());
        }
        
    }
    
    private  void shutdownPool()
    {
       if(!pool.isShutdown())
        pool.shutdown(); // Disable new tasks from being submitted
    try {
     // Wait a while for existing tasks to terminate
     if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
       pool.shutdownNow(); // Cancel currently executing tasks
       // Wait a while for tasks to respond to being cancelled      
     }
   } catch (InterruptedException ie) {
     // (Re-)Cancel if current thread also interrupted
     pool.shutdownNow();
     // Preserve interrupt status
     Thread.currentThread().interrupt();
   } 
        
    }
    
    private class AuctionClientUDPHandler extends ServerUDP.Handler {
        
        private Log out=null;
       
        
        public AuctionClientUDPHandler(Log out)
        {
                this.out=out;
                this.out.output("Constructor:Create AuctionClientUDPHandler", 2);
                
        }
        
        public String checkIncomingMessage(String msg)
        {
            String newMessage=null;
            newMessage=CheckRequest.checkAuctionAnswer.checkandget(msg,
                    userstatus.getUser());
            if(newMessage==null)
                return msg;
            else 
                return newMessage;
           // userstatus
        }
        
        public void handle(String msg)
        {
            out.output("AuctionClientUDPHandlerThread running..", 2);
            String checkedmessage=this.checkIncomingMessage(msg);
            out.output(checkedmessage);
            out.output("AuctionClientUDPHandlerThread finished..", 2);
        }
    }
    
    private class AuctionClientTCPHandler implements Runnable {
        
        private Log out=null;
        private Client client=null;
        
        public AuctionClientTCPHandler(Client client,Log out)
        {
                this.out=out;
                this.client=client;
                out.output("Constructor:Create AuctionClientTCPHandler", 2);
        }
        
        

        public void run()
        {
            String msg=null;
            
            Operation op=null;
            
            while(!Thread.currentThread().isInterrupted())
            {
                out.output("AuctionClientTCPHandlerThread is running..", 2);
                try {
                    
                    op  = new Operation(this.client);
                    msg = op.readString();
                    out.output(msg);
                    
                } catch (OperationException ex) {
                   out.output("AuctionClientTCPHandlerThread: OperationException");
                   Thread.currentThread().interrupt();
                  
                }
            }
            out.output("AuctionClientTCPHandlerThread finished..", 2);
            
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
    
    
     
}
