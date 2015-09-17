package com.mapplinks.rdcalculator;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    Button submit;
    Intent intent = getIntent();

    EditText name,email,feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feedback);

        submit=(Button) findViewById(R.id.submit_feedback);

        name=(EditText)findViewById(R.id.name_add);
        email=(EditText)findViewById(R.id.email_add);
        feedback=(EditText)findViewById(R.id.feedback);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = name.getText().toString();
                String fEmail = email.getText().toString();
                String fFeedback = feedback.getText().toString();
                if (isOnline()) {
                    new SendEmail(getApplicationContext()).execute(fName, fEmail, fFeedback);

                    Toast.makeText(FeedbackActivity.this, "Thankyou!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(FeedbackActivity.this, "The internet doesn't seem to be working!", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }

    public boolean isOnline(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


    public class SendEmail extends AsyncTask<String, Void, String> {
        private Context context;

        SendEmail(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://docs.google.com/forms/d/1sfW-XMvjK5LYFNzIs4WmEQ2SpUoK276N8jRBYsZu2qo/formResponse");

            //if (context != null) {
            try {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("entry.1829229446", params[0]));
                pairs.add(new BasicNameValuePair("entry.592808657", params[1]));
                pairs.add(new BasicNameValuePair("entry.198154210", params[2]));

                httppost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject json_object = new JSONObject(result);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return null;
            //}
            // return null;
        }

        protected void onPostExecute(String msgs) {
            super.onPostExecute(msgs);
            if (msgs != null && context != null)
                Toast.makeText(FeedbackActivity.this, "Message Sent!", Toast.LENGTH_SHORT).show();
        }
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
        if (id == R.id.rate) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.mapplinks.rdcalculator"));
            if (!MyStartActivity(intent)) {
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?[Id]"));
                if (!MyStartActivity(intent)) {
                    Toast.makeText(this, "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                }
            }
        }else if (id == R.id.share) {
            shareTextUrl();
        }

        return super.onOptionsItemSelected(item);
    }
}
