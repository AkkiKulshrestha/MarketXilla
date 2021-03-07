package in.techxilla.www.marketxilla;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static android.Manifest.permission.READ_PHONE_STATE;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SplashActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
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
        if(checkAndRequestPermissions()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isInternetPresent) {
                        force_update();
                        //inAppUpdate();
                    } else {
                        CommonMethods.DisplayToastInfo(getApplicationContext(), "No Internet Connection");
                    }
                }
            }, SPLASH_TIME_OUT);
        }

    }

    private boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int writepermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        int permissionLocation = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION);
        int read_sms_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);


        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(read_sms_permission != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);

        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        force_update();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                            checkAndRequestPermissions();
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                            startActivity(i);
                            finish();
                        }
                    }
                }
            }
        }

    }

    private void inAppUpdate() {
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
            }
        });
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