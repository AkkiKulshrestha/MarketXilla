package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.adaptor.ImageSliderAdapter;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class PaidUserFragment extends Fragment {


    String StrMemberId, StrMemberName, StrMemberEmailId, StrMemberMobile, StrMemberUserName;
    ProgressDialog myDialog;
    TextView TV_NameTxt, TV_Day_TimeDisplayingTxt;
    String greeting;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    SliderView sliderView;
    ImageSliderAdapter adapter;

    TextView tv_noRecordFound;
    RecyclerView recycler_list;
    RecyclerView.LayoutManager layoutManager;
    private int mStackCount = 30;


    ArrayList<CallModel> callModel_list = new ArrayList<>();
    CallModel callListModel;
    StackLayoutAdapter stackLayoutAdapter;

    public Toolbar toolbar;
    public ActionBar actionBar;
    TextView tv_title_plan, tv_valid_till;
    String StrPlanId,mSubscribed_till;
    View rootView;
    Context mContext;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_paid_user, null);

        initUI();
        return rootView;


    }

    private void initUI() {

        mContext = getContext();
        NewDashboard activity = (NewDashboard) getActivity();
        toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        ImageView iv_refresh = (ImageView) toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlanDetail();
            }
        });

        tv_title_plan = (TextView) rootView.findViewById(R.id.tv_title_plan);
        tv_valid_till = (TextView) rootView.findViewById(R.id.tv_valid_till);
        tv_noRecordFound = (TextView) rootView.findViewById(R.id.tv_noRecordFound);
        recycler_list = rootView.findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setNestedScrollingEnabled(false);


        final FragmentActivity c = getActivity();
        layoutManager = new LinearLayoutManager(c);
        recycler_list.setLayoutManager(layoutManager);

        getPlanDetail();


    }

    private void getPlanDetail() {


        String Uiid_id = UUID.randomUUID().toString();
        String StrMemberId = UtilitySharedPreferences.getPrefs(mContext, "MemberId");
        final String get_plan_details_info = ROOT_URL + "get_user_subscription_details.php?"+Uiid_id+"&user_id=" + StrMemberId;
        Log.d("URL --->", get_plan_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_plan_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response", "" + response);

                            JSONObject obj = new JSONObject(response);


                            boolean status = obj.getBoolean("status");
                            String Message = obj.getString("message");

                            JSONArray m_jArry = obj.getJSONArray("data");

                            mStackCount = m_jArry.length();

                            if (mStackCount == 0)
                            {
                                tv_title_plan.setText("NO ACTIVE PLAN");
                                tv_valid_till.setVisibility(View.GONE);

                            }else {
                                for (int i = 0; i < m_jArry.length(); i++) {

                                    JSONObject jo_data = m_jArry.getJSONObject(i);

                                    StrPlanId = jo_data.getString("plan_id");
                                    String plan_name = jo_data.getString("plan_name");
                                    String subscribed_till = jo_data.getString("subscribed_till");
                                    SimpleDateFormat sdf, sdf2, sdf21;
                                    Date newSubscriptedTilldate, currentdate2, newSubscriptedOndate;

                                    if (!subscribed_till.equalsIgnoreCase("")) {
                                        try {
                                            sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            newSubscriptedTilldate = sdf.parse(subscribed_till);
                                            sdf21 = new SimpleDateFormat("dd MMM, yyyy");
                                            sdf2 = new SimpleDateFormat("dd/MM/yyyy");
                                            mSubscribed_till = sdf21.format(newSubscriptedTilldate);
                                            String SubcribedTill_DMY = sdf2.format(newSubscriptedTilldate);
                                            if(i==0 && !CommonMethods.isDateExpired(SubcribedTill_DMY)) {
                                                tv_title_plan.setText("Current Plan : " + plan_name);
                                                tv_valid_till.setVisibility(View.VISIBLE);
                                                tv_valid_till.setText("Valid Till \n" + mSubscribed_till);
                                                fetchCallData(StrPlanId);

                                            }else {
                                                tv_title_plan.setText("NO ACTIVE PLAN");
                                                tv_valid_till.setVisibility(View.GONE);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                }

                            }


                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        //Log.d("Vollley Err", volleyError.toString());

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            } else {
                  CommonMethods.DisplayToastWarning(mContext, "No Internet Connection");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }



    private void fetchCallData(String PlanId) {

        if (callModel_list != null) {
            callModel_list = new ArrayList<>();
        }

        String Uiid_id = UUID.randomUUID().toString();
        String URL_GetCallList = ROOT_URL + "fetchPerformanceDataForPaidUser.php?_" + Uiid_id + "&plan_id="+PlanId;


        try {
            Log.d("URL", URL_GetCallList);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetCallList,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);

                                try {

                                    JSONObject jobj = new JSONObject(response);

                                    boolean status = jobj.getBoolean("status");

                                    if(status) {
                                        JSONArray data = jobj.getJSONArray("data");
                                        mStackCount = data.length();
                                        if (mStackCount > 0) {
                                            for (int k = 0; k < data.length(); k++) {
                                                JSONObject jsonObject = data.getJSONObject(k);
                                                Log.d("jobj", "" + jsonObject);
                                                String id = jsonObject.getString("id");
                                                String stock_name = jsonObject.getString("stock_name");
                                                String date = jsonObject.getString("date");
                                                String performance_for = jsonObject.getString("performance_for");
                                                String performance_for_id = jsonObject.getString("performance_for_id");
                                                String is_buy_sell = jsonObject.getString("is_buy_sell");
                                                String buy_sell_above_below = jsonObject.getString("buy_sell_above_below");
                                                String stop_loss = jsonObject.getString("stop_loss");
                                                String target1 = jsonObject.getString("target1");
                                                String target2 = jsonObject.getString("target2");
                                                String target3 = jsonObject.getString("target3");
                                                String ce_pe = jsonObject.getString("ce_pe");
                                                String strike = jsonObject.getString("strike");
                                                String buy_sell_closing_price = jsonObject.getString("buy_sell_closing_price");
                                                String profit_loss = jsonObject.getString("profit_loss");
                                                String is_active_performance = jsonObject.getString("is_active_performance");
                                                String is_call_for_paid_customer = jsonObject.getString("is_call_for_paid_customer");

                                                callListModel = new CallModel();
                                                callListModel.setId(id);
                                                callListModel.setStock_name(stock_name.toUpperCase());
                                                callListModel.setDate(date);
                                                callListModel.setPerformance_for(performance_for);
                                                callListModel.setPerformance_for_id(performance_for_id);
                                                callListModel.setIs_buy_sell(is_buy_sell);
                                                callListModel.setBuy_sell_above_below(buy_sell_above_below);
                                                callListModel.setStop_loss(stop_loss);
                                                callListModel.setTarget1(target1);
                                                callListModel.setTarget2(target2);
                                                callListModel.setTarget3(target3);
                                                callListModel.setCe_pe(ce_pe);
                                                callListModel.setStrike(strike);
                                                callListModel.setBuy_sell_closing_price(buy_sell_closing_price);
                                                callListModel.setProfit_loss(profit_loss);
                                                callListModel.setIs_active_performance(is_active_performance);
                                                callListModel.setIs_call_for_paid_customer(is_call_for_paid_customer);

                                                callModel_list.add(callListModel);


                                            }

                                            stackLayoutAdapter = new StackLayoutAdapter(mContext, callModel_list);
                                            recycler_list.setVisibility(View.VISIBLE);
                                            tv_noRecordFound.setVisibility(View.GONE);
                                            recycler_list.setAdapter(stackLayoutAdapter);
                                            stackLayoutAdapter.notifyDataSetChanged();
                                        }
                                    }else {

                                        tv_noRecordFound.setVisibility(View.VISIBLE);
                                        recycler_list.setVisibility(View.GONE);

                                    }

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();

                    }
                });

                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, -1, 0));
                requestQueue.add(stringRequest);


            } else {
                CommonMethods.DisplayToastInfo(mContext, "No Internet Connection");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }


    }


    class StackLayoutAdapter extends RecyclerView.Adapter<StackLayoutAdapter.StackHolder> {
        private ArrayList<CallModel> callArrayList;
        Context mCtx;

        @NonNull
        @Override
        public StackLayoutAdapter.StackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_calls, viewGroup, false);
            return new StackHolder(view);
        }

        public StackLayoutAdapter(Context mCtx, ArrayList<CallModel> callArrayList) {
            this.mCtx = mCtx;
            this.callArrayList = callArrayList;

        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull final StackLayoutAdapter.StackHolder viewHolder, int position) {
            int res;


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
                    viewHolder.tv_above_below.setText("ABOVE " + callArrayList.get(position).getBuy_sell_above_below());
                }

                viewHolder.tv_sell_buy.setText("INTRADAY BUY");
                viewHolder.tv_sell_buy.setBackground(mCtx.getDrawable(R.drawable.cw_button_shadow_green));
                viewHolder.iv_up_down.setImageDrawable(mCtx.getDrawable(R.drawable.up));
                viewHolder.iv_up_down.setImageTintList(ColorStateList.valueOf(mCtx.getResources().getColor(R.color.mpn_green)));
            } else if (callArrayList.get(position).getIs_buy_sell() != null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("2")) {
                if (callArrayList.get(position).getPerformance_for().equalsIgnoreCase("Commodity")) {
                    viewHolder.tv_above_below.setText("BETWEEN \n" + callArrayList.get(position).getBuy_sell_above_below());
                } else {
                    viewHolder.tv_above_below.setText("BELOW " + callArrayList.get(position).getBuy_sell_above_below());
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
                        viewHolder.tv_profit_loss.setTextColor(mContext.getResources().getColor(R.color.mpn_green));
                        viewHolder.tv_profit_loss.setText("\u20B9 " + mProfit_loss);
                    } else {
                        viewHolder.tv_profit_loss.setTextColor(mContext.getResources().getColor(R.color.mpn_red));
                        viewHolder.tv_profit_loss.setText("\u20B9 " + mProfit_loss);
                    }
                } else {
                    viewHolder.tv_profit_loss.setTextColor(mContext.getResources().getColor(R.color.black));
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

        class StackHolder extends RecyclerView.ViewHolder {
            View itemView;
            ImageView iv_up_down;
            TextView TvStockName, tv_date_time, tv_above_below, tv_sell_buy, tv_strike_ce, tv_target1, tv_target2, tv_target3, tv_stop_loss, tv_closingprice, tv_profit_loss;
            LinearLayout linear_closinglayout;

            StackHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;

                linear_closinglayout = (LinearLayout) itemView.findViewById(R.id.row_linear_closinglayout);
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

}