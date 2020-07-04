package com.rabbitt.momobill.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.rabbitt.momobill.adapter.OrderAdapter;
import com.rabbitt.momobill.model.ProductInvoice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InvoiceFrag extends Fragment implements InvoicePAdapter.OnRecyleItemListener, View.OnClickListener, CartSheet.cartDelete, OrderAdapter.OnRecyleItemListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "maluInvoice";

    private String mParam1;
    private String mParam2;

    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data = new ArrayList<>();
    private List<ProductInvoice> cart = new ArrayList<>();
    private InvoicePAdapter productAdapter;
    private MaterialSpinner spinner;

    final ArrayList<String> clients = new ArrayList<>();
    final ArrayList<String> client_id = new ArrayList<>();

    private LinearLayout order_layout;
    private RecyclerView order_recycler;

    private List<ProductInvoice> order = new ArrayList<>();
    private List<ProductInvoice> credit = new ArrayList<>();
    private OrderAdapter OrderAdapter;

    private Button cart_btn_order;



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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_invoice, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View inflate) {

        invoice_recycler = inflate.findViewById(R.id.recycler_product_invoice);

        Button cart = inflate.findViewById(R.id.cart_btn);
        cart_btn_order = inflate.findViewById(R.id.cart_btn_order);

        spinner = (MaterialSpinner) inflate.findViewById(R.id.spinner);

        //Layout Initialization
        order_layout = inflate.findViewById(R.id.order_layout);
        order_recycler = inflate.findViewById(R.id.recycler_order_invoice);

        getClients();

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
                    String in_ex = snapshot.child("in_ex").getValue(String.class);

                    ProductInvoice product = new ProductInvoice();
                    product.setImg_url(img_url);
                    product.setProduct_name(product_name);
                    product.setQuantity(quantity);
                    product.setSale_rate(sale_rate);
                    product.setUnit(unit);
                    product.setProduct_id(product_id);
                    product.setCgst(gst);
                    product.setCess(cess);
                    product.setIn(in_ex);

                    data.add(product);
                }
                updateRecycler(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EditText edx = inflate.findViewById(R.id.txt_name);
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

        cart.setOnClickListener(this);
        cart_btn_order.setOnClickListener(this);
    }

    private void getClients() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Client");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange: " + dataSnapshot);
//                if (data != null) {
//                    data.clear();
//                }

                clients.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Getting string from the snapshot
                    String name = snapshot.child("name").getValue(String.class);
                    String client_id_ = snapshot.child("client_id").getValue(String.class);
                    //Arraylist for the spinner
                    clients.add(name);
                    //Arraylist for the ID
                    client_id.add(client_id_);
                }
                spinner.setItems(clients);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                Log.i(TAG, "onItemSelected: "+client_id.get(position)+" Position "+position+" ClientName "+clients.get(position));
                getOrder(client_id.get(position));
            }
        });
    }

    private void getOrder(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Order").child(getDate()).child(s);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange: " + dataSnapshot);

                order.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ProductInvoice data = new ProductInvoice();
                    String img_url = snapshot.child("img_url").getValue(String.class);
                    String product_name = snapshot.child("product_name").getValue(String.class);
                    String quantity = snapshot.child("quantity").getValue(String.class);
                    String sale_rate = snapshot.child("sale_rate").getValue(String.class);
                    String unit = snapshot.child("unit").getValue(String.class);
                    String product_id = snapshot.child("product_id").getValue(String.class);
                    String gst = snapshot.child("cgst").getValue(String.class);
                    String cess = snapshot.child("cess").getValue(String.class);
                    String in_ex = snapshot.child("in_ex").getValue(String.class);

                    ProductInvoice product = new ProductInvoice();
                    product.setImg_url(img_url);
                    product.setProduct_name(product_name);
                    product.setQuantity(quantity);
                    product.setSale_rate(sale_rate);
                    product.setUnit(unit);
                    product.setProduct_id(product_id);
                    product.setCgst(gst);
                    product.setCess(cess);
                    product.setIn(in_ex);

                    order.add(product);
                }
                Log.i(TAG, "onDataChange: "+order.size());

                if (order.size() != 0)
                {
                    updateOrder(order);
                }
//                Log.i(TAG, "onDataChange: "+order.remove(order.size()-1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateOrder(List<ProductInvoice> data)
    {
        order_layout.setVisibility(View.VISIBLE);
        OrderAdapter = new OrderAdapter(data, this, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(getActivity());
        order_recycler.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        order_recycler.setAdapter(OrderAdapter);
        OrderAdapter.notifyDataSetChanged();
        order_recycler.setVisibility(View.VISIBLE);
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
        Log.i(TAG, "OnItemClick: " + position);
        Log.i(TAG, "pos " + position);
        ProductInvoice model = data.get(position);

        String product_id = model.getProduct_id();
        String unit = model.getUnit();
        String quantity = model.getQuantity();
        String name = model.getProduct_name();

        Log.i(TAG, "OnItemClick: " + product_id + "  " + unit + "  " + quantity + "  " + name);

        openDialog(model, unit, quantity, name, product_id);
    }

    @SuppressLint("SetTextI18n")
    public void openDialog(final ProductInvoice model, final String ex_unit, String quantity, String name_, final String product_id) {
        
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.invoice_dialog);
        dialog.setCancelable(true);

        TextView name = dialog.findViewById(R.id.text);
        TextView quanitiy = dialog.findViewById(R.id.dia_quantity);
        final EditText units = dialog.findViewById(R.id.units);

        name.setText(name_);
        quanitiy.setText(quantity + " ml");

        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (units.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please enter units", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    double sale_ = Double.parseDouble(model.getSale_rate()) * Double.parseDouble(units.getText().toString().trim());
                    ProductInvoice product = new ProductInvoice();
                    product.setProduct_name(model.getProduct_name());
                    product.setQuantity(model.getQuantity());
                    product.setSale_rate(String.valueOf(sale_));
                    product.setUnit(units.getText().toString().trim());
                    product.setProduct_id(model.getProduct_id());
                    product.setCgst(model.getCgst());
                    product.setCess(model.getCess());
                    product.setImg_url(model.getImg_url());
                    product.setIn(model.getIn());

                    cart.add(product);
                    dialog.dismiss();
//                     createInvoice(dialog, ex_unit, product_id, units.getText().toString().trim());
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



    private void createInvoice(final Dialog dialog, String ex_unit, String product_id, String trim) {

//        int unit = Integer.parseInt(ex_unit) + Integer.parseInt(new_unit);

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Invoice");
//
//        IncrementPref pref = new IncrementPref(getActivity());
//        String invoice_num = pref.getInvoiceId();
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("invoice_id", invoice_num);
//
//        Log.i(TAG, "addProduct: " + hashMap.toString());
//        reference.child(invoice_num).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Log.i(TAG, "onComplete: " + task.toString());
//                Toast.makeText(getActivity(), "Units added successfully", Toast.LENGTH_SHORT).show();
//                dialog.dismiss();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.i(TAG, "onFailure: " + e.toString());
//            }
//        });
//        dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cart_btn_order)
        {
            Toast.makeText(getActivity(), "Buttonclicked", Toast.LENGTH_SHORT).show();
            cart = order;
//            order.clear();
            order_layout.setVisibility(View.GONE);
        }
        else
        {
            Toast.makeText(getActivity(), "cart", Toast.LENGTH_SHORT).show();

            CartSheet cartSheet = new CartSheet(cart, this, this, this, "invoice",  client_id.get(spinner.getSelectedIndex()), getDate(), getContext());
            cartSheet.show(getParentFragmentManager(), "cart");
        }
    }

    private void filter(String text) {
        List<ProductInvoice> filteredList = new ArrayList<>();
        for (ProductInvoice item : data) {
            if (item.getProduct_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        productAdapter.filterList(filteredList);
    }

    @Override
    public void OnDelete(int position) {
        cart.remove(position);
    }

    public String getDate() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.short_format));

        return df.format(c);
    }

    @Override
    public void OrderAdd(int position) {
        Toast.makeText(getActivity(), "Add", Toast.LENGTH_SHORT).show();
    }
}