package in.techxilla.www.marketxilla.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;

public class StackLayoutAdapter extends RecyclerView.Adapter<StackLayoutAdapter.StackHolder> {
    private ArrayList<CallModel> callArrayList;
    Context mCtx;
    int mStackCount;

    @NonNull
    @Override
    public StackLayoutAdapter.StackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_calls, viewGroup, false);
        return new StackLayoutAdapter.StackHolder(view);
    }

    public StackLayoutAdapter(Context mCtx, ArrayList<CallModel> callArrayList) {
        this.mCtx = mCtx;
        this.callArrayList = callArrayList;
        mStackCount = callArrayList.size();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull final StackLayoutAdapter.StackHolder viewHolder, int position) {
        int res;

        viewHolder.tv_performance_for.setText(callArrayList.get(position).getPerformance_for());
        viewHolder.TvStockName.setText(callArrayList.get(position).getStock_name());

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(callArrayList.get(position).getDate());
            String convertDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);

            viewHolder.tv_date_time.setText(convertDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (callArrayList.get(position).getIs_buy_sell() != null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("1")) {
            if (callArrayList.get(position).getPerformance_for().equalsIgnoreCase("Commodity")) {
                viewHolder.tv_above_below.setText("BETWEEN \n" + callArrayList.get(position).getBuy_sell_above_below());
            } else {
                viewHolder.tv_above_below.setText("BUY ABOVE " + callArrayList.get(position).getBuy_sell_above_below());
            }

            viewHolder.tv_sell_buy.setText("INTRADAY BUY");
            viewHolder.tv_sell_buy.setBackground(mCtx.getDrawable(R.drawable.cw_button_shadow_green));
            viewHolder.iv_up_down.setImageDrawable(mCtx.getDrawable(R.drawable.up));
            viewHolder.iv_up_down.setImageTintList(ColorStateList.valueOf(mCtx.getResources().getColor(R.color.mpn_green)));
        } else if (callArrayList.get(position).getIs_buy_sell() != null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("2")) {
            if (callArrayList.get(position).getPerformance_for().equalsIgnoreCase("Commodity")) {
                viewHolder.tv_above_below.setText("BETWEEN \n" + callArrayList.get(position).getBuy_sell_above_below());
            } else {
                viewHolder.tv_above_below.setText("SELL BELOW " + callArrayList.get(position).getBuy_sell_above_below());
            }

            viewHolder.tv_sell_buy.setText("INTRADAY SELL");
            viewHolder.tv_sell_buy.setBackground(mCtx.getDrawable(R.drawable.cw_button_shadow_red));
            viewHolder.iv_up_down.setImageDrawable(mCtx.getDrawable(R.drawable.down));
            viewHolder.iv_up_down.setImageTintList(ColorStateList.valueOf(mCtx.getResources().getColor(R.color.mpn_red)));

        }


        if (callArrayList.get(position).getCe_pe() != null && callArrayList.get(position).getCe_pe().equalsIgnoreCase("1")) {
            viewHolder.tv_strike_ce.setText("STRIKE " + CommonMethods.DecimalNumberDisplayFormattingWithComma(callArrayList.get(position).getStrike()) + " CE");
            viewHolder.tv_strike_ce.setVisibility(View.VISIBLE);

        } else if (callArrayList.get(position).getCe_pe() != null && callArrayList.get(position).getCe_pe().equalsIgnoreCase("2")) {
            viewHolder.tv_strike_ce.setText("STRIKE " + CommonMethods.DecimalNumberDisplayFormattingWithComma(callArrayList.get(position).getStrike()) + " PE");
            viewHolder.tv_strike_ce.setVisibility(View.VISIBLE);

        } else {
            viewHolder.tv_strike_ce.setVisibility(View.INVISIBLE);
        }

        try {
            if (callArrayList.get(position).getBuy_sell_closing_price() != null && !callArrayList.get(position).getBuy_sell_closing_price().equalsIgnoreCase("")) {
                if ((Double.parseDouble(callArrayList.get(position).getBuy_sell_closing_price())) > 0) {
                    viewHolder.linear_closinglayout.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.linear_closinglayout.setVisibility(View.GONE);
                }
            } else {
                viewHolder.linear_closinglayout.setVisibility(View.GONE);
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        try
        {
            String mProfit_loss;
            mProfit_loss = String.format("%2.7s", callArrayList.get(position).getProfit_loss());

            System.out.println(mProfit_loss);
            if (callArrayList.get(position).getProfit_loss() != null && !callArrayList.get(position).getProfit_loss().equalsIgnoreCase("")) {
                if ((Double.parseDouble(String.valueOf(callArrayList.get(position).getProfit_loss()))) > 0) {
                    viewHolder.tv_profit_loss.setTextColor(mCtx.getResources().getColor(R.color.mpn_green));
                    viewHolder.tv_profit_loss.setText("\u20B9 " + mProfit_loss);
                } else {
                    viewHolder.tv_profit_loss.setTextColor(mCtx.getResources().getColor(R.color.mpn_red));
                    viewHolder.tv_profit_loss.setText("\u20B9 " + mProfit_loss);
                }
            } else {
                viewHolder.tv_profit_loss.setTextColor(mCtx.getResources().getColor(R.color.black));
                viewHolder.tv_profit_loss.setText("\u20B9 0");
            }
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }

        viewHolder.tv_target1.setText("\u20B9 " + callArrayList.get(position).

                getTarget1());
        viewHolder.tv_target2.setText("\u20B9 " + callArrayList.get(position).

                getTarget2());
        viewHolder.tv_target3.setText("\u20B9 " + callArrayList.get(position).

                getTarget3());
        viewHolder.tv_stop_loss.setText("\u20B9 " + callArrayList.get(position).

                getStop_loss());
        viewHolder.tv_closingprice.setText("\u20B9 " + callArrayList.get(position).

                getBuy_sell_closing_price());

    }

    @Override
    public int getItemCount() {
        return mStackCount;
    }

    public class StackHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView iv_up_down;
        TextView tv_performance_for,TvStockName, tv_date_time, tv_above_below, tv_sell_buy, tv_strike_ce, tv_target1, tv_target2, tv_target3, tv_stop_loss, tv_closingprice, tv_profit_loss;
        LinearLayout linear_closinglayout;

        StackHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            linear_closinglayout = (LinearLayout) itemView.findViewById(R.id.row_linear_closinglayout);
            tv_performance_for = (TextView) itemView.findViewById(R.id.tv_performance_for);
            TvStockName = (TextView) itemView.findViewById(R.id.TvStockName);
            tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
            tv_above_below = (TextView) itemView.findViewById(R.id.tv_above_below);
            tv_strike_ce = (TextView) itemView.findViewById(R.id.tv_strike_ce);
            tv_sell_buy = (TextView) itemView.findViewById(R.id.tv_sell_buy);
            iv_up_down = (ImageView) itemView.findViewById(R.id.iv_up_down);
            tv_target1 = (TextView) itemView.findViewById(R.id.tv_target1);
            tv_target2 = (TextView) itemView.findViewById(R.id.tv_target2);
            tv_target3 = (TextView) itemView.findViewById(R.id.tv_target3);
            tv_stop_loss = (TextView) itemView.findViewById(R.id.tv_stop_loss);
            tv_profit_loss = (TextView) itemView.findViewById(R.id.tv_profit_loss);
            tv_closingprice = (TextView) itemView.findViewById(R.id.tv_closingprice);


        }
    }
}