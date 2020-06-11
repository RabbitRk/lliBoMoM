package com.rabbitt.momobill.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.LiveAdapter;
import com.rabbitt.momobill.model.Opening;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LiveReport extends AppCompatActivity implements LiveAdapter.OnRecyleItemListener {

    private static final String TAG = "maluLive";
    private RecyclerView recyclerView;
    private List<Opening> data = new ArrayList<>();
    private LiveAdapter openAdapter;
    private HashMap<String, Object> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_report);

        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.recycler_product);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Opening").child(getDate());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: " + dataSnapshot);
                if (data != null) {
                    data.clear();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    int _sale = 0;
                    String product_name = snapshot.child("product_name").getValue(String.class);
                    String unit = snapshot.child("unit").getValue(String.class);
                    String quantity = snapshot.child("quantity").getValue(String.class);
                    String sale = snapshot.child("sale").getValue(String.class);

                    //Updating modal
                    Opening product = new Opening();
                    product.setProduct_name(product_name+" "+quantity+" ml");
                    product.setUnit(unit);

                    if (sale!=null)
                    {
                        product.setSale(sale);
                        _sale = Integer.parseInt(sale);
                    }
                    else
                    {
                        product.setSale("0");
                        _sale = 0;
                    }

                    int _unit = Integer.parseInt(unit);
                    int _bal = _unit - _sale;

                    //Setting Balance
                    product.setBalance(String.valueOf(_bal));





                    product.setProduct_id(snapshot.getKey());

                    data.add(product);
                }
                updateRecycler(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateRecycler(List<Opening> data) {
        if (openAdapter != null) {
            openAdapter.notifyDataSetChanged();
        }
        //Update the recycler view
        openAdapter = new LiveAdapter(data, this, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(openAdapter);
        openAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);

    }


    public String getDate() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.short_format));

        return df.format(c);
    }

    @Override
    public void OnItemClick(int position) {

    }
}