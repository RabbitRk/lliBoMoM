package com.rabbitt.momobill.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.ClientActivity;
import com.rabbitt.momobill.model.Client;

public class ClientFrag extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "maluClientFrag";

    private String mParam1;
    private String mParam2;

    public ClientFrag() {
        // Required empty public constructor
    }

    public static ClientFrag newInstance(String param1, String param2) {
        ClientFrag fragment = new ClientFrag();
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
        View inflate = inflater.inflate(R.layout.fragment_client, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View view) {
        //Initialization and Declaration
        FloatingActionButton fab = view.findViewById(R.id.fab_product_add);


        //Onclick listener
        fab.setOnClickListener(this);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Client");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: "+dataSnapshot);
                Client client = dataSnapshot.getValue(Client.class);
                if (client != null) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "FAB Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), ClientActivity.class));
    }
}