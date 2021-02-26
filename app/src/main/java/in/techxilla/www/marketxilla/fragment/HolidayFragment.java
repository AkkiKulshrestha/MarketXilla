package in.techxilla.www.marketxilla.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class HolidayFragment extends Fragment {
    private View rootView;
    private Context mContext;
    private LinearLayout ll_parent_holiday;
    private Toolbar toolbar;
    private ProgressDialog myDialog;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_holiday, null);
        initUI();
        return rootView;
    }

    private void initUI() {
        mContext = getContext();
        myDialog = new ProgressDialog(mContext);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        NewDashboard activity = (NewDashboard) getActivity();
        if (activity != null) {
            toolbar = activity.findViewById(R.id.toolbar);
            activity.setSupportActionBar(toolbar);
        }

        final ImageView iv_refresh = (ImageView) toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchHolidayData();
            }
        });

        ll_parent_holiday = (LinearLayout) rootView.findViewById(R.id.ll_parent_holiday);
        fetchHolidayData();
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SimpleDateFormat"})
    private void fetchHolidayData() {
        if (ll_parent_holiday != null && ll_parent_holiday.getChildCount() > 0) {
            ll_parent_holiday.removeAllViews();
        }
        if (myDialog != null && !myDialog.isShowing()) {
            myDialog.show();
        }
        final String Uiid_id = UUID.randomUUID().toString();
        final String URL_GetHolidayList = ROOT_URL + "holiday_list.php?_" + Uiid_id;
        try {
            Log.d("URL", URL_GetHolidayList);
            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetHolidayList,
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
                                        final String holiday = jsonObject.getString("holiday");
                                        final String date = jsonObject.getString("date");
                                        final String day = jsonObject.getString("day");
                                        final String year = jsonObject.getString("year");
                                        final Object is_national_holiday = jsonObject.get("is_national_holiday");
                                        final Object is_active = jsonObject.get("is_active");

                                        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        final View rowView1 = inflater.inflate(R.layout.row_holiday_info, null);
                                        rowView1.setPadding(10, 10, 10, 10);

                                        final RelativeLayout relativeDate = rowView1.findViewById(R.id.relativeDate);
                                        final TextView tv_date = (TextView) rowView1.findViewById(R.id.tv_date);
                                        final TextView tv_holiday_name = (TextView) rowView1.findViewById(R.id.tv_holiday_name);
                                        final TextView tv_day = (TextView) rowView1.findViewById(R.id.tv_day);

                                        if (is_national_holiday.equals("1")) {
                                            relativeDate.setBackgroundColor(mContext.getResources().getColor(R.color.mpn_red));
                                        } else {
                                            relativeDate.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                                        }

                                        final DateFormat format = new SimpleDateFormat("MMMM d,yyyy", Locale.ENGLISH);
                                        Date newDate = format.parse(date);
                                        final DateFormat format1 = new SimpleDateFormat("dd \nMMM");
                                        final String FormattedDate = format1.format(newDate);
                                        tv_date.setText(FormattedDate.toUpperCase());
                                        tv_holiday_name.setText(holiday);
                                        tv_day.setText(day);
                                        ll_parent_holiday.addView(rowView1);
                                    }
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
}
