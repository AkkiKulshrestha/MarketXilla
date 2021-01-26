package in.techxilla.www.marketxilla.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.squareup.picasso.Picasso;
import com.wangsun.upi.payment.UpiPayment;
import com.wangsun.upi.payment.model.PaymentDetail;
import com.wangsun.upi.payment.model.TransactionDetails;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.SubscriptionActivity;
import in.techxilla.www.marketxilla.model.SmartPlanModel;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static android.app.Activity.RESULT_OK;
import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.utils.CommonMethods.md5;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder>  {

    public List<SubscritPlanModel> smartPlanModelsList;
    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;
    Uri uri;
    String mUserId, mPlan_id, mSubscripbed_on, mPayment_detail, mPackage_id, msubscribed_till;
    private Context context;
   // PaymentDetail payment;

    public SubscriptionPlanAdapter(List<SubscritPlanModel> smartPlanModelsList, Context context) {
        this.smartPlanModelsList = smartPlanModelsList;
        this.context = context;
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @NonNull
    @Override
    public SubscriptionPlanAdapter.PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plan_slide1, parent, false);

        return new SubscriptionPlanAdapter.PlanViewHolder(itemView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull SubscriptionPlanAdapter.PlanViewHolder holder, int position) {

        final SubscritPlanModel smartPlanModel = smartPlanModelsList.get(position);

        mUserId = smartPlanModel.getId();

        holder.img_green.setText(smartPlanModel.getsPlan());
        holder.tv_title.setText(smartPlanModel.getsPlanName());
        holder.tv_msg1.setText(smartPlanModel.getsDetails());
        holder.tv_one_month.setText("1 Month\n\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(smartPlanModel.getAmount1Month()));
        holder.tv_two_month.setText("2 Months\n\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(smartPlanModel.getAmount2Months()));
        holder.tv_three_month.setText("3 Months\n\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(smartPlanModel.getAmount3Months()));


        if (smartPlanModel.issStock_Future()) {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_ftr);
        } else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_ftr);
         }

        if (smartPlanModel.issStock_Options()) {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_opt);
         } else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_opt);
        }

        if (smartPlanModel.issIndex_Future()) {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_ftr);
        } else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_ftr);
         }

        if (smartPlanModel.issIndex_Options()) {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_opt);
       } else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_opt);
        }

        if (smartPlanModel.issCommodity()) {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_commodity);
         } else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_commodity);
          }

        if (smartPlanModel.issTelegram_Updates()) {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);
        } else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_telegram_update);
        }

        holder.tv_one_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUsing_googlepay(smartPlanModel.getId(), smartPlanModel.getAmount1Month(), "1",holder.tv_title.getText().toString().trim());
             //   payWithGPay();
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });

        holder.tv_two_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUsing_googlepay(smartPlanModel.getId(), smartPlanModel.getAmount2Months(), "2",holder.tv_title.getText().toString().trim());
               // payWithGPay();
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });

        holder.tv_three_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PayUsing_googlepay(smartPlanModel.getId(), smartPlanModel.getAmount3Months(), "3",holder.tv_title.getText().toString().trim());
               // payWithGPay();
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });

    }

    private void PayUsing_googlepay(String PlanId, String Amount, String Package_id,String PlanName) {
            mPlan_id = PlanId;
            mPackage_id = Package_id;

            mSubscripbed_on = CommonMethods.DisplayCurrentDate();

            Calendar calendar = Calendar.getInstance();

            if (mPackage_id.equalsIgnoreCase("1")) {
                calendar.add(Calendar.MONTH, 1);
                System.out.println("Current Date = " + calendar.getTime());
            } else if (mPackage_id.equalsIgnoreCase("2")) {
                calendar.add(Calendar.MONTH, 2);
                System.out.println("Current Date = " + calendar.getTime());
            } else if (mPackage_id.equalsIgnoreCase("3")) {
                calendar.add(Calendar.MONTH, 3);
                System.out.println("Current Date = " + calendar.getTime());
            }
            SimpleDateFormat formDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


        msubscribed_till = formDate.format(new Date(calendar.getTimeInMillis())); //
        System.out.println("msubscribed_till Date = " + msubscribed_till);

        String StrSubscrptionAmount= String.format("%.2f",Double.parseDouble(Amount));
        String transactionId = "TID" + System.currentTimeMillis();
        String StrUpiAccountId = UtilitySharedPreferences.getPrefs(context, "UpiAccountId");
        String StrUPI_MerchantName = UtilitySharedPreferences.getPrefs(context, "UpiMerchantName");

        ArrayList<String> existingApps = UpiPayment.getExistingUpiApps(context);

        PaymentDetail paymentDetail = new PaymentDetail(StrUpiAccountId,StrUPI_MerchantName,"",transactionId,PlanName,StrSubscrptionAmount);
        new UpiPayment((FragmentActivity) context)
                .setPaymentDetail(paymentDetail)
                .setUpiApps(UpiPayment.getUPI_APPS())
                .setCallBackListener(new UpiPayment.OnUpiPaymentListener() {


                    @Override
                    public void onSubmitted(@NotNull TransactionDetails data) {
                        Toast.makeText(context, "transaction pending: " + data, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NotNull String message) {
                        Toast.makeText(context, "transaction failed: " + message, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(@NotNull TransactionDetails data) {
                        Toast.makeText(context, "transaction success: " + data.toString(), Toast.LENGTH_LONG).show();
                        onTransactionSuccess(data.toString());
                    }
                }).pay();



    }

    private void onTransactionSuccess(String TransactionDetails) {
        // Payment Success
        System.out.println("Success");
        AddUserSubscriptionDetailApi(TransactionDetails);

    }

    private void onTransactionSubmitted() {
        // Payment Pending
        System.out.println("Pending | Submitted");

    }

    private void onTransactionFailed() {
        // Payment Failed
        System.out.println("Failed");

    }

    @Override
    public int getItemCount() {
        return smartPlanModelsList.size();
    }

    private void payWithGPay() {
        if (isAppInstalled(context, GOOGLE_PAY_PACKAGE_NAME)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            ((Activity) context).startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
        } else {
            Toast.makeText(context, "Please Install GPay", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddUserSubscriptionDetailApi(String transactionDetails) {


        String API_AddUserSubscriptionDetail = ROOT_URL + "add_user_subscription_detail.php";
        try {

            ConnectionDetector cd = new ConnectionDetector(context);
            boolean isInternetPresent = cd.isConnectingToInternet();


            if (isInternetPresent) {

                Log.d("URL", API_AddUserSubscriptionDetail);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_AddUserSubscriptionDetail,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    System.out.println("Add User Subscription response"+response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if (status) {

                                        JSONObject dataObj = jsonObject.getJSONObject("data");
                                        String message = jsonObject.getString("message");
                                        CommonMethods.DisplayToastSuccess(context,message);
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

                            }
                        }) {

                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", mUserId);
                        params.put("plan_id", mPlan_id);
                        params.put("subscribed_on", mSubscripbed_on);
                        params.put("payment_detail", ""+transactionDetails);
                        params.put("package_id", mPackage_id);
                        params.put("subscribed_till", msubscribed_till);
                        Log.d("ParrasRegister", params.toString());

                        return params;
                    }


                };

                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);


            } else {
                CommonMethods.DisplayToastInfo(context, "Please check your internet connection");
            }
        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
        }

        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(context, "Transaction Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Transaction Failed", Toast.LENGTH_SHORT).show();
        }
    }*/

    public class PlanViewHolder extends RecyclerView.ViewHolder {

        TextView  tv_title,tv_msg1;
        TextView tv_one_month, tv_two_month, tv_three_month;
        TextView img_green;
        ImageView iv_stk_ftr, iv_stk_opt, iv_index_ftr, iv_index_opt, iv_commodity, iv_telegram_update;

        public PlanViewHolder(View view) {
            super(view);
            img_green = (TextView) view.findViewById(R.id.img);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_msg1 = (TextView) view.findViewById(R.id.tv_msg1);
            iv_stk_ftr = (ImageView) view.findViewById(R.id.iv_stk_ftr);
            iv_stk_opt = (ImageView) view.findViewById(R.id.iv_stk_opt);
            iv_index_ftr = (ImageView) view.findViewById(R.id.iv_index_ftr);
            iv_index_opt = (ImageView) view.findViewById(R.id.iv_index_opt);
            iv_commodity = (ImageView) view.findViewById(R.id.iv_commodity);
            iv_telegram_update = (ImageView) view.findViewById(R.id.iv_telegram_update);
            tv_one_month = (TextView) view.findViewById(R.id.tv_one_month);
            tv_two_month = (TextView) view.findViewById(R.id.tv_two_month);
            tv_three_month = (TextView) view.findViewById(R.id.tv_three_month);
        }
    }


}
