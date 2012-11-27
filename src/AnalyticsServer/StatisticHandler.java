/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import Event.AnalyticsControllEvent;
import Event.AuctionEvent;
import Event.BidEvent;
import Event.Event;
import Event.UserEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author sanker
 */
public class StatisticHandler implements Runnable{
    private LinkedBlockingQueue<Event> inputchannel=null;
    private LinkedBlockingQueue<Event> outputchannel=null;
    private Logger logger=null;
    
    /*Statistic tracking values**/
    private long count_Bid_Won=0;
    private long count_Auctions=0;
    private double bid_max=0;
    private long user_sessiontime_max=0;
    private long user_sessiontime_min=0;
    private double user_sessiontime_avg=0;
    
    private long sum_user_sessiontime=0;
    private long count_user_sessions=0;
   /*Statistic time values [ms]**/
    private long sum_AllAuctionTime; //AuctionTime1 + AuctionTime2+..+AuctionTimeN
    
    
    public StatisticHandler(ExecutorService pool,LinkedBlockingQueue<Event> inputchannel
            ,LinkedBlockingQueue<Event> outputchannel)
    {
       this.inputchannel=inputchannel;
       this.outputchannel=outputchannel;
       logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME+"."+StatisticHandler.class.getSimpleName());
    }
       
    
    public void run()
    {
        
       logger.entry();
        Event event=null;
        while(!Thread.currentThread().isInterrupted())
        {
            try {
                
               event=inputchannel.take();
               if(event.getType().contains(AnalyticsControllEvent.AnalyticsControllEventType.CLOSE_STATISTIC.name()))
               {
                   Thread.currentThread().interrupt();
                   break;
               }
               else if( (event.getType().contains(UserEvent.UserEventType.USER_LOGOUT.name()))
                       || (event.getType().contains(UserEvent.UserEventType.USER_LOGOUT.name()) ))
               {
                 //track USER LOGOUTS
                   UserEvent userevent=(UserEvent)event;
                   sum_user_sessiontime+=userevent.getUserTime();
                   count_user_sessions++;
                   user_sessiontime_avg=sum_user_sessiontime/count_user_sessions;
                   if(count_user_sessions==1)//user_session_min=user_session_max
                       user_sessiontime_min=userevent.getUserTime();
                   if(user_sessiontime_min>userevent.getUserTime())
                       user_sessiontime_min=userevent.getUserTime();
                   if(user_sessiontime_max<userevent.getUserTime())
                       user_sessiontime_max=userevent.getUserTime();
                   
               }else if(event.getType().contains(BidEvent.BidEventType.BID_WON.name()))
               {
                 //BID_PRICE_MAX
                   BidEvent bidevent =(BidEvent)event;
                   if(this.bid_max<bidevent.getPrice())
                   {
                       this.bid_max=bidevent.getPrice();
                   }
                 //AUCTION_SUCCESS_RATIO
                   
                   count_Bid_Won++;
                   
                   
               }else if(event.getType().contains(BidEvent.BidEventType.BID_PLACED.name()))
               {
                 //for bid_COUNT_PER_MINUTE
                   
                   
               }else if(event.getType().contains(AuctionEvent.AuctionType.AUCTION_ENDED.name()))
               {
                   
                 //for AUCTION_TIME_AVG
                   AuctionEvent auctionevent = (AuctionEvent)event;
                   count_Auctions++;
                   sum_AllAuctionTime+=auctionevent.getAuctionTime();
                   long auction_time_avg=sum_AllAuctionTime/count_Auctions;
                   
                   
               }
                                           
              
            } catch (InterruptedException ex) {
              logger.catching(ex);
              Thread.currentThread().interrupt();
            }catch (Exception ex) {
              logger.catching(ex);
              Thread.currentThread().interrupt();
            }             
        
        }
     
     logger.exit(); 
        
        
    }
    
}
