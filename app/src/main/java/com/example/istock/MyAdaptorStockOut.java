package com.example.istock;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdaptorStockOut extends RecyclerView.Adapter<MyAdaptorStockOut.MyViewHolderStockOut> {
    Context context;
    ArrayList<ModelStockOut> stockOuts;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root;

    public MyAdaptorStockOut(Context c, ArrayList<ModelStockOut> so)
    {
        context = c;
        stockOuts = so;
    }

    @NonNull
    @Override
    public MyAdaptorStockOut.MyViewHolderStockOut onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderStockOut(LayoutInflater.from(context).inflate(R.layout.card_stock_out,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdaptorStockOut.MyViewHolderStockOut holder, int position) {

        holder.soBarcodeNo.setText(stockOuts.get(position).getProduct_Barcode());
        holder.soCustomerName.setText(stockOuts.get(position).getCustomer_Name());
        holder.soCustomerNumber.setText(stockOuts.get(position).getCustomer_Number());
        holder.soProductName.setText(stockOuts.get(position).getProduct_Name());
        holder.soProductPrice.setText(stockOuts.get(position).getProduct_Price());
        holder.soProductQuantity.setText(stockOuts.get(position).getProduct_Quantity());
        holder.soTime.setText(stockOuts.get(position).getDate_Time());
        holder.soTotalPrice.setText(stockOuts.get(position).getPrice());

        String soPushId = stockOuts.get(position).getPushId();

        holder.soDeleteItem.setOnClickListener(new View.OnClickListener() {
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
                        stockOuts.remove(position);
                        stockOuts.clear();
                        root = db.getReference().child("Users").child(firebaseUser.getUid()).child("Stock Movement");
                        root.child("Stock-Out").child(soPushId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,stockOuts.size());
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
        return stockOuts.size();
    }

    public class MyViewHolderStockOut extends RecyclerView.ViewHolder {

        TextView soBarcodeNo, soProductName,soTime,soCustomerName,soCustomerNumber,soProductPrice,soProductQuantity,soTotalPrice;
        Button soDeleteItem;

        public MyViewHolderStockOut(@NonNull View itemView) {
            super(itemView);

            soBarcodeNo = itemView.findViewById(R.id.cardStockOutBarcode);
            soProductName = itemView.findViewById(R.id.cardStockOutProductName);
            soTime = itemView.findViewById(R.id.cardStockOutTime);
            soProductPrice = itemView.findViewById(R.id.cardStockOutProductPrice);
            soProductQuantity = itemView.findViewById(R.id.cardStockOutProductQuantity);
            soTotalPrice = itemView.findViewById(R.id.cardStockOutTotalPrice);
            soCustomerName = itemView.findViewById(R.id.cardStockOutCustomerName);
            soCustomerNumber = itemView.findViewById(R.id.cardStockOutCustomerNumber);
            soDeleteItem = itemView.findViewById(R.id.cardStockOutDeleteBtn);

        }
    }
}
