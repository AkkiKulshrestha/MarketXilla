package in.techxilla.www.marketxilla.model;

public class SubscritPlanModel {
    int image_green;
    private String sPlan;
    private String sPlanName;
    private String sDetails;
    private boolean sStock_Future;
    private boolean sStock_Options;
    private boolean sIndex_Future;
    private boolean sIndex_Options;
    private boolean sCommodity;
    private boolean sTelegram_Updates;
    private String Amount1Month;
    private String Amount2Months;
    private String Amount3Months;
    private String Id;

    public SubscritPlanModel(final String sPlan,
                             final String sPlanName,
                             final String sDetails,
                             final boolean sStock_Future,
                             final boolean sStock_Options,
                             final boolean sIndex_Future,
                             final boolean sIndex_Options,
                             final boolean sCommodity,
                             final boolean sTelegram_Updates,
                             final String Amount1Month,
                             final String Amount2Months,
                             final String Amount3Months,
                             final String Id) {

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
        this.Id = Id;
    }

    public SubscritPlanModel() {
    }

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

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
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
