package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.webservices.RestClient.NSE_GET_FUTURE_STOCK_URL;

public class ActiveFutureFragment extends Fragment {
    private View rootView;
    private LinearLayout ll_parent_market;
    private Context mContext;
    private TextView tv_AsOnDate, tvExpiryDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_active_future, container, false);
        initId();
        return rootView;
    }

    private void initId() {
        mContext = getContext();
        NewDashboard activity = (NewDashboard) getActivity();
        final Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        final ImageView iv_refresh = (ImageView) toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetch_MarketData();
            }
        });

        ll_parent_market = (LinearLayout) rootView.findViewById(R.id.ll_parent_market);
        tv_AsOnDate = (TextView) rootView.findViewById(R.id.tv_AsOnDate);
        tvExpiryDate = (TextView) rootView.findViewById(R.id.tvExpiryDate);
        fetch_MarketData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "DefaultLocale", "InflateParams"})
    private void fetch_MarketData() {
        if (ll_parent_market != null && ll_parent_market.getChildCount() > 0) {
            ll_parent_market.removeAllViews();
        }
        try {
            Log.d("GET_FUT_STK_URL", NSE_GET_FUTURE_STOCK_URL);
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, NSE_GET_FUTURE_STOCK_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                Log.d("Response", response);
                                try {
                                    final JSONObject jobj = new JSONObject(response);
                                    final JSONObject volumeObj = jobj.getJSONObject("volume");
                                    final String time = volumeObj.getString("timestamp");
                                    final JSONArray data = volumeObj.getJSONArray("data");
                                    for (int k = 0; k < data.length(); k++) {
                                        final JSONObject jsonObject = data.getJSONObject(k);
                                        final String symbol = jsonObject.getString("underlying");
                                        final String instrument = jsonObject.getString("instrument");
                                        double ltp = jsonObject.getDouble("lastPrice");
                                        double netPrice = jsonObject.getDouble("pChange");
                                        final String expiryDate = jsonObject.getString("expiryDate");
                                        final String totalTurnover = jsonObject.getString("totalTurnover");
                                        double total_Turnover = Double.parseDouble(totalTurnover);
                                        final String tradedQuantity = String.format("%.2f", total_Turnover);
                                        final String netChangePer = String.format("%.2f", netPrice);
                                        final String Ltp = String.format("%.2f", ltp);
                                        tv_AsOnDate.setText(time);
                                        tvExpiryDate.setText("EX DT: " + expiryDate);
                                        tvExpiryDate.setVisibility(View.VISIBLE);

                                        final LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        final View rowView1 = Objects.requireNonNull(inflater).inflate(R.layout.row_market_info, null);
                                        rowView1.setPadding(10, 10, 10, 10);

                                        final TextView tv_stock_symbol = (TextView) rowView1.findViewById(R.id.stock_symbol);
                                        final TextView tv_ltp = (TextView) rowView1.findViewById(R.id.tv_ltp);
                                        final TextView tv_vol = (TextView) rowView1.findViewById(R.id.tv_vol);
                                        final TextView tc_change_per = (TextView) rowView1.findViewById(R.id.tc_change_per);
                                        final TextView tv_row_strike_ce = (TextView) rowView1.findViewById(R.id.tv_row_strike_ce);
                                        final ImageView iv_change_arrow = (ImageView) rowView1.findViewById(R.id.change_arrow);

                                        tv_row_strike_ce.setText(instrument);
                                        tv_row_strike_ce.setText(View.VISIBLE);
                                        tv_stock_symbol.setText(symbol);
                                        tv_ltp.setText(Ltp);
                                        tv_vol.setText("Vol: " + tradedQuantity);
                                        if (netPrice > 0) {
                                            final String text2 = "+" + netChangePer + " % ";
                                            tc_change_per.setText(text2);
                                            tc_change_per.setTextColor(mContext.getResources().getColor(R.color.result_points));
                                            iv_change_arrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.up));
                                        } else {
                                            final String text2 = netChangePer + " % ";
                                            tc_change_per.setText(text2);
                                            iv_change_arrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.down));
                                            tc_change_per.setTextColor(mContext.getResources().getColor(R.color.md_red_a400));
                                        }
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
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("x-api-key", "x_api_key");
                        Log.d("Headers", "" + headers);
                        return headers;
                    }
                };
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