package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import in.techxilla.www.marketxilla.CalculatorActivity;
import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.adaptor.CallsAdapter;
import in.techxilla.www.marketxilla.adaptor.ImageSliderAdapter;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.model.SliderItem;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class HomeFragment extends Fragment {
    private static final Integer[] IMAGES = {R.drawable.slider1, R.drawable.slider2, R.drawable.slider3, R.drawable.slider4, R.drawable.slider5, R.drawable.slider6};
    public Toolbar toolbar;
    TextView tv_AvailTrail, no_call_txt;
    private ProgressDialog myDialog;
    private SliderView sliderView;
    private ImageSliderAdapter adapter;
    private RecyclerView recycler_list;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<CallModel> callModel_list = new ArrayList<>();
    private CallModel callListModel;
    private CallsAdapter callsAdapter;
    private Spinner SpnCallTenure, SpnMonthwisePerformance, SpnSegment;
    private View rootView;
    private Context mContext;
    private List<String> MonthsList = new ArrayList<>();
    private String SelectedMonth, SelectedSegment;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, null);
        initUI();
        return rootView;
    }

    private void initUI() {
        mContext = getContext();
        NewDashboard activity = (NewDashboard) getActivity();
        toolbar = Objects.requireNonNull(activity).findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        myDialog = new ProgressDialog(mContext);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        final ImageView iv_refresh = toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(v -> {
            fetchCallData();
            SpnCallTenure.setSelection(0);
            SpnMonthwisePerformance.setVisibility(View.GONE);
            SpnMonthwisePerformance.setSelection(0);
            SpnSegment.setSelection(0);
        });

        final ViewPager mPager = rootView.findViewById(R.id.image_pager);
        sliderView = rootView.findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.colorPrimary));
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        sliderView.setOnIndicatorClickListener(position -> sliderView.setCurrentPagePosition(position));

        adapter = new ImageSliderAdapter(mContext);
        final List<SliderItem> sliderItemList = new ArrayList<>();
        for (final Integer image : IMAGES) {
            final SliderItem sliderItem = new SliderItem();
            sliderItem.setDrawableImage(image);
            sliderItemList.add(sliderItem);
            adapter.addItem(sliderItem);
        }
        sliderView.setSliderAdapter(adapter);

        SocialNetworkingLinks();

        SpnCallTenure = rootView.findViewById(R.id.SpnCallTenure);
        SpnCallTenure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String SelectedTenure = parent.getItemAtPosition(position).toString();
                if (SelectedTenure.equalsIgnoreCase("Today Calls")) {
                    fetchCallData();
                    SpnMonthwisePerformance.setVisibility(View.GONE);
                } else if (SelectedTenure.equalsIgnoreCase("Past Performance")) {
                    SpnMonthwisePerformance.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpnMonthwisePerformance = rootView.findViewById(R.id.SpnMonthwisePerformance);
        SpnMonthwisePerformance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (position > 0) {
                    SelectedMonth = parent.getItemAtPosition(position).toString();
                    fetchPastPerformance();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SpnSegment = rootView.findViewById(R.id.SpnSegment);
        SpnSegment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (position > -1) {
                    SelectedSegment = parent.getItemAtPosition(position).toString();
                    filterSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        recycler_list = rootView.findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setNestedScrollingEnabled(false);

        final FragmentActivity c = getActivity();
        layoutManager = new LinearLayoutManager(c);
        recycler_list.setLayoutManager(layoutManager);

        SpnMonthwisePerformance.setVisibility(View.GONE);
        tv_AvailTrail = rootView.findViewById(R.id.tv_AvailTrail);
        no_call_txt = rootView.findViewById(R.id.no_call_txt);
        String isTrailApplicable = UtilitySharedPreferences.getPrefs(mContext, "isTrailApplicable");
        if (isTrailApplicable != null && isTrailApplicable.equalsIgnoreCase("false")) {
            tv_AvailTrail.setVisibility(View.GONE);
        } else {
            tv_AvailTrail.setVisibility(View.VISIBLE);
        }

        tv_AvailTrail.setOnClickListener(v -> AddUserTRAILDetailApi());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        ((NewDashboard) context).validateUser();
        super.onAttach(context);
    }

    private void AddUserTRAILDetailApi() {
        myDialog.show();
        final String StrSubscriptionAmount = "0.0";
        final String transactionId = String.valueOf(System.currentTimeMillis());
        String mSubscripbed_on = CommonMethods.DisplayCurrentDate();
        Calendar calendar = CommonMethods.addWorkingDays(5);
        SimpleDateFormat formDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String msubscribed_till = formDate.format(new Date(calendar.getTimeInMillis()));

        JSONObject transactionObj = new JSONObject();
        try {
            transactionObj.put("transaction_id", transactionId);
            transactionObj.put("transaction_amount", StrSubscriptionAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String API_AddUserSubscriptionDetail = ROOT_URL + "add_user_trail_detail.php";
        try {
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                Log.d("URL", API_AddUserSubscriptionDetail);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, API_AddUserSubscriptionDetail,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                                try {
                                    final JSONObject jsonObject = new JSONObject(response);
                                    final boolean status = jsonObject.getBoolean("status");
                                    if (status) {
                                        CommonMethods.DisplayToastSuccess(mContext, "YOUR TRAIL IS ACTIVATED");
                                        final Intent i = new Intent(mContext, NewDashboard.class);
                                        mContext.startActivity(i);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/x-www-form-urlencoded; charset=UTF-8";
                    }

                    @Override
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("user_id", UtilitySharedPreferences.getPrefs(mContext, "MemberId"));
                        params.put("plan_id", "7");
                        params.put("subscribed_on", mSubscripbed_on);
                        params.put("payment_details", transactionObj.toString());
                        params.put("subscribed_till", msubscribed_till);
                        Log.d("ParrasRegister", params.toString());
                        return params;
                    }
                };
                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                requestQueue.add(stringRequest);
            } else {
                CommonMethods.DisplayToastInfo(mContext, "Please check your internet connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        fetchCallData();
        fetchMonthList();
    }

    private void SocialNetworkingLinks() {
        final ImageView iv_gmail = rootView.findViewById(R.id.iv_gmail);
        final ImageView iv_sms = rootView.findViewById(R.id.iv_sms);
        final ImageView iv_whatsapp = rootView.findViewById(R.id.iv_whatsapp);
        final ImageView iv_calc = rootView.findViewById(R.id.iv_calc);

        iv_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final String appPackageName = mContext.getPackageName();
                    final Intent intent = new Intent(Intent.ACTION_SEND);
                    final String[] recipients = {"marketxilla@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Welcome To MarketXilla");
                    intent.putExtra(Intent.EXTRA_TEXT, "Greetings, \n\n" +
                            "Welcome To MarketXilla, \n" +
                            "\"An Intra Day Research Service Providing App For NSE Future And Options, and Commodity\" \n\n\n\n" +
                            "MarketXilla App is Specially Designed For Intra Day Traders. Our Research is Based On Dynamic Data And Technical Analysis. For Consistent Profit and Hassle-Free Trading, You Can Study Our Live Research Recommendations.\n" +
                            "MarketXilla App Is Now Available for Android On Google Play Store. Download MarketXilla -  https://play.google.com/store/apps/details?id=" + appPackageName);
                    intent.putExtra(Intent.EXTRA_CC, "info@marketxilla.com");
                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    mContext.startActivity(Intent.createChooser(intent, "Send mail"));
                } catch (ActivityNotFoundException e) {
                    //TODO smth
                }
            }
        });

        iv_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = "+919022409928";
                final String message = "ENQUIRY. \nHello MarketXilla,\nWant To Know More About Your Services, Kindly Revert Back.";
                final Uri sms_uri = Uri.parse("smsto:" + phone);
                final Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                sms_intent.putExtra("sms_body", message);
                mContext.startActivity(sms_intent);
            }
        });

        iv_whatsapp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View view) {
                final String phone = "+919022409928";
                final String message = "ENQUIRY. \nHello MarketXilla,\nWant To Know More About Your Services, Kindly Revert Back.";
                final PackageManager packageManager = mContext.getPackageManager();
                final Intent i = new Intent(Intent.ACTION_VIEW);
                try {
                    String url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        mContext.startActivity(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent_calc = new Intent(getContext(), CalculatorActivity.class);
                startActivity(intent_calc);
            }
        });


    }

    private void fetchMonthList() {
        final String Uiid_id = UUID.randomUUID().toString();
        final String URL_GetCallList = ROOT_URL + "getMonthList.php?_" + Uiid_id;
        try {
            Log.d("URL", URL_GetCallList);
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetCallList,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(final String response) {
                                Log.d("Response", response);
                                try {
                                    final JSONObject jobj = new JSONObject(response);
                                    final JSONArray data = jobj.getJSONArray("data");
                                    MonthsList = new ArrayList<>();
                                    MonthsList.add("Select Month");
                                    for (int k = 0; k < data.length(); k++) {
                                        final JSONObject jsonObject = data.getJSONObject(k);
                                        final String month = jsonObject.getString("month");
                                        MonthsList.add(month);
                                    }
                                    final ArrayAdapter<String> adp = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, MonthsList);
                                    SpnMonthwisePerformance.setAdapter(adp);
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "DefaultLocale"})
    private void fetchCallData() {
        if (callModel_list != null && callModel_list.size() > 0) {
            callModel_list = new ArrayList<>();
            callsAdapter = new CallsAdapter(mContext, callModel_list);
            recycler_list.setAdapter(callsAdapter);
            callsAdapter.notifyDataSetChanged();
        }
        if (myDialog != null && !myDialog.isShowing()) {
            myDialog.show();
        }
        final String Uiid_id = UUID.randomUUID().toString();
        final String URL_GetCallList = ROOT_URL + "fetchPerformanceData.php?_" + Uiid_id + "&per_for=all";
        try {
            Log.d("URL", URL_GetCallList);
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetCallList,
                        response -> {
                            Log.d("Response", response);
                            try {
                                final JSONObject jobj = new JSONObject(response);
                                final JSONArray data = jobj.getJSONArray("data");
                                if (data.length() > 0) {
                                    for (int k = 0; k < data.length(); k++) {
                                        final JSONObject jsonObject = data.getJSONObject(k);
                                        final String id = jsonObject.getString("id");
                                        final String stock_name = jsonObject.getString("stock_name");
                                        final String date = jsonObject.getString("date");
                                        final String performance_for = jsonObject.getString("performance_for");
                                        final String performance_for_id = jsonObject.getString("performance_for_id");
                                        final String is_buy_sell = jsonObject.getString("is_buy_sell");
                                        final String buy_sell_above_below = jsonObject.getString("buy_sell_above_below");
                                        final String stop_loss = jsonObject.getString("stop_loss");
                                        final String target1 = jsonObject.getString("target1");
                                        final String target2 = jsonObject.getString("target2");
                                        final String target3 = jsonObject.getString("target3");
                                        final String ce_pe = jsonObject.getString("ce_pe");
                                        final String strike = jsonObject.getString("strike");
                                        final String buy_sell_closing_price = jsonObject.getString("buy_sell_closing_price");
                                        final String profit_loss = jsonObject.getString("profit_loss");
                                        final String is_active_performance = jsonObject.getString("is_active_performance");
                                        final String is_call_for_paid_customer = jsonObject.getString("is_call_for_paid_customer");

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
                                    no_call_txt.setVisibility(View.GONE);
                                    callsAdapter = new CallsAdapter(mContext, callModel_list);
                                    recycler_list.setAdapter(callsAdapter);
                                    recycler_list.setVisibility(View.VISIBLE);
                                    callsAdapter.notifyDataSetChanged();
                                    filterSearch();
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                } else {
                                    no_call_txt.setVisibility(View.VISIBLE);
                                    recycler_list.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                            }
                        }, error -> {
                            error.printStackTrace();
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "DefaultLocale"})
    private void fetchPastPerformance() {
        if (callModel_list != null && callModel_list.size() > 0) {
            callModel_list = new ArrayList<>();
            callsAdapter = new CallsAdapter(mContext, callModel_list);
            recycler_list.setAdapter(callsAdapter);
            callsAdapter.notifyDataSetChanged();
        }
        if (myDialog != null && !myDialog.isShowing()) {
            myDialog.show();
        }
        final String Uiid_id = UUID.randomUUID().toString();
        final String URL_GetCallList = ROOT_URL + "past_performance_data_monthwise.php?_" + Uiid_id + "&month=" + URLEncoder.encode(SelectedMonth);
        try {
            Log.d("URL", URL_GetCallList);
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetCallList,
                        response -> {
                            Log.d("Response", response);
                            try {
                                final JSONObject jobj = new JSONObject(response);
                                final JSONArray data = jobj.getJSONArray("data");
                                if (data.length() > 0) {
                                    for (int k = 0; k < data.length(); k++) {
                                        final JSONObject jsonObject = data.getJSONObject(k);
                                        final String id = jsonObject.getString("id");
                                        final String stock_name = jsonObject.getString("stock_name");
                                        final String date = jsonObject.getString("date");
                                        final String performance_for = jsonObject.getString("performance_for");
                                        final String performance_for_id = jsonObject.getString("performance_for_id");
                                        final String is_buy_sell = jsonObject.getString("is_buy_sell");
                                        final String buy_sell_above_below = jsonObject.getString("buy_sell_above_below");
                                        final String stop_loss = jsonObject.getString("stop_loss");
                                        final String target1 = jsonObject.getString("target1");
                                        final String target2 = jsonObject.getString("target2");
                                        final String target3 = jsonObject.getString("target3");
                                        final String ce_pe = jsonObject.getString("ce_pe");
                                        final String strike = jsonObject.getString("strike");
                                        final String buy_sell_closing_price = jsonObject.getString("buy_sell_closing_price");
                                        final String profit_loss = jsonObject.getString("profit_loss");
                                        final String is_active_performance = jsonObject.getString("is_active_performance");
                                        final String is_call_for_paid_customer = jsonObject.getString("is_call_for_paid_customer");

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
                                    no_call_txt.setVisibility(View.GONE);
                                    callsAdapter = new CallsAdapter(mContext, callModel_list);
                                    recycler_list.setAdapter(callsAdapter);
                                    recycler_list.setVisibility(View.VISIBLE);
                                    callsAdapter.notifyDataSetChanged();
                                    filterSearch();
                                    if (myDialog != null && myDialog.isShowing()) {
                                        myDialog.dismiss();
                                    }
                                } else {
                                    no_call_txt.setVisibility(View.VISIBLE);
                                    recycler_list.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (myDialog != null && myDialog.isShowing()) {
                                    myDialog.dismiss();
                                }
                            }
                        }, error -> {
                            VolleyLog.d("volley", "Error: " + error.getMessage());
                            error.printStackTrace();
                            if (myDialog != null && myDialog.isShowing()) {
                                myDialog.dismiss();
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

    private void filterSearch() {
        if (callsAdapter != null && SelectedSegment != null && !SelectedSegment.isEmpty()) {
            callsAdapter.getFilter().filter(SelectedSegment);
        }
    }
}