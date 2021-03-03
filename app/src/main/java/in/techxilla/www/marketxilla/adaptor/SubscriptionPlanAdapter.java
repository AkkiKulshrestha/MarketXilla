package in.techxilla.www.marketxilla.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder> {

    private final Context context;
    public ArrayList<SubscritPlanModel> smartPlanModelsList;
    private String mUserId, mPlan_id, mSubscripbed_on, mPayment_detail, mPackage_id, msubscribed_till;

    public SubscriptionPlanAdapter(ArrayList<SubscritPlanModel> smartPlanModelsList, Context context) {
        this.smartPlanModelsList = smartPlanModelsList;
        this.context = context;
    }



    @NonNull
    @Override
    public SubscriptionPlanAdapter.PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plan_slide1, parent, false);

        return new PlanViewHolder(itemView);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull SubscriptionPlanAdapter.PlanViewHolder holder, int position) {

        final SubscritPlanModel smartPlanModel = smartPlanModelsList.get(position);
        mPlan_id = smartPlanModel.getId();
        holder.tv_plan_type.setText(smartPlanModel.getsPlan() + " PLAN");
        holder.tv_title.setText(smartPlanModel.getsPlanName());
        holder.tv_msg1.setText(smartPlanModel.getsDetails());
        holder.tv_tenure.setText(smartPlanModel.getTenure());

        if(holder.tv_tenure.getText().toString().contains(",")){
            String [] tenures = holder.tv_tenure.getText().toString().split(",");
            {
                for(String ten : tenures){
                    if(ten.equalsIgnoreCase("1M")){
                        holder.tv_one_month.setVisibility(View.VISIBLE);
                    }

                    if(ten.equalsIgnoreCase("2M")){
                        holder.tv_two_month.setVisibility(View.VISIBLE);
                    }

                    if(ten.equalsIgnoreCase("3M")){
                        holder.tv_three_month.setVisibility(View.VISIBLE);
                    }

                    holder.tv_custom_tenure.setVisibility(View.GONE);
                }
            }
        }else {
            holder.tv_one_month.setVisibility(View.GONE);
            holder.tv_two_month.setVisibility(View.GONE);
            holder.tv_three_month.setVisibility(View.GONE);
            holder.tv_custom_tenure.setVisibility(View.VISIBLE);
                if(holder.tv_tenure.getText().toString().contains("D")){
                    String [] tenures = holder.tv_tenure.getText().toString().split("D");
                    {
                        mSubscripbed_on = CommonMethods.DisplayCurrentDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, Integer.parseInt(tenures[0]));
                        SimpleDateFormat formDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        msubscribed_till = formDate.format(new Date(calendar.getTimeInMillis())); //
                        holder.tv_custom_tenure.setText(tenures[0] + " Days");
                    }
                }
        }

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
                ((NewDashboard)context).pop_up_bank_details();
               /* ((NewDashboard)context).PayUMoneySdk(smartPlanModel.getId(), smartPlanModel.getAmount1Month(), "1", holder.tv_title.getText().toString().trim());
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);*/
            }
        });

        holder.tv_two_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NewDashboard)context).pop_up_bank_details();
               /* ((NewDashboard)context).PayUMoneySdk(smartPlanModel.getId(), smartPlanModel.getAmount2Months(), "2", holder.tv_title.getText().toString().trim());
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);*/
            }
        });

        holder.tv_three_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((NewDashboard)context).pop_up_bank_details();
               /* ((NewDashboard)context).PayUMoneySdk(smartPlanModel.getId(), smartPlanModel.getAmount3Months(), "3", holder.tv_title.getText().toString().trim());
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);*/
            }
        });

        holder.tv_custom_tenure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog myDialog = new ProgressDialog(context);
                myDialog.setMessage("Please wait...");
                myDialog.setCancelable(false);
                myDialog.setCanceledOnTouchOutside(false);
                myDialog.show();
                AddUserTRAILDetailApi(myDialog);
            }
        });
    }

    private void AddUserTRAILDetailApi(ProgressDialog dialog) {

        final String StrSubscriptionAmount = "0.0";
        final String transactionId = "TID" + System.currentTimeMillis();

        JSONObject transactionObj = new JSONObject();
        try {
            transactionObj.put("transaction_id",transactionId);
            transactionObj.put("transaction_amount",StrSubscriptionAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String API_AddUserSubscriptionDetail = ROOT_URL + "add_user_trail_detail.php";
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
                                if (dialog!=null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                try {
                                    final JSONObject jsonObject = new JSONObject(response);
                                    final boolean status = jsonObject.getBoolean("status");
                                    if (status) {
                                        CommonMethods.DisplayToastSuccess(context, "YOUR TRAIL IS ACTIVATED");
                                        final Intent i = new Intent(context, NewDashboard.class);
                                        context.startActivity(i);
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
                        params.put("user_id", UtilitySharedPreferences.getPrefs(context, "MemberId"));
                        params.put("plan_id", mPlan_id);
                        params.put("subscribed_on", mSubscripbed_on);
                        params.put("payment_details",transactionObj.toString());
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


    @Override
    public int getItemCount() {
        return smartPlanModelsList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_title, tv_msg1;
        private final TextView tv_one_month, tv_two_month, tv_three_month;
        private final TextView tv_plan_type,tv_tenure,tv_custom_tenure;
        private final ImageView iv_stk_ftr, iv_stk_opt, iv_index_ftr, iv_index_opt, iv_commodity, iv_telegram_update;

        public PlanViewHolder(View view) {
            super(view);
            tv_plan_type = (TextView) view.findViewById(R.id.tv_plan_type);
            tv_tenure = (TextView) view.findViewById(R.id.tv_tenure);
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
            tv_custom_tenure = (TextView) view.findViewById(R.id.tv_custom_tenure);
        }
    }
}
