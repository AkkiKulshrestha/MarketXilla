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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import in.techxilla.www.marketxilla.utils.MyValidator;
import in.techxilla.www.marketxilla.utils.PayUMoneyAppPreference;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastError;
import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastWarning;
import static in.techxilla.www.marketxilla.utils.CommonMethods.md5;
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
    boolean isPaidUser = false;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mStackCount = 30;
    private Dialog dialogResetPassword;
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;

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
    }

    public void setBottomNavigation() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Menu nav_Menu = navigation.getMenu();
        if (isPaidUser) {
            nav_Menu.findItem(R.id.navigation_paid_user).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.navigation_paid_user).setVisible(false);
        }
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
                                Log.d("PaidUser", "No");
                            } else {
                                for (int i = 0; i < m_jArry.length(); i++) {
                                    JSONObject jo_data = m_jArry.getJSONObject(i);
                                    String StrPlanId = jo_data.getString("plan_id");
                                    String plan_name = jo_data.getString("plan_name");
                                    String subscribed_till = jo_data.getString("subscribed_till");
                                    SimpleDateFormat sdf, sdf2, sdf21;
                                    Date newSubscriptedTilldate, currentdate2, newSubscriptedOndate;

                                    if (!subscribed_till.equalsIgnoreCase("")) {
                                        try {
                                            sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            newSubscriptedTilldate = sdf.parse(subscribed_till);
                                            sdf21 = new SimpleDateFormat("dd/MM/yyyy");
                                            String mSubscribed_till = sdf21.format(newSubscriptedTilldate);
                                            if (i == 0 && CommonMethods.isDateExpired(mSubscribed_till)) {
                                                isPaidUser = true;
                                                Log.d("PaidUser", "Yes");
                                            } else {
                                                isPaidUser = false;
                                                Log.d("PaidUser", "No");
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

        TextView nav_header_userName = (TextView) hView.findViewById(R.id.nav_header_userName);
        TextView nav_user_email = (TextView) hView.findViewById(R.id.nav_Email);
        if (StrMemberName != null && !StrMemberName.equalsIgnoreCase("")) {
            nav_header_userName.setText(StrMemberName.toUpperCase());
        }

        if (StrMemberMobile != null && !StrMemberMobile.equalsIgnoreCase("")) {
            nav_user_email.setText("Mobile No.: " + StrMemberMobile);
        }

        ImageView iv_facebook = (ImageView) navigationView1.findViewById(R.id.iv_facebook);
        ImageView iv_twitter = (ImageView) navigationView1.findViewById(R.id.iv_twitter);
        ImageView iv_telegram = (ImageView) navigationView1.findViewById(R.id.iv_telegram);
        ImageView iv_linked_in = (ImageView) navigationView1.findViewById(R.id.iv_linked_in);

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

        TextView tv_registered_mobile_no = (TextView) dialogResetPassword.findViewById(R.id.tv_registered_mobile_no);
        tv_registered_mobile_no.setText(StrMemberEmailId);

        TextInputLayout etCurrentPasswordLayout = (TextInputLayout) dialogResetPassword.findViewById(R.id.etCurrentPasswordLayout);
        etCurrentPasswordLayout.setVisibility(View.VISIBLE);
        etCurrentPassword = (EditText) dialogResetPassword.findViewById(R.id.etCurrentPassword);
        etNewPassword = (EditText) dialogResetPassword.findViewById(R.id.etNewPassword);
        etConfirmPassword = (EditText) dialogResetPassword.findViewById(R.id.etConfirmPassword);

        ImageView iv_close = (ImageView) dialogResetPassword.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogResetPassword.dismiss();
            }
        });


        Button btn_reset = (Button) dialogResetPassword.findViewById(R.id.btn_reset);
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

}
