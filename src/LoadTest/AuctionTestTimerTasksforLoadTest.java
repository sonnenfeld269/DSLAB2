/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadTest;

import MyLogger.Log;
import auctionmanagement.Auction;
import communication.Client;
import communication.Operation;
import communication.OperationException;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author sanker
 */
public  class AuctionTestTimerTasksforLoadTest {
    
    
    public static class PerMinuteTask extends TimerTask {

        private Long auction_id;
        private Auction auc = null;
       
        public PerMinuteTask(
                Client clientTCP,  //send messages ti auctionServer
                LinkedBlockingQueue<String> messaging,//get answers from server
                String clientname,
                AuctionTest.Properties properties,
                HashMap<Long, Auction> activeForeignAuctions,//active auction set up bids, 
                                              //updated by another thread
                ReentrantLock lockforactiveForeignAuctions,//for mutual exclusion
                              //of the active Auction List 
                AuctionTestClient parentThread,
                Log errorlog // error and debuging notifications                
                ) 
        {
          

        }

        public void run() {
            
        }
        
    }
    
    
    
     public static class UpdateListTask extends TimerTask {

        private Log logger=null;
        private Client clientTCP=null;
        private LinkedBlockingQueue<String> messaging=null;
        private HashMap activeForeignAuctions=null;
        private ReentrantLock lockforactiveForeignAuctions=null;
       
        private String clientname;
        
        public UpdateListTask(
                Client clientTCP,  //send messages ti auctionServer
                LinkedBlockingQueue<String> messaging,//get answers from server
                String clientname,
                HashMap<Long, Auction> activeForeignAuctions,//active auction set up bids, 
                                              //updated by another thread
                ReentrantLock lockforactiveForeignAuctions,//for mutual exclusion
                              //of the active Auction List 
                AuctionTestClient parentThread,
                Log logger // error and debuging notifications
                ) 
        {
            this.logger=logger;
            this.activeForeignAuctions=activeForeignAuctions;
            this.clientTCP=clientTCP;
            this.lockforactiveForeignAuctions=lockforactiveForeignAuctions;
            this.messaging=messaging;
            this.clientname=clientname;

        }

        public void run() {
           logger.output(this.clientname+":UpdateListTask started.",3);
             try{
                 //get Lock
                this.lockforactiveForeignAuctions.lock();
                Operation op = new Operation(this.clientTCP);
                op.writeString("!list");
                String s=this.messaging.take();
                HashMap<Long, Auction>  newmap =ParseClientInput.parseList(s, clientname) ;
                if(newmap!=null)
                {
                    this.activeForeignAuctions.clear();
                    this.activeForeignAuctions.putAll(newmap);
                }else{
                    logger.output(this.clientname+":UpdateListTask:No Auctions avaible.");
                }
                
               
            }catch(OperationException ex)   
            {
               logger.output(this.clientname+":UpdateListTask:OperationException:"+ex.getMessage()); 
               this.notify();
               
            }catch(InterruptedException ex)   
            {
               logger.output(this.clientname+":UpdateListTask:OperationException:"+ex.getMessage()); 
               this.notify();
               
            }finally
            {
                this.lockforactiveForeignAuctions.unlock();
            }
           
            
           
           logger.output(this.clientname+":UpdateListTask finished.",3);
        }
        
    }
     
     
}
