
package auctionmanagement;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanker
 */

import auctionmanagement.AuctionServer;
import auctionmanagement.AuctionServerException;
import MyLogger.Log;
import MyLogger.Log;
import java.io.PrintWriter;


public class PartBServer {
    
     String[] arguments=null;
     int tcpPort=0;
     AuctionServer auction=null;
     
    public PartBServer(String[] args)
    {
        arguments=args;
    }
    
    
   public boolean checkandgetArguments()
    {
        if(arguments.length != 1)
                  return false;
        tcpPort=Integer.valueOf(arguments[0]);
        
       
       return true;
    }
    
    
    public void printUsage()
    {
        System.err.println("usage: <run-server>  -tcpPort"
                + "\n\t"+"tcpPort:  TCP connection port on which the auction server will "
                + "\n\t\t"+"receive incomming (command) messages from clients."
             
                 );
    }
    
    
   public int run()
    {
        
        
      if(!this.checkandgetArguments()) 
        {
            this.printUsage();
            return -1;
        }
       Log output=new Log(new PrintWriter(System.out),0); 
       
       
        try {
            auction = new AuctionServer(this.tcpPort,output);
            auction.run();
        } catch (AuctionServerException e) {
            output.output("PartBServer:"+e.getMessage());
            auction.close();
            output.close();
            return -1;
            
        }
        auction.close();
        output.close();
        return 0;
    }
}