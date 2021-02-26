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

import java.util.Objects;

import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.webservices.RestClient.TOP_VOLUME_URL;

public class ActiveByVolumeFragment extends Fragment {
    private View rootView;
    private LinearLayout ll_parent_market;
    private Context mContext;
    private TextView tv_AsOnDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_active_by_volume, container, false);
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
                fetch_MarketDataForVolume();
            }
        });

        ll_parent_market = (LinearLayout) rootView.findViewById(R.id.ll_parent_market);
        tv_AsOnDate = (TextView) rootView.findViewById(R.id.tv_AsOnDate);
        fetch_MarketDataForVolume();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "DefaultLocale", "InflateParams"})
    private void fetch_MarketDataForVolume() {
        if (ll_parent_market != null && ll_parent_market.getChildCount() > 0) {
            ll_parent_market.removeAllViews();
        }
        try {
            Log.d("TOP_VOLUME_URL", TOP_VOLUME_URL);
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, TOP_VOLUME_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                try {
                                    final JSONObject jobj = new JSONObject(response);
                                    final String time = jobj.getString("time");
                                    final JSONArray data = jobj.getJSONArray("data");
                                    for (int k = 0; k < data.length(); k++) {
                                        final JSONObject jsonObject = data.getJSONObject(k);
                                        final String symbol = jsonObject.getString("symbol");
                                        final String ltp = jsonObject.getString("ltp");
                                        final String netPrice = jsonObject.getString("netPrice");
                                        final String previousPrice = jsonObject.getString("previousPrice");
                                        final String series = jsonObject.getString("series");
                                        final Object openPrice = jsonObject.get("openPrice");
                                        final Object highPrice = jsonObject.get("highPrice");
                                        final Object lowPrice = jsonObject.get("lowPrice");
                                        final String tradedQuantity = jsonObject.getString("tradedQuantity");
                                        final String turnoverInLakhs = jsonObject.getString("turnoverInLakhs");
                                        final String lastCorpAnnouncementDate = jsonObject.getString("lastCorpAnnouncementDate");
                                        final String lastCorpAnnouncement = jsonObject.getString("lastCorpAnnouncement");
                                        final String remove_comma_ltp = ltp.replace(",", "");
                                        double ltp_close = Double.parseDouble(remove_comma_ltp);
                                        final String remove_comma_previousPrice = previousPrice.replace(",", "");
                                        double previou_close = Double.parseDouble(remove_comma_previousPrice);
                                        double difference_rate = ltp_close - previou_close;
                                        final String DifferenceAmount = String.format("%.2f", difference_rate);
                                        tv_AsOnDate.setText(time);

                                        final LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        final View rowView1 = Objects.requireNonNull(inflater).inflate(R.layout.row_market_info, null);
                                        rowView1.setPadding(10, 10, 10, 10);

                                        final TextView tv_stock_symbol = (TextView) rowView1.findViewById(R.id.stock_symbol);
                                        final TextView tv_ltp = (TextView) rowView1.findViewById(R.id.tv_ltp);
                                        final TextView tv_vol = (TextView) rowView1.findViewById(R.id.tv_vol);
                                        final TextView tc_change_per = (TextView) rowView1.findViewById(R.id.tc_change_per);
                                        final ImageView iv_change_arrow = (ImageView) rowView1.findViewById(R.id.change_arrow);
                                        tv_stock_symbol.setText(symbol);
                                        tv_ltp.setText(ltp);
                                        tv_vol.setText("Vol: " + tradedQuantity);
                                        if (netPrice != null && Double.parseDouble(netPrice) > 0.0) {
                                            final String text2 = "+" + DifferenceAmount + "(" + netPrice + " %)";
                                            tc_change_per.setText(text2);
                                            iv_change_arrow.setImageDrawable(mContext.getResources().getDrawable(R.drawable.up));
                                            tc_change_per.setTextColor(mContext.getResources().getColor(R.color.result_points));
                                        } else {
                                            final String text2 = DifferenceAmount + "(" + netPrice + " %)";
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