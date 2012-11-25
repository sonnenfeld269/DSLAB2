package BillingServer;

/**
 *
 * @author Dave
 */
public class InitPriceStep {

    PriceStep level_1 = new PriceStep(0,100,3,0.07);
    PriceStep level_2 = new PriceStep(100,200,5,0.065);
    PriceStep level_3 = new PriceStep(200,500,7,0.06);
    PriceStep level_4 = new PriceStep(500,1000,10,0.055);
    
    @Override
    public String toString(){
        return level_1 + "\n" + level_2 + "\n" + level_3 + "\n" + level_4;
    }
}
