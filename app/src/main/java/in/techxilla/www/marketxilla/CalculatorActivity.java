package in.techxilla.www.marketxilla;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity {

    TextView tv_title;
    ImageView iv_back;
    TextView gann_cart_9,classic_pivot,fibaonacci_pivot,camerilla_pivot,forex_pivot,woddie_pivot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        init();

    }

    private void init() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.back_btn_toolbar);

        tv_title.setText(getResources().getString(R.string.trade_calculators));
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gann_cart_9 = (TextView)findViewById(R.id.gann_cart_9);

        classic_pivot  = (TextView)findViewById(R.id.classic_pivot);
        fibaonacci_pivot = (TextView)findViewById(R.id.fibaonacci_pivot);
        camerilla_pivot = (TextView)findViewById(R.id.camerilla_pivot);
        forex_pivot = (TextView)findViewById(R.id.forex_pivot);
        woddie_pivot = (TextView)findViewById(R.id.woddie_pivot);

        gann_cart_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("calc",gann_cart_9.getText().toString());
                Intent intent = new Intent(getApplicationContext(),GannCartCalc.class);
                intent.putExtras(bundle);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                startActivity(intent);
            }
        });

        classic_pivot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("calc",classic_pivot.getText().toString());
                Intent intent = new Intent(getApplicationContext(),PivotCalc.class);
                intent.putExtras(bundle);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                startActivity(intent);

            }
        });


        fibaonacci_pivot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("calc",fibaonacci_pivot.getText().toString());
                Intent intent = new Intent(getApplicationContext(),PivotCalc.class);
                intent.putExtras(bundle);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                startActivity(intent);

            }
        });

        camerilla_pivot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("calc",camerilla_pivot.getText().toString());
                Intent intent = new Intent(getApplicationContext(),PivotCalc.class);
                intent.putExtras(bundle);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                startActivity(intent);

            }
        });

        forex_pivot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("calc",forex_pivot.getText().toString());
                Intent intent = new Intent(getApplicationContext(),PivotCalc.class);
                intent.putExtras(bundle);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                startActivity(intent);

            }
        });

        woddie_pivot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("calc",woddie_pivot.getText().toString());
                Intent intent = new Intent(getApplicationContext(),PivotCalc.class);
                intent.putExtras(bundle);
                overridePendingTransition(R.animator.move_left,R.animator.move_right);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), NewDashboard.class);
        startActivity(i);
        overridePendingTransition(R.animator.left_right,R.animator.right_left);
        finish();
    }
}
