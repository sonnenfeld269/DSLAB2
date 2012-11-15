/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionmanagement;

/**
 *
 * @author sanker
 */

import communication.Server;
import communication.ServerException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import MyLogger.Log;
//import communication.ClientUDPException;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import Event.*;

/*********** RMI *******************************/
import AnalyticsServer.AnalyticsServerInterface;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
/*********** RMI *******************************/


public class AuctionManagementSystem implements Runnable{

    private  LinkedBlockingQueue<CommandTask> incomingrequest=null;
    private  ConcurrentHashMap<String,Account> account_map=null;
    private  ConcurrentHashMap<Long,Auction> auction_map=null;
    private  Timer timer=null;
    private LinkedBlockingQueue<Answer> outgoingmessagechannel = null;
    private AuctionTCPMessageServer outgoingMessageServer =null;
    private AuctionUDPMessageServer NotificationServer=null;
    private LinkedBlockingQueue<Notification> notificationchannel=null;
    private final ExecutorService pool;
    private Log logger=null;
    /*********** RMI *******************************/
    private final String HOST = "localhost";
    private final int PORT = 1099;
    private Registry registry=null;
    private boolean rmiAvailability=false;
    private AnalyticsServerInterface analytic=null;
    /*********** RMI *******************************/
    private CommandTask readRequest()
    {
        CommandTask r =null;
        try {
            r=incomingrequest.take();
        } catch (InterruptedException ex) {
           
        }
        return r;
    }
    
    
    
    //public ----------------------------------------- 
    public AuctionManagementSystem(LinkedBlockingQueue<CommandTask> incomingrequest, ExecutorService pool,Log output) 
    {
        this.pool=pool;
        this.logger=output;
        this.incomingrequest = incomingrequest;
        this.account_map=new ConcurrentHashMap<String,Account>();
        this.auction_map=new ConcurrentHashMap<Long,Auction>();
        this.timer = new Timer(true);
        this.notificationchannel = new LinkedBlockingQueue<Notification>();
        //this.NotificationServer = new AuctionUDPMessageServer(notificationchannel,this.logger);
        this.outgoingmessagechannel = new LinkedBlockingQueue<Answer>();
        this.outgoingMessageServer = new AuctionTCPMessageServer(outgoingmessagechannel,this.logger);
        
        //this.pool.execute(NotificationServer);
        this.pool.execute(outgoingMessageServer);
        this.logger.output("AuctionManagementSystem created...", 2);
        try {
            /*
             * ***************RMI**********************
             */
             this.registry = LocateRegistry.getRegistry(HOST, PORT);
             analytic = (AnalyticsServerInterface) registry.lookup(AnalyticsServerInterface.class.getSimpleName());
             rmiAvailability=true;
             /*
             * ***************RMI**********************
             */
        } catch (RemoteException ex) {
           this.logger.output("AuctionManagementSystem:RMI:Error:"+ex.getMessage(), 2);
        }catch(NotBoundException ex)
        {
          this.logger.output("AuctionManagementSystem:RMI:Error:"+ex.getMessage(), 2);
        }
        
        
        
    }
    //returns true if the queue had enogh space 
   
    

    @Override
    public void run() {
        this.logger.output("AuctionManagementSystem started...", 2);
        CommandTask newRequestTask=null;
        AMS_Handler amshandler=null;
        
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                
                newRequestTask=incomingrequest.take();
                amshandler = new AMS_Handler(newRequestTask);
                pool.execute(amshandler);
                                             
              
            } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
            }          
        
        }
        this.logger.output("AuctionManagementSystem end...", 2);
        
    }
    
    
    public void close()
    {
        this.account_map.clear();
        this.auction_map.clear();
        timer.cancel();
       this.logger.output("AuctionManagementSystem closed...", 2);
    }
    
    
    
    public class Task extends TimerTask{
        private Long auction_id;
        private Auction auc=null;
        private Account owner=null;
        private Account bidder=null;
        private Log error=null;
     
        public Task(long auction_id,Log error)
        {
           this.auction_id=new Long(auction_id);
           this.error=error;
          // account_map
                        
        }
        
        
        
        public void run()
        {
            this.error.output("TimerTaskHandleThread started a schedule...", 2);
            this.auc=auction_map.remove(this.auction_id);
            this.error.output("Close auction:"+"id:"+auc.getID()+",desc:"+auc.getDescription(), 3);
            this.error.output("Owner:"+auc.getOwner()+",Hbidder:"+auc.getHighestBidder(), 3);
            owner = account_map.get(this.auc.getOwner());
            if(auc.getHighestBidder().contains("none"))
                bidder=null;
            else
                bidder=account_map.get(this.auc.getHighestBidder());
            
              /*********** RMI *******************************/
            
            if(rmiAvailability)
            {
                try {
                    analytic.processEvents(
                        new AuctionEvent(AuctionEvent.AuctionType.AUCTION_ENDED,
                            auc.getID())
                        );
                    logger.output("TimerTask:RMI"+
                    ":processEvent:Invoke::"
                        + "AuctionID:"+auc.getID()+"\n"
                        + "Type:AUCTION_ENDED",3);
                    if(bidder!=null)
                    {
                        analytic.processEvents(
                            new BidEvent(bidder.getName(),
                                auc.getID(),
                                auc.getHighestBid(),
                                BidEvent.BidEventType.BID_WON)
                            );
                        logger.output("TimerTask:RMI"+
                            ":processEvent:Invoke::"
                                + "AuctionID:"+auc.getID()
                                +"\nUser:"+auc.getHighestBidder()
                                +"\nPrice:"+auc.getHighestBid()
                                +"\nType:BID_WON",3);
                    }

                } catch (RemoteException ex) {
                    logger.output("TimerTaskHandle:RemoteException:"+ex.getMessage(), 2);
                }
                
            }
             /*********** RMI *******************************/                
            
          
            //Notification notificationOwner=null;
            //Notification notificationBidder=null;
                    
            /*       
            String messageforBidderOwner = new String("!auction-ended"+" "
                            +auc.getHighestBidder()
                            +" "
                            +Double.toString(auc.getHighestBid())
                            +" "
                            +auc.getDescription());
           
            try
            {
             if(owner.isOnline())
                notificationOwner=new Notification(messageforBidderOwner,
                    owner.getClientUDP());
             if(bidder!=null)
             {
                if(bidder.isOnline())
                    notificationBidder=new Notification(messageforBidderOwner,
                        bidder.getClientUDP());
             }
             
             if(notificationOwner!=null)
                 notificationchannel.offer(notificationOwner);
             else
                 owner.addNotification(messageforBidderOwner);
             if(notificationBidder!=null)
                 notificationchannel.offer(notificationBidder);
             else{
                 if(bidder!=null)
                    bidder.addNotification(messageforBidderOwner);
             }
             
             
            }catch(ClientUDPException e)
            {
                this.error.output("TimerTaskThread:ClientUDPException:"+e.getMessage());
            }*/
             this.error.output("TimerTaskHandleThread finished a schedule...", 2);
        }
    
    
    }
    
    public class AMS_Handler implements Runnable{
       
        private CommandTask commandtask=null;
        /*
        this.pool=pool;
        this.logger=output;
       
        this.account_map=new ConcurrentHashMap<String,Account>();
        this.auction_map=new ConcurrentHashMap<Integer,Auction>();
        this.timer = new Timer(true);
        this.notificationchannel = new LinkedBlockingQueue<Notification>();
        this.NotificationServer = new AuctionUDPMessageServer(notificationchannel,this.logger);
        this.outgoingmessagechannel = new LinkedBlockingQueue<Answer>();
        this.outgoingMessageServer = 
         * 
         */
     
        public AMS_Handler(CommandTask commandtask)
        {
           this.commandtask=commandtask;
           logger.output("AMS_HandlerThread Created",2);
                        
        }
        
        
        
        public void run()
        {
            logger.output("AMS_HandlerThread started",2);
            if(this.commandtask.list!=null)
            {
              try{
                StringBuffer list=new StringBuffer();
                Iterator<Map.Entry<Long,Auction>> iterator =auction_map.entrySet().iterator();
                if(iterator.hasNext()){//if no auction entry avaible, no sending
                    while(iterator.hasNext())
                    {
                        Map.Entry<Long,Auction> entry = iterator.next(); 
                        list.append(entry.getKey().toString()+"."+" '"
                                +entry.getValue().getDescription()+"' "
                                +entry.getValue().getOwner()+" "
                                +entry.getValue().getEndDate()+" "
                                +Double.toString(entry.getValue().getHighestBid())+" "
                                +entry.getValue().getHighestBidder()+"\n" );
                    }

                    list.setCharAt(list.length()-1,' ');
                    Answer a = new Answer(list.toString(),this.commandtask.list.client);

                    outgoingmessagechannel.offer(a);
                }
              }catch(Exception e){
                  logger.output("AMSHandlerThread:list:Exception:"+e.getMessage());
              }
   
            }else  if(this.commandtask.login!=null)
            {
             try{ 
                  logger.output("AMSHandlerThread:login for user:"+commandtask.login.user, 3);
                if(account_map.containsKey(this.commandtask.login.user)) 
                {
                    logger.output("AMSHandlerThread:login start with existing account ", 3);
                    Account ac=null;
                    if((ac=account_map.get(commandtask.login.user))!=null)
                    {
                        ac.activateAccount(this.commandtask.login.client, 
                                this.commandtask.login.udpPort);
                        if(account_map.replace(commandtask.login.user, ac)!=null)
                        {
                            String answer = new String("Succesfully logged in as"
                                +" "+commandtask.login.user+"!");
                            Answer a = new Answer(answer,commandtask.login.client);
                            outgoingmessagechannel.offer(a);
                            logger.output("AMSHandlerThread:login finished:"+
                                    "user "+commandtask.login.user, 3);
                           
                            /*********** RMI *******************************/
                            if(rmiAvailability)
                            {
                                try{
                                    analytic.processEvents(
                                        new UserEvent(commandtask.login.user,
                                            UserEvent.UserEventType.USER_LOGIN)
                                        );
                                    logger.output("AMSHandlerThread:login:RMI"+
                                        ":processEvent:Invoke::"
                                            + "\nUser:"+commandtask.login.user,3);
                                }catch(RemoteException e)
                                {
                                    logger.output("AMS_HandlerThread:login:RemoteException"
                                            +":"+e.getMessage(), 2);
                                }
                            }
                            
                            /*********** RMI *******************************/
                            
                        }
                    }
                }else{   
                    logger.output("AMSHandlerThread:login start with new account", 3);
                    Account acc = new Account(commandtask.login.user,
                        commandtask.login.client,
                        commandtask.login.udpPort,
                        notificationchannel);
                    if(account_map.put(acc.getName(), acc)!=null)
                    {
                        String answer = new String("Succesfully logged in as"
                            +" "+commandtask.login.user+"!");
                        Answer a = new Answer(answer,acc.getClient());
                        outgoingmessagechannel.offer(a);
                        logger.output("AMSHandlerThread:login finished:"+
                                    "user "+commandtask.login.client, 3);
                        /*********** RMI *******************************/
                            if(rmiAvailability)
                            {
                                try{
                                    analytic.processEvents(
                                        new UserEvent(commandtask.login.user,
                                            UserEvent.UserEventType.USER_LOGIN)
                                        );
                                    logger.output("AMSHandlerThread:login:RMI"+
                                        ":processEvent:Invoke::"
                                            + "\nUser:"+commandtask.login.user,3);
                                }catch(RemoteException e)
                                {
                                    logger.output("AMS_HandlerThread:login:RemoteException"
                                            +":"+e.getMessage(), 2);
                                }
                            }
                            
                         /*********** RMI *******************************/
                    }
                }
                }catch(AccountException e){
                  logger.output("AMSHandlerThread:login:"+e.getMessage());
                }catch(Exception e){
                  logger.output("AMSHandlerThread:login:Exception:"+e.getMessage());
                } 
            
                
            }else  if(this.commandtask.logout!=null)
            {
                try{
                    Account ac=null;
                    if((ac=account_map.get(commandtask.logout.user))!=null)
                    {
                        ac.deactivateAccount();
                        if(account_map.replace(commandtask.logout.user, ac)!=null)
                        {
                            String answer = new String("Succesfully logged out as"
                                +" "+commandtask.logout.user+"!");
                            Answer a = new Answer(answer,commandtask.logout.client);
                            outgoingmessagechannel.offer(a);
                            
                            /*********** RMI *******************************/
                            if(rmiAvailability)
                            {
                                try{
                                    analytic.processEvents(
                                        new UserEvent(commandtask.login.user,
                                            UserEvent.UserEventType.USER_LOGOUT)
                                        );
                                    logger.output("AMSHandlerThread:logout:RMI"+
                                        ":processEvent:Invoke::"
                                            + "User:"+commandtask.login.user,3);
                                }catch(RemoteException e)
                                {
                                    logger.output("AMS_HandlerThread:logout:RemoteException:"
                                            +e.getMessage(), 2);
                                }
                            }
                            
                         /*********** RMI *******************************/
                            
                            
                            
                            
                            
                        }else throw new Exception("Could not deactivate User!");
                    }else throw new Exception("Didn't find user!");
                }catch(Exception e){
                  logger.output("AMSHandlerThread:logout:Exception:"+e.getMessage());
                } 
                
           
                
            }else  if(this.commandtask.create!=null)
            {
              try{
                if(account_map.containsKey(commandtask.create.user))
                {
                    Auction auc = new Auction(commandtask.create.user,
                            commandtask.create.expire,commandtask.create.description);
                   auction_map.put((new Long(auc.getID())), auc) ;
                   timer.schedule( (new Task(auc.getID(),logger)) ,(auc.getPeriodofTime()*1000));
                   String answer=new String("An auction"+" '"
                           +auc.getDescription()+"' "+"with id"+" "
                           +auc.getID()+" "+"has been created and will end on"+" "
                           +auc.getEndDate()+".");
                    Answer a = new Answer(answer,commandtask.create.client);
                    outgoingmessagechannel.offer(a);
                    
                    /*********** RMI *******************************/
                            if(rmiAvailability)
                            {
                                try{
                                    analytic.processEvents(
                                        new AuctionEvent(AuctionEvent.AuctionType.AUCTION_STARTED,
                                            auc.getID())
                                        );
                                    logger.output("AMSHandlerThread:create:RMI"+
                                        ":processEvent:Invoke::"
                                            + "AuctionID:"+auc.getID(),3);
                                 }catch(RemoteException e)
                                {
                                    logger.output("AMS_HandlerThread:create:RemoteException"
                                            +":"+e.getMessage(), 2);
                                }
                            }
                            
                         /*********** RMI *******************************/
                    

                    
                }
              }catch(Exception e){
                  logger.output("AMSHandlerThread:create:Exception:"+e.getMessage());
              } 
                    
            
                
            }else  if(this.commandtask.bid!=null)
            {

                Auction auc=null;
                try{
                    logger.output("AMSHandlerThread:Bid:start",2);
                    Long id = new Long(commandtask.bid.id);
                    if((auc = auction_map.get(id))==null)
                    {
                        throw new Exception("No Auction with id "+id.toString()+" found.");
                    }
                    Account oldBidder=null;
                     logger.output("AMSHandlerThread:Bid:oldBidder:"
                             +auc.getHighestBidder()+","+auc.getHighestBidder().contains("none"),2);
                     boolean b=false;
                    if(!(auc.getHighestBidder().contains("none")))
                    {
                            oldBidder=account_map.get(auc.getHighestBidder());
                            logger.output("AMSHandlerThread:Bid:oldBidder:"+oldBidder.getName(),3);
                    }
                    
                    if(auc==null) 
                        throw new Exception("Auction id is not avaible,canbot bid.");
                    if(auc.setnewBid(commandtask.bid.user, commandtask.bid.amount))
                    {
                        logger.output("AMSHandlerThread:Bid:setNewBid:newBidder"+auc.getHighestBidder(),3);
                        logger.output("AMSHandlerThread:Bid:setNewBid:newBid"+auc.getHighestBid(),3);
                        auction_map.replace(id,auc);
                        
                        String not=new String("!new-bid"+" "
                                +auc.getDescription());
                        String ans=new String("You succesfully bid with"
                                + " "+auc.getHighestBid()+" "
                                +"on"+" '"+auc.getDescription()+"'.");
                        
                        Answer a = new Answer(ans,commandtask.bid.client);
                        outgoingmessagechannel.offer(a);
                        
                        /*********** RMI *******************************/
                            if(rmiAvailability)
                            {
                                try{
                                    analytic.processEvents(
                                        new BidEvent(auc.getHighestBidder(),
                                            auc.getID(),auc.getHighestBid(),
                                            BidEvent.BidEventType.BID_PLACED)
                                        );
                                    logger.output("AMSHandlerThread:bid:RMI"+
                                        ":processEvent:Invoke::"
                                            + "AuctionID:"+auc.getID()
                                            +"\nUser:"+auc.getHighestBidder()
                                            +"\nPrice:"+auc.getHighestBid()
                                            +"\nType:BID_PLACED",3);
                                 }catch(RemoteException e)
                                {
                                    logger.output("AMS_HandlerThread:bid:RemoteException"
                                            +":"+e.getMessage(), 2);
                                }
                            }
                            
                         /*********** RMI *******************************/
                        
                        
                        
                        if(oldBidder!=null)
                        {
                            /*
                            if(oldBidder.getClient()!=null)
                            {
                                
                                Notification n = new Notification(not,oldBidder.getClientUDP());
                                notificationchannel.offer(n);
                                logger.output("AMSHandlerThread:Bid:Notification send to user"+oldBidder.getName(),3);
                                
                                
                            }else
                            {
                                oldBidder.addNotification(not);
                                 logger.output("AMSHandlerThread:Bid:Notification store for user"+oldBidder.getName(),3);
                            }
                            */
                            
                            /*********** RMI *******************************/
                            if(rmiAvailability)
                            {
                                try{
                                    analytic.processEvents(
                                        new BidEvent(oldBidder.getName(),
                                            auc.getID(),
                                            auc.getHighestBid(),
                                            BidEvent.BidEventType.BID_OVERBID)
                                        );
                                    logger.output("AMSHandlerThread:bid:RMI"+
                                        ":processEvent:Invoke::"
                                            + "AuctionID:"+auc.getID()
                                            +"\nUser:"+auc.getHighestBidder()
                                            +"\nPrice:"+auc.getHighestBid()
                                            +"\nType:BID_OVERBID",3);
                                 }catch(RemoteException e)
                                {
                                    logger.output("AMS_HandlerThread:bid:RemoteException"
                                            +":"+e.getMessage(), 2);
                                }
                            }
                            
                         /*********** RMI *******************************/
                            
                            
                            
                        }
                        
                    }else
                    {
                        logger.output("AMSHandlerThread:Bid:unsuccesful",3);
                        String ans=new String("You unsuccesfully bid with"
                                + " "+auc.getHighestBid()+" "
                                +"on"+" '"+auc.getDescription()+"'."
                                +" Current highest bid is"+" "
                                +auc.getHighestBid());
                        
                        Answer a = new Answer(ans,commandtask.bid.client);
                        outgoingmessagechannel.offer(a);
                    }
                    
              }catch(Exception e){
                  logger.output("AMSHandlerThread:bid:Exception:"+e.getMessage(),3);
              } 
                
            }
             logger.output("AMS_HandlerThread finished",2);
        }
    
    
    }
   
    
    
}
