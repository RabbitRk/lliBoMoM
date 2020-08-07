package com.rabbitt.momobill.demo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rabbitt.momobill.MainActivity;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.TabAdapter;
import com.rabbitt.momobill.model.ProductInvoice;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class pdfreader extends AppCompatActivity {

    private static final String TAG = "maluPDFReader";
    String fname;
    List<ProductInvoice> data = null;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        fname = getIntent().getStringExtra("inv");
        bundle = getIntent().getBundleExtra("inc");

        if (bundle != null) {
            Log.i(TAG, "onCreate: PDF Reader " + bundle.size());
        } else {
            Log.i(TAG, "onCreate: Buble null");
        }
        ArrayList<ProductInvoice> object = (ArrayList<ProductInvoice>) bundle.getSerializable("valuesArray");

        if (object != null) {
            Log.i(TAG, "onCreate: Array yes "+object.size());
            for(ProductInvoice ob : object)
            {
                Log.i(TAG, "Product Name "+ob.getProduct_name());
                Log.i(TAG, "Quantity "+ob.getUnit());
                Log.i(TAG, "Total "+ob.getSale_rate());

            }
        }

        else
        {
            Log.i(TAG, "onCreate: Array: no");
        }

//        try {
//            data = (List<ProductInvoice>) bytes2Object(bundle.getByteArray("val"));
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }



        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("PDF"));
        tabs.addTab(tabs.newTab().setText("Thermal"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        TabAdapter tabAdapter = new TabAdapter(this, getSupportFragmentManager(), tabs.getTabCount(), fname, object);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("nav",true);
        startActivity(intent);
    }

    static public Object bytes2Object(byte raw[])
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(raw);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return o;
    }
}
