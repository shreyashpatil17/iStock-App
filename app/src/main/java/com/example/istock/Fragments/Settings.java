package com.example.istock.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.istock.ChangePass;
import com.example.istock.LoginActivity;
import com.example.istock.Logout;
import com.example.istock.Profile;
import com.example.istock.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Settings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Settings extends Fragment {

    private Button logOutBtn,profileBtn,changePassBtn;
    FrameLayout fragContainer;
    private FirebaseAuth mAuth;
    private TextView mFullname,mEmail;
    private ImageView mPhoto;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root ;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Settings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Settings.
     */
    // TODO: Rename and change types and number of parameters
    public static Settings newInstance(String param1, String param2) {
        Settings fragment = new Settings();
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

        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFullname = view.findViewById(R.id.setName);
        mEmail = view.findViewById(R.id.setEmail);
        mPhoto = view.findViewById(R.id.profile_img);

        String UID = firebaseUser.getUid().toString();
            // Name,  email address, and profile photo Url
            root = db.getReference().child("Users").child(UID);

            root.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("Name").getValue().toString();
                    String email = snapshot.child("Email").getValue().toString();
                    String sname = snapshot.child("Surname").getValue().toString();
                    String imagePath = snapshot.child("ImageURL").getValue().toString();

                    mFullname.setText(name +" "+sname);
                    mEmail.setText(email);
                    if(!imagePath.isEmpty()) {
                    Picasso .get().load(imagePath).into(mPhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "Database Connection Failed", Toast.LENGTH_SHORT).show();
                }
            });


//            String name = firebaseUser.getDisplayName();
//            String email = firebaseUser.getEmail();
//            Uri photoUrl = firebaseUser.getPhotoUrl();
//            mName.setText(name);
//            mEmail.setText(email);
//            Glide.with(getActivity()).load(photoUrl).into(mPhoto);





        profileBtn = view.findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Profile.class));
            }
        });

        changePassBtn = view.findViewById(R.id.changePassBtn);
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePass.class));
            }
        });



        logOutBtn = view.findViewById(R.id.logOutBtn);
//      Logging Out from this fragment
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), Logout.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}