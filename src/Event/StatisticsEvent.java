/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Event;

/**
 *
 * @author Marko
 */


public class StatisticsEvent extends  Event {
   private double value=0;
    
    public StatisticsEvent(double value, StatisticsEvent.StatisticsEventType type)
    {        
        super(type.name());
        this.value=value;
    
    }
    
    public StatisticsEvent(StatisticsEvent e)
    {
        super(e);
        this.value=e.value;
    }
    
  
    
    public static enum StatisticsEventType
    {
        USER_SESSIONTIME_MIN,
        USER_SESSIONTIME_MAX,
        USER_SESSIONTIME_AVG,
        BID_PRICE_MAX,
        BID_COUNT_PER_MINUTE,
        AUCTION_TIME_AVG,
        AUCTION_SUCCESS_RATIO
    }
    
    public double getTypeValue()
    {
        return this.value;
    }

    public String toString()
    {
        return (super.toString()+"\n"
                +"value:"+this.value+"\n"
                );
    }
   
}

