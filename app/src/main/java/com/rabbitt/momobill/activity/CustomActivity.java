package com.rabbitt.momobill.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.CustomAdapter;
import com.rabbitt.momobill.adapter.DeleteSheet;
import com.rabbitt.momobill.adapter.OpenAdapter;
import com.rabbitt.momobill.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomActivity extends AppCompatActivity implements CustomAdapter.OnRecyleItemListener {
    private static final String TAG = "maluCustom";

    private RecyclerView recyclerView;
    private List<Product> data = new ArrayList<>();
    private CustomAdapter openAdapter;
    private String clientid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        clientid = getIntent().getStringExtra("clientid");
        init();
    }

    private void init() {
        recyclerView = findViewById(R.id.customRecycler);

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
                    String sale_rate = snapshot.child("sale_rate").getValue(String.class);
                    String unit = snapshot.child("unit").getValue(String.class);
                    String product_id = snapshot.child("product_id").getValue(String.class);
                    String gst = snapshot.child("cgst_sgst").getValue(String.class);
                    String inc = snapshot.child("in_ex").getValue(String.class);
                    String cess = snapshot.child("cess").getValue(String.class);

                    Product product = new Product();
                    product.setImg_url(img_url);
                    product.setProduct_name(product_name);
                    product.setSale_rate(sale_rate);
                    product.setUnit(unit);
                    product.setProduct_id(product_id);

                    //GST value
                    product.setGst(gst);
                    product.setInc(inc);
                    product.setCess(cess);


                    data.add(product);
                }
                updateRecycler(data);
//                parseData(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateRecycler(List<Product> data) {
        if (openAdapter != null) {
            openAdapter.notifyDataSetChanged();
        }
        //Update the recycler view
        openAdapter = new CustomAdapter(data, this, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(openAdapter);
        openAdapter.notifyDataSetChanged();

    }

    @Override
    public void OnSettle(int position) {
        Product pro = data.get(position);
        String pro_id = pro.getProduct_id();
        String pro_name = pro.getProduct_name();
        openDialog(pro_id, pro_name);
    }

    @SuppressLint("SetTextI18n")
    public void openDialog(final String pro_id, String pro_name) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_pop);
        dialog.setCancelable(true);

        TextView name = dialog.findViewById(R.id.text);
        final EditText rate = dialog.findViewById(R.id.rate);

        name.setText(pro_name);

        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rate.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter rate", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "onClick: "+"data");
                    updateFirebase(dialog, pro_id, rate.getText().toString().trim());
                }
            }
        });

        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public void updateFirebase(final Dialog dialog, String ex_unit, String product_id) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Custom").child(clientid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("product_id", String.valueOf(ex_unit));
        hashMap.put("sale_rate", String.valueOf(product_id));

        Log.i(TAG, "addProduct: " + hashMap.toString());
        reference.child(ex_unit).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "onComplete: " + task.toString());
                Toast.makeText(getApplicationContext(), "Units added successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.toString());
            }
        });
        dialog.dismiss();
    }
}