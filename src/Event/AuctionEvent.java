/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Event;

/**
 *
 * @author Marko
 */
public class AuctionEvent extends  Event {
    private long auctionID=0;
    
    public AuctionEvent(AuctionEvent.AuctionType type,long auctionID)
    {
        super(type.name());
        this.auctionID=auctionID;
        
    }
  
    public AuctionEvent(AuctionEvent e)
    {
        super(e);
        this.auctionID=e.auctionID;
    }
    
    public long getAuctionID()
    {
        return this.auctionID;
    }
    
    public static enum AuctionType
    {
        AUCTION_STARTED,
        AUCTION_ENDED
    }
    
    public String toString()
    {
        return (super.toString()+"\n"
                +"AuctionID:"+this.auctionID+"\n"
                );
    }


    
}

