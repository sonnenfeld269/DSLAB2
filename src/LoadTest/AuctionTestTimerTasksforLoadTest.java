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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author sanker
 */
public  class AuctionTestTimerTasksforLoadTest {
    
    
    public static class PerMinuteTask extends TimerTask {

        private Log logger=null;
        private Client clientTCP=null;
        private LinkedBlockingQueue<String> messaging=null;
        private HashMap activeForeignAuctions=null;
        private AuctionTest.Properties properties=null;
        private ReentrantLock lockforactiveForeignAuctions=null;
        private AuctionTestClient.WaitingRoom waiting=null;
        private String clientname;
        private long auctioncounter=0;
        
        public PerMinuteTask(
                Client clientTCP,  //send messages ti auctionServer
                LinkedBlockingQueue<String> messaging,//get answers from server
                String clientname,
                AuctionTest.Properties properties,
                HashMap<Long, Auction> activeForeignAuctions,//active auction set up bids, 
                                              //updated by another thread
                ReentrantLock lockforactiveForeignAuctions,//for mutual exclusion
                              //of the active Auction List 
                AuctionTestClient.WaitingRoom waiting,
                Log logger // error and debuging notifications                
                ) 
        {
            this.logger=logger;
            this.activeForeignAuctions=activeForeignAuctions;
            this.clientTCP=clientTCP;
            this.lockforactiveForeignAuctions=lockforactiveForeignAuctions;
            this.messaging=messaging;
            this.clientname=clientname;
            this.properties=properties;
            this.waiting=waiting;

        }

        
        public void run() {
            
            
            logger.output(this.clientname+":PerMinuteTask started.",2);
             try{
                 //get Lock
                this.lockforactiveForeignAuctions.lock();
                Operation op = new Operation(this.clientTCP);
                //Timeunit in milliseconds
                long waitTime = 600L;//wait 200ms for messages and then continue
              
                //CREATE #'auctionsPerMin' auctions every minute
                for(int i=0;i<this.properties.auctionsPerMin;i++)
                {
                     op.writeString("!create"+" "+this.clientname+" "
                             +this.properties.auctionDuration+" "
                             +("Auction "+auctioncounter+" of "+this.clientname));
                     //String s=this.messaging.poll(waitTime, TimeUnit.MILLISECONDS);
                     String s=this.messaging.take();
                     if(s==null)
                         throw new Exception("No response from AuctionServer.");
                     if(!ParseClientInput.parseCreate(s))
                         throw new Exception("Client cannot crate a Auction.");
                     
                     logger.output(this.clientname+":PerMinuteTask:Created the"
                             +auctioncounter+" Auction.",2);
                     auctioncounter++;
                     
                }
                
                //BID on #'bidsPerMin' Auction every minute
                int activeAuctionSize=this.activeForeignAuctions.size();
                if(activeAuctionSize>0)
                {
                    Random random=new Random();
                   
                    
                    for(int i=0;i<this.properties.bidsPerMin;i++)
                    {
                       int select = random.nextInt(activeAuctionSize);
                       //the key of activeForeignAuctions map is the correct AuctionID
                       //and the same AuctionId of corresponding Auction at the AuctionServer
                       //the "intern" id auf class Auction is "NOT" the same Id of the AuctionID
                       //at the AuctionServer
                       
                       Auction a =(Auction)(this.activeForeignAuctions.get((long)select));
                       long timenow=(new Date()).getTime();
                       long auctionstarttime=a.getstartDateObject().getTime();
                       double newBid=(double)(timenow-auctionstarttime);
                       
                       op.writeString("!bid"    +" "
                             +this.clientname   +" "
                             +select            +" "
                             +newBid);
                        String s=this.messaging.poll(waitTime, TimeUnit.MILLISECONDS);
                        if(s==null)
                            throw new Exception("No response from AuctionServer.");
                        if(!ParseClientInput.parseBid(s)){
                            logger.output(this.clientname+":PerMinuteTask"
                                    +":Bid of "+newBid
                                    +"on Auction ID "+select
                                    +"was not successfully.");
                        }
                        
                        logger.output(this.clientname+":PerMinuteTask:"
                                +"Set Bid on Auction ID "
                                +select
                                +" with "
                                +newBid+".",2);

                    }
                }
                
                
            
               
            }catch(OperationException ex)   
            {
               logger.output(this.clientname+":PerMinuteTask:OperationException:"+ex.getMessage()); 
               this.waiting.callingfromWaitingRoom();
               
            }catch(InterruptedException ex)   
            {
               logger.output(this.clientname+":PerMinuteTask:OperationException:"+ex.getMessage()); 
               this.waiting.callingfromWaitingRoom();
               
            }catch(Exception ex)   
            {
               logger.output(this.clientname+":PerMinuteTask:OperationException:"+ex.getMessage()); 
               this.waiting.callingfromWaitingRoom();
               
            }finally
            {
                this.lockforactiveForeignAuctions.unlock();
            }
           
            
           
           logger.output(this.clientname+":PerMinuteTask finished.",2);
            
        }
        
    }
    
    
    
     public static class UpdateListTask extends TimerTask {

        private Log logger=null;
        private Client clientTCP=null;
        private LinkedBlockingQueue<String> messaging=null;
        private HashMap activeForeignAuctions=null;
        private ReentrantLock lockforactiveForeignAuctions=null;
        private AuctionTestClient.WaitingRoom waiting=null;
        private String clientname;
        
        public UpdateListTask(
                Client clientTCP,  //send messages ti auctionServer
                LinkedBlockingQueue<String> messaging,//get answers from server
                String clientname,
                HashMap<Long, Auction> activeForeignAuctions,//active auction set up bids, 
                                              //updated by another thread
                ReentrantLock lockforactiveForeignAuctions,//for mutual exclusion
                              //of the active Auction List 
                AuctionTestClient.WaitingRoom waiting,
                Log logger // error and debuging notifications
                ) 
        {
            this.logger=logger;
            this.activeForeignAuctions=activeForeignAuctions;
            this.clientTCP=clientTCP;
            this.lockforactiveForeignAuctions=lockforactiveForeignAuctions;
            this.messaging=messaging;
            this.clientname=clientname;
            this.waiting=waiting;
            

        }

        public void run() {
           logger.output(this.clientname+":UpdateListTask started.",2);
             try{
                 //get Lock
                this.lockforactiveForeignAuctions.lock();
                Operation op = new Operation(this.clientTCP);
                op.writeString("!list");
                 //Timeunit in milliseconds
                long waitTime = 500L;//wait 200ms for messages and then continue
                
                
                //String s=this.messaging.poll(waitTime, TimeUnit.MILLISECONDS);
                String s=this.messaging.take();
                if(s==null)
                      throw new Exception("No response from AuctionServer.");
                HashMap<Long, Auction>  newmap =ParseClientInput.parseList(s, clientname) ;
                if(newmap!=null)
                {
                    this.activeForeignAuctions.clear();
                    this.activeForeignAuctions.putAll(newmap);
                }else{
                    logger.output(this.clientname+":UpdateListTask:No Auctions avaible.",2);
                }
                
               
            }catch(OperationException ex)   
            {
               logger.output(this.clientname+":UpdateListTask:OperationException:"+ex.getMessage()); 
               this.waiting.callingfromWaitingRoom();
               
            }catch(InterruptedException ex)   
            {
               logger.output(this.clientname+":UpdateListTask:OperationException:"+ex.getMessage()); 
               this.waiting.callingfromWaitingRoom();
               
            }catch(Exception ex)   
            {
               logger.output(this.clientname+":UpdateListTask:Exception:"+ex.getMessage()); 
               this.waiting.callingfromWaitingRoom();
               
            }finally
            {
                this.lockforactiveForeignAuctions.unlock();
            }
           
            
           
           logger.output(this.clientname+":UpdateListTask finished.",2);
        }
        
    }
     
     
}