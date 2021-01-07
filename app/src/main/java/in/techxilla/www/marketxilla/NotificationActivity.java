package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class NotificationActivity extends AppCompatActivity {


    ProgressDialog myDialog;
    TextView tv_title;
    ImageView iv_back;

    LinearLayout ll_parent_notification;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
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

        tv_title.setText("NOTIFICATIONS");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ll_parent_notification = (LinearLayout) findViewById(R.id.ll_parent_notification);
        fetchNotificationList();



        Handler handler = new Handler();
        handler.postDelayed
                (new Runnable() {

                    @Override
                    public void run() {
                        fetchNotificationList();
                    }
                }, 60000);
    }

    private void fetchNotificationList() {

        myDialog = new ProgressDialog(this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        if(myDialog!=null && !myDialog.isShowing()){
            myDialog.show();
        }



        if(ll_parent_notification!=null && ll_parent_notification.getChildCount()>0){
            ll_parent_notification.removeAllViews();
        }

        String Uiid_id = UUID.randomUUID().toString();


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "get_notifications_list.php?_" + Uiid_id;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(String response) {
                    try {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }

                        Log.d("URL Response", "--> " + response);

                        JSONObject jsonresponse = new JSONObject(response);

                        boolean status = jsonresponse.getBoolean("status");
                        if(status) {
                            String result = jsonresponse.getString("data");
                            JSONArray resultArry = new JSONArray(result);
                            if (resultArry.length() > 0) {


                                for (int i = 0; i < resultArry.length(); i++) {

                                    JSONObject customer_inspect_obj = resultArry.getJSONObject(i);

                                    String id = customer_inspect_obj.getString("id");
                                    String text_message = customer_inspect_obj.getString("text_message");
                                    String created_at = customer_inspect_obj.getString("created_at");


                                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.row_notification_info, null);
                                    rowView.setPadding(10, 10, 10, 10);

                                    TextView tv_id = (TextView) rowView.findViewById(R.id.tv_id);
                                    TextView tv_row_date_time = (TextView) rowView.findViewById(R.id.tv_row_date_time);
                                    TextView row_notification = (TextView) rowView.findViewById(R.id.row_notification);

                                    tv_id.setText(id);
                                    row_notification.setText(text_message.toUpperCase());

                                    Date date= null;
                                    try {
                                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(created_at);
                                        String convertDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);

                                        tv_row_date_time.setText(convertDate);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }




                                    ll_parent_notification.addView(rowView);
                                }

                            } else {
                                String message = jsonresponse.getString("message");

                                TextView textView = new TextView(getApplicationContext());
                                textView.setText(message);
                                textView.setBackground(getResources().getDrawable(R.drawable.cw_button_shadow_red));
                                textView.setTextColor(getResources().getColor(R.color.white));
                                textView.setGravity(Gravity.CENTER);
                                textView.setPadding(30, 30, 30, 30);
                                textView.setAllCaps(true);

                                ll_parent_notification.addView(textView);

                            }
                        }else {
                            String message = jsonresponse.getString("message");

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
                    DisplaySnackBar(viewGroup,"Something goes wrong. Please try again","WARNING");

                    //Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                }
            }) ;
            int socketTimeout = 50000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postrequest.setRetryPolicy(policy);
            // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postrequest);
        }else {
            DisplaySnackBar(viewGroup,"No Internet Connection","WARNING");

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.left_right,R.animator.right_left);
        finish();
    }



}
