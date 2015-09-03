package com.mapplinks.rdcalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    EditText installment;
    EditText duration;
    EditText interest;
    TextView result;
    Button calc, reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        installment = (EditText) findViewById(R.id.installment);
        duration = (EditText) findViewById(R.id.duration);
        interest = (EditText) findViewById(R.id.interest);
        result = (TextView) findViewById(R.id.result);

        calc = (Button) findViewById(R.id.Calculate);
        reset = (Button) findViewById(R.id.Reset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetValues();
            }
        });

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (errorCheck() == 0)
                calculate();
            }
        });
    }

    public int errorCheck() {
        String s1, s2, s3;
        s1 = installment.getText().toString();
        s2 =duration.getText().toString();
        s3=interest.getText().toString();

        if(s1.length()==0 || s2.length()==0 || s3.length()==0){
            Toast.makeText(MainActivity.this, "Enter All the fields!", Toast.LENGTH_SHORT).show();
            resetValues();
            return 1;
        }else if((Integer.parseInt(s2)%3)!=0){
            Toast.makeText(MainActivity.this, "Months aren't a multiple of 3!\nEnter again!", Toast.LENGTH_SHORT).show();
            resetValues();
            return 2;
        }else {
            return 0;
        }
    }

    public void resetValues(){
        installment.setText(null);
        duration.setText(null);
        interest.setText(null);
        result.setText(null);
    }

    private void calculate() {
        int R = Integer.parseInt(installment.getText().toString());
        int n = Integer.parseInt(duration.getText().toString()) / 3;
        double r = Double.parseDouble(interest.getText().toString()) / 400.0;
        double M;
        int Margin, Interest;
        M = (R * ((Math.pow(1.0 + r, n) - 1.0)) / (1.0 - Math.pow(1.0 + r, (-1.0 / 3.0))));
        Margin = (int) Math.round(M);
        Interest = Margin - R*n*3;
        result.setText("Margin:\n" + Margin + "\n\nInterest:\n" + Interest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.feedback) {
            Intent i = new Intent(MainActivity.this, FeedbackActivity.class);
            MainActivity.this.startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
