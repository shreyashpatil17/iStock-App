package com.example.istock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyAdaptorProduct extends RecyclerView.Adapter<MyAdaptorProduct.MyViewHolderProduct> {

     Context context;
     Context newContext;
     ArrayList<ModelProduct> products;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root;


    public MyAdaptorProduct(Context c, ArrayList<ModelProduct> p)
    {
        context = c;
        products = p;
    }

    @NonNull
    @Override
    public MyViewHolderProduct onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderProduct(LayoutInflater.from(context).inflate(R.layout.card_products,parent,false));

    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolderProduct holder, int position) {

        DecimalFormat format = new DecimalFormat("0.#");
        String getQty = products.get(position).getAvailable_Quantity();
        double convQty = Double.parseDouble(getQty);

        String prbarodeNo = products.get(position).getBarcode_Number().toString();
        String prName = products.get(position).getName().toString();
        String prManName = products.get(position).getManufacturer_Name().toString();
        String prDistName = products.get(position).getDistributor_Name().toString();
        String prDistNo = products.get(position).getDistributor_Number().toString();
        String prPrice = products.get(position).getPrice().toString();
        String prImgURL = products.get(position).getImageURL().toString();


        holder.prbarcodeNo.setText(products.get(position).getBarcode_Number());
        holder.prName.setText(products.get(position).getName());
        holder.prPrice.setText(products.get(position).getPrice());
        holder.prManName.setText(products.get(position).getManufacturer_Name());
        holder.prDistName.setText(products.get(position).getDistributor_Name());
        holder.prDistNo.setText(products.get(position).getDistributor_Number());
        holder.prQty.setText(format.format(convQty));
        Picasso.get().load(products.get(position).getImageURL()).into(holder.prImage);

        holder.editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditProduct.class);
                intent.putExtra("barcode",prbarodeNo);
                intent.putExtra("name",prName);
                intent.putExtra("manName",prManName);
                intent.putExtra("distName",prDistName);
                intent.putExtra("distNo",prDistNo);
                intent.putExtra("price",prPrice);
                intent.putExtra("image",prImgURL);
                intent.putExtra("position",position);
                context.startActivity(intent);
            }
        });



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
                        products.remove(position);
                        products.clear();
                        root = db.getReference().child("Users").child(firebaseUser.getUid());
                        root.child("Products").child(prbarodeNo).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        root.child("Product Names").child(prbarodeNo).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position,products.size());
                                                        dialog.dismiss();
                                                        Toast.makeText(context, "Successfully Removed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
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
        return products.size();
    }

    public class MyViewHolderProduct extends RecyclerView.ViewHolder{

        TextView prbarcodeNo, prName, prPrice,prManName, prDistName, prDistNo, prQty;
        CircularImageView prImage;
        Button deleteItem,editItem;

        public MyViewHolderProduct(@NonNull View itemView) {
            super(itemView);

            prbarcodeNo = itemView.findViewById(R.id.cardProductBarcode);
            prName = itemView.findViewById(R.id.cardProductName);
            prPrice = itemView.findViewById(R.id.cardProductPrice);
            prManName = itemView.findViewById(R.id.cardProductManName);
            prDistName = itemView.findViewById(R.id.cardProductDistName);
            prDistNo = itemView.findViewById(R.id.cardProductDistNo);
            prQty = itemView.findViewById(R.id.cardProductQty);
            prImage = itemView.findViewById(R.id.cardProductImage);
            deleteItem = itemView.findViewById(R.id.cardProductDeleteBtn);
            editItem = itemView.findViewById(R.id.cardProductEditBtn);

        }
    }
}
