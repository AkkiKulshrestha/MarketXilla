/*
package in.techxilla.www.marketxilla.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.techxilla.www.marketxilla.MainActivity;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;

public class StackLayoutAdapter extends RecyclerView.Adapter<StackLayoutAdapter.StackHolder> {
    private ArrayList<CallModel> callArrayList;
    Context mCtx;
    private int mStackCount = 30;


    @NonNull
    @Override
    public StackLayoutAdapter.StackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_calls, viewGroup, false);
        return new StackHolder(view);
    }

    public  StackLayoutAdapter(Context mCtx, ArrayList<CallModel> callArrayList) {
        this.mCtx = mCtx;
        this.callArrayList = callArrayList;

    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final StackLayoutAdapter.StackHolder viewHolder, int position) {
        int res;



        viewHolder.TvStockName.setText(callArrayList.get(position).getStock_name());

        Date date= null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(callArrayList.get(position).getDate());
            String convertDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);

            viewHolder.tv_date_time.setText(convertDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(callArrayList.get(position).getIs_buy_sell()!=null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("1")){
            viewHolder.tv_above_below.setText("ABOVE "+callArrayList.get(position).getBuy_sell_above_below() );
            viewHolder.tv_sell_buy.setText("INTRADAY BUY");
            viewHolder.tv_sell_buy.setBackground(mCtx.getResources().getDrawable(R.drawable.cw_button_shadow_green));
            viewHolder.iv_up_down.setImageDrawable(mCtx.getDrawable(R.drawable.up));
        }else if(callArrayList.get(position).getIs_buy_sell()!=null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("2")){
            viewHolder.tv_above_below.setText("BELOW "+callArrayList.get(position).getBuy_sell_above_below());
            viewHolder.tv_sell_buy.setText("INTRADAY SELL");
            viewHolder.tv_sell_buy.setBackground(mCtx.getResources().getDrawable(R.drawable.cw_button_shadow_red));
            viewHolder.iv_up_down.setImageDrawable(mCtx.getDrawable(R.drawable.down));

        }



        if(callArrayList.get(position).getCe_pe()!=null && callArrayList.get(position).getCe_pe().equalsIgnoreCase("1")){
            viewHolder.tv_strike_ce.setText("STRIKE "+ CommonMethods.DecimalNumberDisplayFormattingWithComma(callArrayList.get(position).getStrike())+" CE" );
            viewHolder.tv_strike_ce.setVisibility(View.VISIBLE);

        }else if(callArrayList.get(position).getCe_pe()!=null && callArrayList.get(position).getCe_pe().equalsIgnoreCase("2")){
            viewHolder.tv_strike_ce.setText("STRIKE "+CommonMethods.DecimalNumberDisplayFormattingWithComma(callArrayList.get(position).getStrike())+" PE" );
            viewHolder.tv_strike_ce.setVisibility(View.VISIBLE);

        }else {
            viewHolder.tv_strike_ce.setVisibility(View.INVISIBLE);
        }




        viewHolder.tv_target1.setText("\u20B9 "+callArrayList.get(position).getTarget1());
        viewHolder.tv_target2.setText("\u20B9 "+callArrayList.get(position).getTarget2());
        viewHolder.tv_target3.setText("\u20B9 "+callArrayList.get(position).getTarget3());
        viewHolder.tv_stop_loss.setText("\u20B9 "+callArrayList.get(position).getStop_loss());

    }

    @Override
    public int getItemCount() {
        return mStackCount;
    }

    static class StackHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView iv_up_down;
        TextView TvStockName,tv_date_time,tv_above_below,tv_sell_buy,tv_strike_ce,tv_target1,tv_target2,tv_target3,tv_stop_loss;

        StackHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            TvStockName = (TextView) itemView.findViewById(R.id.TvStockName);
            tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
            tv_above_below = (TextView) itemView.findViewById(R.id.tv_above_below);
            tv_strike_ce = (TextView) itemView.findViewById(R.id.tv_strike_ce);
            tv_sell_buy = (TextView) itemView.findViewById(R.id.tv_sell_buy);
            iv_up_down = (ImageView)itemView.findViewById(R.id.iv_up_down);
            tv_target1 = (TextView) itemView.findViewById(R.id.tv_target1);
            tv_target2 = (TextView) itemView.findViewById(R.id.tv_target2);
            tv_target3 = (TextView) itemView.findViewById(R.id.tv_target3);
            tv_stop_loss = (TextView) itemView.findViewById(R.id.tv_stop_loss);





        }
    }
}
*/
