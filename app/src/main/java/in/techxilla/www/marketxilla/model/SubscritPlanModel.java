package in.techxilla.www.marketxilla.model;

public class SubscritPlanModel {
     String sPlan;
    String sPlanName;
    String sDetails;
    String sStock_Future;
    String sStock_Options;
    String sIndex_Future;
    String sIndex_Options;
    String sCommodity;
    String sTelegram_Updates;
    String sDuration;

    public SubscritPlanModel(String sPlan, String sPlanName, String sDetails, String sStock_Future, int image_green,String sStock_Options, String sIndex_Future, String sIndex_Options, String sCommodity, String sTelegram_Updates, String sDuration,  int image_red) {
        this.sPlan = sPlan;
        this.sPlanName = sPlanName;
        this.sDetails = sDetails;
        this.sStock_Future = sStock_Future;
        this.sStock_Options = sStock_Options;
        this.sIndex_Future = sIndex_Future;
        this.sIndex_Options = sIndex_Options;
        this.sCommodity = sCommodity;
        this.sTelegram_Updates = sTelegram_Updates;
        this.sDuration = sDuration;
        this.image_green = image_green;
        this.image_red = image_red;
    }

    int image_green;


    public int getImage_green() {
        return image_green;
    }

    public void setImage_green(int image_green) {
        this.image_green = image_green;
    }

    public int getImage_red() {
        return image_red;
    }

    public void setImage_red(int image_red) {
        this.image_red = image_red;
    }

    int image_red;
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

    public String getsStock_Future() {
        return sStock_Future;
    }

    public void setsStock_Future(String sStock_Future) {
        this.sStock_Future = sStock_Future;
    }

    public String getsStock_Options() {
        return sStock_Options;
    }

    public void setsStock_Options(String sStock_Options) {
        this.sStock_Options = sStock_Options;
    }

    public String getsIndex_Future() {
        return sIndex_Future;
    }

    public void setsIndex_Future(String sIndex_Future) {
        this.sIndex_Future = sIndex_Future;
    }

    public String getsIndex_Options() {
        return sIndex_Options;
    }

    public void setsIndex_Options(String sIndex_Options) {
        this.sIndex_Options = sIndex_Options;
    }

    public String getsCommodity() {
        return sCommodity;
    }

    public void setsCommodity(String sCommodity) {
        this.sCommodity = sCommodity;
    }

    public String getsTelegram_Updates() {
        return sTelegram_Updates;
    }

    public void setsTelegram_Updates(String sTelegram_Updates) {
        this.sTelegram_Updates = sTelegram_Updates;
    }

    public String getsDuration() {
        return sDuration;
    }

    public void setsDuration(String sDuration) {
        this.sDuration = sDuration;
    }




}
