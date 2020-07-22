package com.rabbitt.momobill.demo;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.joanzapata.pdfview.PDFView;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.TabAdapter;

import java.io.File;
import java.io.IOException;

public class pdfreader extends AppCompatActivity {

    String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        fname = getIntent().getStringExtra("inv");

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("PDF"));
        tabs.addTab(tabs.newTab().setText("Thermal"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        TabAdapter tabAdapter = new TabAdapter(this, getSupportFragmentManager(),tabs.getTabCount(),fname);
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
