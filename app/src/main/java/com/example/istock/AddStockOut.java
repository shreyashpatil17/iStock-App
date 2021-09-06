package com.example.istock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class AddStockOut extends AppCompatActivity {
    private Button backBtn,submit,calculate;
    private EditText stockOutCustName,stockOutCustNo,stockOutQty,stockOutPrice,creditAmt,hideText;
    private Spinner spinner;
    private TextView stockInDate,dispPrice;
    private String date,date_time,spinSel,itemName,barcodeNo,newQty;
    private Calendar calendar = Calendar.getInstance();
    private Switch creditSwitch;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");
    private DatabaseReference rootSpinner;
    private DatabaseReference newRoot;

    private ValueEventListener spListener;
    private ArrayList<String> spList;
    private ArrayAdapter<String> spAdaptor;
    private Boolean done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock_out);

        backBtn = findViewById(R.id.backkBtn);
        stockInDate = findViewById(R.id.stockInDate);
        stockOutCustName = findViewById(R.id.stockOutCustName);
        stockOutCustNo = findViewById(R.id.stockOutCustNo);
        stockOutQty = findViewById(R.id.stockOutQty);
        stockOutPrice = findViewById(R.id.stockOutPrice);
        spinner = findViewById(R.id.stockOutSpinner);
        submit = findViewById(R.id.submitStockOut);
        calculate = findViewById(R.id.stockOutCalculatePrice);
        dispPrice = findViewById(R.id.stockOutDispPrice);
        creditSwitch = findViewById(R.id.creditSwitch);
        creditAmt = findViewById(R.id.creditAmount);
        hideText = findViewById(R.id.hideStockOutText);

        hideText.setVisibility(View.INVISIBLE);


        rootSpinner = db.getReference().child("Users").child(firebaseUser.getUid()).child("Product Names");

        spList = new ArrayList<String>();
        spAdaptor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spList);
        spinner.setAdapter(spAdaptor);

        fetchData();

        creditAmt.setVisibility(View.INVISIBLE);


//        Display Date in XML
        date = DateFormat.getDateInstance().format(calendar.getTime());
        stockInDate.setText(date);

        creditSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    creditAmt.setVisibility(View.VISIBLE);

                }
                else
                {
                    creditAmt.setVisibility(View.INVISIBLE);
                }
            }
        });



        done = false;
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinSel = spinner.getSelectedItem().toString();
                itemName = spinSel.replaceAll("[^A-Za-z]","").trim();
                barcodeNo = spinSel.replaceAll("[^0-9]", "").trim();

                newRoot = db.getReference().child("Users").child(firebaseUser.getUid()).child("Products").child(barcodeNo);
                newRoot.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String lastQty = snapshot.child("Available_Quantity").getValue().toString().trim();
                        int v1 = Integer.parseInt(lastQty);
                        int v2;
                        String value2 = stockOutQty.getText().toString().trim();
                        if(value2.isEmpty()) {
                           v2 = 0;
                        }
                        else
                        {
                            v2 = Integer.parseInt(value2);
                        }
                        int v3 = (v1 - v2);
                        String changeQty = String.valueOf(v3);
                        hideText.setText(changeQty);



                        if(v2>v1)
                        {
                            Toast.makeText(AddStockOut.this, "Entered Quantity is Greater Than Available Stock", Toast.LENGTH_SHORT).show();

                        }
                        else if(v1 >= v2)
                        {
                            if(done == true)
                            {
                                finish();
                                onBackPressed();
                            }
                            else
                            {   done=true;
                                sendData();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddStockOut.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });
                    }

        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatePrice();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void back()
    {
        onBackPressed();
    }

    private void fetchData()
    {
        spListener = rootSpinner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot spData : snapshot.getChildren())
                    spList.add(spData.getKey() + " " + spData.getValue().toString());
                spAdaptor.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddStockOut.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendData()
    {
//        Initializing Dialog Box
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loading_dialog);
        dialog.setCancelable(false);

        String custName = stockOutCustName.getText().toString();
        String custNo = stockOutCustNo.getText().toString();
        String outQty = stockOutQty.getText().toString();
        String outPrice = stockOutPrice.getText().toString();
        String dPrice = dispPrice.getText().toString();
        String creditAmount = creditAmt.getText().toString();
        String newQty = hideText.getText().toString();
        date_time = java.text.DateFormat.getDateTimeInstance().format(new Date());
        String pushId = root.child(firebaseUser.getUid()).child("Stock Movement").child("Credit").push().getKey();

        if(custName.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(custNo.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(outQty.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(outPrice.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(creditAmt.getVisibility() == View.VISIBLE && creditAmount.isEmpty())
        {
            Toast.makeText(this, "Please Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(dPrice.isEmpty())
        {
            Toast.makeText(this, "Enter All Fields And Click On Calculate Button", Toast.LENGTH_SHORT).show();
        }

        else
        {
            HashMap<String,String> addStockOut = new HashMap<>();
            addStockOut.put("Product_Name",itemName);
            addStockOut.put("Customer_Name", custName);
            addStockOut.put("Customer_Number", custNo);
            addStockOut.put("Product_Quantity",outQty);
            addStockOut.put("Product_Price",outPrice);
            addStockOut.put("Date_Time",date_time);
            addStockOut.put("Type","StockOut");
            addStockOut.put("Price",dPrice);
            addStockOut.put("Product_Barcode",barcodeNo);
            addStockOut.put("PushId",pushId);

            int visibility = creditAmt.getVisibility();
            dialog.show();
            if (visibility == View.VISIBLE) {
                if(creditAmount.isEmpty())
                {
                    Toast.makeText(this, "Enter Credit Amount", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    addStockOut.put("Credit", creditAmount);
                    //            Add Stock Out Data in Firebase Database
                    root.child(firebaseUser.getUid()).child("Stock Movement").child("Credit").child(pushId).setValue(addStockOut)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    root.child(firebaseUser.getUid()).child("Products").child(barcodeNo).child("Available_Quantity").setValue(newQty);
                                    root.child(firebaseUser.getUid()).child("Stock Movement").child("Stock-Out").child(pushId).setValue(addStockOut);
                                    dialog.dismiss();
                                    Toast.makeText(AddStockOut.this, "Stock-Out Added Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddStockOut.this, "Failed To Add Stock-Out", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            finish();
                            back();
                        }
                    });
                }
            }

            else {
                root.child(firebaseUser.getUid()).child("Stock Movement").child("Stock-Out").child(pushId).setValue(addStockOut)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                root.child(firebaseUser.getUid()).child("Products").child(barcodeNo).child("Available_Quantity").setValue(newQty)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                dialog.dismiss();
                                                Toast.makeText(AddStockOut.this, "Stock-Out Added Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddStockOut.this, "Failed To Add StockOut", Toast.LENGTH_SHORT).show();
                                        finish();
                                        back();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddStockOut.this, "Failed To Add Stock", Toast.LENGTH_SHORT).show();
                        finish();
                        back();
                    }
                });
            }
        }
    }



    private void calculatePrice()
    {

        String m1 = stockOutQty.getText().toString().trim();
        String m2 = stockOutPrice.getText().toString().trim();

        if(m1.isEmpty() && m2.isEmpty())
        {
            Toast.makeText(this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(m1.isEmpty())
        {
            Toast.makeText(this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else if(m2.isEmpty())
        {
            Toast.makeText(this, "Enter All The Fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            float value1 = Integer.parseInt(m1);
            float value2 = Integer.parseInt(m2);
            double mValue = value1*value2;

            String dPrice = String.valueOf(mValue);
            dispPrice.setText(dPrice);
        }

    }

    private void test()
    {
        done = true;
    }
}