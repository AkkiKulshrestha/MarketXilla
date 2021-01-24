package in.techxilla.www.marketxilla.adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.SubscriptionActivity;
import in.techxilla.www.marketxilla.model.SmartPlanModel;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static android.app.Activity.RESULT_OK;
import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder> {


    public List<SubscritPlanModel> smartPlanModelsList;
    private Context context;


    public SubscriptionPlanAdapter(List<SubscritPlanModel> smartPlanModelsList, Context context) {
        this.smartPlanModelsList = smartPlanModelsList;
        this.context = context;
    }


    @NonNull
    @Override
    public SubscriptionPlanAdapter.PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plan_slide1, parent, false);

        return new SubscriptionPlanAdapter.PlanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionPlanAdapter.PlanViewHolder holder, int position) {

        final SubscritPlanModel smartPlanModel = smartPlanModelsList.get(position);


        holder.img_green.setText(smartPlanModel.getsPlan());
        holder.tv_title.setText(smartPlanModel.getsPlanName());
        holder.tv_msg1.setText(smartPlanModel.getsDetails());
        holder.tv_one_month.setText("1 Month \n \u20B9 "+ CommonMethods.NumberDisplayFormattingWithComma(smartPlanModel.getAmount1Month()));
        holder.tv_two_month.setText("2 Month \n \u20B9 "+ CommonMethods.NumberDisplayFormattingWithComma(smartPlanModel.getAmount2Months()));
        holder.tv_three_month.setText("3 Month \n \u20B9 "+ CommonMethods.NumberDisplayFormattingWithComma(smartPlanModel.getAmount3Months()));


        if(smartPlanModel.issStock_Future()){
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_ftr);
        }else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_ftr);
        }

        if(smartPlanModel.issStock_Options()){
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_opt);
        }else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_opt);
        }

        if(smartPlanModel.issIndex_Future()){
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_ftr);
        }else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_ftr);
        }

        if(smartPlanModel.issStock_Options()){
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_opt);
        }else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_opt);
        }

        if(smartPlanModel.issCommodity()){
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_commodity);
        }else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_commodity);
        }

        if(smartPlanModel.issTelegram_Updates()){
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);
        }else {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_telegram_update);
        }

        holder.tv_one_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUsing_googlepay(smartPlanModel.getId(),smartPlanModel.getAmount1Month());

                ((Activity)  context).overridePendingTransition(R.animator.move_left,R.animator.move_right);
            }
        });

        holder.tv_two_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUsing_googlepay(smartPlanModel.getId(),smartPlanModel.getAmount2Months());
                ((Activity)  context).overridePendingTransition(R.animator.move_left,R.animator.move_right);
            }
        });

        holder.tv_three_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayUsing_googlepay(smartPlanModel.getId(),smartPlanModel.getAmount3Months());

                ((Activity)  context).overridePendingTransition(R.animator.move_left,R.animator.move_right);
            }
        });

    }

    private void PayUsing_googlepay(String PlanId,String Amount) {
        String StrSubscrptionAmount = Amount;
        String StrUpiAccountId= UtilitySharedPreferences.getPrefs(context,"UpiAccountId");
        String StrUPI_MerchantName =  UtilitySharedPreferences.getPrefs(context,"UpiMerchantName");

        Uri uri =
                new Uri.Builder()
                        .scheme("upi")
                        .authority("pay")
                        .appendQueryParameter("pa", StrUpiAccountId)       // virtual ID
                        .appendQueryParameter("pn", StrUPI_MerchantName)          // name
                        .appendQueryParameter("am", StrSubscrptionAmount)           // amount
                        .appendQueryParameter("cu", "INR")                         // currency
                        .build();

        String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        int GOOGLE_PAY_REQUEST_CODE = 123;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
        ((Activity)  context).startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);

    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      //  ((Activity)  context).onActivityResult(requestCode, resultCode, data);
        Log.e("main ", "response "+resultCode );

    }

    @Override
    public int getItemCount() {
        return smartPlanModelsList.size();
    }


    public class PlanViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_msg1;
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
