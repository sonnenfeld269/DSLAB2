/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sanker
 */
public class Filter {
    private static long counter=0;
    private long filterID;
    private ConcurrentHashMap<Long,Pattern> RegexFilterMap=null;
   
    
    public Filter()
    {
        filterID=getnewID();
        RegexFilterMap= new ConcurrentHashMap<Long,Pattern>();
    
    }
    
    boolean checkMessage(String message)
    {
       Iterator<Map.Entry<Long,Pattern>> iter = this.RegexFilterMap.entrySet().iterator();
        
        while(iter.hasNext())
        {
            Map.Entry<Long,Pattern> entry = iter.next();
            
            Matcher match=entry.getValue().matcher(message);
            if(match.matches())
            {
                return true;
            }
                 
        }
        
        return false;
    }
    
    public long getFilterID()
    {
        return filterID;
    }
    
    public boolean subscribeRegex(Long id,Pattern pattern)
    {
        
        this.RegexFilterMap.put(id, pattern);
        if(RegexFilterMap.containsKey(id))
            return true;
        else
            return false;
    }
    
    public boolean subscribeRegex(Long id,String regex)
    {
        
        this.RegexFilterMap.put(id, Pattern.compile(regex));
        if(RegexFilterMap.containsKey(id))
            return true;
        else
            return false;
     
    }
    
    public boolean  unsubscribeRegex(Long id)
    {
      
      if((this.RegexFilterMap.remove(id))!=null)
         return true;
      else 
          return false;

    }
    
    private synchronized long getnewID()
    {
        return counter++;
    }

}