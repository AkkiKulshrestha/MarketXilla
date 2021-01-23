package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import androidx.viewpager.widget.ViewPager;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.youngtr.numberprogressbar.NumberProgressBar;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;

import in.techxilla.www.marketxilla.adaptor.MarketPagerAdapter;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.webservices.RestClient.NSE_ADVANCE_DECLINE_URL;
import static in.techxilla.www.marketxilla.webservices.RestClient.NSE_INDEX_WATCH_URL;
import static in.techxilla.www.marketxilla.webservices.RestClient.NSE_MARKET_STATUS_URL;
import static in.techxilla.www.marketxilla.webservices.RestClient.NSE_URL;


public class MarketFragment extends Fragment {

    View rootView;
    Context mContext;
    TabLayout tabLayout;
    TextView tv_market_open_close,tv_nifty_50_value,tv_nift_value1,tv_nifty_bank_value,tv_nift_bank_value1;
    ImageView iv_nifty_50,iv_nifty_bank;
    Toolbar toolbar;

    NumberProgressBar progress_bar_nifty_50,progress_bar_nifty_bank;
    TextView nifty50_adv,nifty50_dec,niftybank_adv,niftybank_dec;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_market, null);
        mContext = getContext();



        initUI();
        return rootView;


    }


    private void initUI() {

        NewDashboard activity = (NewDashboard) getActivity();
        toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        ImageView iv_refresh = (ImageView) toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetch_MarketData();
                fetch_MarketStatus();
                fetchAdvanceDecline();
            }
        });

        tabLayout = (TabLayout) rootView.findViewById(R.id.tab_layout);


        tabLayout.addTab(tabLayout.newTab().setText("Top \nGainers"));
        tabLayout.addTab(tabLayout.newTab().setText("Top \nLosers"));
        tabLayout.addTab(tabLayout.newTab().setText("Active Calls \nStock"));
        tabLayout.addTab(tabLayout.newTab().setText("Active Puts \nStock"));
        tabLayout.addTab(tabLayout.newTab().setText("Active Calls \nIndex"));
        tabLayout.addTab(tabLayout.newTab().setText("Active Puts \nIndex"));
        tabLayout.addTab(tabLayout.newTab().setText("Active by \nValue"));
        tabLayout.addTab(tabLayout.newTab().setText("Active by \nVolume"));
       // tabLayout.addTab(tabLayout.newTab().setText("Active \nFuture"));
        tabLayout.setTabTextColors(mContext.getResources().getColor(R.color.white),mContext.getResources().getColor(R.color.colorPrimary));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        final MarketPagerAdapter adapter = new MarketPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        tv_market_open_close = (TextView)rootView.findViewById(R.id.tv_market_open_close);
        tv_nifty_50_value = (TextView)rootView.findViewById(R.id.tv_nifty_50_value);
        tv_nift_value1 = (TextView)rootView.findViewById(R.id.tv_nift_value1);
        tv_nifty_bank_value = (TextView)rootView.findViewById(R.id.tv_nifty_bank_value);
        tv_nift_bank_value1 = (TextView)rootView.findViewById(R.id.tv_nift_bank_value1);

         iv_nifty_50 = (ImageView)rootView.findViewById(R.id.iv_nifty_50);
        iv_nifty_bank = (ImageView)rootView.findViewById(R.id.iv_nifty_bank);

        progress_bar_nifty_50 = (NumberProgressBar) rootView.findViewById(R.id.progress_bar_nifty_50);
        progress_bar_nifty_bank = (NumberProgressBar)rootView.findViewById(R.id.progress_bar_nifty_bank);
        nifty50_adv = (TextView)rootView.findViewById(R.id.nifty50_adv);
        nifty50_dec = (TextView)rootView.findViewById(R.id.nifty50_dec);
        niftybank_adv = (TextView)rootView.findViewById(R.id.niftybank_adv);
        niftybank_dec = (TextView)rootView.findViewById(R.id.niftybank_dec);



        fetch_MarketData();
        fetch_MarketStatus();
        fetchAdvanceDecline();

    }

    private void fetchAdvanceDecline() {


        try {
            Log.d("NSE_ADVANCE_DECLINE_URL",NSE_ADVANCE_DECLINE_URL);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, NSE_ADVANCE_DECLINE_URL,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);

                                try {

                                    JSONObject jobj = new JSONObject(response);
                                    JSONArray data = jobj.getJSONArray("data");
                                    for(int k = 0; k< data.length();k++) {
                                        JSONObject jsonObject = data.getJSONObject(k);
                                        Log.d("jobj",""+jsonObject);
                                        String indice = jsonObject.getString("indice");
                                        String advances = jsonObject.getString("advances");
                                        String declines = jsonObject.getString("declines");
                                        String unchanged = jsonObject.getString("unchanged");

                                        int adv = 0; int dec = 0; int unch = 0;
                                        if(advances!=null && !advances.equalsIgnoreCase("")){
                                            adv = Integer.parseInt(advances);
                                        }

                                        if(declines!=null && !declines.equalsIgnoreCase("")){
                                            dec = Integer.parseInt(declines);
                                        }

                                        if(unchanged!=null && !unchanged.equalsIgnoreCase("")){
                                            unch = Integer.parseInt(unchanged);
                                        }
                                        if(indice!=null && indice.equalsIgnoreCase("NIFTY 50")){

                                            int max = adv+dec+unch;
                                            progress_bar_nifty_50.setMax(max);
                                            progress_bar_nifty_50.setProgress(adv);
                                            nifty50_adv.setText(advances);
                                            nifty50_dec.setText(declines);

                                        }
                                        if(indice!=null && indice.equalsIgnoreCase("NIFTY BANK")){

                                            int max = adv+dec+unch;
                                            progress_bar_nifty_bank.setMax(max);
                                            progress_bar_nifty_bank.setProgress(adv);
                                            niftybank_adv.setText(advances);
                                            niftybank_dec.setText(declines);
                                        }

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
                CommonMethods.DisplayToastInfo(mContext,"No Internet Connection");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void fetch_MarketStatus() {


        try {
            Log.d("NSE_MARKET_STATUS_URL",NSE_MARKET_STATUS_URL);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, NSE_MARKET_STATUS_URL,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);

                                try {

                                    JSONObject jobj = new JSONObject(response);
                                    String data = jobj.getString("NormalMktStatus");

                                    tv_market_open_close.setText("MARKET "+data);

                                    if(data!=null && !data.equalsIgnoreCase("")) {
                                        if (data.contains("open")) {
                                            tv_market_open_close.setTextColor(mContext.getResources().getColor(R.color.result_points));
                                        } else if (data.contains("close")) {
                                            tv_market_open_close.setTextColor(mContext.getResources().getColor(R.color.red_close));
                                        }
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
                CommonMethods.DisplayToastInfo(mContext,"No Internet Connection");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }

    }


    private void fetch_MarketData() {


        try {
            Log.d("NSE_INDEX_WATCH_URL",NSE_INDEX_WATCH_URL);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, NSE_INDEX_WATCH_URL,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);

                                try {

                                    JSONObject jobj = new JSONObject(response);
                                    JSONArray data = jobj.getJSONArray("data");
                                    for(int k = 0; k< data.length();k++) {
                                        JSONObject jsonObject = data.getJSONObject(k);
                                        Log.d("jobj",""+jsonObject);
                                        String indexName = jsonObject.getString("indexName");
                                        String last = jsonObject.getString("last");
                                        String percChange = jsonObject.getString("percChange");
                                        String previousClose = jsonObject.getString("previousClose");

                                        if(indexName!=null && indexName.equalsIgnoreCase("NIFTY 50")){

                                            tv_nifty_50_value.setText(last);

                                            String remove_comma_last = last.replace(",","");
                                            double last_close = Double.parseDouble(remove_comma_last);
                                            Log.d("last_close",""+last_close);

                                            String remove_comma_previousClose = previousClose.replace(",","");
                                            double previou_close = Double.parseDouble(remove_comma_previousClose);
                                            Log.d("previou_close",""+previou_close);

                                            double difference_rate = last_close - previou_close;
                                            Log.d("difference_rate",""+difference_rate);

                                            String DifferenceAmount = String.format("%.2f",difference_rate);
                                            Log.d("DifferenceAmount",""+DifferenceAmount);



                                            double per_change = Double.parseDouble(percChange);
                                            if(per_change > 0){
                                                String text2 = "+"+DifferenceAmount +" ("+percChange+" %)";
                                                Log.d("text2",""+text2);

                                                tv_nift_value1.setText(text2);

                                                tv_nift_value1.setTextColor(mContext.getResources().getColor(R.color.result_points));
                                                iv_nifty_50.setImageDrawable(mContext.getDrawable(R.drawable.up));
                                            }else {
                                                String text2 = DifferenceAmount +" ("+percChange+" %)";
                                                Log.d("text2",""+text2);

                                                tv_nift_value1.setText(text2);


                                                tv_nift_value1.setTextColor(mContext.getResources().getColor(R.color.red_close));
                                                iv_nifty_50.setImageDrawable(mContext.getDrawable(R.drawable.down));

                                            }


                                        }
                                        if(indexName!=null && indexName.equalsIgnoreCase("NIFTY BANK")){

                                            tv_nifty_bank_value.setText(last);

                                            String remove_comma_last = last.replace(",","");
                                            double last_close = Double.parseDouble(remove_comma_last);
                                            Log.d("last_close",""+last_close);

                                            String remove_comma_previousClose = previousClose.replace(",","");
                                            double previou_close = Double.parseDouble(remove_comma_previousClose);
                                            Log.d("previou_close",""+previou_close);

                                            double difference_rate = last_close - previou_close;
                                            Log.d("difference_rate",""+difference_rate);

                                            String DifferenceAmount = String.format("%.2f",difference_rate);
                                            Log.d("DifferenceAmount",""+DifferenceAmount);



                                            double per_change = Double.parseDouble(percChange);


                                            if(per_change > 0){
                                                String text2 = "+"+DifferenceAmount +" ("+percChange+" %)";
                                                Log.d("text2",""+text2);

                                                tv_nift_bank_value1.setText(text2);


                                                tv_nift_bank_value1.setTextColor(mContext.getResources().getColor(R.color.result_points));
                                                iv_nifty_bank.setImageDrawable(mContext.getDrawable(R.drawable.up));

                                            }else {
                                                String text2 = DifferenceAmount +" ("+percChange+" %)";
                                                Log.d("text2",""+text2);

                                                tv_nift_bank_value1.setText(text2);
                                                tv_nift_bank_value1.setTextColor(mContext.getResources().getColor(R.color.red_close));
                                                iv_nifty_bank.setImageDrawable(mContext.getDrawable(R.drawable.down));

                                            }

                                        }




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
                CommonMethods.DisplayToastInfo(mContext,"No Internet Connection");

            }
        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
