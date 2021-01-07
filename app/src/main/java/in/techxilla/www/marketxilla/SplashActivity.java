package in.techxilla.www.marketxilla;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;


public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 24;
    Thread splashTread;
    private static final String TAG = "SplashActivity";
    String Force_Update_flag="0";;
    int YourApkVersionCode;
    String SystemOtp,StrMemberId="",StrMobileNo="",StrEmailId="",StrName="",SavedPassword="",StrPassword="",IS_ADMIN="",IS_PASSWORD_SET="";
    EditText edt_member_name,edt_member_mobile,edt_member_email,edt_password;
    ProgressDialog myDialog;
    LottieAnimationView animationView,animationView1;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        init();

    }

    private void init() {

        StrMobileNo =  UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberMobile");
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");


        animationView = (LottieAnimationView)findViewById(R.id.animationView);
        //animationView.setAnimationFromUrl("https://assets4.lottiefiles.com/packages/lf20_yRrgc4.json");
        animationView1 = (LottieAnimationView)findViewById(R.id.animationView1);
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            force_update();
        }else {
            DisplaySnackBar(viewGroup,"No Internet Connection","INFO");
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        //checkInternetConnection(null);

    }







    private void force_update() {


        int currentVersionCode = getCurrentVersion();
        //Log.d("Current version = ",currentVersionCode);
        String Uiid_id = UUID.randomUUID().toString();
        final String get_latest_version_info = ROOT_URL +"getLatestUpdateVersion.php?_"+Uiid_id;
        Log.d("URL --->", get_latest_version_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_latest_version_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response",""+response);
                            String latestVersion = "";
                            JSONObject Jobj = new JSONObject(response);
                            String data = Jobj.getString("data");
                            JSONObject jobject = new JSONObject(data);

                            String Id = jobject.getString("id");
                            String VersionCode = jobject.getString("version_code");
                            String VersionName = jobject.getString("version_name");
                            Force_Update_flag = jobject.getString("is_force_update");
                            update_dialog(VersionCode);

                            Log.d("Latest version:",latestVersion);
                        } catch (Exception e) {
                            Log.d("Exception",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Log.d("Vollley Err", volleyError.toString());
                        if(volleyError.toString().equalsIgnoreCase("com.android.volley.ServerError")){
                            DisplaySnackBar(viewGroup,"App under maintenance","INFO");
                        }
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);

            }else {
                DisplaySnackBar(viewGroup,"No Internet Connection","INFO");

            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private int getCurrentVersion(){
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo =  pm.getPackageInfo(this.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        String currentVersion = pInfo.versionName;
        YourApkVersionCode = pInfo.versionCode;
        Log.d("YourVersionCode",""+YourApkVersionCode);
        String version_code = String.valueOf(YourApkVersionCode);
        return YourApkVersionCode;
    }


    private void update_dialog(String versionCode) {
        if(versionCode!="" || versionCode!=null){
            int v_code = Integer.valueOf(versionCode);
            if((YourApkVersionCode < v_code) && Force_Update_flag.equalsIgnoreCase("1")){
                Log.d("version code",""+v_code);
                Log.d("Your APK CODE ",""+YourApkVersionCode);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Dialog dialog = new Dialog(SplashActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.pop_up_app_update);
                        dialog.getWindow().setBackgroundDrawable(
                                new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        TextView title = (TextView)dialog.findViewById(R.id.title) ;
                        TextView Upgrade_text = (TextView)dialog.findViewById(R.id.Upgrade_text) ;
                        TextView tv_ok = (TextView)dialog.findViewById(R.id.tv_ok);
                        dialog.show();
                        tv_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                UtilitySharedPreferences.clearPref(getApplicationContext());
                                CommonMethods.deleteCache(getApplicationContext());
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                finish();
                            }
                        });
                    }
                }, SPLASH_TIME_OUT);
            }else
            {
                /*UtilitySharedPreferences.clearPref(this);*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if((StrMemberId!=null &&  !StrMemberId.equalsIgnoreCase(""))|| (StrMobileNo!=null &&  !StrMobileNo.equalsIgnoreCase(""))  ) {
                            Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                            startActivity(i);
                            overridePendingTransition(R.animator.move_left,R.animator.move_right);
                            finish();
                        }else {
                            Intent i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
                            startActivity(i);
                            overridePendingTransition(R.animator.move_left,R.animator.move_right);
                            finish();
                        }
                    }
                }, SPLASH_TIME_OUT);

            }
        }else{
            if((StrMemberId!=null &&  !StrMemberId.equalsIgnoreCase(""))|| (StrMobileNo!=null &&  !StrMobileNo.equalsIgnoreCase(""))  ) {
                Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                startActivity(i);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                finish();
            }else {
                Intent i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                finish();
            }
        }
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


}
