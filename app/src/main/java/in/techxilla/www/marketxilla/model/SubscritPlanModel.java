package in.techxilla.www.marketxilla.model;

public class SubscritPlanModel {
     String sPlan;
    String sPlanName;
    String sDetails;
    boolean sStock_Future;
    boolean sStock_Options;
    boolean sIndex_Future;
    boolean sIndex_Options;
    boolean sCommodity;
    boolean sTelegram_Updates;
    String Amount1Month;
    String Amount2Months;
    String Amount3Months;
    String package_id;

    public SubscritPlanModel(String sPlan, String sPlanName, String sDetails,
                             boolean sStock_Future, boolean sStock_Options, boolean sIndex_Future, boolean sIndex_Options, boolean sCommodity, boolean sTelegram_Updates,
                             String Amount1Month,  String Amount2Months,String Amount3Months,String package_id ) {
        this.sPlan = sPlan;
        this.sPlanName = sPlanName;
        this.sDetails = sDetails;
        this.sStock_Future = sStock_Future;
        this.sStock_Options = sStock_Options;
        this.sIndex_Future = sIndex_Future;
        this.sIndex_Options = sIndex_Options;
        this.sCommodity = sCommodity;
        this.sTelegram_Updates = sTelegram_Updates;
        this.Amount1Month = Amount1Month;
        this.Amount2Months = Amount2Months;
        this.Amount3Months = Amount3Months;
        this.package_id = package_id;

    }

    public  SubscritPlanModel(){

    }

    int image_green;

    public String getAmount1Month() {
        return Amount1Month;
    }

    public void setAmount1Month(String amount1Month) {
        Amount1Month = amount1Month;
    }

    public String getAmount2Months() {
        return Amount2Months;
    }

    public void setAmount2Months(String amount2Months) {
        Amount2Months = amount2Months;
    }

    public String getAmount3Months() {
        return Amount3Months;
    }

    public void setAmount3Months(String amount3Months) {
        Amount3Months = amount3Months;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }


    public String getsPlan() {
        return sPlan;
    }

    public void setsPlan(String sPlan) {
        this.sPlan = sPlan;
    }

    public String getsPlanName() {
        return sPlanName;
    }

    public void setsPlanName(String sPlanName) {
        this.sPlanName = sPlanName;
    }

    public String getsDetails() {
        return sDetails;
    }

    public void setsDetails(String sDetails) {
        this.sDetails = sDetails;
    }

    public boolean issStock_Future() {
        return sStock_Future;
    }

    public void setsStock_Future(boolean sStock_Future) {
        this.sStock_Future = sStock_Future;
    }

    public boolean issStock_Options() {
        return sStock_Options;
    }

    public void setsStock_Options(boolean sStock_Options) {
        this.sStock_Options = sStock_Options;
    }

    public boolean issIndex_Future() {
        return sIndex_Future;
    }

    public void setsIndex_Future(boolean sIndex_Future) {
        this.sIndex_Future = sIndex_Future;
    }

    public boolean issIndex_Options() {
        return sIndex_Options;
    }

    public void setsIndex_Options(boolean sIndex_Options) {
        this.sIndex_Options = sIndex_Options;
    }

    public boolean issCommodity() {
        return sCommodity;
    }

    public void setsCommodity(boolean sCommodity) {
        this.sCommodity = sCommodity;
    }

    public boolean issTelegram_Updates() {
        return sTelegram_Updates;
    }

    public void setsTelegram_Updates(boolean sTelegram_Updates) {
        this.sTelegram_Updates = sTelegram_Updates;
    }
}
