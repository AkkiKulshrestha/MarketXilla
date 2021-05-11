package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.base.models.PaymentMode;
import com.payu.base.models.PaymentType;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.adaptor.ReviewOrderRecyclerViewAdapter;
import in.techxilla.www.marketxilla.fragment.HolidayFragment;
import in.techxilla.www.marketxilla.fragment.HomeFragment;
import in.techxilla.www.marketxilla.fragment.PackageFragment;
import in.techxilla.www.marketxilla.fragment.PaidUserFragment;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.MyValidator;
import in.techxilla.www.marketxilla.utils.PayUMoneyAppPreference;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastError;
import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastWarning;
import static in.techxilla.www.marketxilla.utils.CommonMethods.md5;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class NewDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    /*PAYU CONTANTS*/

    private final String MERCHANT_ID = "8025939";
    private final String MERCHANT_NAME = "Marketxilla";
    private final String MERCHANT_EMAIL = "marketxilla@gmail.com";
    private final String SURL = "https://payuresponse.firebaseapp.com/success";
    private final String FURL = "https://payuresponse.firebaseapp.com/failure";

    private final boolean isProd = false;

    // PROD CRED
    private final String PROD_KEY = "hVyMSm";
    private final String PROD_SALT = "kE3fB8BV";
    private final String PROD_SALTV2 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCgnUYNokw7luwyWC/YeNJBVYS/5v++p6TSULw/hcyV37ICsdOm1Q4stgVPl0brVOvHCPGdcXQVk74lg/wkuh8qoGzjD6qpnqFxy4T+4YjfWJooTAIe6nsYRUG+fg095Ka3rV67D+ix19bKn8jUNTzGayV2Srv8lWe6XL8mNURwEiPpFTWSTZH3RJTTUtHoh8EObbfUrx5y2yyurcyK4gJ9LKAhSyzMIPR+YKlCc8UjM1UvSL0TcxMXYcI8pWFBGOYFn59YDH+VN1DplWiUgKWxe11mv3+J7ZLTb/c9gUbSyyXCFG9NcsgPJL1N0iqhp46/qFdufv/BGi+mdaxazlrrAgMBAAECggEALvdOcNYwrIwpbV9UPly2PtyfAO6vgjTLgaSSJ5EcjgbnqtoNJg/PIUvoqal214HxoDYBUSTH6TdWtumtZZ/3VSOsM4l1QjxcwUXeEhopSAZErdvsEtZGQIaf/vAlNyARkExmExVH2hlfecjXlRYsO2lBfyHDbszRlDFiw+13ob11lRhEXAOBPDoarhCMqAWX6MuTc92OvWcAp8w8Vwv/hIzOmg2yGv2Dhs36YPQsR9nHSqIbt330QkLMMaVEj8A0IQakouZ3uuoBC0KejfgLQCFGdeKbDY7QtDWpAfKZVWFwCZQAi3q1n3rj+jQIQ2KFpMEzvRaECINZaubMm3GRQQKBgQDRKlTQhmVqGntudSXzvP88Xqb2s6nNK9OMwEmWUkdqhwa/GYP5P9MQYL+hap9IS7273dAmACAYjHWuchch4WTqDMTTWg7hJjhxRylJ1zZb1Rn6qTNnBSp/8a8UNeL7nZc6fZowqcY5Epxeak6ZeJNiaRgK90XF/XWrPMOY3ci5mQKBgQDEk+00vSXNIsrf1VehuqbQ54OxKOpaKUwnBvV+5njXwPouhjKnb6tCXkNz5mIfmY5L8OXiPC2vdzfCg08qoMZd8+bZwePUK1NaP1xwl3pcxiUhq2tGanKgR6xMdZb7pUTyA2UcLoEaNHiCFjjjME79YTZdDY4rgqz8xL5Rp6+zIwKBgE5PtAuSlfvAyH/Vmo0EMOeQZKCvKZ7ojr7+6049pgFrZoo76l5yl/pkzrqHqfUubm4dISZpG5s1U4YpryF/OwIqH7Ml37ZKUg2PYBUGX5LIWX6wxM8Ibx4SBcPiXQZpvUon5ofbuJx7rFHpKV5qd3v77wWECPqU5+5hxLXCK7nZAoGAEG/uHcLTLlwCasUEFtnsqPsy39V0AyYA4CKM1Jeg8ymHwewmwCluQJZxPXe+LLZCV8dE8a3mhA2L9A/WxtG6xJBodTzpOAyHY7x4llGUQb2vzSjwR2sPOqfDmIEcpt4i7bmq8rhQw0gv63DAQP8BG97NFOrVQH4kyN4Kq/lBj9ECgYEAsgmADFPambDl9SgX2BIQaZ9d/ef/EKXg0dC7tQDeqYlw4cNcNvecFJmjHi/CFQIqeGwqk2oeGcfVnTbGYUJAjKgnZzamye3q3hblqhGdoR/KsCGaU3f43N7XMTMteKCiIa9oG547gEuLZYND9y+OlSwNimr2vdXGcQXxhlWdWE0=";

    //TEST CRED
    private final String TEST_KEY = "gtKFFx";
    private final String TEST_SALT = "eCwWELxi";


    String StrMemberId, StrMemberName, StrMemberEmailId, StrMemberMobile, StrDeviceUniqueId;
    ProgressDialog myDialog;
    int notifications_count = 0;
    ImageView iv_notification;
    ViewGroup viewGroup;
    int loader_position = 0;
    Fragment fragment = null;
    TextView badge_new_notification;
    boolean isPaidUser = false, isTrailApplicable = true;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mStackCount = 30;
    private Dialog dialogResetPassword, dialogBankDetails;
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private String mUserId, mPlan_id, mSubscripbed_on, mPackage_id, msubscribed_till;
    private JSONObject transactionObj, bankObj;
    private String KEY = "", SALT = "";
    private long mLastClickTime = 0;
    private ReviewOrderRecyclerViewAdapter reviewOrderAdapter;

    /**
     * Hash Should be generated from your sever side only.
     * <p>
     * Do not use this, you may use this only for testing.
     * This should be done from server side..
     * Do not keep salt anywhere in app.
     */
    private static String calculateHash(String hashString) {
        try {
            StringBuilder hash = new StringBuilder();
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }

            return hash.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_dashboard);
        if (getIntent().getExtras() != null) {
            loader_position = getIntent().getExtras().getInt("load_fragment");
        } else {
            loader_position = 0;
        }
        setBottomNavigation();
        viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        myDialog = new ProgressDialog(this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        initUI();
        navigationView();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void initUI() {
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        StrMemberName = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberName");
        StrMemberEmailId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberEmailId");
        StrMemberMobile = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberMobile");
        StrDeviceUniqueId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "DeviceId");

        PayUMoneyAppPreference appPreference = new PayUMoneyAppPreference();
        appPreference.setUserEmail(StrMemberEmailId);
        appPreference.setUserId(StrMemberId);
        appPreference.setUserMobile(StrMemberMobile);
        appPreference.setUserFullName(StrMemberName);

        iv_notification = findViewById(R.id.iv_notification);
        iv_notification.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
        });
        getPlanDetail();
        fetchNotificationList();
        getBankDetail();
    }

    @Override
    protected void onResume() {
        validateUser();
        super.onResume();
    }

    public void validateUser() {
        final String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL + "getUserDeviceAuthentication.php?_" + Uiid_id + "&user_id=" + StrMemberId + "&device_id=" + StrDeviceUniqueId;
        Log.d("URL --->", get_bank_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            Log.d("Response", "" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            boolean status = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");
                            if (!status) {
                                PopUpDisplayMessage(message);
                            }/*else {
                                CommonMethods.DisplayToastSuccess(getApplicationContext(),message);
                            }*/

                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        CommonMethods.DisplayToastWarning(getApplicationContext(), "Something goes wrong. Please try again");
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastWarning(getApplicationContext(), "No Internet Connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void PopUpDisplayMessage(String message) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.pop_up_display_message);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView title = dialog.findViewById(R.id.title);
        TextView tv_message = dialog.findViewById(R.id.message);
        title.setText("DEVICE NOT FOUND");
        tv_message.setText(message);

        TextView tv_ok = dialog.findViewById(R.id.tv_ok);
        dialog.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                logout();
            }
        });
    }

    public void setBottomNavigation() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Menu nav_Menu = navigation.getMenu();
        nav_Menu.findItem(R.id.navigation_paid_user).setVisible(isPaidUser);
        if (loader_position == 0) {
            navigation.setSelectedItemId(R.id.navigation_home);
            fragment = new HomeFragment();
        } else if (loader_position == 1) {
            navigation.setSelectedItemId(R.id.navigation_holiday);
            fragment = new HolidayFragment();
        } else if (loader_position == 2) {
            navigation.setSelectedItemId(R.id.navigation_package);
            fragment = new PackageFragment();
        } else {
            navigation.setSelectedItemId(R.id.navigation_home);
            fragment = new HomeFragment();
        }
        loadFragment(fragment);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    private void getPlanDetail() {
        isTrailApplicable = true;
        UtilitySharedPreferences.setPrefs(getApplicationContext(), "isTrailApplicable", "" + isTrailApplicable);
        String Uiid_id = UUID.randomUUID().toString();
        String StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        final String get_plan_details_info = ROOT_URL + "get_user_subscription_details.php?" + Uiid_id + "&user_id=" + StrMemberId;
        Log.d("URL --->", get_plan_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_plan_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response", "" + response);
                            JSONObject obj = new JSONObject(response);
                            boolean status = obj.getBoolean("status");
                            String Message = obj.getString("message");
                            JSONArray m_jArry = obj.getJSONArray("data");
                            mStackCount = m_jArry.length();
                            if (mStackCount == 0) {
                                isPaidUser = false;
                                isTrailApplicable = true;
                                UpdatePaymentDetails("0");
                                UtilitySharedPreferences.setPrefs(getApplicationContext(), "isTrailApplicable", "" + isTrailApplicable);
                                Log.d("PaidUser", "No");
                            } else {
                                for (int i = 0; i < m_jArry.length(); i++) {
                                    JSONObject jo_data = m_jArry.getJSONObject(i);
                                    String StrPlanId = jo_data.getString("plan_id");
                                    String plan_name = jo_data.getString("plan_name");
                                    String subscribed_till = jo_data.getString("subscribed_till");
                                    SimpleDateFormat sdf, sdf2, sdf21;
                                    Date newSubscriptedTilldate, currentdate2, newSubscriptedOndate;

                                    if (StrPlanId.equalsIgnoreCase("7")) {
                                        isTrailApplicable = false;
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(), "isTrailApplicable", "" + isTrailApplicable);
                                    }

                                    if (!subscribed_till.equalsIgnoreCase("")) {
                                        try {
                                            sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            newSubscriptedTilldate = sdf.parse(subscribed_till);
                                            sdf21 = new SimpleDateFormat("dd/MM/yyyy");
                                            String mSubscribed_till = sdf21.format(newSubscriptedTilldate);

                                            if (i == 0) {
                                                isPaidUser = !CommonMethods.isDateExpired(mSubscribed_till);
                                                UpdatePaymentDetails(StrPlanId);
                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            setBottomNavigation();
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        //Log.d("Vollley Err", volleyError.toString());
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastWarning(getApplicationContext(), "No Internet Connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void UpdatePaymentDetails(String mPlan_id) {

        String Uiid_id = UUID.randomUUID().toString();
        String URL_ResetPassword = ROOT_URL + "add_user_payment_details.php?_" + Uiid_id;
        try {
            Log.d("URL_ResetPassword", URL_ResetPassword);
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ResetPassword,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                Log.d("mainResponse", response);
                                try {
                                    JSONObject jObj = new JSONObject(response);

                                } catch (JSONException e) {
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", StrMemberId);
                        params.put("plan_id", mPlan_id);

                        Log.d("updatePaymentDetails", params.toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                if (myDialog != null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(getApplicationContext(), "Please check your internet connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void navigationView() {
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        StrMemberName = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberName");
        StrMemberEmailId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberEmailId");
        StrMemberMobile = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberMobile");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView1 = findViewById(R.id.nav_view1);
        NavigationView navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawer.setDrawerListener(mDrawerToggle);
        View hView = navigationView.getHeaderView(0);

        TextView nav_header_userName = hView.findViewById(R.id.nav_header_userName);
        TextView nav_user_email = hView.findViewById(R.id.nav_Email);
        if (StrMemberName != null && !StrMemberName.equalsIgnoreCase("")) {
            nav_header_userName.setText(StrMemberName.toUpperCase());
        }

        if (StrMemberMobile != null && !StrMemberMobile.equalsIgnoreCase("")) {
            nav_user_email.setText("Mobile No.: " + StrMemberMobile);
        }

        ImageView iv_facebook = navigationView1.findViewById(R.id.iv_facebook);
        ImageView iv_twitter = navigationView1.findViewById(R.id.iv_twitter);
        ImageView iv_telegram = navigationView1.findViewById(R.id.iv_telegram);
        ImageView iv_linked_in = navigationView1.findViewById(R.id.iv_linked_in);

        iv_facebook.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getResources().getString(R.string.facebook_page)));
            startActivity(i);
        });

        iv_twitter.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getResources().getString(R.string.twitter_page)));
            startActivity(i);
        });

        iv_telegram.setOnClickListener(view -> {
            Intent telegram = new Intent(Intent.ACTION_VIEW);
            telegram.setData(Uri.parse(getResources().getString(R.string.telegram)));
            startActivity(telegram);
        });

        iv_linked_in.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getResources().getString(R.string.linked_in_page)));
            startActivity(i);
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;

           /* case R.id.navigation_market:
                fragment = new MarketFragment();
                break;*/

            case R.id.navigation_holiday:
                fragment = new HolidayFragment();
                break;

            case R.id.navigation_package:
                fragment = new PackageFragment();
                break;

            case R.id.navigation_paid_user:
                fragment = new PaidUserFragment();
                break;

            case R.id.nav_my_subscription:
                Intent intent_subscription = new Intent(NewDashboard.this, MySubscriptionPlanActivity.class);
                startActivity(intent_subscription);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
                break;

            case R.id.nav_calc:
                Intent intent_calc = new Intent(NewDashboard.this, CalculatorActivity.class);
                startActivity(intent_calc);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                finish();
                break;

            case R.id.nav_reset_password:
                pop_up_reset_password();
                break;

            case R.id.nav_about_us:
                Intent intent_about_us = new Intent(Intent.ACTION_VIEW);
                intent_about_us.setData(Uri.parse("https://marketxilla.com/about-us"));
                startActivity(intent_about_us);
                break;

            case R.id.nav_contact_us:
                Intent intent_contact_us = new Intent(Intent.ACTION_VIEW);
                intent_contact_us.setData(Uri.parse("https://marketxilla.com/contact-us"));
                startActivity(intent_contact_us);
                break;

            case R.id.nav_disclaimer:
                Intent int_disclaimer = new Intent(Intent.ACTION_VIEW);
                int_disclaimer.setData(Uri.parse("https://marketxilla.com/disclaimer"));
                startActivity(int_disclaimer);
                break;

            case R.id.nav_terms_n_condition:
                Intent intent_tnc = new Intent(Intent.ACTION_VIEW);
                intent_tnc.setData(Uri.parse("https://marketxilla.com/terms-and-conditions"));
                startActivity(intent_tnc);
                break;

            case R.id.nav_privacy_policy:
                Intent intent_pp = new Intent(Intent.ACTION_VIEW);
                intent_pp.setData(Uri.parse("https://marketxilla.com/privacy-policy"));
                startActivity(intent_pp);
                break;

            case R.id.nav_rate_us:
                try {
                    Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                    Log.d("MarketUri", "" + marketUri);
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                } catch (ActivityNotFoundException e) {
                    Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Log.d("MarketUri", "" + marketUri);
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                    startActivity(marketIntent);
                }
                break;

            case R.id.nav_share:
                Context context = getApplicationContext();
                final String appPackageName = context.getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Greetings, \n\n" +
                        "Welcome To Marketxilla, \n" +
                        "\"An Intra Day Research Service Providing App For NSE Future And Options, and Commodity\" \n\n\n\n" +
                        "MarketXilla App is Specially Designed For Intra Day Traders. Our Research is Based On Dynamic Data And Technical Analysis. For Consistent Profit and Hassle-Free Trading, You Can Study Our Live Research Recommendations.\n" +
                        "MarketXilla App Is Now Available for Android On Google Play Store. Download MarketXilla -  https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
                context.startActivity(sendIntent);
                break;

            case R.id.nav_logout:
                logout();
                break;
        }

        return loadFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.exit)
                .setMessage(R.string.strExit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton(R.string.no, null).show();
    }

    private void fetchNotificationList() {
        String Uiid_id = UUID.randomUUID().toString();
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "get_notifications_list.php?_" + Uiid_id;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, response -> {
                try {
                    Log.d("URL Response", "--> " + response);
                    JSONObject jsonresponse = new JSONObject(response);
                    boolean status = jsonresponse.getBoolean("status");
                    if (status) {
                        String result = jsonresponse.getString("data");
                        JSONArray resultArry = new JSONArray(result);
                        notifications_count = resultArry.length();
                        SetNotificationBadge();
                    } else {
                        notifications_count = 0;
                        SetNotificationBadge();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, volleyError -> {
                volleyError.printStackTrace();
                DisplayToastError(getApplicationContext(), "Something goes wrong. Please try again");
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            postrequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
            requestQueue.add(postrequest);
        } else {
            DisplayToastWarning(getApplicationContext(), "No Internet Connection");
        }
    }

    private void SetNotificationBadge() {
        badge_new_notification = findViewById(R.id.badge_new_notification);
        badge_new_notification.setText(String.format("%02d", notifications_count));
    }

    private void pop_up_reset_password() {

        dialogResetPassword = new Dialog(this);
        dialogResetPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogResetPassword.setCanceledOnTouchOutside(false);
        dialogResetPassword.setCancelable(true);
        dialogResetPassword.setContentView(R.layout.custom_dialog_for_reset_password);
        dialogResetPassword.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogResetPassword.show();

        TextView tv_registered_mobile_no = dialogResetPassword.findViewById(R.id.tv_registered_mobile_no);
        tv_registered_mobile_no.setText(StrMemberEmailId);

        TextInputLayout etCurrentPasswordLayout = dialogResetPassword.findViewById(R.id.etCurrentPasswordLayout);
        etCurrentPasswordLayout.setVisibility(View.VISIBLE);
        etCurrentPassword = dialogResetPassword.findViewById(R.id.etCurrentPassword);
        etNewPassword = dialogResetPassword.findViewById(R.id.etNewPassword);
        etConfirmPassword = dialogResetPassword.findViewById(R.id.etConfirmPassword);

        ImageView iv_close = dialogResetPassword.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(v -> dialogResetPassword.dismiss());


        Button btn_reset = dialogResetPassword.findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(view -> {
            if (validateResetPassword()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(etConfirmPassword.getWindowToken(), 0);
                }
                if ((etNewPassword.getText() != null && etNewPassword.getText().toString().length() != 0) && (etConfirmPassword.getText() != null && etConfirmPassword.getText().toString().length() != 0)) {
                    String StrNewPassword = etNewPassword.getText().toString();
                    String StrConfirmPassword = etConfirmPassword.getText().toString();
                    if (StrNewPassword.equalsIgnoreCase(StrConfirmPassword)) {
                        ResetPasswordApi(StrNewPassword, StrConfirmPassword);
                    } else {
                        etConfirmPassword.setError("New Password & Confirm Password does not Match.");
                    }

                }
            }
        });
    }

    private boolean validateResetPassword() {
        boolean result = true;

        if (!MyValidator.isValidPassword(etCurrentPassword)) {
            etCurrentPassword.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid Current Password");
            result = false;
        }

        if (!MyValidator.isValidPassword(etNewPassword)) {
            etNewPassword.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Valid New Password");
            result = false;
        }

        if (!MyValidator.isValidPassword(etConfirmPassword)) {
            etConfirmPassword.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Re-Enter New Password");
            result = false;
        }

        return result;
    }

    private void ResetPasswordApi(final String strNewPassword, String strConfirmPassword) {

        String StrCurrentPassword = etCurrentPassword.getText().toString();
        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_ResetPassword = ROOT_URL + "reset_password.php?_" + Uiid_id;
        try {
            Log.d("URL_ResetPassword", URL_ResetPassword);
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ResetPassword,
                        response -> {
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            Log.d("mainResponse", response);
                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean status = jObj.getBoolean("status");
                                if (status) {
                                    String message = jObj.getString("message");
                                    CommonMethods.DisplayToastSuccess(getApplicationContext(), message);
                                    if (dialogResetPassword != null && dialogResetPassword.isShowing()) {
                                        dialogResetPassword.dismiss();
                                    }
                                    logout();
                                } else {
                                    String message = jObj.getString("message");
                                    CommonMethods.DisplayToastError(getApplicationContext(), message);
                                }

                            } catch (JSONException e) {
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                e.printStackTrace();
                            }

                        }, error -> {
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    VolleyLog.d("volley", "Error: " + error.getMessage());
                    error.printStackTrace();
                }) {
                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", StrMemberId);
                        params.put("current_password", md5(StrCurrentPassword));
                        params.put("new_password", md5(strNewPassword));
                        Log.d("ParrasResetPassword", params.toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                if (myDialog != null && myDialog.isShowing()) {
                    myDialog.dismiss();
                }
                CommonMethods.DisplayToast(getApplicationContext(), "Please check your internet connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout() {
        UtilitySharedPreferences.clearPref(getApplicationContext());
        Intent i = new Intent(getApplicationContext(), SplashActivity.class);
        startActivity(i);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }

    private void getBankDetail() {
        final String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL + "get_bank_details.php?_" + Uiid_id;
        Log.d("URL --->", get_bank_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, response -> {
                    try {
                        Log.d("Response", "" + response);
                        bankObj = new JSONObject(response);

                    } catch (Exception e) {
                        Log.d("Exception", e.toString());
                    }
                }, volleyError -> {
                    volleyError.printStackTrace();
                    CommonMethods.DisplayToastWarning(getApplicationContext(), "Something goes wrong. Please try again");
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastWarning(getApplicationContext(), "No Internet Connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void pop_up_bank_details() {

        dialogBankDetails = new Dialog(this);
        dialogBankDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBankDetails.setCanceledOnTouchOutside(false);
        dialogBankDetails.setCancelable(true);
        dialogBankDetails.setContentView(R.layout.popup_bank_details);
        dialogBankDetails.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBankDetails.show();

        TextView tv_beneficiary_name = dialogBankDetails.findViewById(R.id.tv_beneficiary_name);
        TextView tv_account_no = dialogBankDetails.findViewById(R.id.tv_account_no);
        TextView tv_bank_name = dialogBankDetails.findViewById(R.id.tv_bank_name);
        TextView tv_bank_ifsc = dialogBankDetails.findViewById(R.id.tv_bank_ifsc);
        TextView tv_bank_branch = dialogBankDetails.findViewById(R.id.tv_bank_branch);

        TextView tv_upi_id = dialogBankDetails.findViewById(R.id.tv_upi_id);
        TextView tv_upi_merchant_name = dialogBankDetails.findViewById(R.id.tv_upi_merchant_name);


        if (bankObj != null) {

            final boolean status;
            try {
                status = bankObj.getBoolean("status");
                if (status) {
                    final String data = bankObj.getString("data");
                    final JSONObject jobject = new JSONObject(data);
                    final String Id = jobject.getString("id");
                    final String beneficiary_name = jobject.getString("beneficiary_name");
                    final String bank_name = jobject.getString("bank_name");
                    final String account_no = jobject.getString("account_no");
                    final String ifsc_code = jobject.getString("ifsc_code");
                    final String StrUpiAccountId = jobject.getString("upi_id");
                    final String StrUPI_MerchantName = jobject.getString("upi_merchant_name");
                    final String branch = jobject.getString("branch");

                    tv_beneficiary_name.setText(beneficiary_name.toUpperCase());
                    tv_account_no.setText(account_no.toUpperCase());
                    tv_bank_name.setText(bank_name.toUpperCase());
                    tv_bank_ifsc.setText(ifsc_code.toUpperCase());
                    tv_bank_branch.setText(branch.toUpperCase());

                    tv_upi_id.setText(StrUpiAccountId);
                    tv_upi_merchant_name.setText(StrUPI_MerchantName.toUpperCase());

                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "UpiAccountId", StrUpiAccountId);
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "UpiMerchantName", StrUPI_MerchantName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        ImageView iv_close = dialogBankDetails.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(v -> dialogBankDetails.dismiss());


        Button btnOk = dialogBankDetails.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(view -> dialogBankDetails.dismiss());
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    public void PayUMoneySdk(String PlanId, String Amount, String Package_id, String PlanName) {

        mPlan_id = PlanId;
        mPackage_id = Package_id;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formDate1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        mSubscripbed_on = formDate1.format(new Date(calendar.getTimeInMillis()));

        if (mPackage_id.equalsIgnoreCase("1")) {
            calendar.add(Calendar.MONTH, 1);
            System.out.println("Current Date = " + calendar.getTime());
        } else if (mPackage_id.equalsIgnoreCase("2")) {
            calendar.add(Calendar.MONTH, 2);
            System.out.println("Current Date = " + calendar.getTime());
        } else if (mPackage_id.equalsIgnoreCase("3")) {
            calendar.add(Calendar.MONTH, 3);
            System.out.println("Current Date = " + calendar.getTime());
        }
        SimpleDateFormat formDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        msubscribed_till = formDate.format(new Date(calendar.getTimeInMillis())); //

        final String StrSubscriptionAmount = String.format("%.2f", Double.parseDouble(Amount));
        final String transactionId = String.valueOf(System.currentTimeMillis());
        final String MemberEmailId = UtilitySharedPreferences.getPrefs(this, "MemberEmailId");
        mUserId = UtilitySharedPreferences.getPrefs(this, "MemberId");
        final String MemberName = UtilitySharedPreferences.getPrefs(this, "MemberName");
        final String MemberMobile = UtilitySharedPreferences.getPrefs(this, "MemberMobile");

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
            return;
        mLastClickTime = SystemClock.elapsedRealtime();

        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, mSubscripbed_on);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, msubscribed_till);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, mUserId);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, mPlan_id);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, Package_id);

        if (isProd) {
            KEY = PROD_KEY;
            SALT = PROD_SALT;
        } else {
            KEY = TEST_KEY;
            SALT = TEST_SALT;
        }

        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(StrSubscriptionAmount)
                .setIsProduction(isProd)
                .setProductInfo(PlanName)
                .setKey(KEY)
                .setPhone(MemberMobile)
                .setTransactionId(transactionId)
                .setFirstName(MemberName)
                .setEmail(MemberEmailId)
                .setSurl(SURL)
                .setFurl(FURL)
                .setUserCredential(KEY + ":" + MERCHANT_EMAIL)
                .setAdditionalParams(additionalParams);
        PayUPaymentParams payUPaymentParams = builder.build();
        initUiSdk(payUPaymentParams);

        transactionObj = new JSONObject();
        try {
            transactionObj.put("transaction_id", transactionId);
            transactionObj.put("transaction_amount", StrSubscriptionAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initUiSdk(PayUPaymentParams payUPaymentParams) {
        PayUCheckoutPro.open(
                NewDashboard.this,
                payUPaymentParams,
                getCheckoutProConfig(),
                new PayUCheckoutProListener() {

                    @Override
                    public void onPaymentSuccess(@NotNull Object response) {
                        Log.d("Response", "" + response);
                        HashMap<String, Object> result = (HashMap<String, Object>) response;
                        try {
                            transactionObj.put("payuData", result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE));
                            transactionObj.put("merchantData", result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AddUserSubscriptionDetailApi(transactionObj.toString());
                    }

                    @Override
                    public void onPaymentFailure(@NotNull Object response) {
                        //CommonMethods.DisplayToastError(getApplicationContext(), "" + response);
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                        CommonMethods.DisplayToastError(getApplicationContext(), getResources().getString(R.string.transaction_cancelled_by_user));
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                        if (TextUtils.isEmpty(errorMessage))
                            errorMessage = getResources().getString(R.string.some_error_occurred);
                        CommonMethods.DisplayToastError(getApplicationContext(), errorMessage);
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        //For setting webview properties, if any. Check Customized Integration section for more details on this
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                        String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                            //Generate Hash from your backend here
                            String hash = calculateHash(hashData + SALT);
                            HashMap<String, String> dataMap = new HashMap<>();
                            dataMap.put(hashName, hash);
                            hashGenerationListener.onHashGenerated(dataMap);
                        }
                    }
                }
        );
    }

    private PayUCheckoutProConfig getCheckoutProConfig() {
        PayUCheckoutProConfig checkoutProConfig = new PayUCheckoutProConfig();
        checkoutProConfig.setPaymentModesOrder(getCheckoutOrderList());
        checkoutProConfig.setShowCbToolbar(true);
        checkoutProConfig.setAutoSelectOtp(false);
        checkoutProConfig.setAutoApprove(true);
        checkoutProConfig.setSurePayCount(0);
        checkoutProConfig.setShowExitConfirmationOnPaymentScreen(true);
        checkoutProConfig.setShowExitConfirmationOnCheckoutScreen(true);
        checkoutProConfig.setMerchantName(MERCHANT_NAME);
        checkoutProConfig.setMerchantLogo(R.drawable.app_logo);
        if (reviewOrderAdapter != null)
            checkoutProConfig.setCartDetails(reviewOrderAdapter.getOrderDetailsList());
        return checkoutProConfig;
    }

    private ArrayList<PaymentMode> getCheckoutOrderList() {
        ArrayList<PaymentMode> checkoutOrderList = new ArrayList<>();
        checkoutOrderList.add(new PaymentMode(PaymentType.UPI, PayUCheckoutProConstants.CP_GOOGLE_PAY));
        checkoutOrderList.add(new PaymentMode(PaymentType.WALLET, PayUCheckoutProConstants.CP_PHONEPE));
        checkoutOrderList.add(new PaymentMode(PaymentType.WALLET, PayUCheckoutProConstants.CP_PAYTM));
        return checkoutOrderList;
    }

    private void AddUserSubscriptionDetailApi(String transactionDetails) {
        myDialog.show();
        final String API_AddUserSubscriptionDetail = ROOT_URL + "add_user_subscription_detail.php";
        try {
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                Log.d("URL", API_AddUserSubscriptionDetail);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_AddUserSubscriptionDetail,
                        response -> {
                            Log.d("Response", response);
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean status = jsonObject.getBoolean("status");
                                if (status) {
                                    String message = jsonObject.getString("message");
                                    CommonMethods.DisplayToastSuccess(getApplicationContext(), message);
                                    final Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                                    startActivity(i);
                                    overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                    finish();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        Throwable::printStackTrace) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", mUserId);
                        params.put("plan_id", mPlan_id);
                        params.put("subscribed_on", mSubscripbed_on);
                        params.put("payment_detail", "" + transactionDetails);
                        params.put("package_id", mPackage_id);
                        params.put("subscribed_till", msubscribed_till);
                        Log.d("ParrasRegister", params.toString());
                        return params;
                    }
                };
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastInfo(getApplicationContext(), "Please check your internet connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
