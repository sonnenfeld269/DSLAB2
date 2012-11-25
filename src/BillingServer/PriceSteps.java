package BillingServer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Dave
 */
public class PriceSteps implements Serializable {

    ArrayList<PriceStep> priceSteps = new ArrayList<PriceStep>();

    public PriceSteps() {
        initDefaultPriceSteps();
    }

    public void createPriceStep(double min_value, double max_value, double fee_fixed, double fee_variable) {
        PriceStep ps = new PriceStep(min_value, max_value, fee_fixed, fee_variable);
        priceSteps.add(ps);
    }

    public void deletePriceStep(double min_value, double max_value) {
        for (PriceStep ps : priceSteps) {
            if (ps.getMin_value() == min_value && ps.getMax_value() == max_value) {
                priceSteps.remove(ps);
            }
        }
    }

    private void initDefaultPriceSteps() {

        PriceStep level_1 = new PriceStep(0, 100, 3, 0.07);
        PriceStep level_2 = new PriceStep(100, 200, 5, 0.065);
        PriceStep level_3 = new PriceStep(200, 500, 7, 0.06);
        PriceStep level_4 = new PriceStep(500, 1000, 10, 0.055);

        priceSteps.add(level_1);
        priceSteps.add(level_2);
        priceSteps.add(level_3);
        priceSteps.add(level_4);
    }

    @Override
    public String toString() {
        String steps = "";
        for (PriceStep ps : priceSteps) {
            steps = steps + ps.toString() + "\n";
        }
        return steps;
    }
}
