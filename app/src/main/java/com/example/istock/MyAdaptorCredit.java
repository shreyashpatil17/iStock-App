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

public class MyAdaptorCredit extends RecyclerView.Adapter<MyAdaptorCredit.MyVierHolderCredit> {
    Context context;
    ArrayList<ModelCredit> credits;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root;

    public MyAdaptorCredit(Context c, ArrayList<ModelCredit> cr)
    {
        context = c;
        credits = cr;
    }

    @NonNull
    @Override
    public MyAdaptorCredit.MyVierHolderCredit onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyVierHolderCredit(LayoutInflater.from(context).inflate(R.layout.card_credit,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdaptorCredit.MyVierHolderCredit holder, int position) {

        holder.crBarcodeNo.setText(credits.get(position).getProduct_Barcode());
        holder.crCustomerName.setText(credits.get(position).getCustomer_Name());
        holder.crCustomerNumber.setText(credits.get(position).getCustomer_Number());
        holder.crProductName.setText(credits.get(position).getProduct_Name());
        holder.crProductPrice.setText(credits.get(position).getProduct_Price());
        holder.crProductQuantity.setText(credits.get(position).getProduct_Quantity());
        holder.crTime.setText(credits.get(position).getDate_Time());
        holder.crTotalPrice.setText(credits.get(position).getPrice());
        holder.crCredit.setText(credits.get(position).getCredit());

        String crPushId = credits.get(position).getPushId();

        holder.crDeleteItem.setOnClickListener(new View.OnClickListener() {
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
                        credits.remove(position);
                        credits.clear();
                        root = db.getReference().child("Users").child(firebaseUser.getUid()).child("Stock Movement");
                        root.child("Credit").child(crPushId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position,credits.size());
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
        return credits.size();
    }

    public class MyVierHolderCredit extends RecyclerView.ViewHolder {

        TextView crBarcodeNo, crProductName,crTime,crCustomerName,crCustomerNumber,crProductPrice,crProductQuantity,crTotalPrice,crCredit;
        Button crDeleteItem;

        public MyVierHolderCredit(@NonNull View itemView) {
            super(itemView);

            crBarcodeNo = itemView.findViewById(R.id.cardCreditBarcode);
            crProductName = itemView.findViewById(R.id.cardCreditProductName);
            crTime = itemView.findViewById(R.id.cardCreditTime);
            crProductPrice = itemView.findViewById(R.id.cardCreditProductPrice);
            crProductQuantity = itemView.findViewById(R.id.cardCreditProductQuantity);
            crTotalPrice = itemView.findViewById(R.id.cardCreditTotalPrice);
            crCustomerName = itemView.findViewById(R.id.cardCreditCustomerName);
            crCustomerNumber = itemView.findViewById(R.id.cardCreditCustomerNumber);
            crCredit = itemView.findViewById(R.id.cardCreditAmount);
            crDeleteItem = itemView.findViewById(R.id.cardCreditDeleteBtn);
        }
    }
}
