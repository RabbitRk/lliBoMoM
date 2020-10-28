package com.rabbitt.momobill.adapter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rabbitt.momobill.fragment.BlueToothFragment;
import com.rabbitt.momobill.fragment.ClientFrag;
import com.rabbitt.momobill.fragment.PDFfragment;
import com.rabbitt.momobill.model.ProductInvoice;

import java.io.Serializable;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    Context context;
    int count;
    String fname;
    List<ProductInvoice> data;
    Bundle data_client;

    public TabAdapter(Context context, FragmentManager supportFragmentManager, int count, String fname, List<ProductInvoice> data, Bundle data_) {
        super(supportFragmentManager);
        this.context = context;
        this.count = count;
        this.fname = fname;
        this.data = data;
        this.data_client = data_;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PDFfragment pdFfragment = new PDFfragment();
                Bundle bundle = new Bundle();
                bundle.putString("inv", fname);
                bundle.putBundle("client", data_client);
                pdFfragment.setArguments(bundle);
                return pdFfragment;

            case 1:
                Bundle bundle1 = new Bundle();
                bundle1.putString("inv", fname);
                bundle1.putSerializable("data", (Serializable) data);
                return BlueToothFragment.newInstance(bundle1);
        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
}
