package com.example.istock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.istock.Fragments.Add;
import com.example.istock.Fragments.Credit;
import com.example.istock.Fragments.Home;
import com.example.istock.Fragments.Products;
import com.example.istock.Fragments.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    FrameLayout fragContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNav);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragContainer, new Home());
        fragmentTransaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();


                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragmentTransaction.replace(R.id.fragContainer, new Home());
                        break;

                    case R.id.navigation_products:
                        fragmentTransaction.replace(R.id.fragContainer, new Products());
                        break;

                    case R.id.navigation_add:
                        fragmentTransaction.replace(R.id.fragContainer, new Add());
                        break;

                    case R.id.navigation_credit:
                        fragmentTransaction.replace(R.id.fragContainer, new Credit());
                        break;

                    case R.id.navigation_settings:
                        fragmentTransaction.replace(R.id.fragContainer, new Settings());
                        break;
                }
                    fragmentTransaction.commit();
                return true;
            }


        });


    }
}

