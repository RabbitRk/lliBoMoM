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
import com.rabbitt.momobill.adapter.OrderDateAdapter;
import com.rabbitt.momobill.model.OrderDate;

import java.util.ArrayList;

public class CheckOrderActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<OrderDate> data;

    public static View.OnClickListener myOnClickListener;



    public static DatabaseReference orderRef;

    ProgressDialog progressDialog;
    String date,dd,mm,yy,fdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_order);

        myOnClickListener = new CheckOrderActivity.MyOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_view);

        progressDialog= ProgressDialog.show(CheckOrderActivity.this,"Please wait","Loading",true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<OrderDate>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("Order");

        populateRecycler();
    }

    public void populateRecycler() {



        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    date = child.getKey();
                    fdate  = date;

                    yy = fdate.substring(0, fdate.indexOf("_"));
                    fdate = fdate.substring(fdate.indexOf("_") + 1);
                    mm = fdate.substring(0, fdate.indexOf("_"));
                    fdate = fdate.substring(fdate.indexOf("_") + 1);
                    dd = fdate;

                    fdate = dd + "/" + mm + "/" + yy;

                    data.add(new OrderDate(fdate, date));

                }
                adapter = new OrderDateAdapter(data);
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;


        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {

            int selectedItemPosition = recyclerView.getChildPosition(v);
            Intent intent = new Intent(context, OrderNameActivity.class);
            intent.putExtra("date",data.get(selectedItemPosition).getDate());
            context.startActivity(intent);

        }

    }
}