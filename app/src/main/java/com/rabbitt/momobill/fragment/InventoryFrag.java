package com.rabbitt.momobill.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.ProductActivity;

public class InventoryFrag extends Fragment implements View.OnClickListener {


    private static final String TAG = "maluInventory";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public InventoryFrag() {
        // Required empty public constructor
    }

    public static InventoryFrag newInstance(String param1, String param2) {
        InventoryFrag fragment = new InventoryFrag();
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
        View inflate = inflater.inflate(R.layout.fragment_product, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View view) {
        //Initialization and Declaration
        FloatingActionButton fab = view.findViewById(R.id.fab_product_add);


        //Onclick listener
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), ProductActivity.class));
        Toast.makeText(getActivity(), "Fab clicked", Toast.LENGTH_SHORT).show();
    }
}