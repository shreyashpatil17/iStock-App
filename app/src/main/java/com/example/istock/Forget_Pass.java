package com.example.istock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorStateListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_Pass extends AppCompatActivity {
    private EditText frgt_email;
    private Button frgtBtn;
    private FirebaseAuth mAuth;
    private ProgressBar frgtProgress;

    private void forget_pass()
    {
        String email = frgt_email.getText().toString().trim();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter Your Registered Email ID", Toast.LENGTH_SHORT).show();
            return;
        }

        frgtProgress.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Forget_Pass.this, "We have sent you instructions to reset password on your email", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(Forget_Pass.this, "Failed To send reset email", Toast.LENGTH_SHORT).show();
                        }
                        frgtProgress.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__pass);

        frgt_email = findViewById(R.id.frgt_email);
        frgtBtn = findViewById(R.id.frgtBtn);
        frgtProgress = findViewById(R.id.frgtProgress);
        mAuth = FirebaseAuth.getInstance();

        frgtProgress.setVisibility(View.GONE);


        frgtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forget_pass();

            }
        });
    }
}