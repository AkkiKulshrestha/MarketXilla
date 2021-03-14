package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class NotificationActivity extends AppCompatActivity {
    private ProgressDialog myDialog;
    private LinearLayout ll_parent_notification;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        myDialog = new ProgressDialog(this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        final TextView tv_title = (TextView) findViewById(R.id.tv_title);
        final ImageView iv_back = (ImageView) findViewById(R.id.back_btn_toolbar);

        tv_title.setText("NOTIFICATIONS");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        mAdView.loadAd(adRequestBuilder.build());

        ll_parent_notification = (LinearLayout) findViewById(R.id.ll_parent_notification);
        fetchNotificationList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume the AdView.
        mAdView.resume();
    }

    @Override
    public void onPause() {
        // Pause the AdView.
        mAdView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Destroy the AdView.
        mAdView.destroy();

        super.onDestroy();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    private void fetchNotificationList() {
        if (myDialog != null && !myDialog.isShowing()) myDialog.show();
        if (ll_parent_notification != null && ll_parent_notification.getChildCount() > 0) {
            ll_parent_notification.removeAllViews();
        }
        final String Uiid_id = UUID.randomUUID().toString();
        ConnectionDetector cd = new ConnectionDetector(this);
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "get_notifications_list.php?_" + Uiid_id;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    try {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        Log.d("URL Response", "--> " + response);
                        final JSONObject jsonResponse = new JSONObject(response);
                        final boolean status = jsonResponse.getBoolean("status");
                        if (status) {
                            final String result = jsonResponse.getString("data");
                            final JSONArray resultArry = new JSONArray(result);
                            if (resultArry.length() > 0) {
                                for (int i = 0; i < resultArry.length(); i++) {
                                    final JSONObject customer_inspect_obj = resultArry.getJSONObject(i);
                                    final String id = customer_inspect_obj.getString("id");
                                    final String text_message = customer_inspect_obj.getString("text_message");
                                    final String created_at = customer_inspect_obj.getString("created_at");

                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.row_notification_info, null);
                                    rowView.setPadding(10, 10, 10, 10);

                                    final TextView tv_id = (TextView) rowView.findViewById(R.id.tv_id);
                                    final TextView tv_row_date_time = (TextView) rowView.findViewById(R.id.tv_row_date_time);
                                    final TextView row_notification = (TextView) rowView.findViewById(R.id.row_notification);
                                    tv_id.setText(id);
                                    row_notification.setText(text_message.toUpperCase());
                                    Date date = null;
                                    try {
                                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(created_at);
                                        final String convertDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);
                                        tv_row_date_time.setText(convertDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    ll_parent_notification.addView(rowView);
                                }
                            } else {
                                final String message = jsonResponse.getString("message");
                                final TextView textView = new TextView(getApplicationContext());
                                textView.setText(message);
                                textView.setBackground(getResources().getDrawable(R.drawable.cw_button_shadow_red));
                                textView.setTextColor(getResources().getColor(R.color.white));
                                textView.setGravity(Gravity.CENTER);
                                textView.setPadding(30, 30, 30, 30);
                                textView.setAllCaps(true);
                                ll_parent_notification.addView(textView);
                            }
                        } else {
                            final String message = jsonResponse.getString("message");
                            TextView textView = new TextView(getApplicationContext());
                            textView.setText(message);
                            textView.setBackground(getResources().getDrawable(R.drawable.cw_button_shadow_red));
                            textView.setTextColor(getResources().getColor(R.color.white));
                            textView.setGravity(Gravity.CENTER);
                            textView.setAllCaps(true);
                            textView.setPadding(30, 30, 30, 30);
                            ll_parent_notification.addView(textView);
                        }
                    } catch (JSONException e) {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (myDialog != null && myDialog.isShowing()) {
                        myDialog.dismiss();
                    }
                    volleyError.printStackTrace();
                    CommonMethods.DisplayToastWarning(getApplicationContext(), "Something goes wrong. Please try again");
                }
            });
            int socketTimeout = 50000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postrequest.setRetryPolicy(policy);
            // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postrequest);
        } else {
            CommonMethods.DisplayToastInfo(getApplicationContext(), "No Internet Connection");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
        finish();
    }
}