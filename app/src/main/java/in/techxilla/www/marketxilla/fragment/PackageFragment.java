package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Month;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.adaptor.SubscriptionPlanAdapter;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;
import in.techxilla.www.marketxilla.utils.CirclePagerIndicatorDecoration;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.ScreenSlidePageFragment;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class PackageFragment extends Fragment {

    View rootView;
    Context mContext;
    RecyclerView recyclerSmartPlan;
    RecyclerView.LayoutManager layoutManager;
    SubscriptionPlanAdapter smartPlanAdapter;
    ViewGroup viewGroup;
    private  ArrayList<SubscritPlanModel> smartPlanModelArrayList = new ArrayList<>();
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int[] layouts;
    private static final int NUM_PAGES = 6;
    String mDuration;
    ArrayList<String> planList = new ArrayList<String>();
    ArrayList<Double> planAmountList = new ArrayList<Double>();

    String StrUpiAccountId,StrUPI_MerchantName;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_packages, null);

        initUI();
        return rootView;


    }

    private void initUI() {

        mContext = getContext();

        //Set up Adapter
        recyclerSmartPlan = (RecyclerView) rootView.findViewById(R.id.recyclerSmartPlan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerSmartPlan.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerSmartPlan);
        recyclerSmartPlan.setHasFixedSize(true);
        final int color = ContextCompat.getColor(mContext, R.color.blue_light);
        recyclerSmartPlan.addItemDecoration(new CirclePagerIndicatorDecoration(color, color));

        fetchPlans();
        getBankDetail();

    }


    private void fetchPlans() {

        String Uiid_id = UUID.randomUUID().toString();
        String URL_GetSubscriptions = ROOT_URL + "get_subscriptions.php?"+ Uiid_id;;

        if(smartPlanModelArrayList.size() > 0){
            smartPlanModelArrayList = new ArrayList<>();
        }


        try {
            Log.d("URL", URL_GetSubscriptions);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetSubscriptions,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray m_jArry = obj.getJSONArray("planList");


                                    for (int i = 0; i < m_jArry.length(); i++) {

                                        JSONObject jo_inside = m_jArry.getJSONObject(i);

                                        //Silver
                                        String id = jo_inside.getString("id");
                                        String plan_name = jo_inside.getString("plan_name");
                                        String plan_type = jo_inside.getString("plan_type");
                                        String plan_description = jo_inside.getString("plan_description");
                                        String stock_future = jo_inside.getString("stock_future");
                                        String stock_option = jo_inside.getString("stock_option");
                                        String index_future = jo_inside.getString("index_future");
                                        String index_option = jo_inside.getString("index_option");
                                        String commodities = jo_inside.getString("commodities");
                                        String telegram_messages = jo_inside.getString("telegram_messages");
                                        String plan_amount1_month = jo_inside.getString("plan_amount1_month");
                                        String plan_amount2_month = jo_inside.getString("plan_amount2_month");
                                        String plan_amount3_month = jo_inside.getString("plan_amount3_month");


                                        SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
                                        subscritPlanModel.setsPlanName(plan_name);
                                        subscritPlanModel.setsPlan(plan_type);
                                        subscritPlanModel.setsDetails(plan_description);
                                        subscritPlanModel.setsStock_Future(Boolean.parseBoolean(stock_future));
                                        subscritPlanModel.setsStock_Options(Boolean.parseBoolean(stock_option));
                                        subscritPlanModel.setsIndex_Future(Boolean.parseBoolean(index_future));
                                        subscritPlanModel.setsIndex_Options(Boolean.parseBoolean(index_option));
                                        subscritPlanModel.setsCommodity(Boolean.parseBoolean(commodities));
                                        subscritPlanModel.setsTelegram_Updates(Boolean.parseBoolean(telegram_messages));
                                        subscritPlanModel.setId(id);
                                        subscritPlanModel.setAmount1Month((plan_amount1_month));
                                        subscritPlanModel.setAmount2Months((plan_amount2_month));
                                        subscritPlanModel.setAmount3Months((plan_amount3_month));

                                        smartPlanModelArrayList.add(subscritPlanModel);

                                        smartPlanAdapter = new SubscriptionPlanAdapter(smartPlanModelArrayList,mContext);
                                        recyclerSmartPlan.setAdapter(smartPlanAdapter);

                                    }

                                } catch (JSONException e) {
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

    private void getBankDetail() {
        String Uiid_id = UUID.randomUUID().toString();
        final String get_bank_details_info = ROOT_URL + "get_bank_details.php?_" + Uiid_id;
        Log.d("URL --->", get_bank_details_info);
        try {
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, get_bank_details_info, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Response", "" + response);

                            JSONObject Jobj = new JSONObject(response);

                            boolean status = Jobj.getBoolean("status");

                            if (status) {

                                String data = Jobj.getString("data");
                                JSONObject jobject = new JSONObject(data);

                                String Id = jobject.getString("id");
                                String beneficiary_name = jobject.getString("beneficiary_name");
                                String bank_name = jobject.getString("bank_name");
                                String account_no = jobject.getString("account_no");
                                String ifsc_code = jobject.getString("ifsc_code");
                                StrUpiAccountId = jobject.getString("upi_id");
                                StrUPI_MerchantName = jobject.getString("upi_merchant_name");
                                String branch = jobject.getString("branch");

                                UtilitySharedPreferences.setPrefs(mContext,"UpiAccountId",StrUpiAccountId);
                                UtilitySharedPreferences.setPrefs(mContext,"UpiMerchantName",StrUPI_MerchantName);

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
                        DisplaySnackBar(viewGroup, "Something goes wrong. Please try again", "WARNING");

                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(20), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                requestQueue.add(stringRequest);
            } else {
                DisplaySnackBar(viewGroup, "No Internet Connection", "WARNING");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }



}
