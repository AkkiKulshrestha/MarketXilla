package in.techxilla.www.marketxilla;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.MyValidator;

public class GannCartCalc extends AppCompatActivity {

    private String CalcName;
    private EditText Edt_LastTradedPrice;

    private String StrLastTradedPrice;
    private double last_traded_price = 0.0;

    private TextView tv_buy_above, tv_sell_below;
    private TextView buy_abv_target1, sell_bel_target1;
    private TextView buy_abv_target2, sell_bel_target2;
    private TextView buy_abv_target3, sell_bel_target3;
    private TextView buy_abv_target4, sell_bel_target4;
    private TextView buy_abv_target5, sell_bel_target5;
    private TextView buy_abv_stop_loss, sell_blw_stop_loss;

    private String Str_tv_buy_above, Str_tv_sell_below;
    private String Str_buy_abv_target1, Str_sell_bel_target1;
    private String Str_buy_abv_target2, Str_sell_bel_target2;
    private String Str_buy_abv_target3, Str_sell_bel_target3;
    private String Str_buy_abv_target4, Str_sell_bel_target4;
    private String Str_buy_abv_target5, Str_sell_bel_target5;
    private String Str_buy_abv_stop_loss, Str_sell_blw_stop_loss;

    private double dou_tv_buy_above = 0.0, dou_tv_sell_below = 0.0;
    private double dou_buy_abv_target1 = 0.0, dou_sell_bel_target1 = 0.0;
    private double dou_buy_abv_target2 = 0.0, dou_sell_bel_target2 = 0.0;
    private double dou_buy_abv_target3 = 0.0, dou_sell_bel_target3 = 0.0;
    private double dou_buy_abv_target4 = 0.0, dou_sell_bel_target4 = 0.0;
    private double dou_buy_abv_target5 = 0.0, dou_sell_bel_target5 = 0.0;
    private double dou_buy_abv_stop_loss = 0.0, dou_sell_blw_stop_loss = 0.0;

    private TextView res1, res2, res3, res4, res5;
    private TextView sup1, sup2, sup3, sup4, sup5;

    private String Str_resistance5, Str_resistance4, Str_resistance3, Str_resistance2, Str_resistance1, Str_support1, Str_support2, Str_support3, Str_support4, Str_support5;
    private double dou_resistance5 = 0.0, dou_resistance4 = 0.0, dou_resistance3 = 0.0, dou_resistance2 = 0.0, dou_resistance1 = 0.0, dou_support1 = 0.0, dou_support2 = 0.0, dou_support3 = 0.0, dou_support4 = 0.0, dou_support5 = 0.0;

    private TextView box1, box2, box3, box4, box5, box6, box7, box8, box9, box10, box11, box12, box13, box14, box15, box16, box17, box18, box19,
            box20, box21, box22, box23, box24, box25, box26, box27, box28, box29, box30, box31, box32, box33, box34, box35, box36, box37,
            box38, box39, box40, box41, box42, box43, box44, box45, box46, box47, box48, box49;

    private String box1_str, box2_str, box3_str, box4_str, box5_str, box6_str, box7_str, box8_str, box9_str, box10_str, box11_str, box12_str, box13_str, box14_str, box15_str, box16_str, box17_str, box18_str, box19_str,
            box20_str, box21_str, box22_str, box23_str, box24_str, box25_str, box26_str, box27_str, box28_str, box29_str, box30_str, box31_str, box32_str, box33_str, box34_str, box35_str, box36_str, box37_str,
            box38_str, box39_str, box40_str, box41_str, box42_str, box43_str, box44_str, box45_str, box46_str, box47_str, box48_str, box49_str;

    private double box1_dou = 0.0, box2_dou = 0.0, box3_dou = 0.0, box4_dou = 0.0, box5_dou = 0.0, box6_dou = 0.0, box7_dou = 0.0, box8_dou = 0.0, box9_dou = 0.0, box10_dou = 0.0, box11_dou = 0.0, box12_dou = 0.0, box13_dou = 0.0, box14_dou = 0.0, box15_dou = 0.0, box16_dou = 0.0, box17_dou = 0.0, box18_dou = 0.0, box19_dou = 0.0,
            box20_dou = 0.0, box21_dou = 0.0, box22_dou = 0.0, box23_dou = 0.0, box24_dou = 0.0, box25_dou = 0.0, box26_dou = 0.0, box27_dou = 0.0, box28_dou = 0.0, box29_dou = 0.0, box30_dou = 0.0, box31_dou = 0.0, box32_dou = 0.0, box33_dou = 0.0, box34_dou = 0.0, box35_dou = 0.0, box36_dou = 0.0, box37_dou = 0.0,
            box38_dou = 0.0, box39_dou = 0.0, box40_dou = 0.0, box41_dou = 0.0, box42_dou = 0.0, box43_dou = 0.0, box44_dou = 0.0, box45_dou = 0.0, box46_dou = 0.0, box47_dou = 0.0, box48_dou = 0.0, box49_dou = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gann_calculator);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            CalcName = bundle.getString("calc");
        }
        init();
    }

    private void init() {

        final TextView tv_title = (TextView) findViewById(R.id.tv_title);
        final ImageView iv_back = (ImageView) findViewById(R.id.back_btn_toolbar);

        tv_title.setText(CalcName);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Edt_LastTradedPrice = (EditText) findViewById(R.id.Edt_LastTradedPrice);

        Button btn_Calc = (Button) findViewById(R.id.btn_Calc);
        tv_buy_above = (TextView) findViewById(R.id.tv_buy_above);
        tv_sell_below = (TextView) findViewById(R.id.tv_sell_below);
        buy_abv_target1 = (TextView) findViewById(R.id.buy_abv_target1);
        sell_bel_target1 = (TextView) findViewById(R.id.sell_bel_target1);
        buy_abv_target2 = (TextView) findViewById(R.id.buy_abv_target2);
        sell_bel_target2 = (TextView) findViewById(R.id.sell_bel_target2);
        buy_abv_target3 = (TextView) findViewById(R.id.buy_abv_target3);
        sell_bel_target3 = (TextView) findViewById(R.id.sell_bel_target3);
        buy_abv_target4 = (TextView) findViewById(R.id.buy_abv_target4);
        sell_bel_target4 = (TextView) findViewById(R.id.sell_bel_target4);
        buy_abv_target5 = (TextView) findViewById(R.id.buy_abv_target5);
        sell_bel_target5 = (TextView) findViewById(R.id.sell_bel_target5);
        buy_abv_stop_loss = (TextView) findViewById(R.id.buy_abv_stop_loss);
        sell_blw_stop_loss = (TextView) findViewById(R.id.sell_blw_stop_loss);

        res1 = (TextView) findViewById(R.id.res1);
        res2 = (TextView) findViewById(R.id.res2);
        res3 = (TextView) findViewById(R.id.res3);
        res4 = (TextView) findViewById(R.id.res4);
        res5 = (TextView) findViewById(R.id.res5);

        sup1 = (TextView) findViewById(R.id.sup1);
        sup2 = (TextView) findViewById(R.id.sup2);
        sup3 = (TextView) findViewById(R.id.sup3);
        sup4 = (TextView) findViewById(R.id.sup4);
        sup5 = (TextView) findViewById(R.id.sup5);

        box1 = (TextView) findViewById(R.id.box1);
        box2 = (TextView) findViewById(R.id.box2);
        box3 = (TextView) findViewById(R.id.box3);
        box4 = (TextView) findViewById(R.id.box4);
        box5 = (TextView) findViewById(R.id.box5);
        box6 = (TextView) findViewById(R.id.box6);
        box7 = (TextView) findViewById(R.id.box7);
        box8 = (TextView) findViewById(R.id.box8);
        box9 = (TextView) findViewById(R.id.box9);
        box10 = (TextView) findViewById(R.id.box10);
        box11 = (TextView) findViewById(R.id.box11);
        box12 = (TextView) findViewById(R.id.box12);
        box13 = (TextView) findViewById(R.id.box13);
        box14 = (TextView) findViewById(R.id.box14);
        box15 = (TextView) findViewById(R.id.box15);
        box16 = (TextView) findViewById(R.id.box16);
        box17 = (TextView) findViewById(R.id.box17);
        box18 = (TextView) findViewById(R.id.box18);
        box19 = (TextView) findViewById(R.id.box19);
        box20 = (TextView) findViewById(R.id.box20);
        box21 = (TextView) findViewById(R.id.box21);
        box22 = (TextView) findViewById(R.id.box22);
        box23 = (TextView) findViewById(R.id.box23);
        box24 = (TextView) findViewById(R.id.box24);
        box25 = (TextView) findViewById(R.id.box25);
        box26 = (TextView) findViewById(R.id.box26);
        box27 = (TextView) findViewById(R.id.box27);
        box28 = (TextView) findViewById(R.id.box28);
        box29 = (TextView) findViewById(R.id.box29);
        box30 = (TextView) findViewById(R.id.box30);
        box31 = (TextView) findViewById(R.id.box31);
        box32 = (TextView) findViewById(R.id.box32);
        box33 = (TextView) findViewById(R.id.box33);
        box34 = (TextView) findViewById(R.id.box34);
        box35 = (TextView) findViewById(R.id.box35);
        box36 = (TextView) findViewById(R.id.box36);
        box37 = (TextView) findViewById(R.id.box37);
        box38 = (TextView) findViewById(R.id.box38);
        box39 = (TextView) findViewById(R.id.box39);
        box40 = (TextView) findViewById(R.id.box40);
        box41 = (TextView) findViewById(R.id.box41);
        box42 = (TextView) findViewById(R.id.box42);
        box43 = (TextView) findViewById(R.id.box43);
        box44 = (TextView) findViewById(R.id.box44);
        box45 = (TextView) findViewById(R.id.box45);
        box46 = (TextView) findViewById(R.id.box46);
        box47 = (TextView) findViewById(R.id.box47);
        box48 = (TextView) findViewById(R.id.box48);
        box49 = (TextView) findViewById(R.id.box49);

        btn_Calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    StrLastTradedPrice = Edt_LastTradedPrice.getText().toString().trim();
                    last_traded_price = StrLastTradedPrice != null ? Double.parseDouble(StrLastTradedPrice) : 0.0;
                    if (last_traded_price < 1000000) {
                        Calculate();
                    } else {
                        Edt_LastTradedPrice.setError("Please Enter Amount Below 10 Lacs.");
                        Edt_LastTradedPrice.requestFocus();
                    }
                }
            }
        });

        Edt_LastTradedPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isValid()) {
                    StrLastTradedPrice = Edt_LastTradedPrice.getText().toString().trim();
                    last_traded_price = StrLastTradedPrice != null ? Double.parseDouble(StrLastTradedPrice) : 0.0;
                    if (last_traded_price < 1000000) {
                        Calculate();
                    } else {
                        Edt_LastTradedPrice.setError("Please Enter Amount Below 10 Lacs.");
                        Edt_LastTradedPrice.requestFocus();
                    }
                }
                return false;
            }
        });

    }


    @SuppressLint("DefaultLocale")
    private void Calculate() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(Edt_LastTradedPrice.getWindowToken(), 0);
        double squareRoot_TradedPrice = Math.sqrt(last_traded_price);
        int squre_rt = (int) squareRoot_TradedPrice;
        int base_value = squre_rt - 1;
        box25_dou = base_value * base_value;
        box25_str = String.format("%.2f", box25_dou);
        box25.setText(box25_str);

        box24_dou = Math.pow((base_value + 0.125), 2);
        box24_str = String.format("%.2f", box24_dou);
        box24.setText(box24_str);

        box17_dou = Math.pow((base_value + 0.250), 2);
        box17_str = String.format("%.2f", box17_dou);
        box17.setText(box17_str);

        box18_dou = Math.pow((base_value + 0.375), 2);
        box18_str = String.format("%.2f", box18_dou);
        box18.setText(box18_str);

        box19_dou = Math.pow((base_value + 0.500), 2);
        box19_str = String.format("%.2f", box19_dou);
        box19.setText(box19_str);

        box26_dou = Math.pow((base_value + 0.625), 2);
        box26_str = String.format("%.2f", box26_dou);
        box26.setText(box26_str);

        box33_dou = Math.pow((base_value + 0.750), 2);
        box33_str = String.format("%.2f", box33_dou);
        box33.setText(box33_str);

        box32_dou = Math.pow((base_value + 0.875), 2);
        box32_str = String.format("%.2f", box32_dou);
        box32.setText(box32_str);

        box31_dou = Math.pow((base_value + 1), 2);
        box31_str = String.format("%.2f", box31_dou);
        box31.setText(box31_str);

        box23_dou = Math.pow((base_value + 1.125), 2);
        box23_str = String.format("%.2f", box23_dou);
        box23.setText(box23_str);

        box9_dou = Math.pow((base_value + 1.250), 2);
        box9_str = String.format("%.2f", box9_dou);
        box9.setText(box9_str);

        box11_dou = Math.pow((base_value + 1.375), 2);
        box11_str = String.format("%.2f", box11_dou);
        box11.setText(box11_str);

        box13_dou = Math.pow((base_value + 1.500), 2);
        box13_str = String.format("%.2f", box13_dou);
        box13.setText(box13_str);

        box27_dou = Math.pow((base_value + 1.625), 2);
        box27_str = String.format("%.2f", box27_dou);
        box27.setText(box27_str);

        box41_dou = Math.pow((base_value + 1.750), 2);
        box41_str = String.format("%.2f", box41_dou);
        box41.setText(box41_str);

        box39_dou = Math.pow((base_value + 1.875), 2);
        box39_str = String.format("%.2f", box39_dou);
        box39.setText(box39_str);

        box37_dou = Math.pow((base_value + 2), 2);
        box37_str = String.format("%.2f", box37_dou);
        box37.setText(box37_str);

        box22_dou = Math.pow((base_value + 2.125), 2);
        box22_str = String.format("%.2f", box22_dou);
        box22.setText(box22_str);

        box1_dou = Math.pow((base_value + 2.250), 2);
        box1_str = String.format("%.2f", box1_dou);
        box1.setText(box1_str);

        box4_dou = Math.pow((base_value + 2.375), 2);
        box4_str = String.format("%.2f", box4_dou);
        box4.setText(box4_str);

        box7_dou = Math.pow((base_value + 2.500), 2);
        box7_str = String.format("%.2f", box7_dou);
        box7.setText(box7_str);

        box28_dou = Math.pow((base_value + 2.625), 2);
        box28_str = String.format("%.2f", box28_dou);
        box28.setText(box28_str);

        box49_dou = Math.pow((base_value + 2.750), 2);
        box49_str = String.format("%.2f", box49_dou);
        box49.setText(box49_str);

        box46_dou = Math.pow((base_value + 2.875), 2);
        box46_str = String.format("%.2f", box46_dou);
        box46.setText(box46_str);

        box43_dou = Math.pow((base_value + 3), 2);
        box43_str = String.format("%.2f", box43_dou);
        box43.setText(box43_str);

        if (last_traded_price >= box31_dou && last_traded_price < box23_dou) {
            box30_str = String.format("%.2f", last_traded_price);
            box30.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box30.setText(box30_str);

            dou_tv_buy_above = box23_dou;
            dou_tv_sell_below = box31_dou;

            dou_resistance1 = box9_dou;
            dou_resistance2 = box11_dou;
            dou_resistance3 = box13_dou;
            dou_resistance4 = box27_dou;
            dou_resistance5 = box41_dou;

            dou_support1 = box32_dou;
            dou_support2 = box33_dou;
            dou_support3 = box26_dou;
            dou_support4 = box19_dou;
            dou_support5 = box18_dou;

            dou_buy_abv_stop_loss = box31_dou;
            dou_sell_blw_stop_loss = box23_dou;
        } else {
            box30.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box30.setText("");
        }

        if (last_traded_price >= box23_dou && last_traded_price < box9_dou) {
            box16_str = String.format("%.2f", last_traded_price);
            box16.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box16.setText(box16_str);

            dou_tv_buy_above = box9_dou;
            dou_tv_sell_below = box23_dou;

            dou_resistance1 = box11_dou;
            dou_resistance2 = box13_dou;
            dou_resistance3 = box27_dou;
            dou_resistance4 = box41_dou;
            dou_resistance5 = box39_dou;

            dou_support1 = box31_dou;
            dou_support2 = box32_dou;
            dou_support3 = box33_dou;
            dou_support4 = box26_dou;
            dou_support5 = box19_dou;

            dou_buy_abv_stop_loss = box23_dou;
            dou_sell_blw_stop_loss = box9_dou;
        } else {
            box16.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box16.setText("");
        }

        if (last_traded_price >= box9_dou && last_traded_price < box11_dou) {
            box10_str = String.format("%.2f", last_traded_price);
            box10.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box10.setText(box10_str);

            dou_tv_buy_above = box11_dou;
            dou_tv_sell_below = box9_dou;

            dou_resistance1 = box13_dou;
            dou_resistance2 = box27_dou;
            dou_resistance3 = box41_dou;
            dou_resistance4 = box39_dou;
            dou_resistance5 = box37_dou;

            dou_support1 = box23_dou;
            dou_support2 = box31_dou;
            dou_support3 = box32_dou;
            dou_support4 = box33_dou;
            dou_support5 = box26_dou;

            dou_buy_abv_stop_loss = box9_dou;
            dou_sell_blw_stop_loss = box11_dou;
        } else {
            box10.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box10.setText("");
        }

        if (last_traded_price >= box11_dou && last_traded_price < box13_dou) {
            box12_str = String.format("%.2f", last_traded_price);
            box12.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box12.setText(box12_str);

            dou_tv_buy_above = box13_dou;
            dou_tv_sell_below = box11_dou;

            dou_resistance1 = box27_dou;
            dou_resistance2 = box41_dou;
            dou_resistance3 = box39_dou;
            dou_resistance4 = box37_dou;
            dou_resistance5 = box22_dou;

            dou_support1 = box9_dou;
            dou_support2 = box23_dou;
            dou_support3 = box31_dou;
            dou_support4 = box32_dou;
            dou_support5 = box33_dou;

            dou_buy_abv_stop_loss = box11_dou;
            dou_sell_blw_stop_loss = box13_dou;
        } else {
            box12.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box12.setText("");
        }

        if (last_traded_price >= box13_dou && last_traded_price < box27_dou) {
            box20_str = String.format("%.2f", last_traded_price);
            box20.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box20.setText(box20_str);

            dou_tv_buy_above = box27_dou;
            dou_tv_sell_below = box13_dou;

            dou_resistance1 = box41_dou;
            dou_resistance2 = box39_dou;
            dou_resistance3 = box37_dou;
            dou_resistance4 = box22_dou;
            dou_resistance5 = box1_dou;

            dou_support1 = box11_dou;
            dou_support2 = box9_dou;
            dou_support3 = box22_dou;
            dou_support4 = box31_dou;
            dou_support5 = box32_dou;

            dou_buy_abv_stop_loss = box13_dou;
            dou_sell_blw_stop_loss = box27_dou;
        } else {
            box20.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box20.setText("");
        }

        if (last_traded_price >= box27_dou && last_traded_price < box41_dou) {
            box34_str = String.format("%.2f", last_traded_price);
            box34.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box34.setText(box34_str);

            dou_tv_buy_above = box41_dou;
            dou_tv_sell_below = box27_dou;

            dou_resistance1 = box39_dou;
            dou_resistance2 = box37_dou;
            dou_resistance3 = box22_dou;
            dou_resistance4 = box1_dou;
            dou_resistance5 = box4_dou;

            dou_support1 = box13_dou;
            dou_support2 = box11_dou;
            dou_support3 = box9_dou;
            dou_support4 = box22_dou;
            dou_support5 = box31_dou;

            dou_buy_abv_stop_loss = box27_dou;
            dou_sell_blw_stop_loss = box41_dou;
        } else {
            box34.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box34.setText("");
        }

        if (last_traded_price >= box41_dou && last_traded_price < box39_dou) {
            box40_str = String.format("%.2f", last_traded_price);
            box40.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box40.setText(box40_str);

            dou_tv_buy_above = box39_dou;
            dou_tv_sell_below = box41_dou;

            dou_resistance1 = box37_dou;
            dou_resistance2 = box22_dou;
            dou_resistance3 = box1_dou;
            dou_resistance4 = box4_dou;
            dou_resistance5 = box7_dou;

            dou_support1 = box27_dou;
            dou_support2 = box13_dou;
            dou_support3 = box1_dou;
            dou_support4 = box9_dou;
            dou_support5 = box22_dou;

            dou_buy_abv_stop_loss = box41_dou;
            dou_sell_blw_stop_loss = box39_dou;
        } else {
            box40.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box40.setText("");
        }

        if (last_traded_price >= box39_dou && last_traded_price < box37_dou) {
            box38_str = String.format("%.2f", last_traded_price);
            box38.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.yellow));
            box38.setText(box38_str);

            dou_tv_buy_above = box37_dou;
            dou_tv_sell_below = box39_dou;

            dou_resistance1 = box22_dou;
            dou_resistance2 = box1_dou;
            dou_resistance3 = box4_dou;
            dou_resistance4 = box7_dou;
            dou_resistance5 = box28_dou;


            dou_support1 = box41_dou;
            dou_support2 = box27_dou;
            dou_support3 = box13_dou;
            dou_support4 = box11_dou;
            dou_support5 = box9_dou;

            dou_buy_abv_stop_loss = box39_dou;
            dou_sell_blw_stop_loss = box37_dou;
        } else {
            box38.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
            box38.setText("");
        }

        dou_buy_abv_target1 = dou_resistance1 * 0.9995;
        dou_buy_abv_target2 = dou_resistance2 * 0.9995;
        dou_buy_abv_target3 = dou_resistance3 * 0.9995;
        dou_buy_abv_target4 = dou_resistance4 * 0.9995;
        dou_buy_abv_target5 = dou_resistance5 * 0.9995;

        dou_sell_bel_target1 = dou_support1 * 1.0005;
        dou_sell_bel_target2 = dou_support2 * 1.0005;
        dou_sell_bel_target3 = dou_support3 * 1.0005;
        dou_sell_bel_target4 = dou_support4 * 1.0005;
        dou_sell_bel_target5 = dou_support5 * 1.0005;

        Str_tv_buy_above = String.format("%.2f", dou_tv_buy_above);
        Str_tv_sell_below = String.format("%.2f", dou_tv_sell_below);
        Str_buy_abv_target1 = String.format("%.2f", dou_buy_abv_target1);
        Str_sell_bel_target1 = String.format("%.2f", dou_sell_bel_target1);
        Str_buy_abv_target2 = String.format("%.2f", dou_buy_abv_target2);
        Str_sell_bel_target2 = String.format("%.2f", dou_sell_bel_target2);
        Str_buy_abv_target3 = String.format("%.2f", dou_buy_abv_target3);
        Str_sell_bel_target3 = String.format("%.2f", dou_sell_bel_target3);
        Str_buy_abv_target4 = String.format("%.2f", dou_buy_abv_target4);
        Str_sell_bel_target4 = String.format("%.2f", dou_sell_bel_target4);
        Str_buy_abv_target5 = String.format("%.2f", dou_buy_abv_target5);
        Str_sell_bel_target5 = String.format("%.2f", dou_sell_bel_target5);
        Str_buy_abv_stop_loss = String.format("%.2f", dou_buy_abv_stop_loss);
        Str_sell_blw_stop_loss = String.format("%.2f", dou_sell_blw_stop_loss);

        tv_buy_above.setText(Str_tv_buy_above);
        tv_sell_below.setText(Str_tv_sell_below);
        buy_abv_target1.setText(Str_buy_abv_target1);
        sell_bel_target1.setText(Str_sell_bel_target1);
        buy_abv_target2.setText(Str_buy_abv_target2);
        sell_bel_target2.setText(Str_sell_bel_target2);
        buy_abv_target3.setText(Str_buy_abv_target3);
        sell_bel_target3.setText(Str_sell_bel_target3);
        buy_abv_target4.setText(Str_buy_abv_target4);
        sell_bel_target4.setText(Str_sell_bel_target4);
        buy_abv_target5.setText(Str_buy_abv_target5);
        sell_bel_target5.setText(Str_sell_bel_target5);
        buy_abv_stop_loss.setText(Str_buy_abv_stop_loss);
        sell_blw_stop_loss.setText(Str_sell_blw_stop_loss);

        Str_resistance1 = String.format("%.2f", dou_resistance1);
        Str_resistance2 = String.format("%.2f", dou_resistance2);
        Str_resistance3 = String.format("%.2f", dou_resistance3);
        Str_resistance4 = String.format("%.2f", dou_resistance4);
        Str_resistance5 = String.format("%.2f", dou_resistance5);
        Str_support1 = String.format("%.2f", dou_support1);
        Str_support2 = String.format("%.2f", dou_support2);
        Str_support3 = String.format("%.2f", dou_support3);
        Str_support4 = String.format("%.2f", dou_support4);
        Str_support5 = String.format("%.2f", dou_support5);

        res5.setText(Str_resistance5);
        res4.setText(Str_resistance4);
        res3.setText(Str_resistance3);
        res2.setText(Str_resistance2);
        res1.setText(Str_resistance1);

        sup1.setText(Str_support1);
        sup2.setText(Str_support2);
        sup3.setText(Str_support3);
        sup4.setText(Str_support4);
        sup5.setText(Str_support5);
    }


    private boolean isValid() {
        boolean result = true;
        if (MyValidator.isValidField(Edt_LastTradedPrice)) {
            Edt_LastTradedPrice.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Last Traded Price");
            result = false;
        }
        return result;
    }

    public void OnResetClicked(View view) {
        Edt_LastTradedPrice.setText("");
        tv_buy_above.setText("");
        tv_sell_below.setText("");
        buy_abv_target1.setText("");
        sell_bel_target1.setText("");
        buy_abv_target2.setText("");
        sell_bel_target2.setText("");
        buy_abv_target3.setText("");
        sell_bel_target3.setText("");
        buy_abv_target4.setText("");
        sell_bel_target4.setText("");
        buy_abv_target5.setText("");
        sell_bel_target5.setText("");
        buy_abv_stop_loss.setText("");
        sell_blw_stop_loss.setText("");
        res1.setText("");
        res2.setText("");
        res3.setText("");
        res4.setText("");
        res5.setText("");
        sup1.setText("");
        sup2.setText("");
        sup3.setText("");
        sup4.setText("");
        sup5.setText("");
        box1.setText("");
        box2.setText("");
        box3.setText("");
        box4.setText("");
        box5.setText("");
        box6.setText("");
        box7.setText("");
        box8.setText("");
        box9.setText("");
        box10.setText("");
        box11.setText("");
        box12.setText("");
        box13.setText("");
        box14.setText("");
        box15.setText("");
        box16.setText("");
        box17.setText("");
        box18.setText("");
        box19.setText("");
        box20.setText("");
        box21.setText("");
        box22.setText("");
        box23.setText("");
        box24.setText("");
        box25.setText("");
        box26.setText("");
        box27.setText("");
        box28.setText("");
        box29.setText("");
        box30.setText("");
        box31.setText("");
        box32.setText("");
        box33.setText("");
        box34.setText("");
        box35.setText("");
        box36.setText("");
        box37.setText("");
        box38.setText("");
        box39.setText("");
        box40.setText("");
        box41.setText("");
        box42.setText("");
        box43.setText("");
        box44.setText("");
        box45.setText("");
        box46.setText("");
        box47.setText("");
        box48.setText("");
        box49.setText("");
        box30.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        box16.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        box10.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        box12.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        box20.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        box34.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        box40.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        box38.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
        Edt_LastTradedPrice.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.left_right, R.animator.right_left);
    }
}
