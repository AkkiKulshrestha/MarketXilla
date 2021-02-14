package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import in.techxilla.www.marketxilla.adaptor.StackLayoutAdapter;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class PaidUserFragment extends Fragment {


    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    public Toolbar toolbar;
    public ActionBar actionBar;
    String StrMemberId, StrMemberName, StrMemberEmailId, StrMemberMobile, StrMemberUserName;
    ProgressDialog myDialog;
    TextView TV_NameTxt, TV_Day_TimeDisplayingTxt;
    String greeting;
    SliderView sliderView;
    ImageSliderAdapter adapter;
    TextView tv_noRecordFound;
    RecyclerView recycler_list;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<CallModel> callModel_list = new ArrayList<>();
    CallModel callListModel;
    StackLayoutAdapter stackLayoutAdapter;
    TextView tv_title_plan, tv_valid_till;
    String StrPlanId, mSubscribed_till;
    View rootView;
    Context mContext;
    private int mStackCount = 30;

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
        myDialog = new ProgressDialog(mContext);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

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

        if (myDialog != null && !myDialog.isShowing()) {
            myDialog.show();
        }
        String Uiid_id = UUID.randomUUID().toString();
        String StrMemberId = UtilitySharedPreferences.getPrefs(mContext, "MemberId");
        final String get_plan_details_info = ROOT_URL + "get_user_subscription_details.php?" + Uiid_id + "&user_id=" + StrMemberId;
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

                            if (mStackCount == 0) {
                                tv_title_plan.setText("NO ACTIVE PLAN");
                                tv_valid_till.setVisibility(View.GONE);

                            } else {
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
                                            if (i == 0 && !CommonMethods.isDateExpired(SubcribedTill_DMY)) {
                                                tv_title_plan.setText("Current Plan : " + plan_name);
                                                tv_valid_till.setVisibility(View.VISIBLE);
                                                tv_valid_till.setText("Valid Till \n" + mSubscribed_till);
                                                fetchCallData(StrPlanId);

                                            } else {
                                                tv_title_plan.setText("NO ACTIVE PLAN");
                                                tv_valid_till.setVisibility(View.GONE);
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                }

                            }

                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.hide();
                            }
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.hide();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.hide();
                        }
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
        String URL_GetCallList = ROOT_URL + "fetchPerformanceDataForPaidUser.php?_" + Uiid_id + "&plan_id=" + PlanId;


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

                                    if (status) {
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
                                    } else {

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

}