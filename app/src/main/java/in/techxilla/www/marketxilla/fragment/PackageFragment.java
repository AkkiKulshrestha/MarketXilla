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

        fetchPlans();

    }


    private void fetchPlans() {
        planList = new ArrayList<>();
        planAmountList = new ArrayList<>();
       // planList.add("Select Plan");
        planAmountList.add(0.0);
        String Uiid_id = UUID.randomUUID().toString();
        String URL_GetSubscriptions = ROOT_URL + "get_subscriptions.php";//+ Uiid_id;;


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
                                        String PlanName = jo_inside.getString("PlanName");
                                        String PackageId = jo_inside.getString("PackageId");
                                        String PlanDescription = jo_inside.getString("PlanDescription");
                                        String PlanAmount = jo_inside.getString("PlanAmount");
                                        String SNo = jo_inside.getString("SNo");

                                        if(PlanName!=null && PlanName.equalsIgnoreCase("SILVER")){
                                            SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
                                            subscritPlanModel.setsPlanName(PlanName);
                                            subscritPlanModel.setsPlan(getContext().getResources().getString(R.string.basic));
                                            subscritPlanModel.setsDetails(PlanDescription);
                                            subscritPlanModel.setsStock_Future(true);
                                            subscritPlanModel.setsStock_Options(false);
                                            subscritPlanModel.setsIndex_Future(false);
                                            subscritPlanModel.setsIndex_Options(false);
                                            subscritPlanModel.setsCommodity(false);
                                            subscritPlanModel.setsTelegram_Updates(true);
                                            subscritPlanModel.setPackage_id(PackageId);

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("1")) {
                                                subscritPlanModel.setAmount1Month("1 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("2")) {
                                                subscritPlanModel.setAmount2Months("2 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("3")) {
                                                subscritPlanModel.setAmount3Months("3 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            smartPlanModelArrayList.add(subscritPlanModel);

                                        }

                                        if(PlanName!=null && PlanName.equalsIgnoreCase("GOLD")){
                                            SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
                                            subscritPlanModel.setsPlanName(PlanName);
                                            subscritPlanModel.setsPlan(getContext().getResources().getString(R.string.standered));
                                            subscritPlanModel.setsDetails(PlanDescription);
                                            subscritPlanModel.setsStock_Future(true);
                                            subscritPlanModel.setsStock_Options(false);
                                            subscritPlanModel.setsIndex_Future(false);
                                            subscritPlanModel.setsIndex_Options(false);
                                            subscritPlanModel.setsCommodity(false);
                                            subscritPlanModel.setsTelegram_Updates(true);
                                            subscritPlanModel.setPackage_id(PackageId);

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("1")) {
                                                subscritPlanModel.setAmount1Month("1 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("2")) {
                                                subscritPlanModel.setAmount2Months("2 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("3")) {
                                                subscritPlanModel.setAmount3Months("3 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            smartPlanModelArrayList.add(subscritPlanModel);

                                        }

                                        if(PlanName!=null && PlanName.equalsIgnoreCase("PLATINUM")){
                                            SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
                                            subscritPlanModel.setsPlanName(PlanName);
                                            subscritPlanModel.setsPlan(getContext().getResources().getString(R.string.premium));
                                            subscritPlanModel.setsDetails(PlanDescription);
                                            subscritPlanModel.setsStock_Future(true);
                                            subscritPlanModel.setsStock_Options(true);
                                            subscritPlanModel.setsIndex_Future(true);
                                            subscritPlanModel.setsIndex_Options(true);
                                            subscritPlanModel.setsCommodity(true);
                                            subscritPlanModel.setsTelegram_Updates(true);
                                            subscritPlanModel.setPackage_id(PackageId);

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("1")) {
                                                subscritPlanModel.setAmount1Month("1 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("2")) {
                                                subscritPlanModel.setAmount2Months("2 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("3")) {
                                                subscritPlanModel.setAmount3Months("3 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            smartPlanModelArrayList.add(subscritPlanModel);

                                        }

                                        if(PlanName!=null && PlanName.equalsIgnoreCase("FUTURE SPECIAL")){
                                            SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
                                            subscritPlanModel.setsPlanName(PlanName);
                                            subscritPlanModel.setsPlan(getContext().getResources().getString(R.string.special));
                                            subscritPlanModel.setsDetails(PlanDescription);
                                            subscritPlanModel.setsStock_Future(true);
                                            subscritPlanModel.setsStock_Options(false);
                                            subscritPlanModel.setsIndex_Future(true);
                                            subscritPlanModel.setsIndex_Options(false);
                                            subscritPlanModel.setsCommodity(false);
                                            subscritPlanModel.setsTelegram_Updates(true);
                                            subscritPlanModel.setPackage_id(PackageId);
                                            if(PackageId!=null && PackageId.equalsIgnoreCase("1")) {
                                                subscritPlanModel.setAmount1Month("1 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("2")) {
                                                subscritPlanModel.setAmount2Months("2 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("3")) {
                                                subscritPlanModel.setAmount3Months("3 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            smartPlanModelArrayList.add(subscritPlanModel);

                                        }

                                        if(PlanName!=null && PlanName.equalsIgnoreCase("OPTION SPECIAL")){
                                            SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
                                            subscritPlanModel.setsPlanName(PlanName);
                                            subscritPlanModel.setsPlan(getContext().getResources().getString(R.string.special));
                                            subscritPlanModel.setsDetails(PlanDescription);
                                            subscritPlanModel.setsStock_Future(false);
                                            subscritPlanModel.setsStock_Options(true);
                                            subscritPlanModel.setsIndex_Future(false);
                                            subscritPlanModel.setsIndex_Options(true);
                                            subscritPlanModel.setsCommodity(false);
                                            subscritPlanModel.setsTelegram_Updates(true);
                                            subscritPlanModel.setPackage_id(PackageId);

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("1")) {
                                                subscritPlanModel.setAmount1Month("1 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("2")) {
                                                subscritPlanModel.setAmount2Months("2 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("3")) {
                                                subscritPlanModel.setAmount3Months("3 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            smartPlanModelArrayList.add(subscritPlanModel);

                                        }

                                        if(PlanName!=null && PlanName.equalsIgnoreCase("COMMODITY SPECIAL")){
                                            SubscritPlanModel subscritPlanModel = new SubscritPlanModel();
                                            subscritPlanModel.setsPlanName(PlanName);
                                            subscritPlanModel.setsPlan(getContext().getResources().getString(R.string.special));
                                            subscritPlanModel.setsDetails(PlanDescription);
                                            subscritPlanModel.setsStock_Future(false);
                                            subscritPlanModel.setsStock_Options(false);
                                            subscritPlanModel.setsIndex_Future(false);
                                            subscritPlanModel.setsIndex_Options(false);
                                            subscritPlanModel.setsCommodity(true);
                                            subscritPlanModel.setsTelegram_Updates(true);
                                            subscritPlanModel.setPackage_id(PackageId);

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("1")) {
                                                subscritPlanModel.setAmount1Month("1 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("2")) {
                                                subscritPlanModel.setAmount2Months("2 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            if(PackageId!=null && PackageId.equalsIgnoreCase("3")) {
                                                subscritPlanModel.setAmount3Months("3 Month \n \u20B9 "+CommonMethods.NumberDisplayFormattingWithComma(PlanAmount));
                                            }

                                            smartPlanModelArrayList.add(subscritPlanModel);

                                        }


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



}
