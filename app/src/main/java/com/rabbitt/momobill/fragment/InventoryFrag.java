package com.rabbitt.momobill.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.EditProductActivity;
import com.rabbitt.momobill.activity.ProductActivity;
import com.rabbitt.momobill.adapter.CartSheet;
import com.rabbitt.momobill.adapter.DeleteSheet;
import com.rabbitt.momobill.adapter.ProductAdapter;
import com.rabbitt.momobill.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryFrag extends Fragment implements View.OnClickListener, ProductAdapter.OnRecyleItemListener {

    private static final String TAG = "maluInventory";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<Product> mProduct;

    private List<Product> data = new ArrayList<>();
    private ProductAdapter productAdapter;
    private RecyclerView productRecycler;
    List<Product> filteredList;
    EditText edx;

    public InventoryFrag() {
        // Required empty public constructor
    }

    public static InventoryFrag newInstance(String param1, String param2) {
        InventoryFrag fragment = new InventoryFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_product, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View view) {
        //Initialization and Declaration
        FloatingActionButton fab = view.findViewById(R.id.fab_product_add);
        productRecycler = view.findViewById(R.id.recycler_product);

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

                    Product product = new Product();
                    product.setImg_url(img_url);
                    product.setProduct_name(product_name);
                    product.setSale_rate(sale_rate);
                    product.setUnit(unit);
                    product.setProduct_id(product_id);

                    product.setGst(snapshot.child("cgst_sgst").getValue(String.class));
                    product.setCess(snapshot.child("cess").getValue(String.class));
                    product.setHsn(snapshot.child("hsn_code").getValue(String.class));
                    product.setPuchase(snapshot.child("purchase_rate").getValue(String.class));
                    product.setInc(snapshot.child("in_ex").getValue(String.class));

                    data.add(product);
                }
                updateRecycler(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edx = view.findViewById(R.id.txt_product_search);
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

        //Onclick listener
        fab.setOnClickListener(this);
    }

    private void updateRecycler(List<Product> data) {

        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
        //Update the recycler view
        productAdapter = new ProductAdapter(data, this, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(getActivity());
        productRecycler.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        productRecycler.setItemAnimator(new DefaultItemAnimator());
        productRecycler.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
        productRecycler.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), ProductActivity.class));
    }

    private void filter(String text) {
        filteredList = new ArrayList<>();
        for (Product item : data) {
            if (item.getProduct_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        productAdapter.filterList(filteredList);
    }

    @Override
    public void OnItemClick(int position) {
        Log.i(TAG, "OnItemClick: " + position);
        Log.i(TAG, "pos " + position);

        Product model;
        if (edx.getText().toString().equals("")) {
            model = data.get(position);
        } else {
            model = filteredList.get(position);
        }

        String product_id = model.getProduct_id();
        String unit = model.getUnit();
        String name = model.getProduct_name();
        String imgUrl = model.getImg_url();

        Log.i(TAG, "OnItemClick: " + product_id + "  " + unit + "  " + imgUrl + "  " + name);

        openDialog(unit, name, product_id, imgUrl);
    }

    @Override
    public void OnEditClick(int position) {
        Log.i(TAG, "OnItemClick: " + position);
        Log.i(TAG, "pos " + position);
        Product model = data.get(position);

        Intent intent = new Intent(getActivity(), EditProductActivity.class);
        intent.putExtra("Pro", model);
        startActivity(intent);

    }

    @SuppressLint("SetTextI18n")
    public void openDialog(final String ex_unit, String name_, final String product_id, final String imgUrl) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.unit_dialog);
        dialog.setCancelable(true);

        TextView name = dialog.findViewById(R.id.text);
        TextView quantity = dialog.findViewById(R.id.dia_quantity);
        final EditText units = dialog.findViewById(R.id.units);

        name.setText(name_);

        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (units.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please enter units", Toast.LENGTH_SHORT).show();
                } else {
                    updateFirebase(dialog, ex_unit, product_id, units.getText().toString().trim());
                }
            }
        });

        quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DeleteSheet cartSheet = new DeleteSheet(product_id, imgUrl);
                cartSheet.show(getParentFragmentManager(), "delete");
            }
        });

        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    public void updateFirebase(final Dialog dialog, String ex_unit, String product_id, String new_unit) {
        int unit = Integer.parseInt(ex_unit) + Integer.parseInt(new_unit);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("unit", String.valueOf(unit));

        Log.i(TAG, "addProduct: " + hashMap.toString());
        reference.child(product_id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "onComplete: " + task.toString());
                Toast.makeText(getActivity(), "Units added successfully", Toast.LENGTH_SHORT).show();
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