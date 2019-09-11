package com.example.drawerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private String trueUsername = "admin";
    private String truePassword = "admin";


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

        if (isLoggedIn){
            Intent mainIntent = new Intent(this,MainActivity.class);
            this.startActivity(mainIntent);
            this.finish();
        }


    }

    public void login(View v){
        if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty())
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        else if (username.getText().toString().equals(trueUsername) && password.getText().toString().equals(truePassword)){
            sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isLoggedIn",true);
            editor.commit();
            Intent mainIntent = new Intent(this,MainActivity.class);
            this.startActivity(mainIntent);
            this.finish();
        }else Toast.makeText(this, "username or password incorect .. please concider signing up", Toast.LENGTH_SHORT).show();
    }

    public void register(View v){
        Intent mainIntent = new Intent(this, SignUp.class);
        this.startActivity(mainIntent);
        this.finish();
    }


}
