package com.example.istock;

import android.app.Dialog;
import android.content.Context;
import android.os.CpuUsageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.istock.Fragments.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdaptorStockIn extends RecyclerView.Adapter<MyAdaptorStockIn.MyViewHolderStockIn> {

    Context context;
    ArrayList<ModelStockIn> stockIns;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root;

    public MyAdaptorStockIn(Context c, ArrayList<ModelStockIn> si)
    {
        context = c;
        stockIns = si;
    }

    @NonNull
    @Override
    public MyAdaptorStockIn.MyViewHolderStockIn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderStockIn(LayoutInflater.from(context).inflate(R.layout.card_stockin,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdaptorStockIn.MyViewHolderStockIn holder, int position) {

        holder.siBarcodeNo.setText(stockIns.get(position).getProduct_Barcode());
        holder.siProductName.setText(stockIns.get(position).getProduct_Name());
        holder.siTime.setText(stockIns.get(position).getDate_Time());
        holder.siProductPrice.setText(stockIns.get(position).getProduct_Price());
        holder.siProductQuantity.setText(stockIns.get(position).getProduct_Quantity());
        holder.siTotalPrice.setText(stockIns.get(position).getTotal_Price());

        String siPushId = stockIns.get(position).getPushId();


        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog_box);
                dialog.show();


                Button yes,no;
                yes = dialog.findViewById(R.id.dialogYesBtn);
                no = dialog.findViewById(R.id.dialogNoBtn);

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stockIns.remove(position);
                        stockIns.clear();
                        root = db.getReference().child("Users").child(firebaseUser.getUid()).child("Stock Movement");
                        root.child("Stock-In").child(siPushId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,stockIns.size());
                                        dialog.dismiss();
                                        Toast.makeText(context, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(context, "Failed To delete Item", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return stockIns.size();
    }

    public class MyViewHolderStockIn extends RecyclerView.ViewHolder {

        TextView siBarcodeNo, siProductName,siTime,siProductPrice,siProductQuantity,siTotalPrice;
        Button deleteItem;


        public MyViewHolderStockIn(@NonNull View itemView) {
            super(itemView);

            siBarcodeNo = itemView.findViewById(R.id.cardStockInBarcode);
            siProductName = itemView.findViewById(R.id.cardStockInProductName);
            siTime = itemView.findViewById(R.id.cardStockInTime);
            siProductPrice = itemView.findViewById(R.id.cardStockInProductPrice);
            siProductQuantity = itemView.findViewById(R.id.cardStockInProductQuantity);
            siTotalPrice = itemView.findViewById(R.id.cardStockInTotalPrice);
            deleteItem = itemView.findViewById(R.id.cardStockInDeleteBtn);


        }
    }
}