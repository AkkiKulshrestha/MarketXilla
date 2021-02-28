package in.techxilla.www.marketxilla.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;
import in.techxilla.www.marketxilla.utils.AppEnvironment;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.PayUMoneyAppPreference;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static android.app.Activity.RESULT_OK;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder> {

    private final Context context;
    public ArrayList<SubscritPlanModel> smartPlanModelsList;
    private String mUserId, mPlan_id, mSubscripbed_on, mPayment_detail, mPackage_id, msubscribed_till;

    public SubscriptionPlanAdapter(ArrayList<SubscritPlanModel> smartPlanModelsList, Context context) {
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

    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash.toString();
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
                PayUMoneySdk(smartPlanModel.getId(), smartPlanModel.getAmount1Month(), "1", holder.tv_title.getText().toString().trim());
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });

        holder.tv_two_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUMoneySdk(smartPlanModel.getId(), smartPlanModel.getAmount2Months(), "2", holder.tv_title.getText().toString().trim());
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });

        holder.tv_three_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUMoneySdk(smartPlanModel.getId(), smartPlanModel.getAmount3Months(), "3", holder.tv_title.getText().toString().trim());
                ((Activity) context).overridePendingTransition(R.animator.move_left, R.animator.move_right);
            }
        });
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    private void PayUMoneySdk(String PlanId, String Amount, String Package_id, String PlanName) {
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

        final String StrSubscriptionAmount = String.format("%.2f", Double.parseDouble(Amount));
        final String transactionId = "TID" + System.currentTimeMillis();
        final String StrUpiAccountId = UtilitySharedPreferences.getPrefs(context, "UpiAccountId");
        final String StrUPI_MerchantName = UtilitySharedPreferences.getPrefs(context, "UpiMerchantName");
        final String MemberEmailId = UtilitySharedPreferences.getPrefs(context, "MemberEmailId");
        final String MemberId = UtilitySharedPreferences.getPrefs(context, "MemberId");
        final String MemberName = UtilitySharedPreferences.getPrefs(context, "MemberName");
        final String MemberMobile = UtilitySharedPreferences.getPrefs(context, "MemberMobile");

        PayUMoneyAppPreference appPreference = new PayUMoneyAppPreference();
        appPreference.setUserEmail(MemberEmailId);
        appPreference.setUserId(MemberId);
        appPreference.setUserMobile(MemberMobile);
        appPreference.setUserFullName(MemberName);
        appPreference.setPaymentAmount(StrSubscriptionAmount);
        appPreference.setProductInfo(PlanName);
        appPreference.setPlanId(mPlan_id);

        AppEnvironment appEnvironment = AppEnvironment.SANDBOX;
        String hashSequence = appEnvironment.merchant_Key() + "|" + transactionId + "|" + appPreference.getPaymentAmount() + "|" + appPreference.getProductInfo() + "|" + appPreference.getUserFullName() + "|" + appPreference.getUserEmail() + "|" + appPreference.getUserId() + "|" + appPreference.getUserMobile() + "|||||||||" + appEnvironment.salt();
        String serverGeneratedHash = hashCal("SHA-512", hashSequence);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new
                PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(StrSubscriptionAmount)
                // Payment amount
                .setTxnId(transactionId)                          // Transaction ID
                .setPhone(appPreference.getUserMobile())          // User Phone number
                .setProductName(appPreference.getProductInfo())   // Product Name or description
                .setFirstName(appPreference.getUserFullName())    // User First name
                .setEmail(appPreference.getUserEmail())           // User Email ID
                .setsUrl(appEnvironment.surl())                   // Success URL (surl)
                .setfUrl(appEnvironment.furl())                   //Failure URL (furl)
                .setUdf1(appPreference.getUserId())
                .setUdf2(appPreference.getPlanId())
                .setIsDebug(appEnvironment.debug())               // Integration environment - true (Debug)/ false(Production)
                .setKey(appEnvironment.merchant_Key())            // Merchant key
                .setMerchantId(appEnvironment.merchant_ID());     // Merchant ID

        PayUmoneySdkInitializer.PaymentParam paymentParam = null;
        try {
            paymentParam = builder.build();
            paymentParam.setMerchantHash(serverGeneratedHash);  //set the hash
            PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, (Activity) context, R.style.AppTheme_default, appPreference.isOverrideResultScreen());
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return smartPlanModelsList.size();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    AddUserSubscriptionDetailApi(transactionResponse.toString());
                } else {
                    CommonMethods.DisplayToastWarning(context,"Something went wrong. Please try again.");
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();
                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (resultModel != null && resultModel.getError() != null) {
                CommonMethods.DisplayToastError(context, ""+resultModel.getError().getTransactionResponse().toString());
            } else {
                Log.d("TAG", "Both objects are null!");
            }
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
                                    System.out.println("Add User Subscription response" + response);
                                    boolean status = jsonObject.getBoolean("status");
                                    if (status) {
                                        JSONObject dataObj = jsonObject.getJSONObject("data");
                                        String message = jsonObject.getString("message");
                                        CommonMethods.DisplayToastSuccess(context, message);
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
                        params.put("payment_detail", "" + transactionDetails);
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

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_title, tv_msg1;
        private final TextView tv_one_month, tv_two_month, tv_three_month;
        private final TextView img_green;
        private final ImageView iv_stk_ftr, iv_stk_opt, iv_index_ftr, iv_index_opt, iv_commodity, iv_telegram_update;

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
