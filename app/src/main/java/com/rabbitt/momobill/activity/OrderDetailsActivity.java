
package com.rabbitt.momobill.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

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

    String date,name,product;
    DatabaseReference orderRef,clientRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        date = getIntent().getStringExtra("date");

        recyclerView = findViewById(R.id.recycler_view);

        progressDialog = ProgressDialog.show(this, "Please wait", "Loading", true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<OrderDetails>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("Order");
        clientRef = database.getReference("Client");

        populateRecycler();
    }

    private void populateRecycler() {

        data.clear();

            orderRef.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for(final DataSnapshot clientChild : dataSnapshot.getChildren()) {


                        final String clientId = clientChild.getKey();

                        clientRef.child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                name = dataSnapshot.child("name").getValue().toString();

                                for (DataSnapshot productChild : clientChild.getChildren()) {

//                        String productId = productChild.getKey();

                                    if (productChild.hasChildren()) {

                                        product = productChild.child("product_name").getValue().toString();
                                        Log.i("clientid",name + product);
                                        data.add(new OrderDetails(name,product));
                                        adapter = new OrderDetailsAdapter(data);
                                        recyclerView.setAdapter(adapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        }

                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });
    }
}
