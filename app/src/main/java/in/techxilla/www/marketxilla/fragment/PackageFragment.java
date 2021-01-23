package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.adaptor.SubscriptionPlanAdapter;
import in.techxilla.www.marketxilla.model.SubscritPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.ScreenSlidePageFragment;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class PackageFragment extends Fragment {

    View rootView;
    Context mContext;
    RecyclerView recyclerSmartPlan;
    RecyclerView.LayoutManager layoutManager;
    SubscriptionPlanAdapter smartPlanAdapter;
    ViewGroup viewGroup;
    private static ArrayList<SubscritPlanModel> smartPlanModelArrayList;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private int[] layouts;
    private static final int NUM_PAGES = 6;
    String mDuration;
    ArrayList<String> planList = new ArrayList<String>();
    ArrayList<Double> planAmountList = new ArrayList<Double>();
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

        smartPlanModelArrayList = new ArrayList<SubscritPlanModel>();

        //Set up Adapter
        recyclerSmartPlan = (RecyclerView) rootView.findViewById(R.id.recyclerSmartPlan);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new PagerSnapHelper();
        recyclerSmartPlan.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(recyclerSmartPlan);
        smartPlanAdapter = new SubscriptionPlanAdapter(smartPlanModelArrayList,mContext);
         recyclerSmartPlan.setAdapter(smartPlanAdapter);
         displaySmartPlans();
        fetchPlans(1);

    }


    private void fetchPlans(int package_id) {
        planList = new ArrayList<>();
        planAmountList = new ArrayList<>();
       // planList.add("Select Plan");
        planAmountList.add(0.0);
        String Uiid_id = UUID.randomUUID().toString();
        String URL_GetSubscriptions = ROOT_URL + "get_subscriptions.php"; //+ Uiid_id;;


        try {
            Log.d("URL", URL_GetSubscriptions);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetSubscriptions,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);

                                try {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray m_jArry = obj.getJSONArray("planList");


                                    for (int i = 0; i < m_jArry.length(); i++) {

                                        JSONObject jo_inside = m_jArry.getJSONObject(i);

                                        //Silver
                                        if (jo_inside.getInt("PackageId") == 1 && jo_inside.getString("PlanName").equalsIgnoreCase("SILVER")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 2 && jo_inside.getString("PlanName").equalsIgnoreCase("SILVER")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 3 && jo_inside.getString("PlanName").equalsIgnoreCase("SILVER")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        //GOLD
                                        if (jo_inside.getInt("PackageId") == 1 && jo_inside.getString("PlanName").equalsIgnoreCase("GOLD")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 2 && jo_inside.getString("PlanName").equalsIgnoreCase("GOLD")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 3 && jo_inside.getString("PlanName").equalsIgnoreCase("GOLD")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        //Platinum
                                        if (jo_inside.getInt("PackageId") == 1 && jo_inside.getString("PlanName").equalsIgnoreCase("PLATINUM")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 2 && jo_inside.getString("PlanName").equalsIgnoreCase("PLATINUM")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 3 && jo_inside.getString("PlanName").equalsIgnoreCase("PLATINUM")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        //Future Special
                                        if (jo_inside.getInt("PackageId") == 1 && jo_inside.getString("PlanName").equalsIgnoreCase("FUTURE SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 2 && jo_inside.getString("PlanName").equalsIgnoreCase("FUTURE SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 3 && jo_inside.getString("PlanName").equalsIgnoreCase("FUTURE SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        //Option Special
                                        if (jo_inside.getInt("PackageId") == 1 && jo_inside.getString("PlanName").equalsIgnoreCase("OPTION SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 2 && jo_inside.getString("PlanName").equalsIgnoreCase("OPTION SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 3 && jo_inside.getString("PlanName").equalsIgnoreCase("OPTION SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        //Comodity Special
                                        if (jo_inside.getInt("PackageId") == 1 && jo_inside.getString("PlanName").equalsIgnoreCase("COMMODITY SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 2 && jo_inside.getString("PlanName").equalsIgnoreCase("COMMODITY SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == 3 && jo_inside.getString("PlanName").equalsIgnoreCase("COMMODITY SPECIAL")) {
                                            mDuration=("\u20B9 " + CommonMethods.NumberDisplayFormattingWithComma(jo_inside.getString("PlanAmount")));
                                        }

                                        if (jo_inside.getInt("PackageId") == package_id) {
                                            String PlanName = jo_inside.getString("PlanName");
                                            double PlanAmount = jo_inside.getDouble("PlanAmount");
                                            planList.add(PlanName);
                                            planAmountList.add(PlanAmount);

                                        }
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
    private void displaySmartPlans() {

       // smartPlanModelArrayList.add(new SmartPlanModel("1 Month", getString(R.string.pcakage1_desc), R.drawable.ic_info, R.color.colorPrimary));
        //smartPlanModelArrayList.add(new SmartPlanModel("2 Month", getString(R.string.package2_desc), R.drawable.ic_info, R.color.colorPrimary));
        //smartPlanModelArrayList.add(new SmartPlanModel("3 Month", getString(R.string.package3_desc), R.drawable.ic_info, R.color.colorPrimary));

        smartPlanModelArrayList.add(new SubscritPlanModel(getString(R.string.basic),getString(R.string.sliver),getString(R.string.plan_msg1),getString(R.string.stock_future),R.drawable.ic_info
                ,getString(R.string.stock_options),getString(R.string.index_future),getString(R.string.index_options),getString(R.string.commodity),getString(R.string.telegram_updates),mDuration,R.drawable.ic_info));


        smartPlanModelArrayList.add(new SubscritPlanModel(getString(R.string.standered),getString(R.string.gold),getString(R.string.plan_msg2),getString(R.string.stock_future),R.drawable.ic_info
                ,getString(R.string.stock_options),getString(R.string.index_future),getString(R.string.index_options),getString(R.string.commodity),getString(R.string.telegram_updates),mDuration,R.drawable.ic_info));

        smartPlanModelArrayList.add(new SubscritPlanModel(getString(R.string.premium),getString(R.string.platinum),getString(R.string.plan_msg3),getString(R.string.stock_future),R.drawable.ic_info
                ,getString(R.string.stock_options),getString(R.string.index_future),getString(R.string.index_options),getString(R.string.commodity),getString(R.string.telegram_updates),mDuration,R.drawable.ic_info));

        smartPlanModelArrayList.add(new SubscritPlanModel(getString(R.string.special),getString(R.string.future_special),getString(R.string.plan_msg4),getString(R.string.stock_future),R.drawable.ic_info
                ,getString(R.string.stock_options),getString(R.string.index_future),getString(R.string.index_options),getString(R.string.commodity),getString(R.string.telegram_updates),mDuration,R.drawable.ic_info));

        smartPlanModelArrayList.add(new SubscritPlanModel(getString(R.string.special),getString(R.string.optionsspecial),getString(R.string.plan_msg5),getString(R.string.stock_future),R.drawable.ic_info
                ,getString(R.string.stock_options),getString(R.string.index_future),getString(R.string.index_options),getString(R.string.commodity),getString(R.string.telegram_updates),mDuration,R.drawable.ic_info));

        smartPlanModelArrayList.add(new SubscritPlanModel(getString(R.string.special),getString(R.string.commodityspecial),getString(R.string.plan_msg6),getString(R.string.stock_future),R.drawable.ic_info
                ,getString(R.string.stock_options),getString(R.string.index_future),getString(R.string.index_options),getString(R.string.commodity),getString(R.string.telegram_updates),mDuration,R.drawable.ic_info));



    }


}
