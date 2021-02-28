package in.techxilla.www.marketxilla.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.CallHolder> {
    private final Context mCtx;
    private final int mStackCount;
    private List<CallModel> callArrayList = new ArrayList<>();
    private List<CallModel> tempItemList;
    private List<CallModel> filteredItemList;
    public double netProfitLoss = 0.0;

    public CallsAdapter(Context mCtx, ArrayList<CallModel> callArrayList) {
        this.mCtx = mCtx;
        this.callArrayList = callArrayList;
        this.tempItemList = new ArrayList<>(callArrayList);
        this.filteredItemList = new ArrayList<>();
        mStackCount = callArrayList.size();
        setResult(callArrayList);
    }

    private void setResult(ArrayList<CallModel> callArrayList) {
        this.filteredItemList.clear();
        if (null != callArrayList) {
            this.filteredItemList.addAll(callArrayList);
        }
        grandTotal(filteredItemList);
        notifyDataSetChanged();
    }

    private void grandTotal(List<CallModel> items){

        netProfitLoss = 0.0;
        for(int i = 0 ; i < items.size(); i++) {
            double profit_loss_dou = !items.get(i).getProfit_loss().equalsIgnoreCase("") ? Double.parseDouble(items.get(i).getProfit_loss()) : 0.0;
            netProfitLoss += profit_loss_dou;
        }
    }

    @Override
    public CallsAdapter.CallHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_calls, viewGroup, false);
        return new CallHolder(view);
    }

    @Override
    public void onBindViewHolder(final CallsAdapter.CallHolder viewHolder, int position) {
        final CallModel callModel = filteredItemList.get(position);
        viewHolder.setItem(mCtx, callModel, position, filteredItemList.size());

        viewHolder.tv_netProfitLoss.setText("Net Profit / Loss: \u20B9 " + CommonMethods.DecimalNumberDisplayFormattingWithComma(String.format("%.2f",netProfitLoss)));

        if(position==0){
            viewHolder.tv_netProfitLoss.setVisibility(View.VISIBLE);
        }else {
            viewHolder.tv_netProfitLoss.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredItemList.size();
    }

    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String filterString = charSequence.toString().trim();
                if (filterString.isEmpty()) {
                    filteredItemList = tempItemList;
                    //mFilteredList = mArrayList;
                } else if (filterString.equalsIgnoreCase("All Segments")) {
                    filteredItemList.clear();
                    filteredItemList.addAll(tempItemList);
                } else {
                    final List<CallModel> list = new ArrayList<>(tempItemList);
                    filteredItemList.clear();
                    for (CallModel item : list) {
                        String filterableString = item.getPerformance_for();
                        if (filterableString.toLowerCase().equalsIgnoreCase(filterString)) {
                            filteredItemList.add(item);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItemList;
                filterResults.count = filteredItemList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredItemList = (ArrayList<CallModel>) filterResults.values;
                grandTotal(filteredItemList);
                notifyDataSetChanged();
            }
        };


    }

    public static class CallHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_up_down;
        private final TextView tv_netProfitLoss,tv_performance_for, TvStockName, tv_date_time, tv_above_below, tv_sell_buy, tv_strike_ce, tv_target1, tv_target2, tv_target3, tv_stop_loss, tv_closingprice, tv_profit_loss;
        private final LinearLayout linear_closinglayout;

        CallHolder(@NonNull View itemView) {
            super(itemView);
            tv_netProfitLoss = (TextView) itemView.findViewById(R.id.tv_netProfitLoss);
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

        @SuppressLint({"SimpleDateFormat", "SetTextI18n", "UseCompatLoadingForDrawables", "DefaultLocale"})
        private void setItem(final Context context, final CallModel callModel, final int position, final int size) {
            tv_performance_for.setText(callModel.getPerformance_for());
            TvStockName.setText(callModel.getStock_name());

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(callModel.getDate());
                String convertDate = null;
                if (date != null) {
                    convertDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);
                }
                tv_date_time.setText(convertDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (callModel.getIs_buy_sell() != null && callModel.getIs_buy_sell().equalsIgnoreCase("1")) {
                if (callModel.getPerformance_for().equalsIgnoreCase("Commodity")) {
                    tv_above_below.setText("BUY BETWEEN \n" + callModel.getBuy_sell_above_below());
                } else {
                    tv_above_below.setText("BUY ABOVE " + callModel.getBuy_sell_above_below());
                }

                tv_sell_buy.setText("INTRADAY BUY");
                tv_sell_buy.setBackground(context.getDrawable(R.drawable.cw_button_shadow_green));
                iv_up_down.setImageDrawable(context.getDrawable(R.drawable.up));
                iv_up_down.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.mpn_green)));
            } else if (callModel.getIs_buy_sell() != null && callModel.getIs_buy_sell().equalsIgnoreCase("2")) {
                if (callModel.getPerformance_for().equalsIgnoreCase("Commodity")) {
                    tv_above_below.setText("SELL BETWEEN \n" + callModel.getBuy_sell_above_below());
                } else {
                    tv_above_below.setText("SELL BELOW " + callModel.getBuy_sell_above_below());
                }

                tv_sell_buy.setText("INTRADAY SELL");
                tv_sell_buy.setBackground(context.getDrawable(R.drawable.cw_button_shadow_red));
                iv_up_down.setImageDrawable(context.getDrawable(R.drawable.down));
                iv_up_down.setImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.mpn_red)));
            }

            if (callModel.getCe_pe() != null && callModel.getCe_pe().equalsIgnoreCase("1")) {
                tv_strike_ce.setText("STRIKE " + CommonMethods.DecimalNumberDisplayFormattingWithComma(callModel.getStrike()) + " CE");
                tv_strike_ce.setVisibility(View.VISIBLE);
            } else if (callModel.getCe_pe() != null && callModel.getCe_pe().equalsIgnoreCase("2")) {
                tv_strike_ce.setText("STRIKE " + CommonMethods.DecimalNumberDisplayFormattingWithComma(callModel.getStrike()) + " PE");
                tv_strike_ce.setVisibility(View.VISIBLE);
            } else {
                tv_strike_ce.setVisibility(View.INVISIBLE);
            }

            try {
                if (callModel.getBuy_sell_closing_price() != null && !callModel.getBuy_sell_closing_price().equalsIgnoreCase("")) {
                    if ((Double.parseDouble(callModel.getBuy_sell_closing_price())) > 0) {
                        linear_closinglayout.setVisibility(View.VISIBLE);
                    } else {
                        linear_closinglayout.setVisibility(View.GONE);
                    }
                } else {
                    linear_closinglayout.setVisibility(View.GONE);
                }
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            try {
                String mProfit_loss;
                mProfit_loss = String.format("%2.7s", callModel.getProfit_loss());
                System.out.println(mProfit_loss);
                if (callModel.getProfit_loss() != null && !callModel.getProfit_loss().equalsIgnoreCase("")) {
                    if ((Double.parseDouble(String.valueOf(callModel.getProfit_loss()))) > 0) {
                        tv_profit_loss.setTextColor(context.getResources().getColor(R.color.mpn_green));
                        tv_profit_loss.setText("\u20B9 " + mProfit_loss);
                    } else {
                        tv_profit_loss.setTextColor(context.getResources().getColor(R.color.mpn_red));
                        tv_profit_loss.setText("\u20B9 " + mProfit_loss);
                    }
                } else {
                    tv_profit_loss.setTextColor(context.getResources().getColor(R.color.black));
                    tv_profit_loss.setText("\u20B9 0");
                }
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }

            tv_target1.setText("\u20B9 " + callModel.getTarget1());
            tv_target2.setText("\u20B9 " + callModel.getTarget2());
            tv_target3.setText("\u20B9 " + callModel.getTarget3());
            tv_stop_loss.setText("\u20B9 " + callModel.getStop_loss());
            tv_closingprice.setText("\u20B9 " + callModel.getBuy_sell_closing_price());
        }



    }
}