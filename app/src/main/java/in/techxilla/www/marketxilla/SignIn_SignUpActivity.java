package in.techxilla.www.marketxilla;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.techxilla.www.marketxilla.service.SMSBroadcastReceiver;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.MyValidator;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplayToastWarning;
import static in.techxilla.www.marketxilla.utils.CommonMethods.md5;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SignIn_SignUpActivity extends AppCompatActivity implements SMSBroadcastReceiver.OTPReceiveListener {
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

        LayoutTabSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GOTO_SIGIN();
            }
        });

        LayoutTabSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutTabSignIn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
                LayoutTabSignUp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ll_parent_sign_in.setVisibility(View.GONE);
                ll_parent_sign_up.setVisibility(View.VISIBLE);
            }
        });

        Edt_Email_Mobile = (EditText) findViewById(R.id.Edt_Email_Mobile);
        Edt_Password = (EditText) findViewById(R.id.Edt_Password);
        final Button btnSignIn = (Button) findViewById(R.id.BtnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidLogin()) {
                    LogInApi();
                }
            }
        });

        Edt_SU_Fullname = (EditText) findViewById(R.id.Edt_SU_Fullname);
        Edt_SU_EmailId = (EditText) findViewById(R.id.Edt_SU_EmailId);
        Edt_SU_MobileNo = (EditText) findViewById(R.id.Edt_SU_MobileNo);
        edt_Su_Password = (EditText) findViewById(R.id.edt_Su_Password);

        final Button btnSignUp = (Button) findViewById(R.id.BtnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidRegister()) {
                    RegisterApi();
                }
            }
        });
    }

    private void GOTO_SIGIN() {
        LayoutTabSignIn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        LayoutTabSignUp.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        ll_parent_sign_in.setVisibility(View.VISIBLE);
        ll_parent_sign_up.setVisibility(View.GONE);
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
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
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
                                        } else if (MobileVerified.equalsIgnoreCase("0")) {
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
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
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
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
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
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
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
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailOtpVerified && mobileOtpVerified) {
                    dialog11.dismiss();
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberId", ClientId);
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberName", StrName);
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberEmailId", StrEmail);
                    UtilitySharedPreferences.setPrefs(getApplicationContext(), "MemberMobile", StrMobile);
                    if (verificationID != null) {
                        final PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID, edt_Check_Mobile_Otp.getText().toString());
                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            final Intent i = new Intent(getApplicationContext(), NewDashboard.class);
                                            startActivity(i);
                                            overridePendingTransition(R.animator.move_left, R.animator.move_right);
                                            finish();
                                        } else {
                                            DisplayToastWarning(SignIn_SignUpActivity.this, "The verification code entered was invalid");
                                        }

                                    }
                                });
                    }
                } else {
                    DisplayToastWarning(getApplicationContext(), "Please verify both the OTP");
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog11.dismiss();
            }
        });

        txt_resend_mobile_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendMobilePin();
            }
        });

        txt_resend_email_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendEmailPin();
            }
        });

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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d("TAG", response.toString());
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d("TAG", response.toString());
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("TAG", "Error: " + error.getMessage());
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d("TAG", response.toString());
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
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
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d("TAG", response.toString());
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
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
}
