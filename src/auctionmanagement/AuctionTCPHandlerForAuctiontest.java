
import MyLogger.Log;
import communication.Client;
import communication.Operation;
import communication.OperationException;

    
    public class AuctionTCPHandlerForAuctiontest implements Runnable {
        
        private Log out=null;
        private Client client=null;
        
        public AuctionTCPHandlerForAuctiontest(Client client,Log out)
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
    