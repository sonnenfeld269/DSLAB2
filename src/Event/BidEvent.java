/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Event;

/**
 *
 * @author Marko
 */



public class BidEvent extends  Event {
    private String userName=null; 
    private long auctionID=0;
    private double price=0;
    
    public BidEvent(String userName,long auctionID, double price, BidEvent.BidEventType type)
    {        
        super(type.name());
        this.userName=userName;
        this.price=price;
        this.auctionID=auctionID;    
    
    }
    
    public BidEvent(BidEvent e)
    {
        super(e);
        this.userName=e.userName;
        this.price=e.price;
        this.auctionID=e.auctionID;
    }
    
    public String getUserName()
    {
        return this.userName;
    }
    
    public long getAuctionID()
    {
        return this.auctionID;
    }
    
    public double getPrice()
    {
        return this.price;
    }
    
  
    
    public static enum BidEventType
    {
        BID_PLACED,
        BID_OVERBID,
        BID_WON
    }

   
}
