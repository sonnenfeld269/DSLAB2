/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadTest;

import MyLogger.Log;
import auctionmanagement.Auction;
import communication.Client;
import communication.ClientException;
import communication.Operation;
import communication.OperationException;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sanker
 */
public class AuctionTestClient implements Runnable {

    private int id;
    private final ExecutorService pool;
    private AuctionTCPHandlerForAuctiontest handleTCP = null;
    private Client clientTCP = null;
    private Log logger = null;
    private AuctionTest.Properties properties;
    
    private LinkedBlockingQueue<String> messaging = null;
    
    private HashMap<Long, Auction> activeForeignAuctions=null;
    private ReentrantLock lockforactiveForeignAuctions=null;

    private Timer timer=null;
   

    public AuctionTestClient(int id, String host,
            int tcpPort,
            AuctionTest.Properties properties,
            ExecutorService pool,
            Log logger) throws AuctionTestClientException {

        try {
            logger.output("Client "+id+" inizialize.",3);
            //Initializations
            this.id = id;
            this.pool = pool;
            this.properties=properties;
            this.logger=logger;
            this.clientTCP = new Client(host, tcpPort);
            this.messaging = new LinkedBlockingQueue<String>();
            this.handleTCP = new AuctionTCPHandlerForAuctiontest(this.id, this.clientTCP, this.messaging, logger);
            
            this.activeForeignAuctions=new HashMap<Long, Auction>();
            this.lockforactiveForeignAuctions=new ReentrantLock();
            
            
            pool.execute(handleTCP);
            logger.output("Client "+id+" is starting his AuctionTCPHandlerForAuctiontest.",3);
        } catch (ClientException e) {
            throw (new AuctionTestClientException("id:" + id + ":ClientException:", e));
        } catch (Exception e) {
            throw (new AuctionTestClientException("id:" + id + ":Exception:", e));
        }
    }

    public void run() {
        try {
            logger.output("Client "+id+" is running.",3);
            timer = new Timer();
            AuctionTestTimerTasksforLoadTest.PerMinuteTask minuteTask=null;
            AuctionTestTimerTasksforLoadTest.UpdateListTask updateTask=null;
            
            minuteTask = new AuctionTestTimerTasksforLoadTest.PerMinuteTask(
                    clientTCP,  
                    messaging,
                    ("client"+id),
                    properties,
                    activeForeignAuctions,                         
                    lockforactiveForeignAuctions,
                    this,       
                    logger       
                    );
            updateTask=new AuctionTestTimerTasksforLoadTest.UpdateListTask(
                    clientTCP,
                    messaging,
                    ("client"+id),
                    activeForeignAuctions,
                    lockforactiveForeignAuctions,//for mutual exclusion   
                    this,
                    logger
                    );
            /**********************Login*******************/
            //start registration of the client at the AuctionServer
            Operation op = new Operation(this.clientTCP);
            //udpPort will be set to a fake
            op.writeString("!login" +" "+("client"+id)+" "+0);
            if(!ParseClientInput.parseLogin(this.messaging.take()))
                throw new Exception("Cannot login to AuctionServer.");
            logger.output("Client "+id+" logged succesfully to the AuctionServer.",2);
            
            
            //START TIMER
            timer.scheduleAtFixedRate(updateTask, 0, properties.updateIntervalSec*1000);
            timer.scheduleAtFixedRate(minuteTask, 0, 60000);
            logger.output("Client "+id+" registered all schedules.",3);
            //Thread waites for notification from outside
            logger.output("Client "+id+" is wating to be closed.",3);
            /**********************waiting*******************/
            this.wait();
            
           
            /**********************Logout*******************/
            op.writeString("!logout");
            if(!ParseClientInput.parseLogout(this.messaging.take()))
                throw new Exception("Logout of client unsuccesfully.");
            
            
            logger.output("Client "+id+" is closing all his ressources an will end.",3);
            this.close();
        } //run
        catch (InterruptedException ex) {
            logger.output("Client "+id+":InterruptedException:"+ex.getMessage(),2);
        }catch (OperationException ex) {
            logger.output("Client "+id+":OperationException:"+ex.getMessage());
        }catch (Exception ex) {
            logger.output("Client "+id+":Exception:"+ex.getMessage());
        }finally        
        {
            close();
        }
        
    }//run

    public void close() {

        try {
            if(timer!=null)
            {
                this.timer.cancel();
                logger.output("Timer of Client "+id+" is canceled.",3);
            }
            if(clientTCP!=null)
            {
                this.clientTCP.closeSocket();
                logger.output("Closed socket of Client "+id+".",3);
            }
            
            
        } catch (ClientException e) {
            this.logger.output("ActionClientThread:close():" + e.getMessage());
        }

    }

 
    public class AuctionTCPHandlerForAuctiontest implements Runnable {

        private Log out = null;
        private Client client = null;
        private int id;
        private LinkedBlockingQueue<String> messaging = null;

        public AuctionTCPHandlerForAuctiontest(int id, Client client, LinkedBlockingQueue<String> messaging, Log out) {
            this.out = out;
            this.client = client;
            this.id = id;
            this.messaging = messaging;
            out.output("Constructor:Create AuctionClientTCPHandler " + id, 3);
        }

        public void run() {
            String msg = null;
            Operation op = null;
            try {
                op = new Operation(this.client);
            } catch (OperationException ex) {
                out.output("AuctionClientTCPHandlerThread " + id + ": OperationException");
                Thread.currentThread().interrupt();
            }

            while (!Thread.currentThread().isInterrupted()) {
                out.output("AuctionClientTCPHandlerThread is running..", 3);
                try {


                    msg = op.readString();
                    messaging.add(msg);

                } catch (OperationException ex) {
                    out.output("AuctionClientTCPHandlerThread " + id + ": OperationException", 2);
                    Thread.currentThread().interrupt();

                }
            }
            out.output("AuctionClientTCPHandlerThread finished " + id, 3);

        }
    }
}
