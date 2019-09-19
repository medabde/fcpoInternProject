package com.example.drawerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.drawerapp.MainActivity.tickets;
import static java.lang.System.exit;

public class Login extends AppCompatActivity {

    private String usernameCheck = "";
    private String passwordCheck = "";
    int res;
    String res1;

    EditText username,password;
    Button login;
    ProgressBar progressBar;
    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username =findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

        tickets.clear();

        if (isLoggedIn){
            Intent mainIntent = new Intent(this,MainActivity.class);
            this.startActivity(mainIntent);
            this.finish();
        }

    }

    public void login(View v){
        if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty())
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        else {

            usernameCheck=username.getText().toString();
            passwordCheck=password.getText().toString();

            ConnectivityManager connectivityManager =
                    (ConnectivityManager)Login.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo!=null){
                progressBar.setVisibility(View.VISIBLE);
                new RetrieveData().execute(usernameCheck,passwordCheck);
            }
            else Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();


        }
    }

    public void register(View v){
        Intent mainIntent = new Intent(this, SignUp.class);
        this.startActivity(mainIntent);
        this.finish();
    }




    public class RetrieveData extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("https://tickets.fcpo.ma/phpAPI/user/checkLogin.php?username="
                        +urls[0]+"&password="+urls[1]);
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
            int id=-1;
            progressBar.setVisibility(View.INVISIBLE);

            try {
                JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
                id =object.getInt("idUser");
                if (id==-1) Toast.makeText(Login.this, "username or password incorrect", Toast.LENGTH_SHORT).show();
                else {
                    sharedPref = Login.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("idUser",id);
                    editor.putBoolean("isLoggedIn",true);
                    editor.commit();

                    Intent mainIntent = new Intent(Login.this,MainActivity.class);
                    Login.this.startActivity(mainIntent);
                    Login.this.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class SendData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String data = "";

            HttpURLConnection httpURLConnection = null;
            try {

                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData=" + params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG", result);
            Toast.makeText(Login.this, "yep it said "+result , Toast.LENGTH_SHORT).show();// this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }

    @Override
    public void onBackPressed() {
//        exit(0);
        this.finish();
    }


}
