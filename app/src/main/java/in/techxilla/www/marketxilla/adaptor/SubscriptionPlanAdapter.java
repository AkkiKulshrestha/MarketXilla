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


        /*if(position==0){
            holder.linear_bg.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txt_smartplan_name.setTextColor(Color.WHITE);
            holder.txt_smartplan_description.setTextColor(Color.WHITE);
        }else if(position==1){
            holder.linear_bg.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txt_smartplan_name.setTextColor(Color.WHITE);
            holder.txt_smartplan_description.setTextColor(Color.WHITE);
        }else if(position==2){
            holder.linear_bg.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.txt_smartplan_name.setTextColor(Color.WHITE);
            holder.txt_smartplan_description.setTextColor(Color.WHITE);
        }*/

        holder.img_green.setText(smartPlanModel.getsPlan());
        holder.tv_title.setText(smartPlanModel.getsPlanName());
        holder.tv_msg1.setText(smartPlanModel.getsDetails());

        //Picasso.with(context).load(smartPlanModel.getImage()).into(holder.planicons);

        holder.tv_type1.setText(smartPlanModel.getsStock_Future());
        holder.tv_type1.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.mipmap.ic_check_green, 0);
        holder.tv_type2.setText(smartPlanModel.getsStock_Options());
        holder.tv_type3.setText(smartPlanModel.getsIndex_Future());
        holder.tv_type4.setText(smartPlanModel.getsIndex_Options());
        holder.tv_type5.setText(smartPlanModel.getsCommodity());
        holder.tv_type6.setText(smartPlanModel.getsTelegram_Updates());
        holder.tv_duration.setText(smartPlanModel.getsDuration());

    }

    @Override
    public int getItemCount() {
        return smartPlanModelsList.size();
    }


    public class PlanViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title,tv_msg1,tv_type1,tv_type2,tv_type3,tv_type4,tv_type5,tv_type6,tv_duration;
        TextView img_green;


        public PlanViewHolder(View view) {
            super(view);
            img_green = (TextView) view.findViewById(R.id.img);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_msg1 = (TextView) view.findViewById(R.id.tv_msg1);
            tv_type1 = (TextView) view.findViewById(R.id.tv_type1);
            tv_type2 = (TextView) view.findViewById(R.id.tv_type2);
            tv_type3= (TextView) view.findViewById(R.id.tv_type3);
            tv_type4= (TextView) view.findViewById(R.id.tv_type4);
            tv_type5= (TextView) view.findViewById(R.id.tv_type5);
            tv_type6= (TextView) view.findViewById(R.id.tv_type6);
            tv_duration =(TextView) view.findViewById(R.id.tv_duration);
        }
    }


}
