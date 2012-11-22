/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sanker
 */




public class ManagementClient {
     public static void main(String[] args) {
        // TODO code application logic here
       
       ManagementClients.ManagementClient mc= new ManagementClients.ManagementClient("./src/registry.properties");
       mc.run();
        
       System.exit(0); 
    }

   
}
