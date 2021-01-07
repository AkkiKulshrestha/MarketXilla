package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.model.MarketModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.webservices.RestClient.GAINER_URL;

public class TopGainerFragment extends Fragment {

    View rootView;
    LinearLayout ll_parent_market;
    Context mContext;
    TextView tv_AsOnDate;

    Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_top_gainers, container, false);
         initId();
         return rootView;

    }

    private void initId() {
        mContext = getContext();

        NewDashboard activity = (NewDashboard) getActivity();
        toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        ImageView iv_refresh = (ImageView) toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetch_MarketDataForGainer();
            }
        });

        ll_parent_market = (LinearLayout)rootView.findViewById(R.id.ll_parent_market);

        tv_AsOnDate = (TextView)rootView.findViewById(R.id.tv_AsOnDate);


        fetch_MarketDataForGainer();

    }

    private void fetch_MarketDataForGainer() {


        if(ll_parent_market!=null && ll_parent_market.getChildCount()>0){
            ll_parent_market.removeAllViews();
        }

        try {
            Log.d("GAINER_URL",GAINER_URL);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, GAINER_URL,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);

                                try {

                                    JSONObject jobj = new JSONObject(response);

                                    String time = jobj.getString("time");
                                    JSONArray data = jobj.getJSONArray("data");
                                    for(int k = 0; k< data.length();k++) {
                                        JSONObject jsonObject = data.getJSONObject(k);
                                        Log.d("jobj",""+jsonObject);
                                        String symbol = jsonObject.getString("symbol");
                                        String ltp = jsonObject.getString("ltp");
                                        String previousPrice = jsonObject.getString("previousPrice");
                                        String netPrice = jsonObject.getString("netPrice");
                                        String series = jsonObject.getString("series");
                                        Object openPrice = jsonObject.get("openPrice");
                                        Object highPrice = jsonObject.get("highPrice");
                                        Object lowPrice = jsonObject.get("lowPrice");
                                        String tradedQuantity = jsonObject.getString("tradedQuantity");
                                        String turnoverInLakhs = jsonObject.getString("turnoverInLakhs");
                                        String lastCorpAnnouncementDate = jsonObject.getString("lastCorpAnnouncementDate");
                                        String lastCorpAnnouncement = jsonObject.getString("lastCorpAnnouncement");


                                        String remove_comma_ltp = ltp.replace(",","");
                                        double ltp_close = Double.parseDouble(remove_comma_ltp);
                                        Log.d("ltp_close",""+ltp_close);

                                        String remove_comma_previousPrice = previousPrice.replace(",","");
                                        double previou_close = Double.parseDouble(remove_comma_previousPrice);
                                        Log.d("previou_close",""+previou_close);

                                        double difference_rate = ltp_close - previou_close;
                                        Log.d("difference_rate",""+difference_rate);

                                        String DifferenceAmount = String.format("%.2f",difference_rate);
                                        Log.d("DifferenceAmount",""+DifferenceAmount);




                                        tv_AsOnDate.setText(time);






                                        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        final View rowView1 = Objects.requireNonNull(inflater).inflate(R.layout.row_market_info, null);
                                        rowView1.setPadding(10, 10, 10, 10);

                                        TextView tv_stock_symbol = (TextView)rowView1.findViewById(R.id.stock_symbol);
                                        TextView tv_ltp = (TextView)rowView1.findViewById(R.id.tv_ltp);
                                        TextView tv_vol = (TextView)rowView1.findViewById(R.id.tv_vol);
                                        TextView tc_change_per = (TextView)rowView1.findViewById(R.id.tc_change_per);

                                        ImageView iv_change_arrow = (ImageView) rowView1.findViewById(R.id.change_arrow);



                                        String text2 = "+"+DifferenceAmount +"("+netPrice+" %)";
                                        Log.d("text2",""+text2);



                                        tv_stock_symbol.setText(symbol);
                                        tv_ltp.setText(ltp);
                                        tv_vol.setText("Vol: " +tradedQuantity);
                                        tc_change_per.setText(text2);
                                        tc_change_per.setTextColor(mContext.getResources().getColor(R.color.result_points));
                                        iv_change_arrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.up));

                                        /*rowView1.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                String url = NSE_GET_FUTURE_STOCK_URL+tv_stock_symbol.getText().toString().toUpperCase()+"&instrument=FUTSTK&type=-&strike=-&expiry=28JAN2021";
                                                Log.d("url",url);
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(url));
                                                mContext.startActivity(i);

                                            }
                                        });*/

                                        ll_parent_market.addView(rowView1);

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
