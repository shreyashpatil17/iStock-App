package com.example.istock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.istock.Fragments.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EditProduct extends AppCompatActivity {

    private EditText name,manName,distName,distNo,price;
    private TextView cameraBtn,barcodeDisp;
    private Button saveBtn,backBtn;
    private ImageView dispImage;
    private String user;

    private Uri imageUri;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users").child(firebaseUser.getUid());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        name = findViewById(R.id.editProductName);
        manName = findViewById(R.id.editProductManName);
        distName = findViewById(R.id.editProductDistName);
        distNo = findViewById(R.id.editProductDistNo);
        price = findViewById(R.id.editProductPrice);
        cameraBtn = findViewById(R.id.editProductCameraBtn);
        dispImage= findViewById(R.id.editProductCameraDisp);
        saveBtn = findViewById(R.id.saveEditProductBtn);
        backBtn = findViewById(R.id.editProductBackBtn);
        barcodeDisp = findViewById(R.id.editProductBarcodeDisp);


        String prBarcodeNo = getIntent().getStringExtra("barcode");
        String prName = getIntent().getStringExtra("name");
        String prManName = getIntent().getStringExtra("manName");
        String prDistName = getIntent().getStringExtra("distName");
        String prDistNo = getIntent().getStringExtra("distNo");
        String prPrice = getIntent().getStringExtra("price");
        String prImageUrl = getIntent().getStringExtra("image");

        name.setText(prName);
        manName.setText(prManName);
        distName.setText(prDistName);
        distNo.setText(prDistNo);
        price.setText(prPrice);
        barcodeDisp.setText(prBarcodeNo);
        barcodeDisp.setVisibility(View.INVISIBLE);
        imageUri = Uri.parse(prImageUrl);
        Picasso.get().load(prImageUrl).into(dispImage);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(capture, 1001);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                saveEditProduct();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001)
        {
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
            imageUri = Uri.parse(path);
            dispImage.setImageURI(imageUri);
//            cameraDisp.setImageBitmap(bitmap);
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
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String pathUri = uri.toString();
                        String prBarcodeNo = barcodeDisp.getText().toString();
                        root.child("Products").child(prBarcodeNo).child("ImageURL").setValue(pathUri)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(EditProduct.this, "Product Successfully Updated", Toast.LENGTH_SHORT).show();
                                        finish();
                                        dialog.dismiss();

                                    }
                                });
//                        Toast.makeText(Add_Product.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
            }
        });
    }
    private String getFileExtension(Uri imageUri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imageUri));
    }

    private void saveEditProduct()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        String newName = name.getText().toString();
        String newManName = manName.getText().toString();
        String newDistName = distName.getText().toString();
        String newDistNo = distNo.getText().toString();
        String newPrice = price.getText().toString();
        String barcodeNo = barcodeDisp.getText().toString();

        if(newName.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (newManName.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (newDistName.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (newDistNo.length() !=10)
        {
            Toast.makeText(this, "Mobile Number Must Be Of 10 Digits", Toast.LENGTH_SHORT).show();
        }
        else if (newPrice.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            root.child("Product Names").child(barcodeNo).setValue(newName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (newName.isEmpty()) {
                                Toast.makeText(EditProduct.this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
                            } else if (newManName.isEmpty()) {
                                Toast.makeText(EditProduct.this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
                            } else if (newDistName.isEmpty()) {
                                Toast.makeText(EditProduct.this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
                            } else if (newDistNo.isEmpty()) {
                                Toast.makeText(EditProduct.this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
                            } else if (newPrice.isEmpty()) {
                                Toast.makeText(EditProduct.this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
                            } else {
                                root.child("Products").child(barcodeNo).child("Name").setValue(newName);
                                root.child("Products").child(barcodeNo).child("Manufacturer_Name").setValue(newManName);
                                root.child("Products").child(barcodeNo).child("Distributor_Name").setValue(newDistName);
                                root.child("Products").child(barcodeNo).child("Distributor_Number").setValue(newDistNo);
                                root.child("Products").child(barcodeNo).child("Price").setValue(newPrice);
                                if(imageUri != null)
                                {
                                    uploadImageToFirebase();
                                }
                                finish();
                                dialog.dismiss();
                                onBackPressed();
                                Toast.makeText(EditProduct.this, "Product Successfully Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProduct.this, "Failed To Update Data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void back()
    {
        onBackPressed();
    }

}