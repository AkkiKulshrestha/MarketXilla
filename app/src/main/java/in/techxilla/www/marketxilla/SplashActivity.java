package in.techxilla.www.marketxilla;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.UUID;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2000;
    private static final String TAG = "SplashActivity";
    private String Force_Update_flag = "0";
    private int YourApkVersionCode;
    private String StrMemberId, StrMobileNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        StrMobileNo = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberMobile");
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            force_update();
        } else {
            CommonMethods.DisplayToastInfo(getApplicationContext(), "No Internet Connection");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void force_update() {
        int currentVersionCode = getCurrentVersion();
        final String Uiid_id = UUID.randomUUID().toString();
        final String get_latest_version_info = ROOT_URL + "getLatestUpdateVersion.php?_" + Uiid_id;
        Log.d("URL --->", get_latest_version_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_latest_version_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        try {
                            Log.d("Response", "" + response);
                            final String latestVersion = "";
                            final JSONObject Jobj = new JSONObject(response);
                            final String data = Jobj.getString("data");
                            final JSONObject jobject = new JSONObject(data);

                            final String Id = jobject.getString("id");
                            final String VersionCode = jobject.getString("version_code");
                            final String VersionName = jobject.getString("version_name");
                            Force_Update_flag = jobject.getString("is_force_update");
                            update_dialog(VersionCode);

                            Log.d("Latest version:", latestVersion);
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        if (volleyError.toString().equalsIgnoreCase("com.android.volley.ServerError")) {
                            CommonMethods.DisplayToastInfo(getApplicationContext(), "App under maintenance");
                        }
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastInfo(getApplicationContext(), "No Internet Connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getCurrentVersion() {
        final PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        final String currentVersion = pInfo.versionName;
        YourApkVersionCode = pInfo.versionCode;
        final String version_code = String.valueOf(YourApkVersionCode);
        return YourApkVersionCode;
    }

    private void update_dialog(String versionCode) {
        if (!versionCode.equals("") || versionCode != null) {
            int v_code = Integer.parseInt(versionCode);
            if ((YourApkVersionCode < v_code) && Force_Update_flag.equalsIgnoreCase("1")) {
                Log.d("version code", "" + v_code);
                Log.d("Your APK CODE ", "" + YourApkVersionCode);
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
                        TextView title = (TextView) dialog.findViewById(R.id.title);
                        TextView Upgrade_text = (TextView) dialog.findViewById(R.id.Upgrade_text);
                        TextView tv_ok = (TextView) dialog.findViewById(R.id.tv_ok);
                        dialog.show();
                        tv_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                UtilitySharedPreferences.clearPref1(getApplicationContext(), "MemberId");
                                UtilitySharedPreferences.clearPref1(getApplicationContext(), "MemberName");
                                UtilitySharedPreferences.clearPref1(getApplicationContext(), "MemberEmailId");
                                UtilitySharedPreferences.clearPref1(getApplicationContext(), "MemberMobile");
                                UtilitySharedPreferences.clearPref1(getApplicationContext(), "MemberUsername");
                                CommonMethods.deleteCache(getApplicationContext());
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                finish();
                            }
                        });
                    }
                }, SPLASH_TIME_OUT);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i;
                        if ((StrMemberId != null && !StrMemberId.equalsIgnoreCase("")) || (StrMobileNo != null && !StrMobileNo.equalsIgnoreCase(""))) {
                            i = new Intent(getApplicationContext(), NewDashboard.class);
                        } else {
                            i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
                        }
                        startActivity(i);
                        overridePendingTransition(R.animator.move_left, R.animator.move_right);
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }
        } else {
            Intent i;
            if ((StrMemberId != null && !StrMemberId.equalsIgnoreCase("")) || (StrMobileNo != null && !StrMobileNo.equalsIgnoreCase(""))) {
                i = new Intent(getApplicationContext(), NewDashboard.class);
            } else {
                i = new Intent(getApplicationContext(), SignIn_SignUpActivity.class);
            }
            startActivity(i);
            overridePendingTransition(R.animator.move_left, R.animator.move_right);
            finish();
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