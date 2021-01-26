package in.techxilla.www.marketxilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.littlemango.stacklayoutmanager.StackLayoutManager;
import com.pkmmte.view.CircularImageView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.takusemba.multisnaprecyclerview.MultiSnapHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import in.techxilla.www.marketxilla.adaptor.ImageSliderAdapter;
import in.techxilla.www.marketxilla.adaptor.SmartPlanAdapter;
import in.techxilla.www.marketxilla.model.CallModel;
import in.techxilla.www.marketxilla.model.SliderItem;
import in.techxilla.www.marketxilla.model.SmartPlanModel;
import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.ConnectionDetector;
import in.techxilla.www.marketxilla.utils.UtilitySharedPreferences;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static in.techxilla.www.marketxilla.utils.CommonMethods.DisplaySnackBar;
import static in.techxilla.www.marketxilla.webservices.RestClient.ROOT_URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


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
    RecyclerView.LayoutManager layoutManager;
    SmartPlanAdapter smartPlanAdapter;
    ViewGroup viewGroup;
    private static ArrayList<SmartPlanModel> smartPlanModelArrayList;
    MultiSnapHelper multiSnapHelper;
    TextView badge_new_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewGroup = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        init();
        navigationView();
    }

    private void init() {

        myDialog = new ProgressDialog(MainActivity.this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);

        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberId");
        StrMemberName = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberName");
        StrMemberEmailId = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberEmailId");
        StrMemberMobile = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberMobile");
        StrMemberUserName = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberUsername");


        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        Log.d("Day Of Week",dayOfTheWeek);

        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        if(hours>=5 && hours<12){
            greeting = "morning";
        } else if(hours>=12 && hours<17){
            greeting = "afternoon";
        } else if(hours>=17 && hours<21){
            greeting = "evening";
        } else {
            greeting = "night";
        }
        //Log.d("Greeting",greeting);

        TV_NameTxt = (TextView)findViewById(R.id.TV_NameTxt);

        TV_Day_TimeDisplayingTxt = (TextView)findViewById(R.id.TV_Day_TimeDisplayingTxt);
        String html_text_for_sunday_afternoon = "It's "+dayOfTheWeek+" "+greeting+".";
        String[] each = html_text_for_sunday_afternoon.split(" ");
        SpannableString span1 = new SpannableString(each[1]);
        span1.setSpan(new AbsoluteSizeSpan(30,true), 0, each[1].length(), SPAN_INCLUSIVE_INCLUSIVE);
        SpannableString span2 = new SpannableString(each[2]);
        span2.setSpan(new AbsoluteSizeSpan(20,true), 0, each[2].length(), SPAN_INCLUSIVE_INCLUSIVE);


        CharSequence finalText = TextUtils.concat(each[0], " ", span1);
        CharSequence finalText1 = TextUtils.concat(finalText, " ", span2);


        TV_Day_TimeDisplayingTxt.setText(finalText1);
        if(StrMemberName!=null && !StrMemberName.equalsIgnoreCase("") && !StrMemberName.equalsIgnoreCase("null")){
            String html_text_for_name = "HELLO "+StrMemberName.toUpperCase();
            TV_NameTxt.setText(Html.fromHtml(html_text_for_name));
            TV_NameTxt.setVisibility(View.VISIBLE);
        }else {
            TV_NameTxt.setText("HELLO");
            TV_NameTxt.setVisibility(View.GONE);
        }


        mPager = (ViewPager)findViewById(R.id.image_pager);
        sliderView = (SliderView) findViewById(R.id.imageSlider);

        iv_notification = findViewById(R.id.iv_notification);
        iv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.move_left, R.animator.move_right);

            }
        });

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

        adapter = new ImageSliderAdapter(getApplicationContext());
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

        ImageView iv_facebook = (ImageView)findViewById(R.id.iv_facebook);
        ImageView iv_gmail = (ImageView)findViewById(R.id.iv_gmail);
        ImageView iv_twitter = (ImageView)findViewById(R.id.iv_twitter);
        ImageView iv_telegram = (ImageView)findViewById(R.id.iv_telegram);
        ImageView iv_linked_in = (ImageView)findViewById(R.id.iv_linked_in);
        ImageView iv_sms = (ImageView)findViewById(R.id.iv_sms);

        ImageView iv_whatsapp = (ImageView)findViewById(R.id.iv_whatsapp);


        iv_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getResources().getString(R.string.facebook_page)));
                startActivity(i);
            }
        });

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
                    startActivity(Intent.createChooser(intent, "Send mail"));
                }catch(ActivityNotFoundException e){
                    //TODO smth
                }
            }
        });

        iv_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getResources().getString(R.string.twitter_page)));
                startActivity(i);
            }
        });

        iv_telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent telegram = new Intent(Intent.ACTION_VIEW );
                telegram.setData(Uri.parse(getResources().getString(R.string.telegram)));
                startActivity(telegram);
            }
        });

        iv_linked_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(getResources().getString(R.string.linked_in_page)));
                startActivity(i);
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
                startActivity(sms_intent);

            }
        });

        iv_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phone = "+919022409928";
                String message = "ENQUIRY. \nHello MarketXilla Team, I need your support. Kindly arrange a call back.";
                PackageManager packageManager = getApplicationContext().getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                try {
                    String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
       // refreshLayout= (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        recyclerSmartPlan= (RecyclerView)findViewById(R.id.recyclerSmartPlan);
        layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false);
        recyclerSmartPlan.setLayoutManager(layoutManager);



        smartPlanModelArrayList = new ArrayList<SmartPlanModel>();

        //Set up Adapter
        smartPlanAdapter = new SmartPlanAdapter(smartPlanModelArrayList,MainActivity.this);
        recyclerSmartPlan.setAdapter(smartPlanAdapter);
        displaySmartPlans();

        recyclerSmartCalls = findViewById(R.id.recyclerSmartCalls);




        StackLayoutManager.ScrollOrientation orientation = StackLayoutManager.ScrollOrientation.TOP_TO_BOTTOM ;





        mStackLayoutManager = new StackLayoutManager(orientation);
       // mStackLayoutManager.setVisibleItemCount(3);
         mStackLayoutManager.setItemOffset(120);
        mStackLayoutManager.setPagerMode(true);

        recyclerSmartCalls.setLayoutManager(mStackLayoutManager);
        fetchCallData();

        Handler handler = new Handler();
        handler.postDelayed
                (new Runnable() {

                    @Override
                    public void run() {
                        fetchCallData();
                    }
                }, 60000);



    }

    private void displaySmartPlans() {

        smartPlanModelArrayList.add(new SmartPlanModel("1 Month", getString(R.string.pcakage1_desc),R.drawable.ic_info,R.color.colorPrimary));
        smartPlanModelArrayList.add(new SmartPlanModel("2 Month", getString(R.string.package2_desc),R.drawable.ic_info,R.color.colorPrimary));
        smartPlanModelArrayList.add(new SmartPlanModel("3 Month", getString(R.string.package3_desc),R.drawable.ic_info,R.color.colorPrimary));
    }

    private void fetchCallData() {

        if(callModel_list!=null){
             callModel_list = new ArrayList<>();
        }

        myDialog = new ProgressDialog(this);
        myDialog.setMessage("Please wait...");
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.show();
        String Uiid_id = UUID.randomUUID().toString();
        String URL_GetCallList = ROOT_URL+"fetchPerformanceData.php?_"+Uiid_id+"&per_for=all";


        try {
            Log.d("URL",URL_GetCallList);

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
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

                                    stackLayoutAdapter= new StackLayoutAdapter(MainActivity.this, callModel_list);
                                    recyclerSmartCalls.setAdapter(stackLayoutAdapter);
                                    stackLayoutAdapter.notifyDataSetChanged();


                                    fetchNotificationList();

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

                int socketTimeout = 50000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(policy);
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);

            } else {
                DisplaySnackBar(viewGroup,"No Internet Connection","INFO");

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
        public StackHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_daily_calls, viewGroup, false);
            return new StackHolder(view);
        }

        public  StackLayoutAdapter(Context mCtx, ArrayList<CallModel> callArrayList) {
            this.mCtx = mCtx;
            this.callArrayList = callArrayList;

        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull final StackHolder viewHolder, int position) {
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
                viewHolder.tv_above_below.setText("ABOVE "+callArrayList.get(position).getBuy_sell_above_below() );
                viewHolder.tv_sell_buy.setText("INTRADAY BUY");
                viewHolder.tv_sell_buy.setBackground(getResources().getDrawable(R.drawable.cw_button_shadow_green));
                viewHolder.iv_up_down.setImageDrawable(getDrawable(R.drawable.up));
            }else if(callArrayList.get(position).getIs_buy_sell()!=null && callArrayList.get(position).getIs_buy_sell().equalsIgnoreCase("2")){
                viewHolder.tv_above_below.setText("BELOW "+callArrayList.get(position).getBuy_sell_above_below());
                viewHolder.tv_sell_buy.setText("INTRADAY SELL");
                viewHolder.tv_sell_buy.setBackground(getResources().getDrawable(R.drawable.cw_button_shadow_red));
                viewHolder.iv_up_down.setImageDrawable(getDrawable(R.drawable.down));

            }



            if(callArrayList.get(position).getCe_pe()!=null && callArrayList.get(position).getCe_pe().equalsIgnoreCase("1")){
                viewHolder.tv_strike_ce.setText("STRIKE "+CommonMethods.DecimalNumberDisplayFormattingWithComma(callArrayList.get(position).getStrike())+" CE" );
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

 /*   private void resetRandom() {
        mRandomPosition = Math.abs(new Random().nextInt() % mStackCount);
        selectItems[0] = getResources().getString(R.string.smooth_scroll) + mRandomPosition;
        selectItems[1] = getResources().getString(R.string.scroll) + mRandomPosition;
    }*/

    private void navigationView() {
        StrMemberId = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberId");
        StrMemberName = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberName");
        StrMemberEmailId = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberEmailId");
        StrMemberMobile = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberMobile");
        StrMemberUserName = UtilitySharedPreferences.getPrefs(getApplicationContext(),"MemberUsername");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        //ImageView iv_logo = (ImageView)navigationView.findViewById(R.id.iv_logo);

        //TextView nav_header_userId = navigationView.findViewById(R.id.nav_header_userId);
        View hView =  navigationView.getHeaderView(0);



        TextView nav_header_userName = (TextView)hView.findViewById(R.id.nav_header_userName);
        TextView nav_user_email = (TextView)hView.findViewById(R.id.nav_Email);
        if(StrMemberName!=null && !StrMemberName.equalsIgnoreCase("")) {
            nav_header_userName.setText(StrMemberName.toUpperCase());
        }

        if(StrMemberMobile!=null && !StrMemberMobile.equalsIgnoreCase("")) {
            nav_user_email.setText("Mobile No.: "+StrMemberMobile);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about_us) {
            Intent intent = new Intent(Intent.ACTION_VIEW );
            intent.setData(Uri.parse("https://marketxilla.com/about-us/"));
            startActivity(intent);
        } else if(id == R.id.nav_contact_us){
            Intent intent = new Intent(Intent.ACTION_VIEW );
            intent.setData(Uri.parse("https://marketxilla.com/contact-us/"));
            startActivity(intent);

        }else if(id == R.id.nav_rate_us){
            try {
                Uri marketUri = Uri.parse("market://details?id=" + getPackageName());
                Log.d("MarketUri",""+marketUri);
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            }catch(ActivityNotFoundException e) {
                Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Log.d("MarketUri",""+marketUri);
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
                startActivity(marketIntent);
            }

        }else if(id == R.id.nav_share){

            Context context = getApplicationContext();
            final String appPackageName = context.getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download Marketxilla -  https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");

            context.startActivity(sendIntent);


        } else if(id == R.id.nav_logout){
           // UtilitySharedPreferences.clearPref(getApplicationContext());
            Intent i = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(i);
            overridePendingTransition(R.animator.left_right, R.animator.right_left);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.exit)
                .setMessage(R.string.strExit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton(R.string.no, null).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void fetchNotificationList() {



        String Uiid_id = UUID.randomUUID().toString();


        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            String URL = ROOT_URL + "get_notifications_list.php?_" + Uiid_id;
            Log.d("URL", "--> " + URL);
            StringRequest postrequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(String response) {
                    try {


                        Log.d("URL Response", "--> " + response);

                        JSONObject jsonresponse = new JSONObject(response);

                        boolean status = jsonresponse.getBoolean("status");
                        if(status) {
                            String result = jsonresponse.getString("data");
                            JSONArray resultArry = new JSONArray(result);

                            notifications_count = resultArry.length();

                            SetNotificationBadge();

                        }else {
                            notifications_count = 0;
                            SetNotificationBadge();
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    volleyError.printStackTrace();
                    DisplaySnackBar(viewGroup,"Something goes wrong. Please try again","WARNING");

                    //Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_LONG).show();
                }
            }) ;
            int socketTimeout = 50000; //30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postrequest.setRetryPolicy(policy);
            // RequestQueue requestQueue = Volley.newRequestQueue(this, new HurlStack(null, getSocketFactory()));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postrequest);
        }else {
            DisplaySnackBar(viewGroup,"No Internet Connection","WARNING");

        }


    }

    private void SetNotificationBadge() {

        badge_new_notification = findViewById(R.id.badge_new_notification);
        badge_new_notification.setText(String.format("%02d",notifications_count));
    }

}