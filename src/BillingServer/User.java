package BillingServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 *
 * @author Dave
 */
public class User implements Serializable{

    private String name;
    private ArrayList<Auction> auctions = new ArrayList<Auction>();
    
    
    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<Auction> getAuctionList()
    {
        ArrayList<Auction> list=null;
        return list = new ArrayList<Auction>(auctions);        
    }
    
    public void putAuction(Auction a)
    {
        auctions.add(a);
    }
    
    public Auction getAuction(long id) {
        Iterator<Auction> iter =auctions.iterator();
        Auction a=null;
        while(iter.hasNext())
        {
            if((a=iter.next()).getId()==id)
            {
                return a;
            }
        }
        return null;
    }

    public void setAuctions(ArrayList<Auction> auctions) {
        this.auctions = auctions;
    }
}
