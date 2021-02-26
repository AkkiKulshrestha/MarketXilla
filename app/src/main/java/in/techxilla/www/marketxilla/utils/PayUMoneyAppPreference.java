package in.techxilla.www.marketxilla.utils;

public class PayUMoneyAppPreference {

    private String paymentAmount;
    private String userId;
    private String userFullName;
    private String userMobile;
    private String userEmail;
    private String productInfo;
    private String planId;
    private boolean isOverrideResultScreen = false;

    public static String USER_EMAIL = "user_email";
    public static String USER_MOBILE = "user_mobile";
    public static final String PHONE_PATTERN = "^[1-9]\\d{9}$";
    public static final long MENU_DELAY = 300;
    public static String USER_DETAILS = "user_details";
    public static int selectedTheme = -1;

    private boolean isDisableWallet, isDisableSavedCards, isDisableNetBanking, isDisableThirdPartyWallets, isDisableExitConfirmation;

    boolean isDisableWallet() {
        return isDisableWallet;
    }

    void setDisableWallet(boolean disableWallet) {
        isDisableWallet = disableWallet;
    }

    boolean isDisableSavedCards() {
        return isDisableSavedCards;
    }

    void setDisableSavedCards(boolean disableSavedCards) {
        isDisableSavedCards = disableSavedCards;
    }

    boolean isDisableNetBanking() {
        return isDisableNetBanking;
    }

    void setDisableNetBanking(boolean disableNetBanking) {
        isDisableNetBanking = disableNetBanking;
    }
    boolean isDisableThirdPartyWallets() {
        return isDisableThirdPartyWallets;
    }

    void setDisableThirdPartyWallets(boolean disableThirdPartyWallets) {
        isDisableThirdPartyWallets = disableThirdPartyWallets;
    }
    boolean isDisableExitConfirmation() {
        return isDisableExitConfirmation;
    }

    void setDisableExitConfirmation(boolean disableExitConfirmation) {
        isDisableExitConfirmation = disableExitConfirmation;
    }

    public boolean isOverrideResultScreen() {
        return isOverrideResultScreen;
    }

    public void setOverrideResultScreen(boolean overrideResultScreen) {
        isOverrideResultScreen = overrideResultScreen;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(String productInfo) {
        this.productInfo = productInfo;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
