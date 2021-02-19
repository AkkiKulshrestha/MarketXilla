package in.techxilla.www.marketxilla;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.model.SubscriptionPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class MySubscriptionPlanActivity extends AppCompatActivity {
    ArrayList<SubscriptionPlanModel> planList;
    ProgressDialog myDialog;
    TextView tv_title;
    ImageView iv_back;
    Button btnSubscribe;
    int mStackCount;
    ScrollView scrollView;
    LinearLayout ll_parent_subscription_plan, ll_details;
    String plan_name, date, mSubscribed_till, mSubscribed_on, mShortDate;
    String mUserId, mTransactionId, fileName, mPdf_name, mTransaction_Message;
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    ViewGroup viewGroup;
    TextView tv_title_plan, tv_valid_till;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);
        viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        init();

    }

    private void init() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.back_btn_toolbar);

        tv_title.setText(getResources().getString(R.string.my_subscription));
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // recycler_list_subscriptionplan = (RecyclerView) findViewById(R.id.recycler_list_subscriptionplan);
        planList = new ArrayList<>();

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        ll_parent_subscription_plan = (LinearLayout) findViewById(R.id.ll_parent_subscription_plan);
        ll_details = (LinearLayout) findViewById(R.id.ll_details);
        tv_title_plan = (TextView) findViewById(R.id.tv_title_plan);
        tv_valid_till = (TextView) findViewById(R.id.tv_valid_till);
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                {
                    Intent i = new Intent(MySubscriptionPlanActivity.this, NewDashboard.class);
                    i.putExtra("load_fragment", 2);
                    overridePendingTransition(R.animator.move_left,R.animator.move_right);
                    startActivity(i);
                    finish();
                }
            }
        });

        getPlanDetail();

    }

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
        String Uiid_id = UUID.randomUUID().toString();
        String StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(), "MemberId");
        final String get_plan_details_info = ROOT_URL + "get_user_subscription_details.php?_"+Uiid_id+"&user_id=" + StrMemberId;
        Log.d("URL --->", get_plan_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(this);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_plan_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response", "" + response);
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
                            }
                            JSONObject obj = new JSONObject(response);


                            boolean status = obj.getBoolean("status");
                            String Message = obj.getString("message");

                            JSONArray m_jArry = obj.getJSONArray("data");

                            mStackCount = m_jArry.length();

                            if (mStackCount == 0)
                            {
                                tv_title_plan.setText("NO ACTIVE PLAN");
                                tv_valid_till.setVisibility(View.GONE);
                                btnSubscribe.setVisibility(View.VISIBLE);

                            }
                            for (int i = 0; i < m_jArry.length(); i++) {

                                JSONObject jo_data = m_jArry.getJSONObject(i);
                                mTransactionId = jo_data.getString("transaction_id");
                                String id = jo_data.getString("id");
                                mUserId = jo_data.getString("user_id");
                                String plan_id = jo_data.getString("plan_id");
                                String subscribed_on = jo_data.getString("subscribed_on");
                                String subscribed_till = jo_data.getString("subscribed_till");
                                String payment_detail = jo_data.getString("payment_detail");
                                mPdf_name = jo_data.getString("pdf_name");
                                plan_name = jo_data.getString("plan_name");
                                // String plan_amount = jo_data.getString("plan_amount");
                                String package_id = jo_data.getString("package_id");
                                String plan_description = jo_data.getString("plan_description");
                                String plan_amount1_month = jo_data.getString("plan_amount1_month");
                                String plan_amount2_month = jo_data.getString("plan_amount2_month");
                                String plan_amount3_month = jo_data.getString("plan_amount3_month");

                                LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View rowView1 = Objects.requireNonNull(inflater).inflate(R.layout.row_subscription_plan, null);
                                rowView1.setPadding(10, 10, 10, 10);

                                TextView tv_Subscripte_till_date = (TextView) rowView1.findViewById(R.id.tv_Subscripte_till_date);
                                TextView tv_Subscripte_on_date = (TextView) rowView1.findViewById(R.id.tv_Subscripte_on_date);

                                TextView tv_plan_name = (TextView) rowView1.findViewById(R.id.tv_plan_name);
                                TextView tv_plan_amount = (TextView) rowView1.findViewById(R.id.tv_plan_amount);
                                TextView tv_status = (TextView) rowView1.findViewById(R.id.tv_status);
                                TextView tv_status_text = (TextView)rowView1.findViewById(R.id.tv_status_text);
                                TextView tv_status_text1 = (TextView)rowView1.findViewById(R.id.tv_status_text1);

                                Button btn_payment_receipt = (Button)rowView1.findViewById(R.id.btn_payment_receipt);

                                btn_payment_receipt.setHint(mPdf_name);

                                tv_plan_name.setText(plan_name);

                                if (package_id.equalsIgnoreCase("1")) {
                                    tv_plan_amount.setText("Amount : \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount1_month));
                                }
                                if (package_id.equalsIgnoreCase("2")) {
                                    tv_plan_amount.setText("Amount : \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount2_month));
                                }
                                if (package_id.equalsIgnoreCase("3")) {
                                    tv_plan_amount.setText("Amount : \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount3_month));
                                }

                                SimpleDateFormat sdf, sdf2, sdf21;
                                Date newSubscriptedTilldate, currentdate2,newSubscriptedOndate;

                                if (!subscribed_till.equalsIgnoreCase("")) {
                                    try {
                                        sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                        newSubscriptedTilldate = sdf.parse(subscribed_till);
                                        sdf21 = new SimpleDateFormat("dd MMM, yyyy");
                                        sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                                        mSubscribed_till = sdf21.format(newSubscriptedTilldate);
                                        String SubcribedTill_DMY = sdf2.format(newSubscriptedTilldate);
                                        if (new Date().after(newSubscriptedTilldate)) {
                                            tv_Subscripte_till_date.setText(mSubscribed_till);
                                            tv_status_text.setText("You do not have any active plan.");
                                            tv_status_text1.setText("Buy the best plan and enjoy your service");
                                            btn_payment_receipt.setText("Subscribe Now");
                                            btn_payment_receipt.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent i = new Intent(MySubscriptionPlanActivity.this, NewDashboard.class);
                                                    i.putExtra("load_fragment", 2);
                                                    overridePendingTransition(R.animator.move_left,R.animator.move_right);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            });
                                            tv_status.setText("InActive");
                                            tv_status.setTextColor(getResources().getColor(R.color.md_red_a400));
                                            btnSubscribe.setVisibility(View.VISIBLE);
                                        } else {
                                            tv_Subscripte_till_date.setText(mSubscribed_till);
                                            tv_status_text.setText("Your plan is currently Active.");
                                            tv_status_text1.setText("Enjoy your Service");
                                            btn_payment_receipt.setText("Download Transaction Details");

                                            btn_payment_receipt.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String path_link = ROOT_URL + "payment_receipt/" + btn_payment_receipt.getHint().toString().trim();
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setData(Uri.parse(path_link));
                                                    startActivity(intent);
                                                }
                                            });

                                            tv_status.setText("Active");
                                            tv_status.setTextColor(getResources().getColor(R.color.selected_green));
                                            btnSubscribe.setVisibility(View.GONE);
                                        }

                                        if(i==0 && !CommonMethods.isDateExpired(SubcribedTill_DMY)) {
                                            tv_title_plan.setText("Current Plan : " + plan_name);
                                            tv_valid_till.setVisibility(View.VISIBLE);
                                            tv_valid_till.setText("Valid Till \n" + mSubscribed_till);
                                        }else {
                                            tv_title_plan.setText("NO ACTIVE PLAN");
                                            tv_valid_till.setVisibility(View.GONE);
                                            btnSubscribe.setVisibility(View.VISIBLE);
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
                                        mSubscribed_on = sdf21.format(currentdate2).toString();
                                        newSubscriptedOndate = sdf2.parse(subscribed_on);
                                        mShortDate = new SimpleDateFormat("MMM yyyy").format(newSubscriptedOndate).toString();
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


                                //child layout

                                LayoutInflater inflater2 = (LayoutInflater) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View rowView2 = Objects.requireNonNull(inflater2).inflate(R.layout.plans_items, null);
                                rowView2.setPadding(10, 10, 10, 10);

                                LinearLayout ll_header = (LinearLayout) rowView2.findViewById(R.id.ll_header);
                                TableLayout table_layout = (TableLayout) rowView2.findViewById(R.id.table_layout);

                                ImageView img_add = (ImageView) rowView2.findViewById(R.id.img_add);
                                img_add.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
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
                                    }
                                });


                                TextView date = (TextView) rowView2.findViewById(R.id.date);
                                TextView tv_transaction_details = (TextView) rowView2.findViewById(R.id.tv_transaction_details);
                                TextView tv_Subscribed_till = (TextView) rowView2.findViewById(R.id.tv_Subscribed_till);
                                TextView tv_subscribed_on = (TextView) rowView2.findViewById(R.id.tv_subscribed_on);
                                TextView tv_planname =(TextView) rowView2.findViewById(R.id.tv_planname);

                                TextView tv_paying_via = (TextView) rowView2.findViewById(R.id.tv_paying_via);
                                TextView tv_amount = (TextView) rowView2.findViewById(R.id.tv_amount);
                                TextView tv_payment_receipt = (TextView) rowView2.findViewById(R.id.tv_payment_receipt);
                                TextView tv_Receipt_on_email = (TextView) rowView2.findViewById(R.id.tv_Receipt_on_email);

                                tv_payment_receipt.setHint(mPdf_name);
                                tv_payment_receipt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        /*Intent intent_download =new Intent(SubscriptionPlanActivity.this,WebViewActivity.class);
                                        intent_download.putExtra("path_link",path_link);
                                        startActivity(intent_download);*/
                                        String path_link = ROOT_URL + "payment_receipt/" + tv_payment_receipt.getHint().toString().trim();
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(path_link));
                                        startActivity(intent);

                                    }
                                });
                                tv_Receipt_on_email.setHint(mTransactionId);
                                tv_Receipt_on_email.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        getTransactionDetails(mUserId, tv_Receipt_on_email.getHint().toString());

                                    }
                                });

                                date.setText(mShortDate.toString());
                                tv_planname.setText(plan_name+" - "+package_id+" Months");

                                if (package_id.equalsIgnoreCase("1")) {
                                    tv_amount.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount1_month));
                                }
                                if (package_id.equalsIgnoreCase("2")) {
                                    tv_amount.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount2_month));
                                }
                                if (package_id.equalsIgnoreCase("3")) {
                                    tv_amount.setText("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount3_month));
                                }


                                tv_transaction_details.setText("No Available Data");
                                tv_Subscribed_till.setText(mSubscribed_till.toString());
                                tv_subscribed_on.setText(mSubscribed_on.toString());
                                tv_paying_via.setText("No Available Data");


                                ll_details.addView(rowView2);
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
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            } else {
                //  DisplaySnackBar(viewGroup, "No Internet Connection", "WARNING");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private void getTransactionDetails(String mUserId, String mTransactionId) {
        String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL + "send_transaction_receipt_on_mail.php?"+Uiid_id+"&user_id=" + mUserId + "&transaction_id=" + mTransactionId;
        Log.d("Details URL --->", get_bank_details_info);
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
                            Log.d("ResponseOBJ", "" + Jobj.toString());

                            boolean status = Jobj.getBoolean("status");
                            String message = Jobj.getString("message");
                            if (status) {
                                CommonMethods.DisplayToastSuccess(getApplicationContext(),message);
                            }

                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        //Log.d("Vollley Err", volleyError.toString());

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            } else {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

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

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
            CommonMethods.DisplayToast(MySubscriptionPlanActivity.this, "Successfully completed download. ");

        }
    }

    private void ScrollDown(){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), NewDashboard.class);
        startActivity(i);
        overridePendingTransition(R.animator.left_right,R.animator.right_left);
        finish();
    }
}