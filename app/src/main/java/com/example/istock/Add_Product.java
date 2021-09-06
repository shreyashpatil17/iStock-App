package com.example.istock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


public class Add_Product extends AppCompatActivity {
    private EditText productName, productManuName, productDistName, productDistNumber, productPrice;
    private Button scanBarcode, cameraBtn, addPr, backBtn;
    private ImageView cameraDisp;
    private TextView dispBarcode;
    private String user;

    private Uri imageUri;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser ;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__product);

        productName = findViewById(R.id.prName);
        productManuName = findViewById(R.id.prmanName);
        productDistName = findViewById(R.id.prdistName);
        productDistNumber = findViewById(R.id.prdistNo);
        scanBarcode = findViewById(R.id.barcodeBtn);
        cameraBtn = findViewById(R.id.cameraBtn);
        addPr = findViewById(R.id.saveprBtn);
        backBtn = findViewById(R.id.backBtn);
        cameraDisp = findViewById(R.id.cameraDisp);
        dispBarcode = findViewById(R.id.dispBarcode);
        productPrice = findViewById(R.id.prPrice);

        mAuth = FirebaseAuth.getInstance();

        //        Initializing Cutom Dialog Box
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(capture, 1000);
            }
        });

        addPr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void scanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan the barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String content = scanResult.getContents().toString();
            dispBarcode.setText(content);

        }
        if (requestCode == 1000)
        {
            Bitmap bitmap = (Bitmap)intent.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),bitmap,"val",null);
            imageUri = Uri.parse(path);
            cameraDisp.setImageURI(imageUri);
//            cameraDisp.setImageBitmap(bitmap);
        }

    }

    private void addProduct()
    {
//        Initializing Cutom Dialog Box
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        String prName = productName.getText().toString();
        String prManName = productManuName.getText().toString();
        String prDistName = productDistName.getText().toString();
        String prDistNo = productDistNumber.getText().toString();
        String prPrice = productPrice.getText().toString();
        String prBarcodeNo = dispBarcode.getText().toString();

        if(prName.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (prManName.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (prDistName.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (prDistNo.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (prPrice.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (prBarcodeNo.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (prDistNo.length() != 10)
        {
            Toast.makeText(this, "Mobile Number Must Be of 10 Numbers", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, String> addPrMap = new HashMap<>();
            addPrMap.put("Name", prName);
            addPrMap.put("Manufacturer_Name", prManName);
            addPrMap.put("Distributor_Name", prDistName);
            addPrMap.put("Distributor_Number", prDistNo);
            addPrMap.put("Price", prPrice);
            addPrMap.put("Barcode_Number", prBarcodeNo);
            addPrMap.put("Available_Quantity","0");
            addPrMap.put("ImageURL", "");
            firebaseUser = mAuth.getCurrentUser();

            dialog.show();
            root.child(firebaseUser.getUid()).child("Products").child(prBarcodeNo).setValue(addPrMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            root.child(firebaseUser.getUid()).child("Product Names").child(prBarcodeNo).setValue(prName)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            uploadImageToFirebase();
                                        }
                                    });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(Add_Product.this, "Failed to add Product", Toast.LENGTH_SHORT).show();
                    finish();
                    onBackPressed();
                }
            });
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
    {final Dialog dialog = new Dialog(this);
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
                        String prBarcodeNo = dispBarcode.getText().toString();
                        root.child(firebaseUser.getUid()).child("Products").child(prBarcodeNo).child("ImageURL").setValue(pathUri)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Add_Product.this, "Product Successfully Added", Toast.LENGTH_SHORT).show();
                                        finish();
                                        dialog.dismiss();
                                        onBackPressed();

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
                Toast.makeText(Add_Product.this, "Image Uploading Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getFileExtension(Uri imageUri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(imageUri));
    }

}



