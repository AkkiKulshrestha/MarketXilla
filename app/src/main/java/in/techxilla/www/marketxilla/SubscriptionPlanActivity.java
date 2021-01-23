package in.techxilla.www.marketxilla;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.model.SubscriptionPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class

SubscriptionPlanActivity extends AppCompatActivity {
    ArrayList<SubscriptionPlanModel> planList;
    ProgressDialog myDialog;
    TextView tv_title;
    ImageView iv_back;
    Button btnSubscribe;
    int mStackCount;
    LinearLayout ll_parent_subscription_plan, ll_details;
    String plan_name, date, mSubscribed_till, mSubscribed_on, mShortDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plan);

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
        ll_parent_subscription_plan = (LinearLayout) findViewById(R.id.ll_parent_subscription_plan);
        ll_details = (LinearLayout) findViewById(R.id.ll_details);
        btnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                {
                    Intent i = new Intent(SubscriptionPlanActivity.this, SubscriptionActivity.class);
                    i.putExtra("smart_name", plan_name);
                    startActivity(i);
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
        final String get_plan_details_info = ROOT_URL + "get_user_subscription_details.php?user_id=" + "1";
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
                            JSONArray m_jArry = obj.getJSONArray("data");

                            mStackCount = m_jArry.length();
                            for (int i = 0; i < m_jArry.length(); i++) {

                                JSONObject jo_data = m_jArry.getJSONObject(i);
                                String id = jo_data.getString("id");
                                String user_id = jo_data.getString("user_id");
                                String plan_id = jo_data.getString("plan_id");
                                String subscribed_on = jo_data.getString("subscribed_on");
                                String subscribed_till = jo_data.getString("subscribed_till");
                                String payment_detail = jo_data.getString("payment_detail");
                                plan_name = jo_data.getString("plan_name");
                                String plan_amount = jo_data.getString("plan_amount");
                                String package_id = jo_data.getString("package_id");
                                String plan_description = jo_data.getString("plan_description");

                                LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getApplicationContext()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View rowView1 = Objects.requireNonNull(inflater).inflate(R.layout.row_subscription_plan, null);
                                rowView1.setPadding(10, 10, 10, 10);

                                TextView tv_Subscripte_till_date = (TextView) rowView1.findViewById(R.id.tv_Subscripte_till_date);
                                TextView tv_Subscripte_on_date = (TextView) rowView1.findViewById(R.id.tv_Subscripte_on_date);

                                TextView tv_plan_name = (TextView) rowView1.findViewById(R.id.tv_plan_name);
                                TextView tv_plan_amount = (TextView) rowView1.findViewById(R.id.tv_plan_amount);
                                TextView tv_status = (TextView) rowView1.findViewById(R.id.tv_status);
                                tv_plan_amount.setText("Amount : \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount));
                                tv_plan_name.setText(plan_name);

                                SimpleDateFormat sdf, sdf2, sdf21;
                                Date newSubscriptedTilldate, currentdate2;

                                if (!subscribed_till.equalsIgnoreCase("")) {
                                    try {
                                        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        newSubscriptedTilldate = sdf.parse(subscribed_till);
                                        sdf21 = new SimpleDateFormat("dd MMM, yyyy");
                                        mSubscribed_till = sdf21.format(newSubscriptedTilldate);
                                        mShortDate = new SimpleDateFormat("MMM yyyy").format(newSubscriptedTilldate).toString();
                                        if (new Date().after(newSubscriptedTilldate)) {
                                            tv_Subscripte_till_date.setText(" Subscribed till : " +mSubscribed_till);
                                            tv_status.setText("InActive");
                                            tv_status.setTextColor(getResources().getColor(R.color.md_red_a400));
                                            btnSubscribe.setVisibility(View.VISIBLE);
                                        } else {
                                            tv_Subscripte_till_date.setText(" Subscribed till : " +mSubscribed_till);
                                            tv_status.setText("Active");
                                            tv_status.setTextColor(getResources().getColor(R.color.result_points));
                                            btnSubscribe.setVisibility(View.GONE);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (!subscribed_on.equalsIgnoreCase("")) {
                                    try {
                                        sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                        currentdate2 = sdf2.parse(subscribed_on);
                                        sdf21 = new SimpleDateFormat("dd MMM, yyyy");
                                        mSubscribed_on = sdf21.format(currentdate2).toString();
                                        if (new Date().after(currentdate2)) {
                                            tv_Subscripte_on_date.setText(" Subscribed on : "+mSubscribed_on);
                                            // tv_status.setText("In Active");
                                        } else {
                                            tv_Subscripte_on_date.setText(" Subscribed on : "+mSubscribed_on);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }


                                // if(sdf2.format(currentdate).ge)

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
                                            Glide.with(SubscriptionPlanActivity.this).
                                                    load(R.mipmap.ic_down_white).
                                                    diskCacheStrategy(DiskCacheStrategy.ALL).into(img_add);
                                        } else {
                                            table_layout.setVisibility(View.VISIBLE);
                                            Glide.with(SubscriptionPlanActivity.this).
                                                    load(R.mipmap.ic_up_white).
                                                    diskCacheStrategy(DiskCacheStrategy.ALL).into(img_add);
                                        }
                                    }
                                });


                                TextView date = (TextView) rowView2.findViewById(R.id.date);
                                TextView tv_transaction_details = (TextView) rowView2.findViewById(R.id.tv_transaction_details);
                                TextView tv_Subscribed_till = (TextView) rowView2.findViewById(R.id.tv_Subscribed_till);
                                TextView tv_subscribed_on = (TextView) rowView2.findViewById(R.id.tv_subscribed_on);

                                TextView tv_paying_via = (TextView) rowView2.findViewById(R.id.tv_paying_via);
                                TextView tv_amount = (TextView) rowView2.findViewById(R.id.tv_amount);
                                TextView tv_payment_receipt = (TextView) rowView2.findViewById(R.id.tv_payment_receipt);


                                date.setText(mShortDate.toString());
                                tv_amount.setText(" \u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(plan_amount));
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


}
