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

import androidx.annotation.Nullable;
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
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
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

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class HomeFragment extends Fragment {
    private static final Integer[] IMAGES = {R.drawable.slider1, R.drawable.slider2, R.drawable.slider3, R.drawable.slider4, R.drawable.slider5, R.drawable.slider6};
    public Toolbar toolbar;
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
        toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        myDialog = new ProgressDialog(mContext);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        final ImageView iv_refresh = (ImageView) toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.show();
                fetchCallData();
                SpnCallTenure.setSelection(0);
                SpnMonthwisePerformance.setVisibility(View.GONE);
                SpnMonthwisePerformance.setSelection(0);
                SpnSegment.setSelection(0);
            }
        });

        final ViewPager mPager = (ViewPager) rootView.findViewById(R.id.image_pager);
        sliderView = (SliderView) rootView.findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.colorPrimary));
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
            }
        });

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

        SpnCallTenure = (Spinner) rootView.findViewById(R.id.SpnCallTenure);
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

        SpnMonthwisePerformance = (Spinner) rootView.findViewById(R.id.SpnMonthwisePerformance);
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

        SpnSegment = (Spinner) rootView.findViewById(R.id.SpnSegment);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCallData();
        fetchMonthList();
    }

    private void SocialNetworkingLinks() {
        final ImageView iv_gmail = (ImageView) rootView.findViewById(R.id.iv_gmail);
        final ImageView iv_sms = (ImageView) rootView.findViewById(R.id.iv_sms);
        final ImageView iv_whatsapp = (ImageView) rootView.findViewById(R.id.iv_whatsapp);
        final ImageView iv_calc = (ImageView) rootView.findViewById(R.id.iv_calc);

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
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                Log.d("Response", response);
                                try {
                                    final JSONObject jobj = new JSONObject(response);
                                    final JSONArray data = jobj.getJSONArray("data");
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

                                    callsAdapter = new CallsAdapter(mContext, callModel_list);
                                    recycler_list.setAdapter(callsAdapter);
                                    callsAdapter.notifyDataSetChanged();
                                    filterSearch();
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
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response", response);
                                try {
                                    final JSONObject jobj = new JSONObject(response);
                                    final JSONArray data = jobj.getJSONArray("data");
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

                                    callsAdapter = new CallsAdapter(mContext, callModel_list);
                                    recycler_list.setAdapter(callsAdapter);
                                    callsAdapter.notifyDataSetChanged();
                                    filterSearch();
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
                        VolleyLog.d("volley", "Error: " + error.getMessage());
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

    private void filterSearch() {
        if (callsAdapter != null && !SelectedSegment.isEmpty()) {
            callsAdapter.getFilter().filter(SelectedSegment);
        }
    }
}