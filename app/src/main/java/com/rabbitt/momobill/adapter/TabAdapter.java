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

public class TabAdapter extends FragmentPagerAdapter {

    Context context;
    int count;
    String fname;

    public TabAdapter(Context context, FragmentManager supportFragmentManager, int count, String fname) {
        super(supportFragmentManager);
        this.context = context;
        this.count = count;
        this.fname = fname;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                PDFfragment pdFfragment = new PDFfragment();
                Bundle bundle = new Bundle();
                bundle.putString("inv", fname);
                pdFfragment.setArguments(bundle);
                return pdFfragment;

            case 1:
                return new BlueToothFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
}
