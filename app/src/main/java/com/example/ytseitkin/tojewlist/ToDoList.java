package com.example.ytseitkin.tojewlist;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ToDoList extends AppCompatActivity {

    private int id;
    private int occ_id;
    private String name;

    private ListView list;
    private ToDoAdapter ada;
    private ArrayList <ToDoItem> items;
    private View toDoView;

    private ToDoItem chosenItem;

    Activity that = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        id = getIntent().getExtras().getInt("id");
        occ_id = getIntent().getExtras().getInt("occ_id");
        name = getIntent().getExtras().getString("name");

        ((TextView)findViewById(R.id.header_todolist)).setText(name);

        toDoView = findViewById(R.id.toDoListView);

        items = new ArrayList<>();
        ada = new ToDoAdapter(this,items);

        list = (ListView) findViewById(R.id.toDoListView);

        registerForContextMenu(list);

        list.setAdapter(ada);

        Button add = (Button) findViewById(R.id.AddItem);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String details = ((EditText) findViewById(R.id.content)).getText().toString();

                if(details.equals("")){
                    Toast.makeText(that,"Item can't be empty",Toast.LENGTH_LONG).show();
                    return;
                }

                showProgress(true);

                AddItem add = new AddItem(id,occ_id,details);

                add.execute();
            }
        });

        getToDoList toDoList = new getToDoList(id,occ_id);

        toDoList.execute();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        ListView lv = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        chosenItem = (ToDoItem) lv.getItemAtPosition(acmi.position);


        menu.setHeaderTitle("Choose Action");
        menu.add("Mark as done for the week");
        menu.add("Delete permanently");
        menu.add("Setup reminder");
    }

    public boolean onContextItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(item.getTitle().equals("Mark as done for the week")){
            MarkAsDoneForWeek markAsDoneForWeek = new MarkAsDoneForWeek(id,chosenItem);
            showProgress(true);
            markAsDoneForWeek.execute();
        } else if(item.getTitle().equals("Delete permanently")){
            DeleteForever deleteForever = new DeleteForever(id,chosenItem);
            showProgress(true);
            deleteForever.execute();
        } else {
            Intent intent = new Intent(that,SetupReminder.class);
            intent.putExtra("name",chosenItem.getDetails());
            startActivity(intent);
        }

        return true;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            toDoView.setVisibility(show ? View.GONE : View.VISIBLE);
            toDoView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toDoView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            // mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//           // mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//             //       mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            //mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            toDoView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class getToDoList extends AsyncTask<Void, Void, Boolean> {

        private int id;
        private int occ_id;

        getToDoList(int id,int occ_id) {
            this.id=id;
            this.occ_id = occ_id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("http://www.yuzmanim.com/ToJewLists/get-list.php");

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            //zachweix    qwerty

            nameValuePair.add(new BasicNameValuePair("user_id", Integer.toString(id)));
            nameValuePair.add(new BasicNameValuePair("occasion_id",Integer.toString(occ_id) ));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                return parseResponseForItems(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        }

        public boolean parseResponseForItems( HttpResponse response){

            String line="";
            String data="";
            try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                while((line=br.readLine())!=null){

                    data+=line;
                }
                JSONObject obj = new JSONObject(data);

                if(obj.getInt("code")!=200)
                    return false;

                JSONArray arr = obj.getJSONArray("list");

                for (int i=0;i<arr.length();i++){
                    JSONObject singleToDoItem = arr.getJSONObject(i);

                    items.add(new ToDoItem(singleToDoItem.getString("details"),(singleToDoItem.isNull("master_id") ? 0 : singleToDoItem.getInt("master_id")),
                            (singleToDoItem.isNull("list_id") ? 0 : singleToDoItem.getInt("list_id")),singleToDoItem.getString("list_type")));

                    Log.i("RESPONSE" ,"ride: " + singleToDoItem );
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

    public class MarkAsDoneForWeek extends AsyncTask<Void, Void, Boolean> {

        private int id;
        private ToDoItem item;

        MarkAsDoneForWeek(int id,ToDoItem item) {
            this.id=id;
            this.item = item;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("http://www.yuzmanim.com/ToJewLists/set-finished.php");

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            //zachweix    qwerty

            nameValuePair.add(new BasicNameValuePair("user_id", Integer.toString(id)));
            nameValuePair.add(new BasicNameValuePair("list_type",item.getList_type()));
            nameValuePair.add(new BasicNameValuePair("master_id",item.getMaster_id() + "" ));
            nameValuePair.add(new BasicNameValuePair("list_id",item.getList_id() + "" ));
            nameValuePair.add(new BasicNameValuePair("status", 1 + ""));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                return parseResponseForItems(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        }

        public boolean parseResponseForItems( HttpResponse response){

            String line="";
            String data="";
            try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                while((line=br.readLine())!=null){

                    data+=line;
                }
                JSONObject obj = new JSONObject(data);

                return obj.getInt("code")==200;
            }
            catch(Exception e){
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);

            if (success) {

                finish();
                startActivity(getIntent());

            } else {
                Toast.makeText(that,"Something went wrong",Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    public class DeleteForever extends AsyncTask<Void, Void, Boolean> {

        private int id;
        private ToDoItem item;

        DeleteForever(int id,ToDoItem item) {
            this.id=id;
            this.item = item;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("http://www.yuzmanim.com/ToJewLists/set-active.php");

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            //zachweix    qwerty

            nameValuePair.add(new BasicNameValuePair("user_id", Integer.toString(id)));
            nameValuePair.add(new BasicNameValuePair("list_type",item.getList_type()));
            nameValuePair.add(new BasicNameValuePair("master_id",item.getMaster_id() + "" ));
            nameValuePair.add(new BasicNameValuePair("list_id",item.getList_id() + "" ));
            nameValuePair.add(new BasicNameValuePair("status", 0 + ""));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                return parseResponseForItems(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        }

        public boolean parseResponseForItems( HttpResponse response){

            String line="";
            String data="";
            try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                while((line=br.readLine())!=null){

                    data+=line;
                }
                JSONObject obj = new JSONObject(data);

                return obj.getInt("code")==200;

            }
            catch(Exception e){
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);

            if (success) {

                finish();
                startActivity(getIntent());

            } else {
                Toast.makeText(that,"Something went wrong",Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }

    public class AddItem extends AsyncTask<Void, Void, Boolean> {

        private int id;
        private int occ_id;
        private String details;

        AddItem(int id,int occ_id,String details) {
            this.id=id;
            this.occ_id = occ_id;
            this.details = details;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // replace with your url
            HttpPost httpPost = new HttpPost("http://www.yuzmanim.com/ToJewLists/create-item.php");

            //Post Data
            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();

            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();

            //zachweix    qwerty

            nameValuePair.add(new BasicNameValuePair("user_id", Integer.toString(id)));
            nameValuePair.add(new BasicNameValuePair("occasion_id",Integer.toString(occ_id) ));
            nameValuePair.add(new BasicNameValuePair("details",details ));

            //Encoding POST data
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
            } catch (UnsupportedEncodingException e) {
                // log exception
                return false;
            }

            try {
                HttpResponse response = httpClient.execute(httpPost);

                return parseResponseForItems(response);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }

        }

        public boolean parseResponseForItems( HttpResponse response){

            String line="";
            String data="";
            try(BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()))){

                while((line=br.readLine())!=null){

                    data+=line;
                }
                JSONObject obj = new JSONObject(data);

                return obj.getInt("code")==201;
            }
            catch(Exception e){
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);

            if (success) {

                finish();
                startActivity(getIntent());

            } else {
                Toast.makeText(that,"Something went wrong",Toast.LENGTH_LONG).show();
                finish();
            }
        }

    }
}
