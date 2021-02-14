package in.techxilla.www.marketxilla;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.techxilla.www.marketxilla.utils.CommonMethods;
import in.techxilla.www.marketxilla.utils.MyValidator;

public class PivotCalc extends AppCompatActivity {

    String CalcName = "";
    TextView tv_title;
    ImageView iv_back;
    EditText Edt_PreviousHigh, Edt_PreviousLow, Edt_PreviousClose;
    Button btn_Calc;
    TableLayout result_layout;
    TableRow resistance4, resistance3, resistance2, resistance1, pivot_point, support1, support2, support3, support4;
    TextView tv_resistance4, tv_resistance3, tv_resistance2, tv_resistance1, tv_pivot_point, tv_support1, tv_support2, tv_support3, tv_support4;

    String StrPreviousHigh, StrPreviousLow, StrPreviousClose;
    double previous_high = 0.0, previous_low = 0.0, previous_close = 0.0;
    String Str_resistance4, Str_resistance3, Str_resistance2, Str_resistance1, Str_pivot_point, Str_support1, Str_support2, Str_support3, Str_support4;
    double dou_resistance4 = 0.0, dou_resistance3 = 0.0, dou_resistance2 = 0.0, dou_resistance1 = 0.0, dou_pivot_point = 0.0, dou_support1 = 0.0, dou_support2 = 0.0, dou_support3 = 0.0, dou_support4 = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pivot_calculator);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            CalcName = bundle.getString("calc");
        }
        init();

    }

    private void init() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.back_btn_toolbar);

        tv_title.setText(CalcName);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Edt_PreviousHigh = (EditText) findViewById(R.id.Edt_PreviousHigh);
        Edt_PreviousLow = (EditText) findViewById(R.id.Edt_PreviousLow);
        Edt_PreviousClose = (EditText) findViewById(R.id.Edt_PreviousClose);

        btn_Calc = (Button) findViewById(R.id.btn_Calc);

        result_layout = (TableLayout) findViewById(R.id.result_layout);
        resistance4 = (TableRow) findViewById(R.id.resistance4);
        resistance3 = (TableRow) findViewById(R.id.resistance3);
        resistance2 = (TableRow) findViewById(R.id.resistance2);
        resistance1 = (TableRow) findViewById(R.id.resistance1);
        pivot_point = (TableRow) findViewById(R.id.pivot_point);
        support1 = (TableRow) findViewById(R.id.support1);
        support2 = (TableRow) findViewById(R.id.support2);
        support3 = (TableRow) findViewById(R.id.support3);
        support4 = (TableRow) findViewById(R.id.support4);

        tv_resistance4 = (TextView) findViewById(R.id.tv_resistance4);
        tv_resistance3 = (TextView) findViewById(R.id.tv_resistance3);
        tv_resistance2 = (TextView) findViewById(R.id.tv_resistance2);
        tv_resistance1 = (TextView) findViewById(R.id.tv_resistance1);
        tv_pivot_point = (TextView) findViewById(R.id.tv_pivot_point);
        tv_support1 = (TextView) findViewById(R.id.tv_support1);
        tv_support2 = (TextView) findViewById(R.id.tv_support2);
        tv_support3 = (TextView) findViewById(R.id.tv_support3);
        tv_support4 = (TextView) findViewById(R.id.tv_support4);

        btn_Calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Edt_PreviousClose.getWindowToken(), 0);
                    StrPreviousHigh = Edt_PreviousHigh.getText().toString().trim();
                    StrPreviousLow = Edt_PreviousLow.getText().toString().trim();
                    StrPreviousClose = Edt_PreviousClose.getText().toString().trim();

                    previous_high = StrPreviousHigh != null ? Double.parseDouble(StrPreviousHigh) : 0.0;
                    previous_low = StrPreviousLow != null ? Double.parseDouble(StrPreviousLow) : 0.0;
                    previous_close = StrPreviousClose != null ? Double.parseDouble(StrPreviousClose) : 0.0;

                    if (CalcName != null && CalcName.equalsIgnoreCase("CLASSIC / FLOOR PIVOT")) {
                        ClassicPivotCalc();
                    } else if (CalcName != null && CalcName.equalsIgnoreCase("FIBONACCI PIVOT")) {
                        FibonacciPivotCalc();
                    } else if (CalcName != null && CalcName.equalsIgnoreCase("CAMERILLA PIVOT")) {
                        CamerillaPivotCalc();
                    }/* else if (CalcName != null && CalcName.equalsIgnoreCase("DEMARK's PIVOT")) {
                        DemarkPivotCalc();
                    }*/ else if (CalcName != null && CalcName.equalsIgnoreCase("WOODIE's PIVOT")) {
                        WoodiePivotCalc();
                    }
                }
            }
        });

        Edt_PreviousClose.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (isValid()) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(Edt_PreviousClose.getWindowToken(), 0);
                    StrPreviousHigh = Edt_PreviousHigh.getText().toString().trim();
                    StrPreviousLow = Edt_PreviousLow.getText().toString().trim();
                    StrPreviousClose = Edt_PreviousClose.getText().toString().trim();

                    previous_high = StrPreviousHigh != null ? Double.parseDouble(StrPreviousHigh) : 0.0;
                    previous_low = StrPreviousLow != null ? Double.parseDouble(StrPreviousLow) : 0.0;
                    previous_close = StrPreviousClose != null ? Double.parseDouble(StrPreviousClose) : 0.0;

                    if (CalcName != null && CalcName.equalsIgnoreCase("CLASSIC / FLOOR PIVOT")) {
                        ClassicPivotCalc();
                    } else if (CalcName != null && CalcName.equalsIgnoreCase("FIBONACCI PIVOT")) {
                        FibonacciPivotCalc();
                    } else if (CalcName != null && CalcName.equalsIgnoreCase("CAMERILLA PIVOT")) {
                        CamerillaPivotCalc();
                    }/* else if (CalcName != null && CalcName.equalsIgnoreCase("DEMARK's PIVOT")) {
                        DemarkPivotCalc();
                    }*/ else if (CalcName != null && CalcName.equalsIgnoreCase("WOODIE's PIVOT")) {
                        WoodiePivotCalc();
                    }
                }
                return false;
            }
        });

    }

    private void WoodiePivotCalc() {

        dou_pivot_point = (previous_high + previous_low + 2 * previous_close) / 4;

        dou_resistance1 = (2 * dou_pivot_point) - previous_low;
        dou_resistance2 = dou_pivot_point + (previous_high - previous_low);
        dou_resistance3 = previous_high + 2 * (dou_pivot_point - previous_low);

        dou_support1 = (2 * dou_pivot_point) - previous_high;
        dou_support2 = dou_pivot_point - (previous_high - previous_low);
        dou_support3 = previous_low - 2 * (previous_high - dou_pivot_point);

        setResultToView(false);
    }

    private void DemarkPivotCalc() {

        dou_pivot_point = (previous_high + previous_close + previous_low) / 3;

        dou_resistance1 = (2 * dou_pivot_point) - previous_low;
        dou_resistance2 = dou_pivot_point + (previous_high - previous_low);
        dou_resistance3 = previous_high + 2 * (dou_pivot_point - previous_low);

        dou_support1 = (2 * dou_pivot_point) - previous_high;
        dou_support2 = dou_pivot_point - (previous_high - previous_low);
        dou_support3 = previous_low - 2 * (previous_high - dou_pivot_point);

        setResultToView(false);
    }

    private void CamerillaPivotCalc() {

        dou_pivot_point = (previous_high + previous_close + previous_low) / 3;

        dou_resistance1 = previous_close + (previous_high - previous_low) * 1.1 / 12;
        dou_resistance2 = previous_close + (previous_high - previous_low) * 1.1 / 6;
        dou_resistance3 = previous_close + (previous_high - previous_low) * 1.1 / 4;
        dou_resistance4 = previous_close + (previous_high - previous_low) * 1.1 / 2;

        dou_support1 = previous_close - (previous_high - previous_low) * 1.1 / 12;
        dou_support2 = previous_close - (previous_high - previous_low) * 1.1 / 6;
        dou_support3 = previous_close - (previous_high - previous_low) * 1.1 / 4;
        dou_support4 = previous_close - (previous_high - previous_low) * 1.1 / 2;

        setResultToView(true);

    }

    private void FibonacciPivotCalc() {

        dou_pivot_point = (previous_high + previous_close + previous_low) / 3;

        dou_resistance1 = dou_pivot_point + 0.382 * (previous_high - previous_low);
        dou_resistance2 = dou_pivot_point + 0.618 * (previous_high - previous_low);
        dou_resistance3 = dou_pivot_point + 1 * (previous_high - previous_low);

        dou_support1 = dou_pivot_point - 0.382 * (previous_high - previous_low);
        dou_support2 = dou_pivot_point - 0.618 * (previous_high - previous_low);
        dou_support3 = dou_pivot_point - 1 * (previous_high - previous_low);

        setResultToView(false);

    }

    private void ClassicPivotCalc() {

        dou_pivot_point = (previous_high + previous_close + previous_low) / 3;

        dou_resistance1 = (2 * dou_pivot_point) - previous_low;
        dou_resistance2 = dou_pivot_point + (previous_high - previous_low);
        dou_resistance3 = previous_high + 2 * (dou_pivot_point - previous_low);

        dou_support1 = (2 * dou_pivot_point) - previous_high;
        dou_support2 = dou_pivot_point - (previous_high - previous_low);
        dou_support3 = previous_low - 2 * (previous_high - dou_pivot_point);

        setResultToView(false);

    }

    public void setResultToView(boolean show4thRes_Sup) {

        Str_pivot_point = String.format("%.2f", dou_pivot_point);

        Str_resistance1 = String.format("%.2f", dou_resistance1);
        Str_resistance2 = String.format("%.2f", dou_resistance2);
        Str_resistance3 = String.format("%.2f", dou_resistance3);


        Str_support1 = String.format("%.2f", dou_support1);
        Str_support2 = String.format("%.2f", dou_support2);
        Str_support3 = String.format("%.2f", dou_support3);

        if(show4thRes_Sup){
            Str_resistance4 = String.format("%.2f", dou_resistance4);
            Str_support4 = String.format("%.2f", dou_support4);
        }else {
            Str_resistance4 = "";
            Str_support4 = "";

        }


        if (Str_resistance4 != null && !Str_resistance4.equalsIgnoreCase("")) {
            tv_resistance4.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_resistance4));
            resistance4.setVisibility(View.VISIBLE);
        } else {
            resistance4.setVisibility(View.GONE);
        }

        tv_resistance3.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_resistance3));
        tv_resistance2.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_resistance2));
        tv_resistance1.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_resistance1));
        tv_pivot_point.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_pivot_point));
        tv_support1.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_support1));
        tv_support2.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_support2));
        tv_support3.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_support3));

        if (Str_support4 != null && !Str_support4.equalsIgnoreCase("")) {
            tv_support4.setText(CommonMethods.DecimalNumberDisplayFormattingWithComma(Str_support4));
            support4.setVisibility(View.VISIBLE);
        } else {
            support4.setVisibility(View.GONE);
        }
    }


    private boolean isValid() {

        boolean result = true;

        if (!MyValidator.isValidField(Edt_PreviousHigh)) {
            Edt_PreviousHigh.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Previous High");
            result = false;
        }

        if (!MyValidator.isValidField(Edt_PreviousLow)) {
            Edt_PreviousLow.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Previous Low");
            result = false;
        }

        if (!MyValidator.isValidField(Edt_PreviousClose)) {
            Edt_PreviousClose.requestFocus();
            CommonMethods.DisplayToastWarning(getApplicationContext(), "Please Enter Previous Close");
            result = false;
        }
        return result;
    }

    public void OnResetClicked(View view) {
        Edt_PreviousHigh.setText("");
        Edt_PreviousLow.setText("");
        Edt_PreviousClose.setText("");
        tv_resistance4.setText("");
        tv_resistance3.setText("");
        tv_resistance2.setText("");
        tv_resistance1.setText("");
        tv_pivot_point.setText("");
        tv_support1.setText("");
        tv_support2.setText("");
        tv_support3.setText("");
        tv_support4.setText("");
        Edt_PreviousHigh.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.left_right, R.animator.right_left);

    }
}
