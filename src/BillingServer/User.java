package BillingServer;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Dave
 */
public class User {

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

    public ArrayList<Auction> getAuctions() {
        return auctions;
    }

    public void setAuctions(ArrayList<Auction> auctions) {
        this.auctions = auctions;
    }
}
