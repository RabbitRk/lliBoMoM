
package com.rabbitt.momobill.activity;

import android.app.ProgressDialog;
import android.os.Bundle;

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
import com.rabbitt.momobill.adapter.OrderDetailsAdapter;
import com.rabbitt.momobill.model.OrderDetails;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<OrderDetails> data;

    ProgressDialog progressDialog;

    String date, c_id, product, units, amount;
    DatabaseReference orderRef, clientRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        date = getIntent().getStringExtra("date");
        c_id = getIntent().getStringExtra("c_id");

        recyclerView = findViewById(R.id.recycler_view);

        progressDialog = ProgressDialog.show(this, "Please wait", "Loading", true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<OrderDetails>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("Order");


        populateRecycler();
    }

    private void populateRecycler() {

        data.clear();

        orderRef.child(date).child(c_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot clientChild : dataSnapshot.getChildren()) {
                    final String p_id = clientChild.getKey();
                    DataSnapshot product_child = dataSnapshot.child(p_id);
                    product = product_child.child("product_name").getValue().toString();
                    units = product_child.child("unit").getValue().toString();
                    amount = product_child.child("sale_rate").getValue().toString();
                    data.add(new OrderDetails(product, units, amount));
                }
                adapter = new OrderDetailsAdapter(data);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}

