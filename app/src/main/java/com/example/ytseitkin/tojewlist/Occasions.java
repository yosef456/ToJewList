package com.example.ytseitkin.tojewlist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Occasions extends AppCompatActivity {

    private ProgressBar mProgressView;
    private View occasionsView;
    private OccasionsAdapter ada;
    private ArrayList occ;
    private ListView list;

    private Activity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occasions);

        mProgressView = (ProgressBar) findViewById(R.id.progress);

        occasionsView =  findViewById(R.id.occasionsView);

        occ = new ArrayList();

        ada = new OccasionsAdapter(this,occ);

        list = (ListView) findViewById(R.id.occList);

        list.setAdapter(ada);

        showProgress(true);

        getOccasions get = new getOccasions();

        get.execute();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            occasionsView.setVisibility(show ? View.GONE : View.VISIBLE);
            occasionsView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    occasionsView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            occasionsView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class getOccasions extends AsyncTask<Void, Void, Boolean> {

        getOccasions() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://www.yuzmanim.com/ToJewLists/get-occasions.php");
            // replace with your url

            HttpResponse response;
            try {
                response = client.execute(request);

                Log.d("Response of GET request", response.toString());

                return parseResponseForOcc(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
        }

        public boolean parseResponseForOcc( HttpResponse response){

            String line="";
            String data="";
            try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                while((line=br.readLine())!=null){

                    data+=line;
                }
                JSONObject obj = new JSONObject(data);

                if(obj.getInt("code")!=200)
                    return false;

                JSONArray arr = obj.getJSONArray("occasions");

                for (int i=0;i<arr.length();i++){
                    JSONObject singleOcc = arr.getJSONObject(i);

                    occ.add(new Occasion(singleOcc.getInt("id"),singleOcc.getString("occasion")));

                    Log.i("RESPONSE" ,"ride: " + singleOcc );
                }
            }
            catch(Exception e){
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);

            if (success) {

                ada.notifyDataSetChanged();

            } else {
                Toast.makeText(that,"Something went wrong",Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }
}
