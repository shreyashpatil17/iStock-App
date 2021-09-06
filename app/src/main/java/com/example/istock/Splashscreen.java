package com.example.istock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splashscreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        user = FirebaseAuth.getInstance().getCurrentUser();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(user !=null)
                {
                    startActivity(new Intent(Splashscreen.this, MainActivity.class));
                    finish();
                }
                else
                {
                    startActivity(new Intent(Splashscreen.this, LoginActivity.class ));
                    finish();
                }

            }
        },3500);



    }
}