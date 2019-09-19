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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUp extends AppCompatActivity {

    EditText name,email,username,password,conPassword;
    TextView login;
    Button signUpBtn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.fullName);
        email = findViewById(R.id.userEmailId);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        conPassword = findViewById(R.id.confirmPassword);
        login = findViewById(R.id.already_user);
        signUpBtn = findViewById(R.id.signUpBtn);
        progressBar = findViewById(R.id.loading);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty()||username.getText().toString().isEmpty()||password.getText().toString().isEmpty()||conPassword.getText().toString().isEmpty()){
                    Toast.makeText(SignUp.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    if (!password.getText().toString().equals(conPassword.getText().toString())){
                        Toast.makeText(SignUp.this, "Confirmation error ... ", Toast.LENGTH_SHORT).show();
                    }else {
                        ConnectivityManager connectivityManager =
                                (ConnectivityManager)SignUp.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                        if (networkInfo!=null) {
                            progressBar.setVisibility(View.VISIBLE);
                            new RetrieveData().execute(username.getText().toString(),password.getText().toString(),email.getText().toString(),name.getText().toString());
                        }
                        else Toast.makeText(SignUp.this, "No connection", Toast.LENGTH_SHORT).show();



                    }
                }

            }
        });




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(SignUp.this, Login.class);
                SignUp.this.startActivity(mainIntent);
                SignUp.this.finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(SignUp.this, Login.class);
        SignUp.this.startActivity(mainIntent);
        SignUp.this.finish();
    }


    public class RetrieveData extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("https://tickets.fcpo.ma/phpAPI/user/adduser.php?username="
                        +urls[0].toString()+"&password="+urls[1].toString()+"&nom="+urls[3].toString()+"&email="+urls[2].toString());
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
            progressBar.setVisibility(View.INVISIBLE);
            if(!s.isEmpty()){
                try {
                    JSONObject object = (JSONObject) new JSONTokener(s).nextValue();
                    int id =object.getInt("idUser");
                    if (id==-1) Toast.makeText(SignUp.this, "Sign up error .. please try again later", Toast.LENGTH_SHORT).show();
                    else {

                        Toast.makeText(SignUp.this, "Sign up succesful please sign in", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(SignUp.this, Login.class);
                        SignUp.this.startActivity(mainIntent);
                        SignUp.this.finish();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(SignUp.this, "ERROR", Toast.LENGTH_SHORT).show();
            }
        }
    }






}
