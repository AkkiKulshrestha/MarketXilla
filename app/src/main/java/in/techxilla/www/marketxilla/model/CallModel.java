package in.techxilla.www.marketxilla.model;

public class CallModel {

    private String id;
    private String stock_name;
    private String date;
    private String is_buy_sell;
    private String buy_sell_above_below;
    private String stop_loss;
    private String target1;
    private String target2;
    private String target3;
    private String ce_pe;
    private String strike;
    private String performance_for;
    private String performance_for_id;
    private String profit_loss;
    private String buy_sell_closing_price;
    private String is_active_performance;
    private String is_call_for_paid_customer;

    public CallModel() {
    }

    public String getProfit_loss() {
        return profit_loss;
    }

    public void setProfit_loss(String profit_loss) {
        this.profit_loss = profit_loss;
    }

    public String getBuy_sell_closing_price() {
        return buy_sell_closing_price;
    }

    public void setBuy_sell_closing_price(String buy_sell_closing_price) {
        this.buy_sell_closing_price = buy_sell_closing_price;
    }

    public String getIs_active_performance() {
        return is_active_performance;
    }

    public void setIs_active_performance(String is_active_performance) {
        this.is_active_performance = is_active_performance;
    }

    public String getIs_call_for_paid_customer() {
        return is_call_for_paid_customer;
    }

    public void setIs_call_for_paid_customer(String is_call_for_paid_customer) {
        this.is_call_for_paid_customer = is_call_for_paid_customer;
    }

    public String getPerformance_for() {
        return performance_for;
    }

    public void setPerformance_for(String performance_for) {
        this.performance_for = performance_for;
    }

    public String getPerformance_for_id() {
        return performance_for_id;
    }

    public void setPerformance_for_id(String performance_for_id) {
        this.performance_for_id = performance_for_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStock_name() {
        return stock_name;
    }

    public void setStock_name(String stock_name) {
        this.stock_name = stock_name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIs_buy_sell() {
        return is_buy_sell;
    }

    public void setIs_buy_sell(String is_buy_sell) {
        this.is_buy_sell = is_buy_sell;
    }

    public String getBuy_sell_above_below() {
        return buy_sell_above_below;
    }

    public void setBuy_sell_above_below(String buy_sell_above_below) {
        this.buy_sell_above_below = buy_sell_above_below;
    }

    public String getStop_loss() {
        return stop_loss;
    }

    public void setStop_loss(String stop_loss) {
        this.stop_loss = stop_loss;
    }

    public String getTarget1() {
        return target1;
    }

    public void setTarget1(String target1) {
        this.target1 = target1;
    }

    public String getTarget2() {
        return target2;
    }

    public void setTarget2(String target2) {
        this.target2 = target2;
    }

    public String getTarget3() {
        return target3;
    }

    public void setTarget3(String target3) {
        this.target3 = target3;
    }

    public String getCe_pe() {
        return ce_pe;
    }

    public void setCe_pe(String ce_pe) {
        this.ce_pe = ce_pe;
    }

    public String getStrike() {
        return strike;
    }

    public void setStrike(String strike) {
        this.strike = strike;
    }
}
