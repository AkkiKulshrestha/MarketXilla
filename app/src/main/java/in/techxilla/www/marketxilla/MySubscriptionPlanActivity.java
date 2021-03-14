package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import in.techxilla.www.marketxilla.model.SubscriptionPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class MySubscriptionPlanActivity extends AppCompatActivity {
    private ArrayList<SubscriptionPlanModel> planList;
    private ProgressDialog myDialog;
    private Button btnSubscribe;
    private int mStackCount;
    private ScrollView scrollView;
    private LinearLayout ll_parent_subscription_plan, ll_details;
    private String plan_name, date, mSubscribed_till, mSubscribed_on, mShortDate;
    private String mUserId, mTransactionId, fileName, mPdf_name, mTransaction_Message;
    private TextView tv_title_plan, tv_valid_till;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        init();
    }

    private void init() {
        final TextView tv_title = findViewById(R.id.tv_title);
        final ImageView iv_back = findViewById(R.id.back_btn_toolbar);

        tv_title.setText(getResources().getString(R.string.my_subscription));
        iv_back.setOnClickListener(v -> onBackPressed());

        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adView);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        mAdView.loadAd(adRequestBuilder.build());

        planList = new ArrayList<>();
        scrollView = findViewById(R.id.scrollView);
        ll_parent_subscription_plan = findViewById(R.id.ll_parent_subscription_plan);
        ll_details = findViewById(R.id.ll_details);
        tv_title_plan = findViewById(R.id.tv_title_plan);
        tv_valid_till = findViewById(R.id.tv_valid_till);
        btnSubscribe = findViewById(R.id.btnSubscribe);
        btnSubscribe.setOnClickListener(v -> {
            {
                Intent i = new Intent(MySubscriptionPlanActivity.this, NewDashboard.class);
                i.putExtra("load_fragment", 2);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPlanDetail();
    }

    @SuppressLint({"SetTextI18n", "InflateParams", "SimpleDateFormat"})
    private void getPlanDetail() {
        myDialog = new ProgressDialog(this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        if (ll_parent_subscription_plan != null && ll_parent_subscription_plan.getChildCount() > 0) {
            ll_parent_subscription_plan.removeAllViews();
        }
        if (ll_details != null && ll_details.getChildCount() > 0) {
            ll_details.removeAllViews();
        }
        if (planList != null) {
            planList = new ArrayList<>();
        }
        final String Uiid_id = UUID.randomUUID().toString();
        final String StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        final String get_plan_details_info = ROOT_URL + "get_user_subscription_details.php?_" + Uiid_id + "&user_id=" + StrMemberId;
        Log.d("URL --->", get_plan_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_plan_details_info, response -> {
                    try {
                        Log.d("Response", "" + response);
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        final JSONObject obj = new JSONObject(response);
                        final boolean status = obj.getBoolean("status");
                        final String Message = obj.getString("message");
                        final JSONArray m_jArry = obj.getJSONArray("data");
                        mStackCount = m_jArry.length();
                        if (mStackCount == 0) {
                            tv_title_plan.setText("NO ACTIVE PLAN");
                            tv_valid_till.setVisibility(View.GONE);
                            btnSubscribe.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < m_jArry.length(); i++) {
                            final JSONObject jo_data = m_jArry.getJSONObject(i);
                            mTransactionId = jo_data.getString("transaction_id");
                            final String id = jo_data.getString("id");
                            mUserId = jo_data.getString("user_id");
                            final String plan_id = jo_data.getString("plan_id");
                            final String subscribed_on = jo_data.getString("subscribed_on");
                            final String subscribed_till = jo_data.getString("subscribed_till");
                            final String payment_detail = jo_data.getString("payment_detail");
                            mPdf_name = jo_data.getString("pdf_name");
                            plan_name = jo_data.getString("plan_name");
                            final String package_id = jo_data.getString("package_id");
                            final String plan_description = jo_data.getString("plan_description");
                            final String plan_amount1_month = jo_data.getString("plan_amount1_month");
                            final String plan_amount2_month = jo_data.getString("plan_amount2_month");
                            final String plan_amount3_month = jo_data.getString("plan_amount3_month");

                            final LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView1 = Objects.requireNonNull(inflater).inflate(R.layout.row_subscription_plan, null);
                            rowView1.setPadding(10, 10, 10, 10);

                            final TextView tv_Subscripte_till_date = rowView1.findViewById(R.id.tv_Subscripte_till_date);
                            final TextView tv_Subscripte_on_date = rowView1.findViewById(R.id.tv_Subscripte_on_date);
                            final TextView tv_plan_name = rowView1.findViewById(R.id.tv_plan_name);
                            final TextView tv_plan_amount = rowView1.findViewById(R.id.tv_plan_amount);
                            final TextView tv_status = rowView1.findViewById(R.id.tv_status);
                            final TextView tv_status_text = rowView1.findViewById(R.id.tv_status_text);
                            final TextView tv_status_text1 = rowView1.findViewById(R.id.tv_status_text1);
                            final Button btn_payment_receipt = rowView1.findViewById(R.id.btn_payment_receipt);

                            String PlanAmount = "", StrTransactionId = "", PaymentDetailsObj = "";
                            if (payment_detail != null && !payment_detail.isEmpty()) {
                                if (payment_detail.contains("|")) {
                                    String[] paymentDetail = payment_detail.split("\\|");
                                    StrTransactionId = paymentDetail[0];
                                    PlanAmount = paymentDetail[1];
                                    PaymentDetailsObj = paymentDetail[2];
                                } else {
                                    JSONObject transactionObj = new JSONObject(payment_detail);
                                    StrTransactionId = transactionObj.getString("transaction_id");
                                    PlanAmount = transactionObj.getString("transaction_amount");
                                }
                            }
                            btn_payment_receipt.setHint(mPdf_name);
                            tv_plan_name.setText(plan_name);


                            if (package_id.equalsIgnoreCase("1")) {
                                tv_plan_amount.setText("Amount : \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount1_month));
                            } else if (package_id.equalsIgnoreCase("2")) {
                                tv_plan_amount.setText("Amount : \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount2_month));
                            } else if (package_id.equalsIgnoreCase("3")) {
                                tv_plan_amount.setText("Amount : \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount3_month));
                            } else {
                                tv_plan_amount.setText("Amount : \u20B9 " + PlanAmount);
                            }


                            SimpleDateFormat sdf, sdf2, sdf21;
                            Date newSubscriptedTilldate, currentdate2, newSubscriptedOndate;

                            if (!subscribed_till.equalsIgnoreCase("")) {
                                try {
                                    sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    newSubscriptedTilldate = sdf.parse(subscribed_till);
                                    sdf21 = new SimpleDateFormat("dd MMM, yyyy");
                                    sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                                    mSubscribed_till = sdf21.format(newSubscriptedTilldate);
                                    final String SubcribedTill_DMY = sdf2.format(newSubscriptedTilldate);
                                    if (new Date().after(newSubscriptedTilldate)) {
                                        tv_Subscripte_till_date.setText(mSubscribed_till);
                                        tv_status_text.setText("Your Plan is Expired.");
                                        tv_status_text1.setText("Buy the best plan and enjoy your service");
                                        btn_payment_receipt.setText("Download Transaction Details");

                                        btn_payment_receipt.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                final String path_link = ROOT_URL + "payment_receipt/" + btn_payment_receipt.getHint().toString().trim();
                                                final Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse(path_link));
                                                startActivity(intent);
                                            }
                                        });

                                        tv_status.setText("InActive");
                                        tv_status.setTextColor(getResources().getColor(R.color.md_red_a400));
                                    } else {
                                        tv_Subscripte_till_date.setText(mSubscribed_till);
                                        tv_status_text.setText("Your plan is currently Active.");
                                        tv_status_text1.setText("Enjoy your Service");
                                        btn_payment_receipt.setText("Download Transaction Details");

                                        btn_payment_receipt.setOnClickListener(v -> {
                                            final String path_link = ROOT_URL + "payment_receipt/" + btn_payment_receipt.getHint().toString().trim();
                                            final Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(path_link));
                                            startActivity(intent);
                                        });

                                        tv_status.setText("Active");
                                        tv_status.setTextColor(getResources().getColor(R.color.selected_green));
                                    }

                                    if (i == 0) {
                                        if (!CommonMethods.isDateExpired(SubcribedTill_DMY)) {
                                            tv_title_plan.setText("Current Plan : " + plan_name);
                                            tv_valid_till.setVisibility(View.VISIBLE);
                                            tv_valid_till.setText("Valid Till \n" + mSubscribed_till);
                                            btnSubscribe.setVisibility(View.GONE);
                                        } else {
                                            tv_title_plan.setText("NO ACTIVE PLAN");
                                            tv_valid_till.setVisibility(View.GONE);
                                            btnSubscribe.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!subscribed_on.equalsIgnoreCase("")) {
                                try {
                                    sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    currentdate2 = sdf2.parse(subscribed_on);
                                    sdf21 = new SimpleDateFormat("dd MMM, yyyy");
                                    mSubscribed_on = sdf21.format(currentdate2);
                                    newSubscriptedOndate = sdf2.parse(subscribed_on);
                                    mShortDate = new SimpleDateFormat("MMM yyyy").format(Objects.requireNonNull(newSubscriptedOndate));
                                    if (new Date().after(currentdate2)) {
                                        tv_Subscripte_on_date.setText(mSubscribed_on);
                                        // tv_status.setText("In Active");
                                    } else {
                                        tv_Subscripte_on_date.setText(mSubscribed_on);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            ll_parent_subscription_plan.addView(rowView1);

                            final LayoutInflater inflater2 = (LayoutInflater) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView2 = Objects.requireNonNull(inflater2).inflate(R.layout.plans_items, null);
                            rowView2.setPadding(10, 10, 10, 10);

                            final LinearLayout ll_header = rowView2.findViewById(R.id.ll_header);
                            final TableLayout table_layout = rowView2.findViewById(R.id.table_layout);
                            final ImageView img_add = rowView2.findViewById(R.id.img_add);
                            img_add.setOnClickListener(view -> {
                                if (table_layout.getVisibility() == View.VISIBLE) {
                                    table_layout.setVisibility(View.GONE);
                                    Glide.with(MySubscriptionPlanActivity.this).
                                            load(R.mipmap.ic_down_white).
                                            diskCacheStrategy(DiskCacheStrategy.ALL).into(img_add);
                                } else {
                                    table_layout.setVisibility(View.VISIBLE);
                                    ScrollDown();
                                    Glide.with(MySubscriptionPlanActivity.this).
                                            load(R.mipmap.ic_up_white).
                                            diskCacheStrategy(DiskCacheStrategy.ALL).into(img_add);
                                }
                            });
                            final TextView date = rowView2.findViewById(R.id.date);
                            final TextView tv_transaction_details = rowView2.findViewById(R.id.tv_transaction_details);
                            final TextView tv_Subscribed_till = rowView2.findViewById(R.id.tv_Subscribed_till);
                            final TextView tv_subscribed_on = rowView2.findViewById(R.id.tv_subscribed_on);
                            final TextView tv_planname = rowView2.findViewById(R.id.tv_planname);
                            final TextView tv_paying_via = rowView2.findViewById(R.id.tv_paying_via);
                            final TextView tv_amount = rowView2.findViewById(R.id.tv_amount);
                            final TextView tv_payment_receipt = rowView2.findViewById(R.id.tv_payment_receipt);
                            final TextView tv_Receipt_on_email = rowView2.findViewById(R.id.tv_Receipt_on_email);

                            tv_payment_receipt.setHint(mPdf_name);
                            tv_payment_receipt.setOnClickListener(view -> {
                                final String path_link = ROOT_URL + "payment_receipt/" + tv_payment_receipt.getHint().toString().trim();
                                final Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(path_link));
                                startActivity(intent);

                            });
                            tv_Receipt_on_email.setHint(mTransactionId);
                            tv_Receipt_on_email.setOnClickListener(view -> getTransactionDetails(mUserId, tv_Receipt_on_email.getHint().toString()));

                            date.setText(mShortDate);
                            if (package_id != null && !package_id.equalsIgnoreCase("0")) {
                                tv_planname.setText(plan_name + " - " + package_id + " Months");
                            } else {
                                tv_planname.setText(plan_name + " - 3 Days");
                            }
                            if (package_id.equalsIgnoreCase("1")) {
                                tv_amount.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount1_month));
                            } else if (package_id.equalsIgnoreCase("2")) {
                                tv_amount.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount2_month));
                            } else if (package_id.equalsIgnoreCase("3")) {
                                tv_amount.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount3_month));
                            } else {
                                tv_amount.setText("\u20B9 " + PlanAmount);
                            }

                            if (!StrTransactionId.isEmpty()) {
                                tv_transaction_details.setText(StrTransactionId);
                            } else {
                                tv_transaction_details.setText("No Available Data");
                            }
                            tv_Subscribed_till.setText(mSubscribed_till);
                            tv_subscribed_on.setText(mSubscribed_on);

                            if (PaymentDetailsObj != null && !PaymentDetailsObj.isEmpty()) {
                                JSONObject transactionObj = new JSONObject(PaymentDetailsObj);
                                String paymentMode = transactionObj.getString("mode");
                                tv_paying_via.setText(paymentMode);
                            } else {
                                tv_paying_via.setText("No Available Data");
                            }

                            ll_details.addView(rowView2);
                        }
                    } catch (Exception e) {
                        Log.d("Exception", e.toString());
                    }
                }, volleyError -> {
                    volleyError.printStackTrace();
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastWarning(getApplicationContext(), "No Internet Connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getTransactionDetails(String mUserId, String mTransactionId) {
        final String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL + "send_transaction_receipt_on_mail.php?" + Uiid_id + "&user_id=" + mUserId + "&transaction_id=" + mTransactionId;
        Log.d("Details URL --->", get_bank_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, response -> {
                    try {
                        Log.d("Response", "" + response);
                        final JSONObject Jobj = new JSONObject(response);
                        final boolean status = Jobj.getBoolean("status");
                        final String message = Jobj.getString("message");
                        if (status) {
                            CommonMethods.DisplayToastSuccess(getApplicationContext(), message);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);
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

    private void ScrollDown() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }

    @Override
    public void onBackPressed() {
        final Intent i = new Intent(getApplicationContext(), NewDashboard.class);
        startActivity(i);
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadAsync extends AsyncTask<String, Integer, String> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            CommonMethods.DisplayToast(MySubscriptionPlanActivity.this, "Start download");
            // pogressDialog();
        }

        @Override
        protected String doInBackground(String... url) {


            File mydir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (!mydir.exists()) {
                mydir.mkdirs();
            }
            try {
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                String url_link = url[0];
                Uri downloadUri = Uri.parse(url_link);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);

                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(String.valueOf(downloadUri));
                // concatinate above fileExtension to fileName
                fileName = "." + fileExtension;
                Log.e("File Extension", "" + fileName);

                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date = dateFormat.format(new Date());

                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("Downloading")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        //.setMimeType(fileExtension)
                        .setDestinationInExternalPublicDir("/MarketXill", date + fileName);

                manager.enqueue(request);
            } catch (RuntimeException e) {
                Log.d("SubscriptionPlan", e.getMessage());
            }
            return mydir.getPath() + File.separator + date + fileName;
        }

        @Override
        public void onPostExecute(String s) {
            super.onPostExecute(s);
            CommonMethods.DisplayToast(MySubscriptionPlanActivity.this, "Successfully completed download.");
        }
    }
}
