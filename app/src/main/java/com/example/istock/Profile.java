package com.example.istock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class Profile extends AppCompatActivity {

    private EditText editName,editSname,editMob;
    private CircularImageView diaplayPhoto;
    private Button backBtn,saveBtn,editPhotoBtn;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root ;
            

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editName = findViewById(R.id.profile_name);
        editSname = findViewById(R.id.prrofile_sname);
        editMob = findViewById(R.id.profile_mob);
        backBtn = findViewById(R.id.backBtn);
        saveBtn = findViewById(R.id.saveBtn);
        editPhotoBtn = findViewById(R.id.editPhotoBtn);
        diaplayPhoto = findViewById(R.id.displayPhoto);

        mAuth = FirebaseAuth.getInstance();

        //                Initialize Dialog Box
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        editPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 100);
            }
        });

        String UID = firebaseUser.getUid().toString();

        root = db.getReference().child("Users").child(UID);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("Name").getValue().toString();
                String sname = snapshot.child("Surname").getValue().toString();
                String mob = snapshot.child("Mobile").getValue().toString();

                String imagePath = snapshot.child("ImageURL").getValue().toString();
                if(!imagePath.isEmpty()) {
                    Picasso.get().load(imagePath).into(diaplayPhoto);
                }

                editName.setText(name);
                editSname.setText(sname);
                editMob.setText(mob);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Database Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

//        Updating Profile
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String saveName = editName.getText().toString();
                String saveSname = editSname.getText().toString();
                String saveMob = editMob.getText().toString();

                root = db.getReference();
                root.child("Users").child(firebaseUser.getUid()).child("Name").setValue(saveName);
                root.child("Users").child(firebaseUser.getUid()).child("Surname").setValue(saveSname);
                root.child("Users").child(firebaseUser.getUid()).child("Mobile").setValue(saveMob);
                uploadImageToFirebase();
                dialog.dismiss();
                Toast.makeText(Profile.this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
                back();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            diaplayPhoto.setImageURI(imageUri);
        }
    }

    private void uploadImageToFirebase()
    {
        if(imageUri != null)
        {
            uploadToFirebase(imageUri);
        }
        else
        {
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase(Uri uri)
    {
        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String pathUri = uri.toString();
                        root.child("Users").child(firebaseUser.getUid()).child("ImageURL").setValue(pathUri);

                        Toast.makeText(Profile.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri imageUri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imageUri));
    }

    private void back()
    {
        onBackPressed();
    }

}
