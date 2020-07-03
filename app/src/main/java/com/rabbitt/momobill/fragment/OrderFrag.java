package com.rabbitt.momobill.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.rabbitt.momobill.prefsManager.IncrementPref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OrderFrag extends Fragment implements InvoicePAdapter.OnRecyleItemListener, CartSheet.cartDelete, View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private static final String TAG = "maluOrder";

    private String mParam1;
    private String mParam2;

    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data = new ArrayList<>();
    private List<ProductInvoice> cart = new ArrayList<>();
    private InvoicePAdapter productAdapter;
    private MaterialSpinner spinner;
    final ArrayList<String> clients = new ArrayList<>();
    final ArrayList<String> client_id = new ArrayList<>();

    private DatePicker datePicker;
    private Calendar calendar;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private EditText dateTxt;
    public OrderFrag() {
        // Required empty public constructor
    }

    public static OrderFrag newInstance(String param1, String param2) {
        OrderFrag fragment = new OrderFrag();
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
        View inflate = inflater.inflate(R.layout.fragment_order, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View inflate) {

        invoice_recycler = inflate.findViewById(R.id.recycler_product_invoice);

        Button cart = inflate.findViewById(R.id.cart_btn);
        Button date_ = inflate.findViewById(R.id.date_btn);
        dateTxt = inflate.findViewById(R.id.txt_date);

        spinner = (MaterialSpinner) inflate.findViewById(R.id.spinner);


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
        date_.setOnClickListener(this);
    }

    private void getClients() {

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Client");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange: " + dataSnapshot);

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
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void updateRecycler(List<ProductInvoice> data) {
        productAdapter = new InvoicePAdapter(data, this,this);
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
                    product.setIn(model.getIn());

                    cart.add(product);
                    dialog.dismiss();
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
        cart.clear();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.date_btn:
                showDialog();
                break;
            case R.id.cart_btn:
                if (dateTxt.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(),   "Please select date", Toast.LENGTH_SHORT).show();
                }
                else {

                    if (cart.size() == 0)
                    {
                        Toast.makeText(getActivity(), "Please add products to the cart", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        CartSheet cartSheet = new CartSheet(cart, this, this, this, "order", client_id.get(spinner.getSelectedIndex()), dateTxt.getText().toString(), getContext());
                        cartSheet.show(getParentFragmentManager(), "cart");
                    }
                }
                break;
        }
    }

    private void showDialog() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        c.add(Calendar.DATE, -1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Date date = new Date(year - 1900, monthOfYear , dayOfMonth);
                        dateTxt.setText(getDate(date));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    public String getDate(Date date) {
//        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.short_format));

        return df.format(date);
    }
}