package com.example.istock.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.istock.AddStockIn;
import com.example.istock.AddStockOut;
import com.example.istock.Add_Product;
import com.example.istock.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Add#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add extends Fragment {

    private Button btnAddProduct, btnAddStockin, btnAddtockout;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private ValueEventListener spListener;
    private ArrayList<String> spList;
    private DatabaseReference rootSpinner;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Add() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Add.
     */
    // TODO: Rename and change types and number of parameters
    public static Add newInstance(String param1, String param2) {
        Add fragment = new Add();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        btnAddProduct = view.findViewById(R.id.addProductBtn);
        btnAddStockin = view.findViewById(R.id.addStockInButton);
        btnAddtockout = view.findViewById(R.id.addStockOutButton);

        rootSpinner = db.getReference().child("Users").child(firebaseUser.getUid()).child("Product Names");

        spList = new ArrayList<String>();
        fetchData();


        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Add_Product.class));
            }
        });

            btnAddStockin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!spList.isEmpty()) {
                        startActivity(new Intent(getActivity(), AddStockIn.class));
                    }
                    else{
                        Toast.makeText(getContext(), "No Product Available", Toast.LENGTH_SHORT).show();
                    }

                }
            });


        btnAddtockout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spList.isEmpty()) {
                    startActivity(new Intent(getActivity(), AddStockOut.class));
                }
                else{
                    Toast.makeText(getContext(), "No Product Available", Toast.LENGTH_SHORT).show();
                }
            }
        });




        return view;
    }
    private void fetchData()
    {
        spListener = rootSpinner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot spData : snapshot.getChildren())
                    spList.add(spData.getKey() + " " + spData.getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}