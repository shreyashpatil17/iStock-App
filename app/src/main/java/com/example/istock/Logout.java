package com.example.istock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

public class Logout extends AppCompatActivity {
    private TextView logout,successful;
    private GifImageView gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        gif = findViewById(R.id.animLogout);

//        logout = findViewById(R.id.logoutTxt);
//        successful = findViewById(R.id.successfulTxt);
//
//        logout.animate().translationY(200).setDuration(1000).start();
//        successful.animate().translationY(200).setDuration(1000).start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Logout.this, LoginActivity.class));
                finish();
            }
        },1200);

    }
}