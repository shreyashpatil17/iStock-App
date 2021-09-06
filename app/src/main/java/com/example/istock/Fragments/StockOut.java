package com.example.istock.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.istock.ModelStockIn;
import com.example.istock.ModelStockOut;
import com.example.istock.MyAdaptorStockIn;
import com.example.istock.MyAdaptorStockOut;
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
 * Use the {@link StockOut#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockOut extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root;

    LinearLayoutManager soLLM = new LinearLayoutManager(getActivity());
    RecyclerView stockOutRecyclerView;
    ArrayList<ModelStockOut> solist;
    MyAdaptorStockOut myAdaptorStockOut;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StockOut() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StockOut.
     */
    // TODO: Rename and change types and number of parameters
    public static StockOut newInstance(String param1, String param2) {
        StockOut fragment = new StockOut();
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
        View view =  inflater.inflate(R.layout.fragment_stock_out, container, false);

        mAuth = FirebaseAuth.getInstance();
        stockOutRecyclerView = view.findViewById(R.id.stockOutRecyclerView);
        stockOutRecyclerView.setLayoutManager(soLLM);
        solist = new ArrayList<ModelStockOut>();

        root = db.getReference().child("Users").child(firebaseUser.getUid()).child("Stock Movement").child("Stock-Out");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelStockOut mp = snapshot1.getValue(ModelStockOut.class);
                    solist.add(mp);
                }
                myAdaptorStockOut = new MyAdaptorStockOut(getActivity(), solist);
                stockOutRecyclerView.setAdapter(myAdaptorStockOut);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}