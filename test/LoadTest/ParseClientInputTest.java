/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LoadTest;

import auctionmanagement.Auction;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sanker
 */
public class ParseClientInputTest {
    
    HashMap<Long,Auction> map = null;
    int numberAuctions=5;
    Date time=null;
    public ParseClientInputTest() {
        time = new Date();
        map=new HashMap<Long,Auction>();
       //create Auctions
       for(long i=0;i<numberAuctions;i++)
       {
           Auction a = new Auction(("client"+i),(time.getTime()+(i*60000)),("description "+i));
           map.put(i, a);
       }
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    String printMap()
    {
        StringBuffer list=new StringBuffer();
        Iterator<Entry<Long,Auction>> iter = map.entrySet().iterator();
        while(iter.hasNext())
        {
           
            Entry<Long,Auction> entry = iter.next();
            list.append(entry.getKey().toString() + "." + " '"
                  + entry.getValue().getDescription() + "' "
                  + entry.getValue().getOwner() + " "
                  + entry.getValue().getEndDate() + " "
                  + Double.toString(entry.getValue().getHighestBid()) + " "
                  + entry.getValue().getHighestBidder() + "\n");
        }
        
        return list.toString();
    }
   
   
    @Test
    public void testParseList() {
        System.out.println("parseList");
        System.out.println("Created Auction List with "+this.numberAuctions+" Auctions:");
        String message = printMap();
        String clientOwner = "client"+(this.numberAuctions-1);
        HashMap expResult = this.map;
        HashMap result = ParseClientInput.parseList(message, clientOwner);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
