package in.techxilla.www.marketxilla.model;

public class SubscriptionPlanModel {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getSubscribed_on() {
        return subscribed_on;
    }

    public void setSubscribed_on(String subscribed_on) {
        this.subscribed_on = subscribed_on;
    }

    public String getSubscribed_till() {
        return subscribed_till;
    }

    public void setSubscribed_till(String subscribed_till) {
        this.subscribed_till = subscribed_till;
    }

    public String getPayment_detail() {
        return payment_detail;
    }

    public void setPayment_detail(String payment_detail) {
        this.payment_detail = payment_detail;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getPlan_amount() {
        return plan_amount;
    }

    public void setPlan_amount(String plan_amount) {
        this.plan_amount = plan_amount;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String id;
    String user_id;
    String plan_id;
    String subscribed_on;
    String subscribed_till;
    String payment_detail;
    String plan_name;
    String plan_amount;
    String package_id;
    String status;

}
