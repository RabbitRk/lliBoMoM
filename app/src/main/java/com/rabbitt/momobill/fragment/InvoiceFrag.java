package com.rabbitt.momobill.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.CartSheet;
import com.rabbitt.momobill.adapter.GridSpacingItemDecoration;
import com.rabbitt.momobill.adapter.InvoicePAdapter;
import com.rabbitt.momobill.model.ProductInvoice;

import java.util.ArrayList;
import java.util.List;

public class InvoiceFrag extends Fragment implements InvoicePAdapter.OnRecyleItemListener, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "maluInvoice";

    private String mParam1;
    private String mParam2;

    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data = new ArrayList<>();
    private InvoicePAdapter productAdapter;

    public InvoiceFrag() {
        // Required empty public constructor
    }

    public static InvoiceFrag newInstance(String param1, String param2) {
        InvoiceFrag fragment = new InvoiceFrag();
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
        View inflate = inflater.inflate(R.layout.fragment_invoice, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View inflate) {

        invoice_recycler = inflate.findViewById(R.id.recycler_product_invoice);

        Button cart = inflate.findViewById(R.id.cart_btn);

        MaterialSpinner spinner = (MaterialSpinner) inflate.findViewById(R.id.spinner);
        spinner.setItems("Ice Cream Sandwich", "Jelly Bean", "KitKat", "Lollipop", "Marshmallow");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

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
                    String gst = snapshot.child("cgst_sgst").getValue(String.class);
                    String cess = snapshot.child("cess").getValue(String.class);

                    ProductInvoice product = new ProductInvoice();
                    product.setImg_url(img_url);
                    product.setProduct_name(product_name);
                    product.setQuantity(quantity);
                    product.setSale_rate(sale_rate);
                    product.setUnit(unit);
                    product.setProduct_id(product_id);
                    product.setCgst(gst);
                    product.setCess(cess);

                    data.add(product);
                }
                updateRecycler(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cart.setOnClickListener(this);
    }

    private void updateRecycler(List<ProductInvoice> data) {
        productAdapter = new InvoicePAdapter(data, this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
        invoice_recycler.setLayoutManager(gridLayoutManager);
        invoice_recycler.addItemDecoration(new GridSpacingItemDecoration(3, 10, true));
        invoice_recycler.setItemAnimator(new DefaultItemAnimator());
        invoice_recycler.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
        invoice_recycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void onClick(View v) {
        CartSheet cartSheet = new CartSheet(data, this, this);
        cartSheet.show(getParentFragmentManager(), "cart");
    }
}