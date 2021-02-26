/*
package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.MyValidator;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SubscriptionActivity extends AppCompatActivity {
    final int UPI_PAYMENT = 0;
    private ProgressDialog myDialog;
    private TextView tv_title;
    private ImageView iv_back;
    private TextView tv_silver_1, tv_silver_2, tv_silver_3;
    private TextView tv_gold_1, tv_gold_2, tv_gold_3;
    private TextView tv_platinum_1, tv_platinum_2, tv_platinum_3;
    private TextView tv_fs_1, tv_fs_2, tv_fs_3;
    private TextView tv_os_1, tv_os_2, tv_os_3;
    private TextView tv_cs_1, tv_cs_2, tv_cs_3;
    private Spinner Spn_Package, Spn_Plan;
    private ArrayList<String> planList = new ArrayList<String>();
    private ArrayList<Double> planAmountList = new ArrayList<Double>();
    private String StrPackage, StrPlanSelected;
    private double StrPlanAmount = 0.0;
    private String StrUpiAccountId = "", StrUPI_MerchantName = "", StrSubscrptionAmount = "", status;
    private Button btnSubscribe;
    private ViewGroup viewGroup;
    private String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    private int GOOGLE_PAY_REQUEST_CODE = 123;
    private Uri uri;

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions);


        viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        init();
    }

    private void init() {

        myDialog = new ProgressDialog(this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.back_btn_toolbar);

        tv_title.setText("PACKAGES");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getBankDetail();
        tv_silver_1 = (TextView) findViewById(R.id.tv_silver_1);
        tv_silver_2 = (TextView) findViewById(R.id.tv_silver_2);
        tv_silver_3 = (TextView) findViewById(R.id.tv_silver_3);
        tv_gold_1 = (TextView) findViewById(R.id.tv_gold_1);
        tv_gold_2 = (TextView) findViewById(R.id.tv_gold_2);
        tv_gold_3 = (TextView) findViewById(R.id.tv_gold_3);

        tv_platinum_1 = (TextView) findViewById(R.id.tv_platinum_1);
        tv_platinum_2 = (TextView) findViewById(R.id.tv_platinum_2);
        tv_platinum_3 = (TextView) findViewById(R.id.tv_platinum_3);

        tv_fs_1 = (TextView) findViewById(R.id.tv_fs_1);
        tv_fs_2 = (TextView) findViewById(R.id.tv_fs_2);
        tv_fs_3 = (TextView) findViewById(R.id.tv_fs_3);

        tv_os_1 = (TextView) findViewById(R.id.tv_os_1);
        tv_os_2 = (TextView) findViewById(R.id.tv_os_2);
        tv_os_3 = (TextView) findViewById(R.id.tv_os_3);

        tv_cs_1 = (TextView) findViewById(R.id.tv_cs_1);
        tv_cs_2 = (TextView) findViewById(R.id.tv_cs_2);
        tv_cs_3 = (TextView) findViewById(R.id.tv_cs_3);

        Spn_Package = (Spinner) findViewById(R.id.Spn_Package);
        Spn_Plan = (Spinner) findViewById(R.id.Spn_Plan);


        Spn_Package.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    StrPackage = Spn_Package.getSelectedItem().toString();
                    if (StrPackage.equalsIgnoreCase("1 Month")) {
                        fetchPlans(1);
                    } else if (StrPackage.equalsIgnoreCase("2 Month")) {
                        fetchPlans(2);
                    } else if (StrPackage.equalsIgnoreCase("3 Month")) {
                        fetchPlans(3);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spn_Plan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    StrPlanSelected = Spn_Plan.getSelectedItem().toString();

                    StrPlanAmount = planAmountList.get(position);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {

                // if (isValidSubcription())
                {
                    //CommonMethods.DisplayToastInfo(getApplicationContext(), ""+StrPackage + "-->"+StrPlanSelected + "-->" +StrPlanAmount);

                    StrSubscrptionAmount = "1.00"; //String.format("%.2f", StrPlanAmount);


                    uri = new Uri.Builder()
                            .scheme("upi")
                            .authority("pay")
                            .appendQueryParameter("pa", StrUpiAccountId)       // virtual ID
                            .appendQueryParameter("pn", StrUPI_MerchantName)          // name
                            .appendQueryParameter("am", StrSubscrptionAmount)           // amount
                            .appendQueryParameter("cu", "INR")                         // currency
                            .build();

                    payWithGPay();
                   */
/* Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                    upiPayIntent.setData(uri);
                    Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
                    if (null != chooser.resolveActivity(getPackageManager())) {
                        startActivityForResult(chooser, UPI_PAYMENT);
                    } else {
                        //CommonMethods.DisplayToastWarning(getApplicationContext(),"No UPI app found, please install one to continue");
                        DisplaySnackBar(viewGroup, "No UPI app found, please install one to continue", "WARNING");

                    }*//*

                }
            }
        });

        fetchPlans(1);

    }

    private void payWithGPay() {
        if (isAppInstalled(SubscriptionActivity.this, GOOGLE_PAY_PACKAGE_NAME)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
        } else {
            Toast.makeText(SubscriptionActivity.this, "Please Install GPay", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
        }

        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(SubscriptionActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(SubscriptionActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void getBankDetail() {
        String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL + "get_bank_details.php?_" + Uiid_id;
        Log.d("URL --->", get_bank_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response", "" + response);

                            JSONObject Jobj = new JSONObject(response);

                            boolean status = Jobj.getBoolean("status");

                            if (status) {

                                String data = Jobj.getString("data");
                                JSONObject jobject = new JSONObject(data);

                                String Id = jobject.getString("id");
                                String beneficiary_name = jobject.getString("beneficiary_name");
                                String bank_name = jobject.getString("bank_name");
                                String account_no = jobject.getString("account_no");
                                String ifsc_code = jobject.getString("ifsc_code");
                                StrUpiAccountId = jobject.getString("upi_id");
                                StrUPI_MerchantName = jobject.getString("upi_merchant_name");
                                String branch = jobject.getString("branch");


                            }

                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        //Log.d("Vollley Err", volleyError.toString());
                        DisplaySnackBar(viewGroup, "Something goes wrong. Please try again", "WARNING");

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            } else {
                DisplaySnackBar(viewGroup, "No Internet Connection", "WARNING");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    private boolean isValidSubcription() {
        boolean result = true;

        if (!MyValidator.isValidSpinner(Spn_Package)) {
            Spn_Package.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spn_Plan)) {
            Spn_Plan.requestFocus();
            result = false;
        }

        return result;
    }

    private void fetchPlans(int package_id) {
        planList = new ArrayList<>();
        planAmountList = new ArrayList<>();
        planList.add("Select Plan");
        planAmountList.add(0.0);
        String Uiid_id = UUID.randomUUID().toString();
        String URL_GetSubscriptions = ROOT_URL + "get_subscriptions.php"; //+ Uiid_id;;


        try {
            Log.d("URL", URL_GetSubscriptions);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetSubscriptions,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray m_jArry = obj.getJSONArray("planList");


                                    for (int i = 0; i < m_jArry.length(); i++) {

                                        JSONObject jo_inside = m_jArry.getJSONObject(i);

                                        //Silver
                                        if (jo_inside.getInt("id") == 1 && jo_inside.getString("plan_name").equalsIgnoreCase("SILVER")) {
                                            tv_silver_1.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount1_month")));
                                        }

                                        if (jo_inside.getInt("id") == 2 && jo_inside.getString("plan_name").equalsIgnoreCase("SILVER")) {
                                            tv_silver_2.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount2_month")));
                                        }

                                        if (jo_inside.getInt("id") == 3 && jo_inside.getString("plan_name").equalsIgnoreCase("SILVER")) {
                                            tv_silver_3.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount3_month")));
                                        }

                                        //GOLD
                                        if (jo_inside.getInt("id") == 1 && jo_inside.getString("plan_name").equalsIgnoreCase("GOLD")) {
                                            tv_gold_1.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount1_month")));
                                        }

                                        if (jo_inside.getInt("id") == 2 && jo_inside.getString("plan_name").equalsIgnoreCase("GOLD")) {
                                            tv_gold_2.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount2_month")));
                                        }

                                        if (jo_inside.getInt("id") == 3 && jo_inside.getString("plan_name").equalsIgnoreCase("GOLD")) {
                                            tv_gold_3.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount3_month")));
                                        }

                                        //Platinum
                                        if (jo_inside.getInt("id") == 1 && jo_inside.getString("plan_name").equalsIgnoreCase("PLATINUM")) {
                                            tv_platinum_1.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount1_month")));
                                        }

                                        if (jo_inside.getInt("id") == 2 && jo_inside.getString("plan_name").equalsIgnoreCase("PLATINUM")) {
                                            tv_platinum_2.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount2_month")));
                                        }

                                        if (jo_inside.getInt("id") == 3 && jo_inside.getString("plan_name").equalsIgnoreCase("PLATINUM")) {
                                            tv_platinum_3.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount3_month")));
                                        }

                                        //Future Special
                                        if (jo_inside.getInt("id") == 1 && jo_inside.getString("plan_name").equalsIgnoreCase("FUTURE SPECIAL")) {
                                            tv_fs_1.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount1_month")));
                                        }

                                        if (jo_inside.getInt("id") == 2 && jo_inside.getString("plan_name").equalsIgnoreCase("FUTURE SPECIAL")) {
                                            tv_fs_2.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount2_month")));
                                        }

                                        if (jo_inside.getInt("id") == 3 && jo_inside.getString("plan_name").equalsIgnoreCase("FUTURE SPECIAL")) {
                                            tv_fs_3.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount3_month")));
                                        }

                                        //Option Special
                                        if (jo_inside.getInt("id") == 1 && jo_inside.getString("plan_name").equalsIgnoreCase("OPTION SPECIAL")) {
                                            tv_os_1.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount1_month")));
                                        }

                                        if (jo_inside.getInt("id") == 2 && jo_inside.getString("plan_name").equalsIgnoreCase("OPTION SPECIAL")) {
                                            tv_os_2.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount2_month")));
                                        }

                                        if (jo_inside.getInt("id") == 3 && jo_inside.getString("plan_name").equalsIgnoreCase("OPTION SPECIAL")) {
                                            tv_os_3.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount3_month")));
                                        }

                                        //Comodity Special
                                        if (jo_inside.getInt("id") == 1 && jo_inside.getString("plan_name").equalsIgnoreCase("COMMODITY SPECIAL")) {
                                            tv_cs_1.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount1_month")));
                                        }

                                        if (jo_inside.getInt("id") == 2 && jo_inside.getString("plan_name").equalsIgnoreCase("COMMODITY SPECIAL")) {
                                            tv_cs_2.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount2_month")));
                                        }

                                        if (jo_inside.getInt("id") == 3 && jo_inside.getString("plan_name").equalsIgnoreCase("COMMODITY SPECIAL")) {
                                            tv_cs_3.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("plan_amount2_month")));
                                        }

                                        if (jo_inside.getInt("id") == package_id) {
                                            String PlanName = jo_inside.getString("plan_name");
                                            double PlanAmount = jo_inside.getDouble("plan_amount1_month");

                                            planList.add(PlanName);
                                            planAmountList.add(PlanAmount);
                                        }
                                    }

                                    ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, planList);
                                    Spn_Plan.setAdapter(countryAdapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }

                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);


            } else {
                CommonMethods.DisplayToastInfo(getApplicationContext(), "No Internet Connection");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private String loadJSONFromAssetPlans() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("plan.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), NewDashboard.class);
        startActivity(i);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }


}
*/
