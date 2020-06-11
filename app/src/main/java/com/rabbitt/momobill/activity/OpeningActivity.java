package com.rabbitt.momobill.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.OpenAdapter;
import com.rabbitt.momobill.model.Product;
import com.rabbitt.momobill.prefsManager.IncrementPref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OpeningActivity extends AppCompatActivity implements OpenAdapter.OnRecyleItemListener {
    private static final String TAG = "maluOpen";
    EditText product;

    private RecyclerView recyclerView;
    private List<Product> data = new ArrayList<>();
    private OpenAdapter openAdapter;
    private HashMap<String, Object> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        init();
    }

    private void init() {
        product = findViewById(R.id.txt_product_search);
        recyclerView = findViewById(R.id.recycler_product);

        getData();
    }

    private void getData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Product");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: " + dataSnapshot);
                if (data != null) {
                    data.clear();
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String img_url = snapshot.child("img_url").getValue(String.class);
                    String product_name = snapshot.child("product_name").getValue(String.class);
                    String quantity = snapshot.child("quantity").getValue(String.class);
                    String sale_rate = snapshot.child("sale_rate").getValue(String.class);
                    String unit = snapshot.child("unit").getValue(String.class);
                    String product_id = snapshot.child("product_id").getValue(String.class);

                    Product product = new Product();
                    product.setImg_url(img_url);
                    product.setProduct_name(product_name);
                    product.setQuantity(quantity);
                    product.setSale_rate(sale_rate);
                    product.setUnit(unit);
                    product.setProduct_id(product_id);

                    data.add(product);
                }
                updateRecycler(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EditText edx = findViewById(R.id.txt_product_search);
        edx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product item : data) {
            if (item.getProduct_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        openAdapter.filterList(filteredList);
    }

    private void updateRecycler(List<Product> data) {
        if (openAdapter != null) {
            openAdapter.notifyDataSetChanged();
        }
        //Update the recycler view
        openAdapter = new OpenAdapter(data, this, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(openAdapter);
        openAdapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);

    }

//    public void opening_submit(View view) {
//
//    }

    public void fab_report(View view) {
        startActivity(new Intent(this, LiveReport.class));
    }

    @Override
    public void OnItemClick(int position, String s) {

        if (s.equals(""))
        {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "OnItemClick: " + position + " ent_unit: " + s);
        Log.i(TAG, "pos " + position);
        Product model = data.get(position);

        String product_id = model.getProduct_id();
        String unit = model.getUnit();
        String quantity = model.getQuantity();
        String name = model.getProduct_name();

        Log.i(TAG, "OnItemClick: p: " + product_id + "  u: " + unit + "  q:" + quantity + "  n: " + name);


        final IncrementPref i = new IncrementPref(this);

        //Check in the Inventory
        if (Integer.parseInt(unit) >= Integer.parseInt(s)) {

            final String OP_VAL = i.getOpeningVal();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Opening");

            //Getting Existing data from the database

//            DatabaseReference exist = FirebaseDatabase.getInstance().getReference()
//                    .child("Opening").child(getDate());
//
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.i(TAG, "onDataChange: " + dataSnapshot);
//                    if (data != null) {
//                        data.clear();
//                    }
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                        String img_url = snapshot.child("img_url").getValue(String.class);
//                        String product_name = snapshot.child("product_name").getValue(String.class);
//                        String quantity = snapshot.child("quantity").getValue(String.class);
//                        String sale_rate = snapshot.child("sale_rate").getValue(String.class);
//                        String unit = snapshot.child("unit").getValue(String.class);
//                        String product_id = snapshot.child("product_id").getValue(String.class);
//
//                    }
//                    updateRecycler(data);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });


            //Adding to the entered value to the existing database value


            //adding products to database
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("product_name", name);
            hashMap.put("unit", s);
            hashMap.put("quantity", quantity);
            Log.i(TAG, "addProduct: " + hashMap.toString());
            reference.child(getDate()).child(product_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i(TAG, "onComplete: " + task.toString());
                    i.setOpeningVal(String.valueOf(Integer.parseInt(OP_VAL) + 1));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: " + e.toString());
                }
            });
        }
        else
        {
            Toast.makeText(this, "Units Invalid. Please update the stock", Toast.LENGTH_SHORT).show();
        }

    }

    public String getDate() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.short_format));

        return df.format(c);
    }
}