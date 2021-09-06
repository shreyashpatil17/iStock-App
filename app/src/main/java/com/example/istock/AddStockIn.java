package com.example.istock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddStockIn extends AppCompatActivity {

    private TextView stockInDate, dispPrice;
    private String date, date_time,barcodeNo,itemName,newQty,spinSel;
    private Button back, submit, calculate;
    private Spinner spinner;
    private EditText stockQty, stockPrice, hideText;
    private Calendar calendar = Calendar.getInstance();

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");
    private DatabaseReference rootSpinner;
    private DatabaseReference newRoot;


    private ValueEventListener spListener;
    private ArrayList<String> spList;
    private ArrayAdapter<String> spAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock_in);

        stockInDate = findViewById(R.id.stockInDate);
        back = findViewById(R.id.backkBtn);
        spinner = findViewById(R.id.stockInSpinner);
        submit = findViewById(R.id.submitStockIn);
        dispPrice = findViewById(R.id.stockInDispPrice);
        stockQty = findViewById(R.id.stockInQty);
        stockPrice = findViewById(R.id.stockInPrice);
        calculate = findViewById(R.id.stockInCalculatePrice);
        hideText = findViewById(R.id.hideText);

        hideText.setVisibility(View.INVISIBLE);


        rootSpinner = db.getReference().child("Users").child(firebaseUser.getUid()).child("Product Names");

        spList = new ArrayList<String>();
        spAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spList);
        spinner.setAdapter(spAdaptor);

        mAuth = FirebaseAuth.getInstance();


        fetchData();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        Display Only Date
        date = DateFormat.getDateInstance().format(calendar.getTime());
        stockInDate.setText(date);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatePrice();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //        To get selected Item From Spinner
                 spinSel = spinner.getSelectedItem().toString();
                 itemName = spinSel.replaceAll("[^A-Za-z]","").trim();
                 barcodeNo = spinSel.replaceAll("[^0-9]", "").trim();
                newRoot = db.getReference().child("Users").child(firebaseUser.getUid()).child("Products").child(barcodeNo);

                newRoot.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastQty = snapshot.child("Available_Quantity").getValue().toString();
                        int v1 = Integer.parseInt(lastQty);
                        int v2;
                        String value2 = stockQty.getText().toString().trim();
                        if(value2.isEmpty()) {
                            v2 = 0;
                        }
                        else
                        {
                            v2 = Integer.parseInt(value2);
                        }
                        int v3 = (v1 + v2);
                        String changeQty = String.valueOf(v3);
                        hideText.setText(changeQty);
                        newQty = hideText.getText().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddStockIn.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });

                sendData();

            }
        });
    }

    private void fetchData() {
        spListener = rootSpinner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot spData : snapshot.getChildren())
                    spList.add(spData.getKey() + " " + spData.getValue().toString());

                spAdaptor.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddStockIn.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculatePrice() {

        String m1 = stockQty.getText().toString().trim();
        String m2 = stockPrice.getText().toString().trim();

        if (m1.isEmpty() && m2.isEmpty()) {
            Toast.makeText(this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
        } else if (m1.isEmpty()) {
            Toast.makeText(this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
        } else if (m2.isEmpty()) {
            Toast.makeText(this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
        } else {
            float value1 = Integer.parseInt(m1);
            float value2 = Integer.parseInt(m2);
            double mValue = value1 * value2;

            String dPrice = String.valueOf(mValue);
            dispPrice.setText(dPrice);
        }

    }

    private void sendData() {
//      Initialize Custom Dialog Box
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        String inQty = stockQty.getText().toString();
        String Price = stockPrice.getText().toString();
        String dPrice = dispPrice.getText().toString();
        String pushId =  root.child(firebaseUser.getUid()).child("Stock Movement").child("Stock-In").push().getKey();


        date_time = java.text.DateFormat.getDateTimeInstance().format(new Date());


        if (inQty.isEmpty())
        {
            Toast.makeText(AddStockIn.this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if (Price.isEmpty())
        {
            Toast.makeText(AddStockIn.this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(dPrice.isEmpty())
        {
            Toast.makeText(this, "Enter All Fields And Click On Calculate Button", Toast.LENGTH_SHORT).show();
        }
        else
            {
            HashMap<String, String> addStockIn = new HashMap<>();
            addStockIn.put("Product_Name", itemName);
            addStockIn.put("Product_Quantity", inQty);
            addStockIn.put("Product_Price", Price);
            addStockIn.put("Date_Time", date_time);
            addStockIn.put("Type", "StockIN");
            addStockIn.put("Total_Price", dPrice);
            addStockIn.put("Product_Barcode",barcodeNo);
            addStockIn.put("PushId",pushId);

            dialog.show();

            root.child(firebaseUser.getUid()).child("Stock Movement").child("Stock-In").child(pushId).setValue(addStockIn)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            root.child(firebaseUser.getUid()).child("Products").child(barcodeNo).child("Available_Quantity").setValue(newQty)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialog.dismiss();
                                            Toast.makeText(AddStockIn.this, "Stock-In Added Successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                            onBackPressed();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(AddStockIn.this, "Error...Failed To Add", Toast.LENGTH_SHORT).show();
                                    finish();
                                    onBackPressed();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(AddStockIn.this, "Error...Failed To Add", Toast.LENGTH_SHORT).show();
                    finish();
                    onBackPressed();
                }
            });


        }

    }
}