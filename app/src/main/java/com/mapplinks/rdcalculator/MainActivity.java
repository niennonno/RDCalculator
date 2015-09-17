package com.mapplinks.rdcalculator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
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

        com.mapplinks.rdcalculator.FontChangeCrawler fontChanger = new com.mapplinks.rdcalculator.FontChangeCrawler(getAssets(), "gotham_light.otf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

       SpannableString s = new SpannableString("RD Calculator");
        s.setSpan(new TypefaceSpan("gotham_bold.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Update the action bar title with the TypefaceSpan instance

        getSupportActionBar().setTitle(s);

        String projectToken = "b4648ca2c9a2c23d96630b6690f6d6b3";
        final MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, projectToken);

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
                mixpanel.track("Calculate");

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if (errorCheck() == 0)
                    calculate();
            }
        });
    }

    public int errorCheck() {
        String s1, s2, s3;
        s1 = installment.getText().toString();
        s2 = duration.getText().toString();
        s3 = interest.getText().toString();

        if (s1.length() == 0 || s2.length() == 0 || s3.length() == 0) {
            Toast.makeText(MainActivity.this, "Enter All the fields!", Toast.LENGTH_SHORT).show();

            return 1;
        } else if ((Integer.parseInt(s2) % 3) != 0) {
            Toast.makeText(MainActivity.this, "Months aren't a multiple of 3!\nEnter again!", Toast.LENGTH_SHORT).show();

            return 2;
        } else if (Integer.parseInt(s1) <500) {
            Toast.makeText(MainActivity.this, "Minimum Installment should be Rs.500.", Toast.LENGTH_SHORT).show();
            return 3;
        } else if (Integer.parseInt(s2) == 0) {
            Toast.makeText(MainActivity.this, "You need patience to reap your savings!", Toast.LENGTH_SHORT).show();
            return 4;
        } else if (Float.parseFloat(s3) == 0.0) {
            Toast.makeText(MainActivity.this, "Interest should be more than zero.", Toast.LENGTH_SHORT).show();
            return 5;
        } else {
            return 0;
        }
    }

    public void resetValues() {
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
        int maturity, Interest;
        M = (R * ((Math.pow(1.0 + r, n) - 1.0)) / (1.0 - Math.pow(1.0 + r, (-1.0 / 3.0))));
        maturity = (int) Math.round(M);
        Interest = maturity - R * n * 3;
        if (maturity == 0) {
            Interest = 0;
        }
        result.setText("Maturity Amount:\n" + maturity + "\n\nInterest Amount:\n" + Interest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;


    }

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Recurring Deposit Calculator ");
        share.putExtra(Intent.EXTRA_TEXT, "I am using RD Calculator to improve my #sales efficiency via @Mapplinks. Find it here: https://goo.gl/a3fQRp");

        startActivity(Intent.createChooser(share, "Spread the Word!"));
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
        } else if (id == R.id.rate) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.mapplinks.rdcalculator"));
            if (!MyStartActivity(intent)) {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?[Id]"));
                if (!MyStartActivity(intent)) {
                    Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.share) {
            shareTextUrl();
        }

        return super.onOptionsItemSelected(item);
    }
}
