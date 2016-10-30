package com.example.ytseitkin.tojewlist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class register extends Activity {
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private Activity that = this;

    // UI references.
    //private View mProgressView;
    private View regisertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register = (Button) findViewById(R.id.register_user_button);

        regisertView = findViewById(R.id.register_form);

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(false);

                String first = ((EditText) findViewById(R.id.firstName)).getText().toString();

                String last = ((EditText) findViewById(R.id.lastName)).getText().toString();

                String email = ((EditText) findViewById(R.id.email)).getText().toString();

                String username = ((EditText) findViewById(R.id.userName)).getText().toString();

                String password = ((EditText) findViewById(R.id.password)).getText().toString();

                RegisterUser reg = new RegisterUser(first, last,email, username, password);

                reg.execute();
            }
        });
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            regisertView.setVisibility(show ? View.GONE : View.VISIBLE);
            regisertView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    regisertView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            //mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            regisertView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RegisterUser extends AsyncTask<Void, Void, Boolean> {

        private String first;
        private String last;
        private String email;
        private String username;
        private String password;

        private int id;
        private boolean existsError = false;

        public RegisterUser(String first, String last,String email,String username,String password) {
            this.first = first;
            this.last = last;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("http://www.yuzmanim.com/ToJewLists/create-user.php");

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            //zachweix    qwerty

            nameValuePair.add(new BasicNameValuePair("first_name",first ));
            nameValuePair.add(new BasicNameValuePair("last_name",last ));
            nameValuePair.add(new BasicNameValuePair("username",username ));
            nameValuePair.add(new BasicNameValuePair("password",password ));
            nameValuePair.add(new BasicNameValuePair("email",email ));


            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                String line="";
                String data="";
                try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                    while((line=br.readLine())!=null){

                        data+=line;
                    }

                    JSONObject obj = new JSONObject(data);

                    if(obj.getInt("code")==409){
                        existsError = true;
                        return false;
                    }

                    if(obj.getInt("code")!=201)
                        return false;

                    id = obj.getInt("id");

                    Log.i("RESPONSE", data.length() + "");
                }
                catch(Exception e){
                    return false;
                }

                return true;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                Intent intent = new Intent(that,Occasions.class);

                intent.putExtra("name",first);

                intent.putExtra("id",id);

                startActivity(intent);
            } else {
                if(existsError)
                    Toast.makeText(that,"User name/email already exists",Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(that,"Something went wrong",Toast.LENGTH_LONG).show();
            }
        }
    }
}

