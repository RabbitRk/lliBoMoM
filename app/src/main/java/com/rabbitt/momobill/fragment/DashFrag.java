package com.rabbitt.momobill.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.OpeningActivity;
import com.rabbitt.momobill.activity.SettingsActivity;

public class DashFrag extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    CardView today, month, invoice, credit;

    public DashFrag() {
        // Required empty public constructor
    }

    public static DashFrag newInstance(String param1, String param2) {
        DashFrag fragment = new DashFrag();
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
        View inflate = inflater.inflate(R.layout.fragment_dash, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View view) {
        today = view.findViewById(R.id.txt_today);
        month = view.findViewById(R.id.txt_month);
        credit = view.findViewById(R.id.txt_credit);
        invoice = view.findViewById(R.id.txt_invoice);

        today.setOnClickListener(this);
        month.setOnClickListener(this);
        credit.setOnClickListener(this);
        invoice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.txt_today:
                startActivity(new Intent(getActivity(), OpeningActivity.class));
                break;
            case R.id.txt_month:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.txt_credit:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.txt_invoice:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }
    }


}