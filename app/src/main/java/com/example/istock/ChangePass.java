package com.example.istock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ChangePass extends AppCompatActivity {

    private Button backBtn;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root ;
    private EditText  newPasseditText,oldPasseditText;
    private Button submitPass;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ProgressBar changeProgress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        mAuth = FirebaseAuth.getInstance();

        newPasseditText = findViewById(R.id.newPass);
        oldPasseditText = findViewById(R.id.oldPass);
        submitPass = findViewById(R.id.submitPass);
        changeProgress = findViewById(R.id.changeProgress);

        changeProgress.setVisibility(View.GONE);

        if (firebaseUser != null) {
            // Name,  email address, and profile photo Url
            root = db.getReference().child("Users").child(firebaseUser.getUid());

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                     final String firebasePass = snapshot.child("Password").getValue().toString();

                    submitPass.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String oldPassword = oldPasseditText.getText().toString().trim();
                            String newPassword = newPasseditText.getText().toString().trim();
                            changeProgress.setVisibility(View.VISIBLE);
                                updatePassword(oldPassword,newPassword);

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChangePass.this, "Database Connection Failed", Toast.LENGTH_SHORT).show();
                    changeProgress.setVisibility(View.INVISIBLE);
                }

        });

        }

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void updatePassword(String oldPassword,String newPassword)
    {
        FirebaseUser user = mAuth.getCurrentUser();
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        firebaseUser.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Successfully Authticated!! Begin Update
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        Password Updated!!
                                        root = db.getReference();
                                        root.child("Users").child(firebaseUser.getUid()).child("Password").setValue(newPassword);
                                        Toast.makeText(ChangePass.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                        changeProgress.setVisibility(View.GONE);
                                        finish();
                                        back();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                Password Update Failed
                                Toast.makeText(ChangePass.this, "Failed To Update Password", Toast.LENGTH_SHORT).show();
                                changeProgress.setVisibility(View.INVISIBLE);
                                finish();
                                onBackPressed();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//            Authentication Failed
                Toast.makeText(ChangePass.this, "Old Password Does not Match", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void back()
    {
        onBackPressed();
    }
}