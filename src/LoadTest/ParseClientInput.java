/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadTest;

import auctionmanagement.Auction;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dave
 */
public  class ParseClientInput {

    /*
     * Parse Methods
     */
    public static boolean parseLogin(String message) {
        if (message.contains("Succesfully logged in")) {
            return true;
        }
        return false;
    }

    public static boolean parseLogout(String message) {
        if (message.contains("Succesfully logged out")) {
            return true;
        }
        return false;
    }

    public static boolean parseCreate(String message) {
        if (message.contains("has been created")) {
            return true;
        }
        return false;
    }

    public static boolean parseBid(String message) {
        if (message.contains("You succesfully bid")) {
            return true;
        }
        return false;
    }

    
    /*
     * Example: <ungeordnet aich möglich>
     * 0. 'firstAuction' alice Wed Dec 05 00:05:37 CET 2012 0.0 none
     * 2. 'thirdAcution' alice Wed Dec 05 00:05:55 CET 2012 0.0 none
     * 1. 'secondAuction' alice Wed Dec 05 00:05:46 CET 2012 0.0 none 
     *      
     */
    public  static  HashMap<Long, Auction> parseList(String message, String clientOwner) {
        if(message.isEmpty())
            return null;
        
        
        if (message.contains(".") && message.contains("'")) {
            
            String[] listItems = message.split("\n");
            HashMap<Long, Auction> list = new HashMap<Long, Auction>();
            for (String s : listItems) {
                try {
                    String[] splitted = s.split(" ");
                    String owner = splitted[2];
                    if (!owner.equals(clientOwner)) {
                        String highestBidder = splitted[splitted.length-1];
                        double highestBid = Double.parseDouble(splitted[splitted.length-2]);
                        long id = Long.parseLong(splitted[0].replace(".", ""));
                        String description = splitted[1].substring(1,splitted[1].lastIndexOf('\''));
                        int positionbeforeDate=(s.indexOf(splitted[2]))+(splitted[2].length()+1);
                        int positionafterDate=(s.indexOf(splitted[splitted.length-3])+splitted[splitted.length-3].length());
                        Date date=null;
                        String Date_=s.substring(positionbeforeDate, positionafterDate);

                        date=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).parse(Date_);
                        long expires = date.getTime()-(new Date()).getTime();
                        if(expires>0)
                        {
                            Auction a = new Auction(owner, expires, description);
                            a.setnewBid(owner, id);
                            list.put(id, a); 
                        }
                    }
                    
                    
                } catch (ParseException ex) {
                    return null;
                }catch (NumberFormatException ex) {
                    return null;
                }
               
                
                
            }
            return list;
        }
        return null;
    }
}
