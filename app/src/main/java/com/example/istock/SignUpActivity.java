
package com.example.istock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail, mPass;
    private Button signUpBtn;
    private TextView reg_login;
    private EditText mName, mSname, mMob;
    private String user;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser ;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmail = findViewById(R.id.reg_email);
        mPass = findViewById(R.id.reg_pass);
        reg_login = findViewById(R.id.reg_login);
        signUpBtn = findViewById(R.id.regBtn);
        mName = findViewById(R.id.reg_name);
        mSname = findViewById(R.id.reg_sname);
        mMob = findViewById(R.id.reg_mob);

        mAuth = FirebaseAuth.getInstance();


        reg_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

    }

    private void createUser() {

        //        Initialize Custom Dialog Box
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();
        String name = mName.getText().toString();
        String surname = mSname.getText().toString();
        String mobile = mMob.getText().toString();

        dialog.show();
        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        dialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    firebaseUser = mAuth.getCurrentUser();
                                    user = firebaseUser.getUid();
                                    sendRegData();
                                    dialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Registered Successfully !!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Registration Error !!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mPass.setError("Empty Fields Are not Allowed");
            }
        } else if (email.isEmpty()) {
            mEmail.setError("Empty Fields Are not Allowed");
        } else {
            mEmail.setError("Pleas Enter Correct Email");
        }
    }


//        Sending Data to Firebase
    private void sendRegData() {
        String name = mName.getText().toString();
        String surname = mSname.getText().toString();
        String email = mEmail.getText().toString();
        String mobile = mMob.getText().toString();
        String password = mPass.getText().toString();
        String imageUrl = "";


        HashMap<String, String> userMap = new HashMap<>();

        userMap.put("Name", name);
        userMap.put("Surname", surname);
        userMap.put("Email", email);
        userMap.put("Mobile", mobile);
        userMap.put("Password", password);
        userMap.put("ImageURL",imageUrl);

        firebaseUser = mAuth.getCurrentUser();
        user = firebaseUser.getUid();

        root.child(firebaseUser.getUid()).setValue(userMap);
    }



}
