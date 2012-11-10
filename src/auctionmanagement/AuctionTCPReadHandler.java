/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package auctionmanagement;

import MyLogger.Log;
import communication.Client;
import communication.ClientException;
import communication.Operation;
import communication.OperationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author sanker
 */
public class AuctionTCPReadHandler implements Runnable{
    
    private LinkedBlockingQueue<CommandTask> queue=null;
    private Client client=null;
    private Log output=null;
    
    public AuctionTCPReadHandler(LinkedBlockingQueue<CommandTask> queue,Client client,Log output)
    {
        this.client=client;
        this.queue=queue;
        this.output=output;
        output.output("AuctionTCPReadHandler created....", 2);
    }
    
    
    
    
        private CommandTask  ExtractInfo(Request r)
        {   
            String command=r.getCommandName();
            CommandTask c=null;
            if(command.contains("list"))
            {   
                CommandTask.List l = new CommandTask.List(r.getClient());
                c= new CommandTask(l);
                
            }else if(command.contains("login"))
            {
                CommandTask.Login l = new CommandTask.Login(r.getClient(),r.getUserName(),r.getUDPPort());
                c= new CommandTask(l);
                
            }else if(command.contains("create"))
            {
               CommandTask.Create l = new CommandTask.Create(r.getClient(),
                       r.getUserName(),r.getParameter().createTime,r.getParameter().createDesc);
               c= new CommandTask(l);
            
            }else if(command.contains("logout"))
            {
                CommandTask.Logout l = new CommandTask.Logout(r.getClient(),r.getUserName());
                c= new CommandTask(l);
            
            }else if(command.contains("bid"))
            {
                
               CommandTask.Bid l = new CommandTask.Bid(r.getClient(),
                       r.getUserName(),r.getParameter().bidId,r.getParameter().bidValue);
               c= new CommandTask(l);
            
            }
            /*
            else if(command.contains("end"))
            {
                CommandTask.End l = new CommandTask.End(r.getClient());
                c= new CommandTask(l);
            }
            */
            return c;
        }
                
        
        public void run()
        {
            output.output("ServerSocketHandleThread started....", 2);
            String message=null;
            Operation op=null;
            CommandTask com=null;
            try {
                op=new Operation(this.client);
                
                while(!Thread.currentThread().isInterrupted())
                {   
               
                    
                  message=op.readString();
                  if(message.contains("end"))break;
                  com = this.ExtractInfo(new Request(this.client,message));               
                  this.queue.offer(com); 
                   output.output("ServerSocketHandleThread received"
                           +" message and forwarded a CommandTask Object to AMSThread:\n"
                           +"CommandTask[ServerSocketHandlThread]:"+"\n"
                           +com.toString(), 3);
                  
                
                 
                
                }
          
                 this.client.closeSocket();
                 
            }catch(ClientException e)
            {
                this.output.output("ServerSocketHandleThread:ClientException:"+e.getMessage());
            } catch (OperationException ex) {
                   this.output.output("ServerSocketHandleThread:OperationException:"+ex.getMessage());
                   Thread.currentThread().interrupt();
                }
            output.output("ServerSocketHandleThread end....", 2);
           
            
        }
    
}
