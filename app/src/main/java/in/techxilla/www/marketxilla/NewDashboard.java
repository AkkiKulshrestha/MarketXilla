package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Extras.PayUSdkDetails;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.paymentparamhelper.PaymentParams;
import com.payu.paymentparamhelper.PayuErrors;
import com.payu.paymentparamhelper.PostData;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.fragment.HolidayFragment;
import in.techxilla.www.marketxilla.fragment.HomeFragment;
import in.techxilla.www.marketxilla.fragment.PackageFragment;
import in.techxilla.www.marketxilla.fragment.PaidUserFragment;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.Constant;
import in.techxilla.www.marketxilla.utils.MyValidator;
import in.techxilla.www.marketxilla.utils.PayUMoneyAppPreference;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastError;
import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastWarning;
import static in.techxilla.www.marketxilla.utils.CommonMethods.md5;
import static in.techxilla.www.marketxilla.utils.Constant.MERCHANT_EMAIL;
import static in.techxilla.www.marketxilla.utils.Constant.MERCHANT_KEY;
import static in.techxilla.www.marketxilla.utils.Constant.PAYU_SALT;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class NewDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    String StrMemberId, StrMemberName, StrMemberEmailId, StrMemberMobile;
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
    private Dialog dialogResetPassword,dialogBankDetails;
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private String mUserId, mPlan_id, mSubscripbed_on, mPackage_id, msubscribed_till;
    private JSONObject transactionObj,bankObj;
    private PaymentParams mPaymentParams;
    private String paymentHash1;

    // This sets the configuration
    private PayuConfig payuConfig;

    private String subventionHash, serverHash;

    // Used when generating hash from SDK
    private PayUChecksum checksum;

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash.toString();
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

        PayUMoneyAppPreference appPreference = new PayUMoneyAppPreference();
        appPreference.setUserEmail(StrMemberEmailId);
        appPreference.setUserId(StrMemberId);
        appPreference.setUserMobile(StrMemberMobile);
        appPreference.setUserFullName(StrMemberName);

        iv_notification = findViewById(R.id.iv_notification);
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });
        getPlanDetail();
        fetchNotificationList();
        getBankDetail();
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

        iv_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getResources().getString(R.string.facebook_page)));
                startActivity(i);
            }
        });

        iv_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getResources().getString(R.string.twitter_page)));
                startActivity(i);
            }
        });

        iv_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telegram = new Intent(Intent.ACTION_VIEW);
                telegram.setData(Uri.parse(getResources().getString(R.string.telegram)));
                startActivity(telegram);
            }
        });

        iv_linked_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getResources().getString(R.string.linked_in_page)));
                startActivity(i);
            }
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
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(String response) {
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
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    DisplayToastError(getApplicationContext(), "Something goes wrong. Please try again");
                }
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
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResetPassword.dismiss();
            }
        });


        Button btn_reset = dialogResetPassword.findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
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
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            Log.d("Response", "" + response);
                            bankObj = new JSONObject(response);

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


        if(bankObj!=null){

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
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBankDetails.dismiss();
            }
        });


        Button btnOk = dialogBankDetails.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBankDetails.dismiss();
            }
        });
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
        final String transactionId = "MXILLA-" + System.currentTimeMillis();
        final String StrUpiAccountId = UtilitySharedPreferences.getPrefs(this, "UpiAccountId");
        final String StrUPI_MerchantName = UtilitySharedPreferences.getPrefs(this, "UpiMerchantName");
        final String MemberEmailId = UtilitySharedPreferences.getPrefs(this, "MemberEmailId");
        mUserId = UtilitySharedPreferences.getPrefs(this, "MemberId");
        final String MemberName = UtilitySharedPreferences.getPrefs(this, "MemberName");
        final String MemberMobile = UtilitySharedPreferences.getPrefs(this, "MemberMobile");

        Payu.setInstance(this);
        PayUSdkDetails payUSdkDetails = new PayUSdkDetails();

        Log.d("SDK DETAILS", "Build No: " + payUSdkDetails.getSdkBuildNumber() + "\n Build Type: " + payUSdkDetails.getSdkBuildType() + " \n Build Flavor: " + payUSdkDetails.getSdkFlavor() + "\n Application Id: " + payUSdkDetails.getSdkApplicationId() + "\n Version Code: " + payUSdkDetails.getSdkVersionCode() + "\n Version Name: " + payUSdkDetails.getSdkVersionName());

        String hashSequence = MERCHANT_KEY + "|" + transactionId + "|" + StrSubscriptionAmount + "|" + PlanName + "|" + MemberName + "|" + MemberEmailId + "|" + mUserId + "|" + MemberMobile + "|||||||||" + Constant.PAYU_SALT;
        subventionHash = hashCal("SHA-512", hashSequence);

        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put("SubscribedOn", mSubscripbed_on);
        additionalParams.put("SubscribedTill", msubscribed_till);
        additionalParams.put("PackageId", Package_id);
        additionalParams.put("PlanId", mPlan_id);
        additionalParams.put("UserId", mUserId);

        String userCredentials = MERCHANT_KEY + ":" + MERCHANT_EMAIL;

        int environment;
        if (Constant.isDebug)
            environment = PayuConstants.STAGING_ENV;
        else
            environment = PayuConstants.PRODUCTION_ENV;

        mPaymentParams = new PaymentParams();

        mPaymentParams.setAmount(StrSubscriptionAmount);                        // Payment amount
        mPaymentParams.setTxnId(transactionId);                                             // Transaction ID
        mPaymentParams.setPhone(MemberMobile);                                           // User Phone number
        mPaymentParams.setProductInfo(PlanName);                   // Product Name or description
        mPaymentParams.setFirstName(MemberName);                              // User First name
        mPaymentParams.setEmail(MemberEmailId);                                            // User Email ID
        mPaymentParams.setSurl(Constant.SURL);                    // Success URL (surl);
        mPaymentParams.setFurl(Constant.FURL);                     //Failure URL (furl);
        mPaymentParams.setUdf1(mSubscripbed_on);
        mPaymentParams.setUdf2(msubscribed_till);
        mPaymentParams.setUdf3(mPlan_id);
        mPaymentParams.setUdf4(Package_id);
        mPaymentParams.setUdf5(mUserId);
        mPaymentParams.setNotifyURL(Constant.SURL);                              // Integration environment - true (Debug);/ false(Production);
        mPaymentParams.setKey(MERCHANT_KEY);                        // Merchant key
        mPaymentParams.setUserCredentials(userCredentials);

        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        generateHashFromSDK(mPaymentParams, Constant.PAYU_SALT);

        transactionObj = new JSONObject();
        try {
            transactionObj.put("transaction_id", transactionId);
            transactionObj.put("transaction_amount", StrSubscriptionAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Invoke the following function to open the checkout page.


    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    // Response from Payumoney
                    String payuResponse = transactionResponse.getPayuResponse();
                    // Response from SURl and FURL
                    String merchantResponse = transactionResponse.getTransactionDetails();
                    try {
                        transactionObj.put("payuResponse", payuResponse);
                        transactionObj.put("merchantResponse", merchantResponse);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //AddUserSubscriptionDetailApi(transactionObj.toString());
                    AddUserSubscriptionDetailApi(transactionObj.toString());
                } else {
                    //Failure Transaction
                    CommonMethods.DisplayToastError(getApplicationContext(), "" + transactionResponse);
                }
            }
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {

                /**
                 * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                 * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                 *
                 * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                 * for identifying status of transaction. There are two possible status like, success or failure
                 * */


                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else {
                Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void generateHashFromSDK(PaymentParams mPaymentParams, String salt) {
        PayuHashes payuHashes = new PayuHashes();
        PostData postData = new PostData();
        String hashSequence = mPaymentParams.getKey() + "|" + mPaymentParams.getTxnId() + "|" + mPaymentParams.getAmount() + "|" + mPaymentParams.getProductInfo() + "|" + mPaymentParams.getFirstName() + "|" + mPaymentParams.getEmail() + "|" + mPaymentParams.getUdf5() + "|" + mPaymentParams.getPhone() + "|||||||||" + PAYU_SALT;
        serverHash = hashCal("SHA-512", hashSequence);
//        if(mPaymentParams.getBeneficiaryAccountNumber()== null){

        // payment Hash;
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setAmount(mPaymentParams.getAmount());
        checksum.setKey(mPaymentParams.getKey());
        checksum.setTxnid(mPaymentParams.getTxnId());
        checksum.setEmail(mPaymentParams.getEmail());
        checksum.setSalt(salt);
        checksum.setProductinfo(mPaymentParams.getProductInfo());
        checksum.setFirstname(mPaymentParams.getFirstName());
        checksum.setUdf1(mPaymentParams.getUdf1());
        checksum.setUdf2(mPaymentParams.getUdf2());
        checksum.setUdf3(mPaymentParams.getUdf3());
        checksum.setUdf4(mPaymentParams.getUdf4());
        checksum.setUdf5(mPaymentParams.getUdf5());

        postData = checksum.getHash();
        if (postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setPaymentHash(postData.getResult());
        }
        // checksum for payemnt related details
        // var1 should be either user credentials or default
        String var1 = mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials();
        String key = mPaymentParams.getKey();

        if ((postData = calculateHash(key, PayuConstants.PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // Assign post data first then check for success
            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(postData.getResult());
        //vas
        if ((postData = calculateHash(key, PayuConstants.VAS_FOR_MOBILE_SDK, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setVasForMobileSdkHash(postData.getResult());

        // getIbibocodes
        if ((postData = calculateHash(key, PayuConstants.GET_MERCHANT_IBIBO_CODES, PayuConstants.DEFAULT, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
            payuHashes.setMerchantIbiboCodesHash(postData.getResult());

        if (!var1.contentEquals(PayuConstants.DEFAULT)) {
            // get user card
            if ((postData = calculateHash(key, PayuConstants.GET_USER_CARDS, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) // todo rename storedc ard
                payuHashes.setStoredCardsHash(postData.getResult());
            // save user card
            if ((postData = calculateHash(key, PayuConstants.SAVE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setSaveCardHash(postData.getResult());
            // delete user card
            if ((postData = calculateHash(key, PayuConstants.DELETE_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setDeleteCardHash(postData.getResult());
            // edit user card
            if ((postData = calculateHash(key, PayuConstants.EDIT_USER_CARD, var1, salt)) != null && postData.getCode() == PayuErrors.NO_ERROR)
                payuHashes.setEditCardHash(postData.getResult());
        }

        if (mPaymentParams.getOfferKey() != null) {
            postData = calculateHash(key, PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey(), salt);
            if (postData.getCode() == PayuErrors.NO_ERROR) {
                payuHashes.setCheckOfferStatusHash(postData.getResult());
            }
        }

        if (mPaymentParams.getOfferKey() != null && (postData = calculateHash(key, PayuConstants.CHECK_OFFER_STATUS, mPaymentParams.getOfferKey(), salt)) != null && postData.getCode() == PayuErrors.NO_ERROR) {
            payuHashes.setCheckOfferStatusHash(postData.getResult());
        }

        // we have generated all the hases now lest launch sdk's ui
        launchSdkUI(payuHashes);
    }

    private void launchSdkUI(PayuHashes payuHashes) {
        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        //intent.putExtra(SdkUIConstants.SUBVENTION_HASH, subventionHash);
        intent.putExtra(PayuConstants.SALT, PAYU_SALT);
        intent.putExtra(PayuConstants.PAYU_HASHES, serverHash);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }

    // deprecated, should be used only for testing.
    private PostData calculateHash(String key, String command, String var1, String salt) {
        checksum = null;
        checksum = new PayUChecksum();
        checksum.setKey(key);
        checksum.setCommand(command);
        checksum.setVar1(var1);
        checksum.setSalt(salt);
        return checksum.getHash();
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
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if (status) {
                                        JSONObject dataObj = jsonObject.getJSONObject("data");
                                        String message = jsonObject.getString("message");
                                        CommonMethods.DisplayToastSuccess(getApplicationContext(), message);
                                        final Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                                        startActivity(i);
                                        overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
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
