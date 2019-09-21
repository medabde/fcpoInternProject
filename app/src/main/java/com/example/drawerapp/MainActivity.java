package com.example.drawerapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.drawerapp.Login.sharedPref;
import static java.lang.System.exit;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static FloatingActionButton fab;
    public static ListView listView;
    public static CustomAdapter adapter;
    public static ArrayList<Ticket> tickets= new ArrayList<>();
    public static int id101;
    public static FTPClientFunctions ftpclient=new FTPClientFunctions();


    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }



        if (connected) new RetrieveData().execute();
        else Toast.makeText(this, "NO CONNECTION ...", Toast.LENGTH_SHORT).show();




        listView=findViewById(R.id.ticket_list);

        adapter= new CustomAdapter(this,tickets);


        listView.setAdapter(adapter);
        listView.setDividerHeight(5);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent myIntent = new Intent(MainActivity.this, ModifyText.class);
                startActivity(myIntent);
                id101 = i;
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final int b = i;

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                int id = (int) adapterView.getItemIdAtPosition(b);
                                new delete().execute(tickets.get(id).getId() + "", id + "");
                                new deletePic().execute(tickets.get(id).getPicURL());
                                tickets.remove(id);
                                adapter.notifyDataSetChanged();
                                break;


                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to delete this ticket?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


                return true;
            }
        });
        fab = findViewById(R.id.fab);
        fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_create_black_24dp));


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                if (connected){
                    Intent myIntent = new Intent(MainActivity.this, TakePhoto.class);
                    startActivity(myIntent);
                }else Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();


            }
        });



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }








    public class RetrieveData extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("https://tickets.fcpo.ma/phpAPI/ticket/getAllTickets.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    Ticket ticket=new Ticket();
                    ticket.setId(arr.getJSONObject(i).getInt("idTicket"));
                    ticket.setIdUser(arr.getJSONObject(i).getInt("idUser"));
                    ticket.setName(arr.getJSONObject(i).getString("nom"));
                    ticket.setType(arr.getJSONObject(i).getString("type"));
                    String date=arr.getJSONObject(i).getString("date");
                    SimpleDateFormat mydate=new SimpleDateFormat("yyyy-MM-dd");
                    ticket.setPicURL(arr.getJSONObject(i).getString("picPath"));
                    ticket.setDate(mydate.parse(date));
                    ticket.setPrix(Double.valueOf(arr.getJSONObject(i).getString("price").trim()));


                    if(ticket.getIdUser()==sharedPref.getInt("idUser",-1)){
                        tickets.add(ticket);
                    }
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }





    public class delete extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {

            try {

                URL url = new URL("https://tickets.fcpo.ma/phpAPI/ticket/removeTicket.php?idTicket="+urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.isEmpty()) Toast.makeText(MainActivity.this, "Ticket Deleted", Toast.LENGTH_SHORT).show();
            else Toast.makeText(MainActivity.this, "ERROR while deleted ticket", Toast.LENGTH_SHORT).show();
        }
    }




    public class deletePic extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {

            try {
                final String path=urls[0];

                new Thread(new Runnable() {
                    public void run() {
                        boolean status = false;
                        status = ftpclient.ftpConnect("ftp.fcpo.ma", "tickets@fcpo.ma", "FCPO2019@", 21);
                        ftpclient.ftpRemoveFile(path);
                        if (status) {
                            Log.d(TAG, "Connection Success");
                        } else {
                            Log.d(TAG, "Connection failed");
                        }
                    }
                }).start();

            } catch(Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }

            return "done";
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(MainActivity.this, "deleted", Toast.LENGTH_SHORT).show();
        }
    }











}
