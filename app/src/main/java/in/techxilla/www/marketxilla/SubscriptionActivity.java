package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
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

    ProgressDialog myDialog;
    TextView tv_title;
    ImageView iv_back;
    TextView tv_silver_1,tv_silver_2,tv_silver_3;
    TextView tv_gold_1,tv_gold_2,tv_gold_3;
    TextView tv_platinum_1,tv_platinum_2,tv_platinum_3;
    TextView tv_fs_1,tv_fs_2,tv_fs_3;
    TextView tv_os_1,tv_os_2,tv_os_3;
    TextView tv_cs_1,tv_cs_2,tv_cs_3;

    Spinner Spn_Package,Spn_Plan;

    ArrayList<String> planList = new ArrayList<String>();
    ArrayList<Double> planAmountList = new ArrayList<Double>();

    String StrPackage="",StrPlanSelected = "";
    Double StrPlanAmount = 0.0;
    String StrUpiAccountId="",StrUPI_MerchantName="",StrSubscrptionAmount="";
    final int UPI_PAYMENT = 0;

    Button btnSubscribe;
    ViewGroup viewGroup;

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

        tv_title = (TextView)findViewById(R.id.tv_title);
        iv_back = (ImageView)findViewById(R.id.back_btn_toolbar);

        tv_title.setText("PACKAGES");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getBankDetail();
        tv_silver_1 = (TextView) findViewById(R.id.tv_silver_1) ;
        tv_silver_2 = (TextView) findViewById(R.id.tv_silver_2) ;
        tv_silver_3 = (TextView) findViewById(R.id.tv_silver_3) ;
        tv_gold_1 = (TextView) findViewById(R.id.tv_gold_1) ;
        tv_gold_2 = (TextView) findViewById(R.id.tv_gold_2) ;
        tv_gold_3 = (TextView) findViewById(R.id.tv_gold_3) ;

        tv_platinum_1= (TextView) findViewById(R.id.tv_platinum_1) ;
        tv_platinum_2= (TextView) findViewById(R.id.tv_platinum_2) ;
        tv_platinum_3= (TextView) findViewById(R.id.tv_platinum_3) ;

        tv_fs_1= (TextView) findViewById(R.id.tv_fs_1) ;
        tv_fs_2= (TextView) findViewById(R.id.tv_fs_2) ;
        tv_fs_3= (TextView) findViewById(R.id.tv_fs_3) ;

        tv_os_1= (TextView) findViewById(R.id.tv_os_1) ;
        tv_os_2= (TextView) findViewById(R.id.tv_os_2) ;
        tv_os_3= (TextView) findViewById(R.id.tv_os_3) ;

        tv_cs_1= (TextView) findViewById(R.id.tv_cs_1) ;
        tv_cs_2= (TextView) findViewById(R.id.tv_cs_2) ;
        tv_cs_3= (TextView) findViewById(R.id.tv_cs_3) ;

        Spn_Package = (Spinner)findViewById(R.id.Spn_Package) ;
        Spn_Plan = (Spinner)findViewById(R.id.Spn_Plan);


        Spn_Package.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    StrPackage = Spn_Package.getSelectedItem().toString();
                    if(StrPackage.equalsIgnoreCase("1 Month")){
                        fetchPlans(1);
                    }else  if(StrPackage.equalsIgnoreCase("2 Month")){
                        fetchPlans(2);
                    }else  if(StrPackage.equalsIgnoreCase("3 Month")){
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
                if(position>0){
                    StrPlanSelected = Spn_Plan.getSelectedItem().toString();

                    StrPlanAmount = planAmountList.get(position);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSubscribe = (Button)findViewById(R.id.btnSubscribe);
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {

                if(isValidSubcription()) {
                    //CommonMethods.DisplayToastInfo(getApplicationContext(), ""+StrPackage + "-->"+StrPlanSelected + "-->" +StrPlanAmount);

                    StrSubscrptionAmount = String.format ("%.2f", StrPlanAmount);


                    Uri uri =
                            new Uri.Builder()
                                    .scheme("upi")
                                    .authority("pay")
                                    .appendQueryParameter("pa", StrUpiAccountId)       // virtual ID
                                    .appendQueryParameter("pn", StrUPI_MerchantName)          // name
                                    .appendQueryParameter("am", StrSubscrptionAmount)           // amount
                                    .appendQueryParameter("cu", "INR")                         // currency
                                    .build();

        /*String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        int GOOGLE_PAY_REQUEST_CODE = 123;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
        */

                    Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
                    upiPayIntent.setData(uri);
                    Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
                    if(null != chooser.resolveActivity(getPackageManager())) {
                        startActivityForResult(chooser, UPI_PAYMENT);
                    } else {
                        //CommonMethods.DisplayToastWarning(getApplicationContext(),"No UPI app found, please install one to continue");
                        DisplaySnackBar(viewGroup,"No UPI app found, please install one to continue","WARNING");

                    }
                }
            }
        });

        fetchPlans(1);

    }


    private void getBankDetail() {
        String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL +"get_bank_details.php?_"+Uiid_id;
        Log.d("URL --->", get_bank_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response",""+response);

                            JSONObject Jobj = new JSONObject(response);

                            boolean status = Jobj.getBoolean("status");

                            if(status) {

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
                            Log.d("Exception",e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        //Log.d("Vollley Err", volleyError.toString());
                        DisplaySnackBar(viewGroup,"Something goes wrong. Please try again","WARNING");

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20),0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            }else {
                DisplaySnackBar(viewGroup,"No Internet Connection","WARNING");
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }


    private boolean isValidSubcription() {
        boolean result = true;

        if (!MyValidator.isValidSpinner(Spn_Package)) {
            Spn_Package.requestFocus();
            result = false;
        }

        if (!MyValidator.isValidSpinner(Spn_Plan) ) {
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

        try {
            JSONObject obj = new JSONObject(loadJSONFromAssetPlans());
            JSONArray m_jArry = obj.getJSONArray("planList");


            for (int i = 0; i < m_jArry.length(); i++) {

                JSONObject jo_inside = m_jArry.getJSONObject(i);

                //Silver
                if(jo_inside.getInt("PackageId")==1 && jo_inside.getString("PlanName").equalsIgnoreCase("SILVER")){
                    tv_silver_1.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==2 && jo_inside.getString("PlanName").equalsIgnoreCase("SILVER")){
                    tv_silver_2.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==3 && jo_inside.getString("PlanName").equalsIgnoreCase("SILVER")){
                    tv_silver_3.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                //GOLD
                if(jo_inside.getInt("PackageId")==1 && jo_inside.getString("PlanName").equalsIgnoreCase("GOLD")){
                    tv_gold_1.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==2 && jo_inside.getString("PlanName").equalsIgnoreCase("GOLD")){
                    tv_gold_2.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==3 && jo_inside.getString("PlanName").equalsIgnoreCase("GOLD")){
                    tv_gold_3.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                //Platinum
                if(jo_inside.getInt("PackageId")==1 && jo_inside.getString("PlanName").equalsIgnoreCase("PLATINUM")){
                    tv_platinum_1.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==2 && jo_inside.getString("PlanName").equalsIgnoreCase("PLATINUM")){
                    tv_platinum_2.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==3 && jo_inside.getString("PlanName").equalsIgnoreCase("PLATINUM")){
                    tv_platinum_3.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                //Future Special
                if(jo_inside.getInt("PackageId")==1 && jo_inside.getString("PlanName").equalsIgnoreCase("FUTURE SPECIAL")){
                    tv_fs_1.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==2 && jo_inside.getString("PlanName").equalsIgnoreCase("FUTURE SPECIAL")){
                    tv_fs_2.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==3 && jo_inside.getString("PlanName").equalsIgnoreCase("FUTURE SPECIAL")){
                    tv_fs_3.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                //Option Special
                if(jo_inside.getInt("PackageId")==1 && jo_inside.getString("PlanName").equalsIgnoreCase("OPTION SPECIAL")){
                    tv_os_1.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==2 && jo_inside.getString("PlanName").equalsIgnoreCase("OPTION SPECIAL")){
                    tv_os_2.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==3 && jo_inside.getString("PlanName").equalsIgnoreCase("OPTION SPECIAL")){
                    tv_os_3.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                //Comodity Special
                if(jo_inside.getInt("PackageId")==1 && jo_inside.getString("PlanName").equalsIgnoreCase("COMMODITY SPECIAL")){
                    tv_cs_1.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==2 && jo_inside.getString("PlanName").equalsIgnoreCase("COMMODITY SPECIAL")){
                    tv_cs_2.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")==3 && jo_inside.getString("PlanName").equalsIgnoreCase("COMMODITY SPECIAL")){
                    tv_cs_3.setText("\u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                }

                if(jo_inside.getInt("PackageId")== package_id) {
                    String PlanName = jo_inside.getString("PlanName");
                    double PlanAmount = jo_inside.getDouble("PlanAmount");

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
        Intent i = new Intent(getApplicationContext(),NewDashboard.class);
        startActivity(i);
        overridePendingTransition(R.animator.left_right,R.animator.right_left);
        finish();
    }


}
