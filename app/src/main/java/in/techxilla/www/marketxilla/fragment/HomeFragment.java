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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.littlemango.stacklayoutmanager.StackLayoutManager;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import in.techxilla.www.marketxilla.MainActivity;
import in.techxilla.www.marketxilla.NewDashboard;
import in.techxilla.www.marketxilla.NotificationActivity;
import in.techxilla.www.marketxilla.R;
import in.techxilla.www.marketxilla.adaptor.ImageSliderAdapter;
import in.techxilla.www.marketxilla.adaptor.SmartPlanAdapter;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.model.SliderItem;
import in.techxilla.www.marketxilla.model.SmartPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;

import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class HomeFragment extends Fragment {


    String StrMemberId,StrMemberName,StrMemberEmailId,StrMemberMobile,StrMemberUserName;
    ProgressDialog myDialog;
    TextView TV_NameTxt,TV_Day_TimeDisplayingTxt;
    String greeting;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES= {R.drawable.slider1,R.drawable.slider2,R.drawable.slider3,
            R.drawable.slider4,R.drawable.slider5,R.drawable.slider6};
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();
    TextView Tv_DisplayGolu;
    SliderView sliderView;
    ImageSliderAdapter adapter;
    int notifications_count = 0;
    LinearLayout ll_parent_calls;

    private RecyclerView recyclerSmartCalls;
    SwipeRefreshLayout refreshLayout;
    RecyclerView recycler_list;
    RecyclerView.LayoutManager layoutManager;
    private int mStackCount = 30;
    ImageView iv_notification;
    private int mRandomPosition;

    private StackLayoutManager mStackLayoutManager;
    // SwipeRefreshLayout refreshLayout;

    ArrayList<CallModel> callModel_list = new ArrayList<>();
    CallModel callListModel;
    StackLayoutAdapter stackLayoutAdapter;
    private String[] selectItems;
    RecyclerView recyclerSmartPlan;
    SmartPlanAdapter smartPlanAdapter;
    ViewGroup viewGroup;
    private static ArrayList<SmartPlanModel> smartPlanModelArrayList;
    public Toolbar toolbar;
    public ActionBar actionBar;

    View rootView;
    Context mContext;


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_home, null);

        initUI();
        return rootView;


    }

    private void initUI() {

        mContext = getContext();
        NewDashboard activity = (NewDashboard) getActivity();
         toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        ImageView iv_refresh = (ImageView) toolbar.findViewById(R.id.iv_refresh);
        iv_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCallData();
            }
        });

        mPager = (ViewPager)rootView.findViewById(R.id.image_pager);
        sliderView = (SliderView)rootView.findViewById(R.id.imageSlider);

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
        List<SliderItem> sliderItemList = new ArrayList<>();

        ImagesArray = new ArrayList<>();
        for (Integer image : IMAGES) {

            SliderItem sliderItem = new SliderItem();
            Log.d("In Home Fragment - ", "---> " + image);
            sliderItem.setDrawableImage(image);
            sliderItemList.add(sliderItem);
            adapter.addItem(sliderItem);
        }

        sliderView.setSliderAdapter(adapter);


        SocialNetworkingLinks();

        recycler_list = rootView.findViewById(R.id.recycler_list);
        recycler_list.setHasFixedSize(true);
        recycler_list.setNestedScrollingEnabled(false);


        final FragmentActivity c = getActivity();
        layoutManager = new LinearLayoutManager(c);
        recycler_list.setLayoutManager(layoutManager);




        fetchCallData();

    }


    public void RefreshPage(){


        fetchCallData();

    }

    private void SocialNetworkingLinks() {

        ImageView iv_gmail = (ImageView)rootView.findViewById(R.id.iv_gmail);
        ImageView iv_sms = (ImageView)rootView.findViewById(R.id.iv_sms);
        ImageView iv_whatsapp = (ImageView)rootView.findViewById(R.id.iv_whatsapp);



        iv_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    String[] recipients={"marketxilla@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT,"ENQUIRY");
                    intent.putExtra(Intent.EXTRA_TEXT,"Body of the content here...");
                    intent.putExtra(Intent.EXTRA_CC,"info@marketxilla.com");
                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    mContext.startActivity(Intent.createChooser(intent, "Send mail"));
                }catch(ActivityNotFoundException e){
                    //TODO smth
                }
            }
        });



        iv_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = "+919022409928";
                String message = "ENQUIRY. \nHello MarketXilla Team, I need your support. Kindly arrange a call back.";
                Uri sms_uri = Uri.parse("smsto:"+phone);
                Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                sms_intent.putExtra("sms_body", message);
                mContext.startActivity(sms_intent);

            }
        });

        iv_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = "+919022409928";
                String message = "ENQUIRY. \nHello MarketXilla Team, I need your support. Kindly arrange a call back.";
                PackageManager packageManager = mContext.getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        mContext.startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchCallData() {

        if(callModel_list!=null){
            callModel_list = new ArrayList<>();
        }

        /*myDialog = new ProgressDialog(mContext);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();*/
        String Uiid_id = UUID.randomUUID().toString();
        String URL_GetCallList = ROOT_URL+"fetchPerformanceData.php?_"+Uiid_id+"&per_for=all";


        try {
            Log.d("URL",URL_GetCallList);

            ConnectionDetector cd = new ConnectionDetector(mContext);
            boolean isInternetPresent = cd.isConnectingToInternet();
            if (isInternetPresent) {

                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GetCallList,
                        new Response.Listener<String>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response",response);
                                if(myDialog!=null && myDialog.isShowing()){
                                    myDialog.dismiss();
                                }
                                try {

                                    JSONObject jobj = new JSONObject(response);

                                    //StatusCodeKYCComplaint = jobj.getInt("status");
                                    JSONArray data = jobj.getJSONArray("data");
                                    mStackCount = data.length();
                                    for(int k = 0; k< data.length();k++) {
                                        JSONObject jsonObject = data.getJSONObject(k);
                                        Log.d("jobj",""+jsonObject);
                                        String id = jsonObject.getString("id");
                                        String stock_name = jsonObject.getString("stock_name");
                                        String date = jsonObject.getString("date");
                                        String performance_for = jsonObject.getString("performance_for");
                                        String performance_for_id = jsonObject.getString("performance_for_id");
                                        String is_buy_sell = jsonObject.getString("is_buy_sell");
                                        String buy_sell_above_below = jsonObject.getString("buy_sell_above_below");
                                        String stop_loss = jsonObject.getString("stop_loss");
                                        String target1 = jsonObject.getString("target1");
                                        String target2 = jsonObject.getString("target2");
                                        String target3 = jsonObject.getString("target3");
                                        String ce_pe = jsonObject.getString("ce_pe");
                                        String strike = jsonObject.getString("strike");

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

                                        callModel_list.add(callListModel);



                                    }

                                    stackLayoutAdapter= new StackLayoutAdapter(mContext, callModel_list);
                                    /*recyclerSmartCalls.setAdapter(stackLayoutAdapter);
                                    stackLayoutAdapter.notifyDataSetChanged();*/
                                    recycler_list.setAdapter(stackLayoutAdapter);
                                    stackLayoutAdapter.notifyDataSetChanged();




                                } catch (Exception e) {

                                    e.printStackTrace();

                                }

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("volley", "Error: " + error.getMessage());
                        error.printStackTrace();
                        if(myDialog!=null && myDialog.isShowing()){
                            myDialog.dismiss();
                        }

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


    class StackLayoutAdapter extends RecyclerView.Adapter<StackLayoutAdapter.StackHolder> {
        private ArrayList<CallModel> callArrayList;
        Context mCtx;

        @NonNull
        @Override
        public StackLayoutAdapter.StackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_calls, viewGroup, false);
            return new StackLayoutAdapter.StackHolder(view);
        }

        public  StackLayoutAdapter(Context mCtx, ArrayList<CallModel> callArrayList) {
            this.mCtx = mCtx;
            this.callArrayList = callArrayList;

        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull final StackLayoutAdapter.StackHolder viewHolder, int position) {
            int res;



            viewHolder.TvStockName.setText(callArrayList.get(position).getStock_name());

            Date date= null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(callArrayList.get(position).getDate());
                String convertDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(date);

                viewHolder.tv_date_time.setText(convertDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(callArrayList.get(position).getIs_buy_sell()!=null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("1")){
                if(callArrayList.get(position).getPerformance_for().equalsIgnoreCase("Commodity")){
                    viewHolder.tv_above_below.setText("BETWEEN \n"+callArrayList.get(position).getBuy_sell_above_below());
                }else{
                    viewHolder.tv_above_below.setText("ABOVE "+callArrayList.get(position).getBuy_sell_above_below());
                }

                viewHolder.tv_sell_buy.setText("INTRADAY BUY");
                viewHolder.tv_sell_buy.setBackground(mCtx.getDrawable(R.drawable.cw_button_shadow_green));
                viewHolder.iv_up_down.setImageDrawable(mCtx.getDrawable(R.drawable.up));
            }else if(callArrayList.get(position).getIs_buy_sell()!=null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("2")){
                if(callArrayList.get(position).getPerformance_for().equalsIgnoreCase("Commodity")){
                    viewHolder.tv_above_below.setText("BETWEEN \n"+callArrayList.get(position).getBuy_sell_above_below());
                }else{
                    viewHolder.tv_above_below.setText("BELOW "+callArrayList.get(position).getBuy_sell_above_below());
                }

                viewHolder.tv_sell_buy.setText("INTRADAY SELL");
                viewHolder.tv_sell_buy.setBackground(mCtx.getDrawable(R.drawable.cw_button_shadow_red));
                viewHolder.iv_up_down.setImageDrawable(mCtx.getDrawable(R.drawable.down));

            }



            if(callArrayList.get(position).getCe_pe()!=null && callArrayList.get(position).getCe_pe().equalsIgnoreCase("1")){
                viewHolder.tv_strike_ce.setText("STRIKE "+ CommonMethods.DecimalNumberDisplayFormattingWithComma(callArrayList.get(position).getStrike())+" CE" );
                viewHolder.tv_strike_ce.setVisibility(View.VISIBLE);

            }else if(callArrayList.get(position).getCe_pe()!=null && callArrayList.get(position).getCe_pe().equalsIgnoreCase("2")){
                viewHolder.tv_strike_ce.setText("STRIKE "+CommonMethods.DecimalNumberDisplayFormattingWithComma(callArrayList.get(position).getStrike())+" PE" );
                viewHolder.tv_strike_ce.setVisibility(View.VISIBLE);

            }else {
                viewHolder.tv_strike_ce.setVisibility(View.INVISIBLE);
            }




            viewHolder.tv_target1.setText("\u20B9 "+callArrayList.get(position).getTarget1());
            viewHolder.tv_target2.setText("\u20B9 "+callArrayList.get(position).getTarget2());
            viewHolder.tv_target3.setText("\u20B9 "+callArrayList.get(position).getTarget3());
            viewHolder.tv_stop_loss.setText("\u20B9 "+callArrayList.get(position).getStop_loss());

        }

        @Override
        public int getItemCount() {
            return mStackCount;
        }

        class StackHolder extends RecyclerView.ViewHolder {
            View itemView;
            ImageView iv_up_down;
            TextView TvStockName,tv_date_time,tv_above_below,tv_sell_buy,tv_strike_ce,tv_target1,tv_target2,tv_target3,tv_stop_loss;

            StackHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;

                TvStockName = (TextView) itemView.findViewById(R.id.TvStockName);
                tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
                tv_above_below = (TextView) itemView.findViewById(R.id.tv_above_below);
                tv_strike_ce = (TextView) itemView.findViewById(R.id.tv_strike_ce);
                tv_sell_buy = (TextView) itemView.findViewById(R.id.tv_sell_buy);
                iv_up_down = (ImageView)itemView.findViewById(R.id.iv_up_down);
                tv_target1 = (TextView) itemView.findViewById(R.id.tv_target1);
                tv_target2 = (TextView) itemView.findViewById(R.id.tv_target2);
                tv_target3 = (TextView) itemView.findViewById(R.id.tv_target3);
                tv_stop_loss = (TextView) itemView.findViewById(R.id.tv_stop_loss);





            }
        }
    }

}