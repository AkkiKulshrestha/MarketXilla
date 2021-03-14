package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.adaptor.SubscriptionPlanAdapter;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;
import in.techxilla.www.marketxilla.utils.CirclePagerIndicatorDecoration;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class PackageFragment extends Fragment {
    private View rootView;
    private Context mContext;
    private RecyclerView recyclerSmartPlan;
    private SubscriptionPlanAdapter smartPlanAdapter;
    private ArrayList<SubscritPlanModel> smartPlanModelArrayList = new ArrayList<>();
    private ProgressDialog myDialog;
    private AdView mAdView;


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
        myDialog = new ProgressDialog(mContext);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        MobileAds.initialize(mContext);
        mAdView = rootView.findViewById(R.id.adView);
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        mAdView.loadAd(adRequestBuilder.build());

        recyclerSmartPlan = (RecyclerView) rootView.findViewById(R.id.recyclerSmartPlan);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        final SnapHelper snapHelper = new PagerSnapHelper();
        recyclerSmartPlan.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerSmartPlan);
        recyclerSmartPlan.setHasFixedSize(true);
        final int color = ContextCompat.getColor(mContext, R.color.blue_light);
        recyclerSmartPlan.addItemDecoration(new CirclePagerIndicatorDecoration(color, color));

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchPlans();
    }

    private void fetchPlans() {
        final String Uiid_id = UUID.randomUUID().toString();
        final String URL_GetSubscriptions = ROOT_URL + "get_subscriptions.php?" + Uiid_id;
        ;
        if (smartPlanModelArrayList.size() > 0) {
            smartPlanModelArrayList = new ArrayList<>();
        }
        if (myDialog != null && !myDialog.isShowing()) {
            myDialog.show();
        }
        try {
            Log.d("URL", URL_GetSubscriptions);
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetSubscriptions,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                Log.d("Response", response);
                                try {
                                    final JSONObject obj = new JSONObject(response);
                                    final JSONArray m_jArry = obj.getJSONArray("planList");
                                    String isTrailApplicable = UtilitySharedPreferences.getPrefs(mContext,"isTrailApplicable");
                                    int PackagesSize = m_jArry.length();
                                    if(isTrailApplicable!=null && isTrailApplicable.equalsIgnoreCase("false")){
                                        PackagesSize = PackagesSize - 1 ;
                                    }

                                    for (int i = 0; i < PackagesSize; i++) {
                                        final JSONObject jo_inside = m_jArry.getJSONObject(i);
                                        final String id = jo_inside.getString("id");
                                        final String plan_name = jo_inside.getString("plan_name");
                                        final String plan_type = jo_inside.getString("plan_type");
                                        final String plan_description = jo_inside.getString("plan_description");
                                        final String stock_future = jo_inside.getString("stock_future");
                                        final String stock_option = jo_inside.getString("stock_option");
                                        final String index_future = jo_inside.getString("index_future");
                                        final String index_option = jo_inside.getString("index_option");
                                        final String commodities = jo_inside.getString("commodities");
                                        final String telegram_messages = jo_inside.getString("telegram_messages");
                                        final String plan_amount1_month = jo_inside.getString("plan_amount1_month");
                                        final String plan_amount2_month = jo_inside.getString("plan_amount2_month");
                                        final String plan_amount3_month = jo_inside.getString("plan_amount3_month");
                                        final String tenure = jo_inside.getString("tenure");

                                        final SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
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
                                        subscritPlanModel.setTenure(tenure);
                                        subscritPlanModel.setAmount1Month((plan_amount1_month));
                                        subscritPlanModel.setAmount2Months((plan_amount2_month));
                                        subscritPlanModel.setAmount3Months((plan_amount3_month));
                                        smartPlanModelArrayList.add(subscritPlanModel);
                                        smartPlanAdapter = new SubscriptionPlanAdapter(smartPlanModelArrayList, mContext);
                                        recyclerSmartPlan.setAdapter(smartPlanAdapter);
                                    }
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.hide();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.hide();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.hide();
                        }
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
