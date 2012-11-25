/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import RMI.ManagementClientCallBackInterface;

/**
 *
 * @author sanker
 */
 public class Task{
        
       
            private SUBSCRIBER   subscriber=null;
            private UNSUBSCRIBER unsubscriber=null;
            private boolean isSubscriber;
        
        public Task(SUBSCRIBER   subscriber)
        {
            isSubscriber=true;
            this.subscriber=subscriber;
        }
        
        public Task(UNSUBSCRIBER   unsubscriber)
        {
            isSubscriber=false;
           this.unsubscriber=unsubscriber; 
        }
        
        public boolean isSubscriber()
        {
            return this.isSubscriber;
        }
        
        public SUBSCRIBER getSubscriber()
        {
            if(isSubscriber)
                return this.subscriber;
            else return null;
        }
        
        public UNSUBSCRIBER getUnSubscriber()
        {
            if(!isSubscriber)
                return this.unsubscriber;
            else return null;
        }
    
        public static class SUBSCRIBER
        {
            public ManagementClientCallBackInterface mccbi; 
            public String regex;
             
            public SUBSCRIBER(ManagementClientCallBackInterface mccbi,String regex)
            {
                this.mccbi=mccbi;
                this.regex=regex;
            }
             
        
        }
        
        public static class UNSUBSCRIBER
        {
            
            public long subscriptionID;
            
            public UNSUBSCRIBER(long subscriptionID)
            {
                this.subscriptionID=subscriptionID;
            }
        
        }
    
    }