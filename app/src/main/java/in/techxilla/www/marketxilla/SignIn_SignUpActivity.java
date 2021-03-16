package in.techxilla.www.marketxilla;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import in.techxilla.www.marketxilla.service.SMSBroadcastReceiver;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.MyValidator;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastWarning;
import static in.techxilla.www.marketxilla.utils.CommonMethods.md5;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SignIn_SignUpActivity extends AppCompatActivity implements SMSBroadcastReceiver.OTPReceiveListener {
    private final ViewGroup nullParent = null;
    private final String str_registered_mobile_no = "";
    private final String StrCellInfo = "";
    boolean emailOtpVerified = false;
    boolean mobileOtpVerified = false;
    private LinearLayout ll_parent_sign_in, ll_parent_sign_up;
    private LinearLayout LayoutTabSignIn, LayoutTabSignUp;
    private ProgressDialog myDialog;
    private EditText Edt_Email_Mobile, Edt_Password;
    private EditText Edt_SU_Fullname, Edt_SU_EmailId, Edt_SU_MobileNo, edt_Su_Password;
    private String StrName, StrEmail, StrMobile, ClientId, EmailVerified, MobileVerified;
    private TextView txt_resend_email_otp;
    private TextView txt_resend_mobile_otp;
    private EditText edt_Check_Mobile_Otp, edt_Check_Email_Otp;
    private String MobileOtp, EmailPin;
    private Dialog dialog11;
    private SMSBroadcastReceiver smsBroadcastReceiver;
    private String verificationID;
    private TextView tv_forgot_password;
    private Dialog dialogForgotPassword, dialogEnteringOtp, dialogResetPassword;
    private EditText edt_verification_number;
    private String StrEmailMobile;
    private String StrDeviceUniqueId = "";
    private String StrIMEI1 = "";
    private String StrIMEI2 = "";
    private String StrIMEI = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        init();
    }

    private void init() {
        myDialog = new ProgressDialog(this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);
            UtilitySharedPreferences.setPrefs(getApplicationContext(), "token", newToken);
            System.out.println("New Token :" + UtilitySharedPreferences.getPrefs(getApplicationContext(), "token"));
        });

        LayoutTabSignIn = (LinearLayout) findViewById(R.id.LayoutTabSignIn);
        LayoutTabSignUp = (LinearLayout) findViewById(R.id.LayoutTabSignUp);
        LayoutTabSignIn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        LayoutTabSignUp.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        ll_parent_sign_in = (LinearLayout) findViewById(R.id.ll_parent_sign_in);
        ll_parent_sign_up = (LinearLayout) findViewById(R.id.ll_parent_sign_up);
        ll_parent_sign_in.setVisibility(View.VISIBLE);
        ll_parent_sign_up.setVisibility(View.GONE);

        smsBroadcastReceiver = new SMSBroadcastReceiver();
        smsBroadcastReceiver.setOtpReceiveListener(SignIn_SignUpActivity.this);
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, filter);

        LayoutTabSignIn.setOnClickListener(v -> GOTO_SIGIN());

        LayoutTabSignUp.setOnClickListener(v -> GOTO_SIGNUP());

        Edt_Email_Mobile = (EditText) findViewById(R.id.Edt_Email_Mobile);
        Edt_Password = (EditText) findViewById(R.id.Edt_Password);
        final Button btnSignIn = (Button) findViewById(R.id.BtnSignIn);
        btnSignIn.setOnClickListener(v -> {
            if (isValidLogin()) {
                LogInApi();
            }
        });

        Edt_SU_Fullname = (EditText) findViewById(R.id.Edt_SU_Fullname);
        Edt_SU_EmailId = (EditText) findViewById(R.id.Edt_SU_EmailId);
        Edt_SU_MobileNo = (EditText) findViewById(R.id.Edt_SU_MobileNo);
        edt_Su_Password = (EditText) findViewById(R.id.edt_Su_Password);

        final Button btnSignUp = (Button) findViewById(R.id.BtnSignUp);
        btnSignUp.setOnClickListener(v -> {
            if (isValidRegister()) {
                RegisterApi();
            }
        });

        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        tv_forgot_password.setOnClickListener(v -> popUpForgotPassword());
        StrDeviceUniqueId = getDeviceID();
    }

    private void GOTO_SIGNUP() {
        LayoutTabSignIn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        LayoutTabSignUp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ll_parent_sign_in.setVisibility(View.GONE);
        ll_parent_sign_up.setVisibility(View.VISIBLE);
    }

    private void GOTO_SIGIN() {
        LayoutTabSignIn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        LayoutTabSignUp.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        ll_parent_sign_in.setVisibility(View.VISIBLE);
        ll_parent_sign_up.setVisibility(View.GONE);
    }

    @SuppressLint("HardwareIds")
    public String getDeviceID() {
        String m_szUniqueID = "";
        TelephonyManager telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr != null ? telMgr.getSimState() : 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int simCount = telMgr != null ? telMgr.getPhoneCount() : 0;
            if (simCount == 2) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    return null;
                }

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            StrIMEI1 = telephonyManager != null ? telephonyManager.getImei(0) : "";
                            StrIMEI2 = telephonyManager != null ? telephonyManager.getImei(1) : "";
                        }
                    } else {
                        StrIMEI = Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                    }
                }
            } else {
                StrIMEI = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            // 2 compute DEVICE ID
            if (StrIMEI != null && !StrIMEI.equalsIgnoreCase("") && !StrIMEI.equalsIgnoreCase("null")) {
                m_szUniqueID = StrIMEI;
            } else if (StrIMEI1 != null && !StrIMEI1.equalsIgnoreCase("") && !StrIMEI1.equalsIgnoreCase("null")) {
                m_szUniqueID = StrIMEI1;
            } else if (StrIMEI2 != null && !StrIMEI2.equalsIgnoreCase("") && !StrIMEI2.equalsIgnoreCase("null")) {
                m_szUniqueID = StrIMEI2;
            } else {
                final String m_szDevIDShort = "35"
                        + // we make this look like a valid IMEI
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                        + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                        + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                        + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                        + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                        + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                        + Build.USER.length() % 10; // 13 digits

                final WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                final String m_szWLANMAC = wm != null ? wm.getConnectionInfo().getMacAddress() : "";
                // 5 Bluetooth MAC address android.permission.BLUETOOTH required
                BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
                m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                final String m_szBTMAC = m_BluetoothAdapter.getAddress();
                System.out.println("m_szBTMAC " + m_szBTMAC);

                if (m_szWLANMAC != null && !m_szWLANMAC.equalsIgnoreCase("") && !m_szWLANMAC.equalsIgnoreCase("null")) {
                    m_szUniqueID = m_szWLANMAC;
                } else if (m_szBTMAC != null && !m_szBTMAC.equalsIgnoreCase("") && !m_szBTMAC.equalsIgnoreCase("null")) {
                    m_szUniqueID = m_szBTMAC;
                }

            }
        } else {
            if (telMgr != null) {
                StrIMEI = telMgr.getDeviceId();
                m_szUniqueID = StrIMEI;
            }

        }
        Log.i("--DeviceID--", m_szUniqueID);
        Log.d("DeviceIdCheck", "DeviceId that generated MPreferenceActivity:" + m_szUniqueID);
        return m_szUniqueID;
    }

    private void LogInApi() {
        final String StrUserName = Edt_Email_Mobile.getText().toString();
        final String StrPassword = Edt_Password.getText().toString();
        final String API_LOGIN = ROOT_URL + "login.php";
        myDialog.show();
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                Log.d("URL", API_LOGIN);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_LOGIN,
                        response -> {
                            Log.d("Response", response);
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                final boolean status = jsonObject.getBoolean("status");
                                if (status) {
                                    final JSONObject dataObj = jsonObject.getJSONObject("data");
                                    final String message = jsonObject.getString("message");
                                    ClientId = dataObj.getString("id");
                                    StrName = dataObj.getString("name");
                                    StrEmail = dataObj.getString("email_id");
                                    StrMobile = dataObj.getString("mobile_no");
                                    EmailVerified = dataObj.getString("is_email_verified");
                                    MobileVerified = dataObj.getString("is_mobile_verified");
                                    if (EmailVerified != null && EmailVerified.equalsIgnoreCase("0")) {
                                        emailOtpVerified = false;
                                        resendEmailPin();
                                    } else if (EmailVerified != null && EmailVerified.equalsIgnoreCase("1")) {
                                        emailOtpVerified = true;
                                    }

                                    if (MobileVerified != null && MobileVerified.equalsIgnoreCase("1")) {
                                        mobileOtpVerified = true;
                                    } else if (MobileVerified != null && MobileVerified.equalsIgnoreCase("0")) {
                                        mobileOtpVerified = false;
                                        resendMobilePin();
                                    }

                                    if (!mobileOtpVerified || !emailOtpVerified) {
                                        VerifyEmail_MobilePopup();
                                    } else {
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberId", ClientId);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberName", StrName);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberEmailId", StrEmail);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberMobile", StrMobile);
                                        UtilitySharedPreferences.setPrefs(getApplicationContext(), "DeviceId", StrDeviceUniqueId);
                                        CommonMethods.DisplayToastSuccess(getApplicationContext(), message);
                                        Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                                        startActivity(i);
                                        overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                        finish();
                                    }
                                } else {
                                    final String message = jsonObject.getString("message");
                                    CommonMethods.DisplayToastError(getApplicationContext(), message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                        }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("username", StrUserName.toUpperCase());
                        params.put("password", md5(StrPassword));
                        params.put("token", UtilitySharedPreferences.getPrefs(getApplicationContext(), "token"));
                        params.put("device_id", StrDeviceUniqueId);
                        Log.d("ParrasLogin", params.toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastInfo(this, "Please check your internet connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidLogin() {
        boolean result = true;
        if (!MyValidator.isValidEmailMobile(Edt_Email_Mobile)) {
            Edt_Email_Mobile.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please enter either Email Id or Mobile No");
            result = false;
        }

        if (MyValidator.isValidField(Edt_Password)) {
            Edt_Password.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please enter Password");
            result = false;
        }
        return result;
    }

    private void RegisterApi() {
        StrName = Edt_SU_Fullname.getText().toString();
        StrEmail = Edt_SU_EmailId.getText().toString();
        StrMobile = Edt_SU_MobileNo.getText().toString();
        final String StrPassword = edt_Su_Password.getText().toString();
        final String API_REGISTER = ROOT_URL + "register.php";
        myDialog.show();
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                Log.d("URL", API_REGISTER);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_REGISTER,
                        response -> {
                            Log.d("Response", response);
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            try {
                                final JSONObject jsonObject = new JSONObject(response);
                                boolean status = jsonObject.getBoolean("status");
                                if (status) {
                                    final JSONObject dataObj = jsonObject.getJSONObject("data");
                                    final String message = jsonObject.getString("message");

                                    ClientId = dataObj.getString("id");
                                    StrName = dataObj.getString("name");
                                    StrEmail = dataObj.getString("email_id");
                                    StrMobile = dataObj.getString("mobile_no");
                                    EmailVerified = dataObj.getString("is_email_verified");
                                    MobileVerified = dataObj.getString("is_mobile_verified");

                                    if (EmailVerified != null && EmailVerified.equalsIgnoreCase("0")) {
                                        emailOtpVerified = false;
                                    } else if (EmailVerified != null && EmailVerified.equalsIgnoreCase("1")) {
                                        emailOtpVerified = true;
                                    }

                                    if (MobileVerified != null && MobileVerified.equalsIgnoreCase("1")) {
                                        mobileOtpVerified = true;
                                    } else if (MobileVerified.equalsIgnoreCase("0")) {
                                        mobileOtpVerified = false;
                                    }

                                    if (!mobileOtpVerified || !emailOtpVerified) {
                                        VerifyEmail_MobilePopup();
                                    }
                                } else {
                                    final String message = jsonObject.getString("message");
                                    CommonMethods.DisplayToastError(getApplicationContext(), message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                            error.printStackTrace();
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                        }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("name", StrName);
                        params.put("email_id", StrEmail);
                        params.put("mobile_no", StrMobile);
                        params.put("password", md5(StrPassword));
                        params.put("token", UtilitySharedPreferences.getPrefs(getApplicationContext(), "token"));
                        params.put("device_id", StrDeviceUniqueId);
                        Log.d("ParrasRegister", params.toString());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastInfo(this, "Please check your internet connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void VerifyEmail_MobilePopup() {
        dialog11 = new Dialog(SignIn_SignUpActivity.this);
        dialog11.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog11.setCanceledOnTouchOutside(false);
        dialog11.setContentView(R.layout.popup_verify_email_mobile_otp);
        dialog11.getWindow().getAttributes().width = ActionBar.LayoutParams.MATCH_PARENT;
        final ImageView btnClose = (ImageView) dialog11.findViewById(R.id.btnClose);
        final Button btnContinue = (Button) dialog11.findViewById(R.id.btnContinue);
        edt_Check_Mobile_Otp = (EditText) dialog11.findViewById(R.id.edt_Check_Mobile_Otp);
        edt_Check_Email_Otp = (EditText) dialog11.findViewById(R.id.edt_Check_Email_Otp);
        txt_resend_email_otp = (TextView) dialog11.findViewById(R.id.txt_resend_email_otp);
        txt_resend_mobile_otp = (TextView) dialog11.findViewById(R.id.txt_resend_mobile_otp);

        if (EmailVerified != null && EmailVerified.equals("1")) {
            edt_Check_Email_Otp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
            txt_resend_email_otp.setVisibility(View.GONE);
            emailOtpVerified = true;
            edt_Check_Email_Otp.setEnabled(false);
            edt_Check_Email_Otp.setHint("Email Verified");
            edt_Check_Email_Otp.setHintTextColor(getResources().getColor(R.color.primary_green));
        } else {
            edt_Check_Email_Otp.setHint("Email PIN *");
        }

        if (MobileVerified != null && MobileVerified.equals("1")) {
            edt_Check_Mobile_Otp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
            txt_resend_mobile_otp.setVisibility(View.GONE);
            mobileOtpVerified = true;
            edt_Check_Mobile_Otp.setEnabled(false);
            edt_Check_Mobile_Otp.setHint("");
            edt_Check_Mobile_Otp.setHint("Mobile Verified");
            edt_Check_Mobile_Otp.setHintTextColor(getResources().getColor(R.color.primary_green));
        } else {
            edt_Check_Mobile_Otp.setHint("Mobile OTP *");
        }
        dialog11.show();
        btnContinue.setOnClickListener(view -> {
            if (emailOtpVerified && mobileOtpVerified) {
                dialog11.dismiss();
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberId", ClientId);
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberName", StrName);
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberEmailId", StrEmail);
                UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberMobile", StrMobile);
                if (verificationID != null) {
                    final PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, edt_Check_Mobile_Otp.getText().toString());
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    final Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                                    startActivity(i);
                                    overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                    finish();
                                } else {
                                    DisplayToastWarning(SignIn_SignUpActivity.this, "The verification code entered was invalid");
                                }

                            });
                }
            } else {
                DisplayToastWarning(getApplicationContext(), "Please verify both the OTP");
            }
        });

        btnClose.setOnClickListener(view -> dialog11.dismiss());

        txt_resend_mobile_otp.setOnClickListener(view -> resendMobilePin());

        txt_resend_email_otp.setOnClickListener(view -> resendEmailPin());

        edt_Check_Mobile_Otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input_text = charSequence.toString();
                if (input_text.length() == 4) {
                    MobileOtp = edt_Check_Mobile_Otp.getText().toString().trim();
                    VerifyMobileOtp(MobileOtp);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edt_Check_Email_Otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input_text = charSequence.toString();
                if (input_text.length() == 4) {
                    EmailPin = edt_Check_Email_Otp.getText().toString().trim();
                    VerifyEmailPin(EmailPin);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void resendMobilePin() {
        myDialog.show();
        final String URL_Resend_Mobile_Pin = ROOT_URL + "resendMobilePin.php";
        Log.d("UrlresendMobilePin", URL_Resend_Mobile_Pin);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_Resend_Mobile_Pin,
                response -> {
                    Log.d("TAG", "" + response);
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        final boolean status = jsonObject.getBoolean("status");
                        if (status) {
                            CommonMethods.DisplayToastSuccess(getApplicationContext(), "Mobile OTP Mail Sent Successfully");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    VolleyLog.d("TAG", "Error: " + error.getMessage());
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", ClientId);
                Log.d("resendparams", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
        requestQueue.add(strReq);
    }

    private void resendEmailPin() {
        myDialog.show();
        final String URL_Resend_Email_Pin = ROOT_URL + "resendEmailPin.php";
        Log.d("UrlresendMobilePin", URL_Resend_Email_Pin);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_Resend_Email_Pin,
                response -> {
                    Log.d("TAG", "" + response);
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        final boolean status = jsonObject.getBoolean("status");
                        if (status) {
                            CommonMethods.DisplayToastSuccess(getApplicationContext(), "Email Pin sent Successfully");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    VolleyLog.d("TAG", "Error: " + error.getMessage());
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", ClientId);
                Log.d("resendparams", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
        requestQueue.add(strReq);
    }

    private void VerifyEmailPin(final String EmailPin) {
        final String URL_EMAIL_VERIFY = ROOT_URL + "verifyUserEmail.php";
        Log.d("UrlEmailVerify", URL_EMAIL_VERIFY);
        StringRequest strReq = new StringRequest(Request.Method.POST, URL_EMAIL_VERIFY,
                response -> {
                    Log.d("TAG", "" + response);
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        final boolean status = jsonObject.getBoolean("status");
                        if (status) {
                            CommonMethods.DisplayToastSuccess(getApplicationContext(), "Email ID is Verified Successfully");
                            edt_Check_Email_Otp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
                            edt_Check_Email_Otp.setEnabled(false);
                            emailOtpVerified = true;
                            txt_resend_email_otp.setVisibility(View.GONE);
                        } else {
                            CommonMethods.DisplayToastError(getApplicationContext(), "Invalid Otp");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", ClientId);
                params.put("Email", StrEmail);
                params.put("EmailPin", EmailPin);
                Log.d("ParrasRoboLumpsum", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
        requestQueue.add(strReq);
    }

    private void VerifyMobileOtp(final String MobileOtp) {
        final String MobileVerifyUrl = ROOT_URL + "verifyUserMobile.php";
        Log.d("MobileVerfiyUrl", MobileVerifyUrl);
        StringRequest strReq = new StringRequest(Request.Method.POST, MobileVerifyUrl,
                response -> {
                    Log.d("TAG", "" + response);
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    try {
                        final JSONObject jsonObject = new JSONObject(response);
                        final boolean status = jsonObject.getBoolean("status");
                        if (status) {
                            CommonMethods.DisplayToastSuccess(getApplicationContext(), "Mobile No. is Verified Successfully");
                            edt_Check_Mobile_Otp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tick, 0);
                            edt_Check_Mobile_Otp.setEnabled(false);
                            mobileOtpVerified = true;
                            MobileVerified = "1";
                            txt_resend_mobile_otp.setVisibility(View.GONE);
                        } else {
                            CommonMethods.DisplayToastError(getApplicationContext(), "Invalid Otp");
                            mobileOtpVerified = false;
                            MobileVerified = "0";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    error.printStackTrace();
                }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", ClientId);
                params.put("Mobile", StrMobile);
                params.put("OTP", MobileOtp);
                Log.d("ParamsVerifyMobile", params.toString());
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
        requestQueue.add(strReq);
    }

    private void RESET() {
        Edt_SU_Fullname.setText("");
        Edt_SU_EmailId.setText("");
        Edt_SU_MobileNo.setText("");
        edt_Su_Password.setText("");
        Edt_Email_Mobile.setText("");
        Edt_Password.setText("");
    }

    private boolean isValidRegister() {
        boolean result = true;

        if (!MyValidator.isValidName(Edt_SU_Fullname)) {
            Edt_SU_Fullname.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Full Name");
            result = false;
        }

        if (!MyValidator.isValidEmail(Edt_SU_EmailId)) {
            Edt_SU_EmailId.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Email Id");
            result = false;
        }

        if (!MyValidator.isValidMobile(Edt_SU_MobileNo)) {
            Edt_SU_MobileNo.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Mobile No");
            result = false;
        }

        if (MyValidator.isValidField(edt_Su_Password)) {
            edt_Su_Password.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Password");
            result = false;
        }
        return result;
    }

    @Override
    public void onSuccessOtp(String otp) {
        if (smsBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsBroadcastReceiver);
        }
        edt_Check_Mobile_Otp.setText(otp);
    }

    private void popUpForgotPassword() {
        dialogForgotPassword = new Dialog(SignIn_SignUpActivity.this);
        dialogForgotPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogForgotPassword.setCanceledOnTouchOutside(false);
        dialogForgotPassword.setCancelable(false);
        dialogForgotPassword.setContentView(R.layout.pop_up_forget_password);
        dialogForgotPassword.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogForgotPassword.show();

        EditText edtEmail_Mobile = (EditText) dialogForgotPassword.findViewById(R.id.edtEmail_Mobile);

        ImageView iv_close = (ImageView) dialogForgotPassword.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(v -> dialogForgotPassword.dismiss());

        Button btn_get_otp = (Button) dialogForgotPassword.findViewById(R.id.btn_get_otp);
        btn_get_otp.setOnClickListener(view -> {
            if (!MyValidator.isValidEmailMobile(edtEmail_Mobile)) {
                edtEmail_Mobile.requestFocus();
                edtEmail_Mobile.setError("Please Enter Registered Email Id or Mobile No.");
                CommonMethods.DisplayToastWarning(getApplicationContext(), "Please enter Registered Email Id or Mobile No");
            } else {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(edtEmail_Mobile.getWindowToken(), 0);
                }
                StrEmailMobile = edtEmail_Mobile.getText().toString();
                GetAPI_ForgotPassword();
            }
        });
    }

    private void GetAPI_ForgotPassword() {
        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_SendPasswordOTP = ROOT_URL + "send_password_otp.php?_" + Uiid_id;
        try {
            Log.d("URL_SendPasswordOTP", URL_SendPasswordOTP);
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SendPasswordOTP,
                        response -> {
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            try {
                                if (response != null) {
                                    JSONObject resObj = new JSONObject(response);
                                    boolean status = resObj.getBoolean("status");
                                    String message = resObj.getString("message");
                                    if (status) {
                                        if (dialogForgotPassword != null && dialogForgotPassword.isShowing()) {
                                            dialogForgotPassword.dismiss();
                                        }
                                        JSONObject jsonObject = resObj.getJSONObject("data");
                                        ClientId = jsonObject.getString("id");
                                        CommonMethods.DisplayToastSuccess(getApplicationContext(), message);
                                        pop_up_otp_verification(true);
                                    } else {
                                        CommonMethods.DisplayToastError(getApplicationContext(), message);
                                    }
                                }
                            } catch (Exception e) {
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
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email_mobile", StrEmailMobile);
                        Log.d("ParrasUserRegi", params.toString());
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

    private void pop_up_otp_verification(final boolean showPopupResetPassword) {
        dialogEnteringOtp = new Dialog(SignIn_SignUpActivity.this);
        dialogEnteringOtp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEnteringOtp.setCanceledOnTouchOutside(false);
        dialogEnteringOtp.setCancelable(true);
        dialogEnteringOtp.setContentView(R.layout.custom_dialog_for_entering_otp);
        dialogEnteringOtp.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView til_text = (TextView) dialogEnteringOtp.findViewById(R.id.til_text);
        til_text.setText("User: " + StrEmailMobile);

        edt_verification_number = (EditText) dialogEnteringOtp.findViewById(R.id.edt_verification_number);
        final ImageView iv_close = (ImageView) dialogEnteringOtp.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(v -> {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            dialogEnteringOtp.dismiss();
        });

        final TextView Verify_otp = (TextView) dialogEnteringOtp.findViewById(R.id.Verify_otp);
        Verify_otp.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(edt_verification_number.getWindowToken(), 0);
            }
            String OTP_Entered = edt_verification_number.getText().toString();
            OtpVerifyApi(OTP_Entered);
        });
        dialogEnteringOtp.show();
    }

    private void OtpVerifyApi(final String OTP_Entered) {

        String Uiid_id = UUID.randomUUID().toString();
        String verifyOTP_Api;
        if (StrEmailMobile != null && StrEmailMobile.length() == 10) {
            verifyOTP_Api = ROOT_URL + "verifyUserMobile.php?_" + Uiid_id;
        } else {
            verifyOTP_Api = ROOT_URL + "verifyUserEmail.php?_" + Uiid_id;
        }
        try {
            Log.d("verifyOTP_Api", verifyOTP_Api);
            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                final StringRequest stringRequest = new StringRequest(Request.Method.POST, verifyOTP_Api,
                        response -> {
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            Log.d("mainResponse", response);
                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean status = jObj.getBoolean("status");
                                if (status) {
                                    edt_verification_number.setError(null);
                                    if (dialogEnteringOtp != null && dialogEnteringOtp.isShowing()) {
                                        dialogEnteringOtp.dismiss();
                                    }
                                    pop_up_reset_password();
                                } else {
                                    edt_verification_number.setError("Invalid OTP.");
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
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        if (StrEmailMobile.length() == 10) {
                            params.put("user_id", ClientId);
                            params.put("Mobile", StrEmailMobile);
                            params.put("OTP", OTP_Entered);
                        } else {
                            params.put("user_id", ClientId);
                            params.put("Email", StrEmailMobile);
                            params.put("EmailPin", OTP_Entered);
                        }
                        Log.d("ParrasUserRegi", params.toString());
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

    private void pop_up_reset_password() {

        dialogResetPassword = new Dialog(SignIn_SignUpActivity.this);
        dialogResetPassword.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogResetPassword.setCanceledOnTouchOutside(false);
        dialogResetPassword.setCancelable(true);
        dialogResetPassword.setContentView(R.layout.custom_dialog_for_reset_password);
        dialogResetPassword.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogResetPassword.show();
        TextView tv_registered_mobile_no = (TextView) dialogResetPassword.findViewById(R.id.tv_registered_mobile_no);
        tv_registered_mobile_no.setText(str_registered_mobile_no);
        final EditText etNewPassword = (EditText) dialogResetPassword.findViewById(R.id.etNewPassword);
        final EditText etConfirmPassword = (EditText) dialogResetPassword.findViewById(R.id.etConfirmPassword);
        final ImageView iv_close = (ImageView) dialogResetPassword.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(v -> {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
            dialogResetPassword.dismiss();
        });


        final Button btn_reset = (Button) dialogResetPassword.findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(view -> {
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
        });
    }

    private void ResetPasswordApi(final String strNewPassword, String strConfirmPassword) {
        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_ResetPassword = ROOT_URL + "change_password.php?_" + Uiid_id;
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
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", ClientId);
                        params.put("password", md5(strNewPassword));
                        Log.d("ParrasUserRegi", params.toString());
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
}
