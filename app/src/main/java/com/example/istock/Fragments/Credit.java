package com.example.istock.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.istock.ModelCredit;
import com.example.istock.ModelProduct;
import com.example.istock.MyAdaptorCredit;
import com.example.istock.MyAdaptorProduct;
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
 * Use the {@link Credit#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Credit extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root;
    private TextView crCount;
    private int count;
    private String dispCount;


    RecyclerView crRecyclerView;
    ArrayList<ModelCredit> crList;
    MyAdaptorCredit myAdaptorCredit;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Credit() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Credit.
     */
    // TODO: Rename and change types and number of parameters
    public static Credit newInstance(String param1, String param2) {
        Credit fragment = new Credit();
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
        View view =  inflater.inflate(R.layout.fragment_credit, container, false);

        mAuth = FirebaseAuth.getInstance();

        crCount = view.findViewById(R.id.crCount);

        crRecyclerView = view.findViewById(R.id.creditRecyclerView);
        crRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        crList = new ArrayList<ModelCredit>();
        root = db.getReference().child("Users").child(firebaseUser.getUid()).child("Stock Movement").child("Credit");
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelCredit mp = snapshot1.getValue(ModelCredit.class);
                    crList.add(mp);
                }
                myAdaptorCredit = new MyAdaptorCredit(getActivity(), crList);
                crRecyclerView.setAdapter(myAdaptorCredit);
                count = (int) snapshot.getChildrenCount();
                dispCount = Integer.toString(count);
                crCount.setText(dispCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Database Error", Toast.LENGTH_SHORT).show();
            }
        });




        return view;
    }
}