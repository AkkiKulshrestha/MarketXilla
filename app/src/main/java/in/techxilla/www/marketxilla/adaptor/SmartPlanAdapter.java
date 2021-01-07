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


public class SmartPlanAdapter extends RecyclerView.Adapter<SmartPlanAdapter.SmartPlanViewHolder> {

    public List<SmartPlanModel> smartPlanModelsList;
    private Context context;


    public SmartPlanAdapter(List<SmartPlanModel> smartPlanModelsList, Context context) {
        this.smartPlanModelsList = smartPlanModelsList;
        this.context = context;
    }



    @NonNull
    @Override
    public SmartPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.smartplan_menu, parent, false);

        return new SmartPlanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SmartPlanViewHolder holder, int position) {

        final SmartPlanModel smartPlanModel = smartPlanModelsList.get(position);


        if(position==0){
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
        }

        holder.txt_smartplan_name.setText(smartPlanModel.getPlanname());
        holder.txt_smartplan_description.setText(smartPlanModel.getPlanDescription());

        Picasso.with(context).load(smartPlanModel.getImage()).into(holder.planicons);


        holder.smartplan_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SubscriptionActivity.class);
                i.putExtra("smart_name", smartPlanModel.getPlanname());
                context.startActivity(i);
                ((Activity)  context).overridePendingTransition(R.animator.move_left,R.animator.move_right);
            }
        });

    }

    @Override
    public int getItemCount() {
        return smartPlanModelsList.size();
    }


    public class SmartPlanViewHolder extends RecyclerView.ViewHolder {

        TextView txt_smartplan_name,txt_smartplan_description;
        ImageView planicons;
        CardView smartplan_card;
        LinearLayout linear_bg;

        public SmartPlanViewHolder(View view) {
            super(view);
            planicons = (ImageView) view.findViewById(R.id.planicons);
            txt_smartplan_name = (TextView) view.findViewById(R.id.txt_smartplan_name);
            txt_smartplan_description = (TextView) view.findViewById(R.id.txt_smartplan_description);
            smartplan_card = (CardView) view.findViewById(R.id.smartplan_card);
            linear_bg = (LinearLayout)view.findViewById(R.id.linear_bg);
        }
    }

}