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
    private static long counter=0; 
    private long auctionID=0;
    
    public AuctionEvent(AuctionEvent.AuctionType type)
    {
        super(type.name());
        this.auctionID=AuctionEvent.getNextCount();
        
    }
  
    public AuctionEvent(AuctionEvent e)
    {
        super(e);
        this.auctionID=e.auctionID;
    }
    
    public static enum AuctionType
    {
        AUCTION_STARTED,
        AUCTION_ENDED
    }

    synchronized private static long getNextCount()
    {
        return counter++;
    }
}

