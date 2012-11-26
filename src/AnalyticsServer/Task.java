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
            private REMOTEERROR  remoteerror=null;
            private boolean isSubscriber;
            private boolean isRemoteError;
        
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
        
        public Task(REMOTEERROR   error)
        {
            isSubscriber=false;
            isRemoteError=true;
            this.remoteerror=error;
           
        }
        
        
        public boolean isSubscriber()
        {
            return this.isSubscriber;
        }
        
        public boolean isRemoteError()
        {
            return this.isRemoteError;
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
        public REMOTEERROR getRemoteError()
        {
            return this.remoteerror;
        }
        
    
        public static class SUBSCRIBER
        {
            public ManagementClientCallBackInterface mccbi; 
            public long subscribeID;
            public String regex;
             
            public SUBSCRIBER(ManagementClientCallBackInterface mccbi,long subscribeID,String regex)
            {
                this.mccbi=mccbi;
                this.regex=regex;
                this.subscribeID=subscribeID;
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
        
        public static class RESULT
        {
        
            public boolean success;
            public long subscriptionID;
            public RESULT(boolean success,long subscriptionID)
            {
                this.success=success;
                this.subscriptionID=subscriptionID;
            }
        }
        
        public static class REMOTEERROR
        {
            public long ClientID;
            public Filter filter=null;
            public ManagementClientCallBackInterface mccbi=null;
            public REMOTEERROR(long ClientID,
                        Filter filter,
                        ManagementClientCallBackInterface mccbi)
            {
                this.ClientID=ClientID;
                this.filter=filter;
                this.mccbi=mccbi;
                
            }
    
        }
    
    }