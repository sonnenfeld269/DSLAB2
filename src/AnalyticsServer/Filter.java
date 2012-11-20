/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnalyticsServer;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sanker
 */
public class Filter {
    
    private Vector<Pattern> RegexFilterList=null;
   
    
    public Filter()
    {
        RegexFilterList= new Vector<Pattern>();
    
    }
    
    boolean checkMessage(String message)
    {
        ListIterator<Pattern> iter = this.RegexFilterList.listIterator();
        int pos=0;
        while(iter.hasNext())
        {
            pos=iter.nextIndex();
            Matcher match=iter.next().matcher(message);
            if(match.matches())
            {
                return true;
            }
                 
        }
        
        return false;
    }
    
    public boolean subscribeRegex(Pattern pattern)
    {
       return  this.RegexFilterList.add(pattern);
    }
    
    public boolean subscribeRegex(String regex)
    {
        
       return  this.RegexFilterList.add(Pattern.compile(regex));
    }
    
    public boolean  unsubscribeRegex(Pattern pattern)
    {
      
      return this.RegexFilterList.remove(pattern);

    }
    
    public boolean  unsubscribeRegex(String regex)
    {
      ListIterator<Pattern> iter = this.RegexFilterList.listIterator();
      Pattern p=null;
      int i=0;
      boolean b=false;
      while(iter.hasNext())
      {
           p=iter.next();
           i=p.pattern().compareTo(regex);
           if(i==0)
           {
               iter.remove();
               b=true;
           }
      }
      
      return b;
     
      
      
      
     
    }
    
    
    
    public boolean  unsubscribeRegex(int position)
    {
      Pattern p=null;
      boolean b=false;
       try{
           
        p=this.RegexFilterList.remove(position);
        if(p!=null)
            b=true;
       }catch(IndexOutOfBoundsException e)
       {
           
       }
           
       return b;
      
    }
    
  
    
}
