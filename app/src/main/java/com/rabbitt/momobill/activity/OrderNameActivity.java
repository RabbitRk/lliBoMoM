package com.rabbitt.momobill.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.rabbitt.momobill.adapter.OrderNameAdapter;
import com.rabbitt.momobill.model.OrderName;

import java.util.ArrayList;

public class OrderNameActivity extends AppCompatActivity {

    public static View.OnClickListener myOnClickListener;
    public static DatabaseReference rootRef;
    private static RecyclerView.Adapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<OrderName> data;
    ProgressDialog progressDialog;
    String c_name, c_id, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_name);

        myOnClickListener = new OrderNameActivity.MyOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_view);

        date = getIntent().getStringExtra("date");

        progressDialog = ProgressDialog.show(OrderNameActivity.this, "Please wait", "Loading", true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<OrderName>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        rootRef = database.getReference();

        populateRecycler();
    }

    private void populateRecycler() {

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot date_child : dataSnapshot.child("Order").child(date).getChildren()) {

                    c_id = date_child.getKey();
                    DataSnapshot client_child = dataSnapshot.child("Client").child(c_id).child("name");
                    c_name = String.valueOf(client_child.getValue());
                    data.add(new OrderName(c_id, c_name));
                }
                adapter = new OrderNameAdapter(data);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class MyOnClickListener implements View.OnClickListener {

        Context context;

        public MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View view) {

            int selectedItemPosition = recyclerView.getChildPosition(view);
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("c_id", data.get(selectedItemPosition).getId());
            context.startActivity(intent);

        }
    }
}