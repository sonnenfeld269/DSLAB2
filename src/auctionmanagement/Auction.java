package auctionmanagement;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

/**
 *
 * @author sanker
 */
public class Auction {
    private static long counter=0;
    
    private long id=0;
    private String description=null;
    private String owner=null;
    private long duration=0;//auctions ends in duration seconds
    private Date starttime=null;
    private double highest_bid_amount=0.00;
    private String highest_bidder=null;
    
    public Auction(String owner,long expires,String description)
    {
        
        id=getNewAuctionID();
        this.owner=owner;
        long timenow=Calendar.getInstance().getTimeInMillis();
        this.starttime=new Date(timenow);
        this.duration=expires;
        this.description=description;
        highest_bid_amount=0.00;
        highest_bidder=new String("none");
    }
    
    public long getID() 
    {
        return this.id;
    }
 
    
    public String getDescription()
    {
        return this.description;
    }
    
    public long getPeriodofTime()
    {
        return this.duration;//in seconds
    }
    
    public String getstartDate()
    {
        int medium = DateFormat.MEDIUM;
        DateFormat df = DateFormat.getDateTimeInstance( medium, medium, Locale.GERMAN );
        return df.format(starttime);
    }
    
    public Date getstartDateObject()
    {
       return this.starttime;
    }
    
    public Date getEndDateObject()
    {
       return new Date(this.starttime.getTime()+(this.duration*1000));
    }
    
    public String getEndDate()
    {
       long millisecond=this.starttime.getTime();
       millisecond = millisecond + (this.duration*1000); 
       int medium = DateFormat.MEDIUM;
       DateFormat df = DateFormat.getDateTimeInstance( medium, medium, Locale.GERMAN );
       return df.format(new Date(millisecond));
        
    }
    
    
    public String getOwner()
    {
        return this.owner;
    }
    
    public synchronized long getNewAuctionID()
    {
      return Auction.counter++;  
    }
    
    public synchronized boolean setnewBid(String bidder,double bid)
    {
        if(this.highest_bid_amount>=bid)
            return false;
        this.highest_bid_amount=bid;
        this.highest_bidder=bidder;
        return true;
    }
    public synchronized double getHighestBid()
    {
        return highest_bid_amount;
    }
    
    public synchronized String getHighestBidder()
    {
        return this.highest_bidder;
    }
    
}
