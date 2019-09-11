package com.example.drawerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    EditText name,email,username,password,conPassword;
    TextView login;
    Button signUpBtn;

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



        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUp.this, "Sign up succesful please sign in", Toast.LENGTH_SHORT).show();
                Intent mainIntent = new Intent(SignUp.this, Login.class);
                SignUp.this.startActivity(mainIntent);
                SignUp.this.finish();
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



}
