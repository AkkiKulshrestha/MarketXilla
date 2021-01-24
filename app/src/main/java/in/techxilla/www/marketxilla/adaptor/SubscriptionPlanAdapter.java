package in.techxilla.www.marketxilla.adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.SubscriptionActivity;
import in.techxilla.www.marketxilla.model.SmartPlanModel;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;

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


        if(smartPlanModel.getsPlanName().equalsIgnoreCase("SILVER")) {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_ftr);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_opt);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_ftr);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_opt);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_commodity);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);

            if(smartPlanModel.getPackage_id().equalsIgnoreCase("1")) {
                holder.tv_one_month.setVisibility(View.VISIBLE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_one_month.setText(smartPlanModel.getAmount1Month());
            }

            if(smartPlanModel.getPackage_id().equalsIgnoreCase("2")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_two_month.setText(smartPlanModel.getAmount2Months());
            }

            if(smartPlanModel.getPackage_id().equalsIgnoreCase("3")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setText(smartPlanModel.getAmount3Months());
            }
        }


        if(smartPlanModel.getsPlanName().equalsIgnoreCase("GOLD"))
        {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_ftr);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_opt);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_ftr);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_opt);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_commodity);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);
            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("1")) {
                holder.tv_one_month.setVisibility(View.VISIBLE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_one_month.setText(smartPlanModel.getAmount1Month());
            }

            if(smartPlanModel.getPackage_id()!=null &&  smartPlanModel.getPackage_id().equalsIgnoreCase("2")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_two_month.setText(smartPlanModel.getAmount2Months());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("3")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setText(smartPlanModel.getAmount3Months());
            }
        }

        if(smartPlanModel.getsPlanName().equalsIgnoreCase("PLATINUM"))
        {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_ftr);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_opt);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_ftr);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_opt);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_commodity);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("1")) {
                holder.tv_one_month.setVisibility(View.VISIBLE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_one_month.setText(smartPlanModel.getAmount1Month());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("2")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_two_month.setText(smartPlanModel.getAmount2Months());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("3")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setText(smartPlanModel.getAmount3Months());
            }
        }

        if(smartPlanModel.getsPlanName().equalsIgnoreCase("FUTURE SPECIAL"))
        {
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_ftr);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_opt);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_ftr);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_opt);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_commodity);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("1")) {
                holder.tv_one_month.setVisibility(View.VISIBLE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_one_month.setText(smartPlanModel.getAmount1Month());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("2")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_two_month.setText(smartPlanModel.getAmount2Months());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("3")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setText(smartPlanModel.getAmount3Months());
            }
        }

        if(smartPlanModel.getsPlanName().equalsIgnoreCase("OPTION SPECIAL"))
        {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_ftr);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_stk_opt);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_ftr);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_index_opt);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_commodity);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("1")) {
                holder.tv_one_month.setVisibility(View.VISIBLE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_one_month.setText(smartPlanModel.getAmount1Month());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("2")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_two_month.setText(smartPlanModel.getAmount2Months());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("3")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setText(smartPlanModel.getAmount3Months());
            }
        }

        if(smartPlanModel.getsPlanName().equalsIgnoreCase("COMMODITY SPECIAL"))
        {
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_ftr);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_stk_opt);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_ftr);
            Picasso.with(context).load(R.mipmap.ic_red_close).into(holder.iv_index_opt);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_commodity);
            Picasso.with(context).load(R.mipmap.ic_check_green).into(holder.iv_telegram_update);
            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("1")) {
                holder.tv_one_month.setVisibility(View.VISIBLE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_one_month.setText(smartPlanModel.getAmount1Month());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("2")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setVisibility(View.GONE);
                holder.tv_two_month.setText(smartPlanModel.getAmount2Months());
            }

            if(smartPlanModel.getPackage_id()!=null && smartPlanModel.getPackage_id().equalsIgnoreCase("3")) {
                holder.tv_one_month.setVisibility(View.GONE);
                holder.tv_two_month.setVisibility(View.GONE);
                holder.tv_three_month.setVisibility(View.VISIBLE);
                holder.tv_three_month.setText(smartPlanModel.getAmount3Months());
            }
        }






    }

    @Override
    public int getItemCount() {
        return smartPlanModelsList.size();
    }


    public class PlanViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title,tv_msg1,tv_one_month,tv_two_month,tv_three_month;
        TextView img_green;
        ImageView iv_stk_ftr,iv_stk_opt,iv_index_ftr,iv_index_opt,iv_commodity,iv_telegram_update;

        public PlanViewHolder(View view) {
            super(view);
            img_green = (TextView) view.findViewById(R.id.img);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_msg1 = (TextView) view.findViewById(R.id.tv_msg1);
            iv_stk_ftr = (ImageView) view.findViewById(R.id.iv_stk_ftr);
            iv_stk_opt = (ImageView) view.findViewById(R.id.iv_stk_opt);
            iv_index_ftr= (ImageView) view.findViewById(R.id.iv_index_ftr);
            iv_index_opt= (ImageView) view.findViewById(R.id.iv_index_opt);
            iv_commodity= (ImageView) view.findViewById(R.id.iv_commodity);
            iv_telegram_update= (ImageView) view.findViewById(R.id.iv_telegram_update);
            tv_one_month= (TextView) view.findViewById(R.id.tv_one_month);
            tv_two_month= (TextView) view.findViewById(R.id.tv_two_month);
            tv_three_month =(TextView) view.findViewById(R.id.tv_three_month);
        }
    }


}
