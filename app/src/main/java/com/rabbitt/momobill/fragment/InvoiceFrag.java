package com.rabbitt.momobill.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.CartSheet;
import com.rabbitt.momobill.adapter.ClientAutoAdapter;
import com.rabbitt.momobill.adapter.CreditAdapter;
import com.rabbitt.momobill.adapter.GridSpacingItemDecoration;
import com.rabbitt.momobill.adapter.InvoicePAdapter;
import com.rabbitt.momobill.adapter.LineAdapter;
import com.rabbitt.momobill.adapter.OrderAdapter;
import com.rabbitt.momobill.model.ClientModel;
import com.rabbitt.momobill.model.Credit;
import com.rabbitt.momobill.model.Custom;
import com.rabbitt.momobill.model.Line;
import com.rabbitt.momobill.model.ProductInvoice;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceFrag extends Fragment implements InvoicePAdapter.OnRecyleItemListener, View.OnClickListener, OrderAdapter.OnRecyleItemListener, CreditAdapter.OnRecyleItemListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "maluInvoice";

    final ArrayList<ClientModel> clientList = new ArrayList<>();
    private String mParam1;
    private String mParam2;

    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data = new ArrayList<>();
    private List<ProductInvoice> cart = new ArrayList<>();
    private InvoicePAdapter productAdapter;
//    private MaterialSpinner spinner;

    final ArrayList<String> clients = new ArrayList<>();
    final ArrayList<String> client_id = new ArrayList<>();


    private LinearLayout order_layout;
    private RecyclerView order_recycler;

    private List<ProductInvoice> order = new ArrayList<>();
    private OrderAdapter OrderAdapter;

    private Button cart_btn_order;

    private List<Credit> credit = new ArrayList<>();
    private CreditAdapter creditAdapter;
    private LinearLayout credit_layout;
    private RecyclerView credit_recycler;

    String[] ar;
    String clientId;
    String selectedLine, selectedClient;
    LineAdapter adapter;
    ArrayList<Line> linelist;
    AutoCompleteTextView line, clientAutoTV;
    DatabaseReference lineReference;
    SharedPreferences preferences;
    EditText edx;

    public InvoiceFrag() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.edit().putString("invoiceLine", selectedLine).apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        preferences.edit().putString("invoiceLine", selectedLine).apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        preferences.edit().putString("invoiceLine", selectedLine).apply();
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

        linelist = new ArrayList<>();
        line.setEnabled(true);
        line.setEms(15);

        clientAutoTV.setEnabled(true);
        clientAutoTV.setEms(15);

        lineReference = FirebaseDatabase.getInstance().getReference("Line");
        lineReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;
                ar = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    ar[i] = child.getKey();

                    //Updates to Customer on every single changes
                    linelist.add(new Line(ar[i]));
                    adapter = new LineAdapter(getContext(), linelist);
                    line.setAdapter(adapter);
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return inflate;
    }

    private void init(View inflate) {

        invoice_recycler = inflate.findViewById(R.id.recycler_product_invoice);

        Button cart = inflate.findViewById(R.id.cart_btn);
        cart_btn_order = inflate.findViewById(R.id.cart_btn_order);

//        spinner = (MaterialSpinner) inflate.findViewById(R.id.spinner);

        line = inflate.findViewById(R.id.txt_line);
        clientAutoTV = inflate.findViewById(R.id.txt_client);

        try {

            preferences = getContext().getSharedPreferences("invoiceFrag", Context.MODE_PRIVATE);
            final String lineTxt = preferences.getString("invoiceLine", "");
            selectedLine = lineTxt;
            line.setText(lineTxt);
            getClients();
        } catch (Exception e) {
            e.printStackTrace();
        }

        line.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLine = linelist.get(i).getLine();
                preferences.edit().putString("orderLine", selectedLine).apply();
//                Toast.makeText(getContext(), selectedLine, Toast.LENGTH_SHORT).show();
                getClients();
            }
        });

        //Layout Initialization
        order_layout = inflate.findViewById(R.id.order_layout);
        order_recycler = inflate.findViewById(R.id.recycler_order_invoice);

        credit_layout = inflate.findViewById(R.id.credit_layout);
        credit_recycler = inflate.findViewById(R.id.recycler_credit_invoice);


        edx = inflate.findViewById(R.id.txt_name);
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
                .child("Line");

        reference.child(selectedLine).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Log.i(TAG, "onDataChange: " + dataSnapshot);
                clientList.clear();
                clients.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Getting string from the snapshot
                    String client_id_ = snapshot.getKey();

                    String name = snapshot.getValue().toString();

                    Log.i(TAG, "onDataChange: " + client_id_);
                    //Arraylist for the spinner
                    clientList.add(new ClientModel(name, client_id_));

                    ClientAutoAdapter autoAdapter = new ClientAutoAdapter(getContext(), clientList);
                    clientAutoTV.setAdapter(autoAdapter);

                    clients.add(name);
                    //Arraylist for the ID
                    client_id.add(client_id_);
                }

                Log.i(TAG, "onDataChange: " + client_id);
//                spinner.setItems(clients);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        clientAutoTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedClient = clientList.get(i).getClient();
                clientId = clientList.get(i).getClientid();
                Log.i(TAG, "TextOfCredit: " + selectedClient + "   " + clientId);
                Log.i(TAG, "onItemSelected: " + client_id.get(i) + " Position " + i + " ClientName " + clients.get(i));
                getOrder(clientId);
                getCredit(clientId);
                getProduct(clientId);
            }
        });
    }

    String sale_rate;

    private void getProduct(String clientId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Product");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Custom").child(clientId);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot in) {

                        custom(dataSnapshot, in);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void custom(DataSnapshot dataSnapshot, DataSnapshot custom) {
//        Log.i(TAG, "onDataChange: " + dataSnapshot);
        Log.i(TAG, "Client==========>: " + custom);

        if (data != null) {
            data.clear();
        }
        String product_id = "";

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//            Log.i(TAG, "onDataChange Main: ===============>" + snapshot.getKey());
            String img_url = snapshot.child("img_url").getValue(String.class);
            String product_name = snapshot.child("product_name").getValue(String.class);
            String unit = snapshot.child("unit").getValue(String.class);
            product_id = snapshot.child("product_id").getValue(String.class);
            String gst = snapshot.child("cgst_sgst").getValue(String.class);
            String cess = snapshot.child("cess").getValue(String.class);
            String in_ex = snapshot.child("in_ex").getValue(String.class);
            String hsn = snapshot.child("hsn").getValue(String.class);
            String mrp = snapshot.child("purchase_rate").getValue(String.class);
            sale_rate = snapshot.child("sale_rate").getValue(String.class);
            try {
                for (DataSnapshot in : custom.getChildren()) {
                    Log.i(TAG, "custom: IN "+ in.getKey() +" Pro snap "+in.child("product_id").getValue(String.class)+" Pro_id "+product_id+ " Client id "+clientId );
                    if (in.child("product_id").getValue(String.class).equals(product_id)) {
                        sale_rate = in.child("sale_rate").getValue(String.class);
                        break;
                    }
//                    else {
//                        sale_rate = snapshot.child("sale_rate").getValue(String.class);
//                        break;
//                    }
                }
            }
            catch (Exception ex)
            {
                Log.i(TAG, "custom: Exception "+ ex.toString()+"  "+ex.getMessage());
            }


            ProductInvoice product = new ProductInvoice();
            product.setImg_url(img_url);
            product.setProduct_name(product_name);
            product.setSale_rate(sale_rate);
            product.setUnit(unit);
            product.setProduct_id(product_id);
            product.setCgst(gst);
            product.setCess(cess);
            product.setIn(in_ex);
            product.setHsn(hsn);
            product.setMrp(mrp);

//                Log.i(TAG, "onDataChange: GSON -------------------- " + new Gson().toJson(product));

            data.add(product);
        }

        updateRecycler(data);
    }

    private void getCredit(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Credits").child(s);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange: " + dataSnapshot);

                if (credit.size() != 0) {
                    credit.clear();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.i(TAG, "invoice_id: " + snapshot.getKey());
                    Log.i(TAG, "invoice_id: " + snapshot.child("balance").getValue(String.class));

                    Credit cre = new Credit();
                    cre.setBalance(snapshot.child("balance").getValue(String.class));
                    cre.setDate_on(snapshot.child("date_of").getValue(String.class));
                    cre.setClient_id(snapshot.child("client_id").getValue(String.class));
                    cre.setInvoice_id(snapshot.getKey());

                    if (!snapshot.child("balance").getValue(String.class).equals("0")) {
                        credit.add(cre);
                    }
                }
                Log.i(TAG, "onDataChange: " + credit.size());

                if (credit.size() != 0) {
                    Log.i(TAG, "onDataChange: Ready for Recycler update");
                    updateCredit(credit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateCredit(List<Credit> credit) {
        credit_layout.setVisibility(View.VISIBLE);
        creditAdapter = new CreditAdapter(credit, this, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(getActivity());
        credit_recycler.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        credit_recycler.setAdapter(creditAdapter);
        creditAdapter.notifyDataSetChanged();
        credit_recycler.setVisibility(View.VISIBLE);
    }

    private void getOrder(String s) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Order").child(getDate()).child(s);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange: " + dataSnapshot);

                if (order.size() != 0) {
                    order.clear();
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String img_url = snapshot.child("img_url").getValue(String.class);
                    String product_name = snapshot.child("product_name").getValue(String.class);
                    String sale_rate = snapshot.child("sale_rate").getValue(String.class);
                    String unit = snapshot.child("unit").getValue(String.class);
                    String product_id = snapshot.child("product_id").getValue(String.class);
                    String gst = snapshot.child("cgst").getValue(String.class);
                    String cess = snapshot.child("cess").getValue(String.class);
                    String in_ex = snapshot.child("in_ex").getValue(String.class);
                    String mrp = snapshot.child("purchase_rate").getValue(String.class);

                    ProductInvoice product = new ProductInvoice();
                    product.setImg_url(img_url);
                    product.setProduct_name(product_name);
                    product.setSale_rate(sale_rate);
                    product.setUnit(unit);
                    product.setProduct_id(product_id);
                    product.setCgst(gst);
                    product.setCess(cess);
                    product.setIn(in_ex);
                    product.setMrp(mrp);


                    order.add(product);
                }
                Log.i(TAG, "onDataChange: " + order.size());

                if (order.size() != 0) {
                    updateOrder(order);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateOrder(List<ProductInvoice> data) {
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
    public void OnItemClick(final int position) {
        Log.i(TAG, "OnItemClick: " + position);
        Log.i(TAG, "pos " + position);

        final ProductInvoice model;

        if (edx.getText().toString().equals("")) {
            model = data.get(position);
        } else {
            model = filteredList.get(position);
        }

        final String product_id = model.getProduct_id();
        final String unit = model.getUnit();
        final String name = model.getProduct_name();

        Log.i(TAG, "OnItemClick: " + product_id + "  " + unit + "  " + name);

        DatabaseReference openRef = FirebaseDatabase.getInstance().getReference("Opening");

        openRef.child(getDate()).child(product_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int tot = 0, avl = 0, bal = 0;
                Object ob_unit, sale;
                ob_unit = dataSnapshot.child("unit").getValue();
                sale = dataSnapshot.child("sale").getValue();

                if (ob_unit != null)
                    tot = Integer.parseInt(String.valueOf(ob_unit));
                if (sale != null)
                    avl = Integer.parseInt(String.valueOf(sale));

                bal = tot - avl;

                if (bal > 0)
                    openDialog(model, unit, name, product_id, bal, position);
                else
                    Toast.makeText(getContext(), bal + " units available", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @SuppressLint("SetTextI18n")
    public void openDialog(final ProductInvoice model, final String ex_unit, String name_, final String product_id, final int bal, final int pos) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.invoice_dialog);
        dialog.setCancelable(true);

        TextView name = dialog.findViewById(R.id.text);
//        TextView quanitiy = dialog.findViewById(R.id.dia_quantity);
        final EditText units = dialog.findViewById(R.id.units);

        name.setText(name_);
//        quanitiy.setText(quantity + " ml");
        Log.i(TAG, "openDialog: ========>" + units.getText().toString() + " - " + model.getSale_rate());
        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (units.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please enter units", Toast.LENGTH_SHORT).show();
                } else {
                    int unitTxt = Integer.parseInt(units.getText().toString());
                    if (unitTxt <= bal) {
                        double sale_ = Double.parseDouble(model.getSale_rate()) * Double.parseDouble(units.getText().toString().trim());
                        ProductInvoice product = new ProductInvoice();
                        product.setProduct_name(model.getProduct_name());
                        product.setSale_rate(String.valueOf(sale_));
                        product.setUnit(units.getText().toString().trim());
                        product.setProduct_id(model.getProduct_id());
                        product.setCgst(model.getCgst());
                        product.setCess(model.getCess());
                        product.setImg_url(model.getImg_url());
                        product.setIn(model.getIn());
                        product.setMrp(model.getMrp());
                        product.setHsn(model.getHsn());

                        cart.add(product);
                        ImageView view = invoice_recycler.findViewHolderForAdapterPosition(pos).itemView.findViewById(R.id.tick_view);
                        view.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    } else
                        Toast.makeText(getContext(), "Stock isn't currently available", Toast.LENGTH_SHORT).show();
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
        if (v.getId() == R.id.cart_btn_order) {
            cart = order;
            order_layout.setVisibility(View.GONE);

        } else {

            try {
                if (validate()) {
                    revertTick();
                    CartSheet cartSheet = new CartSheet(cart, this, this, "invoice", clientId, getDate(), getContext(), selectedLine);
                    cartSheet.setCancelable(false);
                    cartSheet.show(getParentFragmentManager(), "cart");
                }
            } catch (Exception e) {
                Log.i(TAG, "Exception: " + e.getMessage() + e.toString());
            }

        }
    }

    private void revertTick() {

        for (int i = 0; i < data.size(); i++) {
            ImageView view = invoice_recycler.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.tick_view);
            view.setVisibility(View.GONE);
        }

    }


    private boolean validate() {

        if (cart.size() == 0) {
            Toast.makeText(getActivity(), "Cart is empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedLine.equals("")) {
            Toast.makeText(getActivity(), "Please select the line", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedClient.equals("")) {
            Toast.makeText(getActivity(), "Please select the line", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    List<ProductInvoice> filteredList;

    private void filter(String text) {
        filteredList = new ArrayList<>();
        for (ProductInvoice item : data) {
            if (item.getProduct_name().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        productAdapter.filterList(filteredList);
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

    @Override
    public void OnSettle(int position) {
        Log.i(TAG, "OnSettle: " + position);

        Credit crea = credit.get(position);

        if (!crea.getBalance().equals("0.0")) {
            addCreditDialog(position, crea.getInvoice_id(), crea.getBalance(), crea.getClient_id());
        } else {
            Toast.makeText(getActivity(), "No credit value found", Toast.LENGTH_SHORT).show();
        }
    }

    private void addCreditDialog(final int position, final String invoice_id, String balance, String client_id) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.credit_dialog);
        dialog.setCancelable(true);

        TextView name = dialog.findViewById(R.id.dia_quantity);
        final EditText units = dialog.findViewById(R.id.units);

        name.setText(balance);

        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (units.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Please enter units", Toast.LENGTH_SHORT).show();
                } else {
//                    final ProgressDialog pdialog = ProgressDialog.show(getActivity(), "Updating credits", "Please wait...", false, true);

                    Credit model = credit.get(position);

                    double amount = Double.parseDouble(model.getBalance()) - Double.parseDouble(units.getText().toString());
                    model.setBalance(String.valueOf(amount));
                    credit.set(position, model);
                    creditAdapter.notifyDataSetChanged();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Credits");

                    HashMap<String, Object> main = new HashMap<>();
                    main.put("balance", String.valueOf(amount));

//                    reference.child(client_id_).child(invoice_id).updateChildren(main).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Log.i(TAG, "onComplete: " + task.toString());
//                            Toast.makeText(getActivity(), "Units added successfully", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            pdialog.dismiss();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.i(TAG, "onFailure: " + e.toString());
//                        }
//                    });
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
}