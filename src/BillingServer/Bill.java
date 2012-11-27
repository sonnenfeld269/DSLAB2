package BillingServer;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Dave
 */
public class Bill implements Serializable{

    private ArrayList<User> users = new ArrayList<User>();
    String billReport = "";
    double fee_fixed;
    double fee_variable;
    double fee_total;

    public void createBill(String user) {
        System.out.println("DEBUG: THE USER IS: " + getUserByName(user));
        double strike_price;
        for (Auction a : getUserByName(user).getAuctions()) {
            strike_price = a.getPrice();
            if (calculateFee(strike_price)) {
                fee_total = fee_fixed + fee_variable;
                billReport = billReport + "StrikePrice: " + strike_price + " FeeFixed: " + fee_fixed + " FeeVariable: " + fee_variable + " FeeTotal: " + fee_total + "\n";
            }
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public User getUserByName(String user) {
        for (User u : users) {
            if (u.getName().equals(user)) {
                return u;
            }
        }
        return null;
    }

    private boolean calculateFee(double strike_price) {
        PriceSteps ps = BillingsServer.ips;
        for (PriceStep pS : ps.priceSteps) {
            if (strike_price >= pS.getMin_value() && strike_price <= pS.getMax_value()) {
                fee_fixed = pS.getFee_fixed();
                fee_variable = pS.getFee_variable();
                System.out.println("Fee was set successfully.");
                return true;
            } else {
                System.out.println("Strike Price is not inside Price Configuration.");
                return false;
            }
        }
        return false;
    }
    
    public String toString(){
        return billReport;
    }
}
