package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.demo.pdfreader;
import com.rabbitt.momobill.model.Client;
import com.rabbitt.momobill.model.HttpInvoice;
import com.rabbitt.momobill.model.Invoice;
import com.rabbitt.momobill.model.ProductInvoice;
import com.rabbitt.momobill.prefsManager.IncrementPref;
import com.rabbitt.momobill.prefsManager.PrefsManager;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CartSheet extends BottomSheetDialogFragment implements CartAdapter.OnRecyleItemListener, View.OnClickListener {

    private static final String TAG = "maluCart";
    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data;
    private CartAdapter productAdapter;
    private Fragment invoiceFrag;
    private Button button, clear;
    private String btn_val;
    private String client_id, client_name;
    private String date_of;
    int invkey = -1, orderkey = -1;
    Context context;
    View v;
    String invoice = "";

    private TextView close;
    private String line;
    ProgressDialog progressDialog;

    public CartSheet(List<ProductInvoice> data, Fragment invoiceFrag, Fragment frag, String value, String order, String s, Context context, String line) {
        this.data = data;
        this.invoiceFrag = invoiceFrag;
        this.btn_val = value;
        this.client_id = order;
        this.date_of = s;
        this.line = line;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.cart_bottom_sheet, container, false);
        init(v);

//        Gets Client name for credit
        DatabaseReference clientReference = FirebaseDatabase.getInstance().getReference("Client");
        try {
            clientReference.child(client_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    client_name = dataSnapshot.child("name").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (NullPointerException e) {
            Toast.makeText(context, "Please enter valid line and Client Name", Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private void init(View v) {
        v.setVisibility(View.VISIBLE);
        invoice_recycler = v.findViewById(R.id.recycler_cart);
        button = v.findViewById(R.id.ok_button);
        clear = v.findViewById(R.id.clear_data);
        close = v.findViewById(R.id.txt_close);

        if (btn_val.equals("order")) {
            button.setText("Take order");
        }
        updateRecycler(data);
        clear.setOnClickListener(this);
        button.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    private void updateRecycler(List<ProductInvoice> data) {
        productAdapter = new CartAdapter(data, invoiceFrag, this);
        LinearLayoutManager reLayout = new LinearLayoutManager(getActivity());
        invoice_recycler.setLayoutManager(reLayout);
        reLayout.setOrientation(RecyclerView.VERTICAL);
        invoice_recycler.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
        invoice_recycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnItemClick(int position) {

        Log.i(TAG, "Card position: " + position);
        Log.i(TAG, "OnItemClick: " + data.size());
        ProductInvoice model = data.get(position);

        String unit = model.getUnit();
        String name = model.getProduct_name();

        Log.i(TAG, "OnItemClick: " + unit + "  " + name);

        editDialog(position, unit, name);

    }

    private void editDialog(final int position, final String unit, String name) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.cart_edit_dialog);
        dialog.setCancelable(true);

        TextView credit = dialog.findViewById(R.id.text);
        final EditText paid = dialog.findViewById(R.id.units);

        credit.setText(name);

        paid.setText(unit);

        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ok");
                if (paid.getText().toString().equals("") || paid.getText().toString().equals("0")) {
                    Toast.makeText(context, "Please enter the unit properly", Toast.LENGTH_SHORT).show();
                }

                try {
                    ProductInvoice model = data.get(position);
                    double amount = Double.parseDouble(model.getSale_rate()) * Double.parseDouble(paid.getText().toString());

                    model.setUnit(paid.getText().toString());
                    model.setSale_rate(String.valueOf(amount));

                    data.set(position, model);
                    productAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.i(TAG, "Exception: " + e.getMessage() + e.toString());
                }


            }
        });

        Button delButton = dialog.findViewById(R.id.delete_button);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: del");

                try {
                    data.remove(position);
                    productAdapter.notifyDataSetChanged();
//                    productAdapter.notifyItemRemoved(position);
//                    productAdapter.notifyItemRangeChanged(position, data.size());
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.i(TAG, "Exception: " + e.getMessage() + e.toString());
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


    private void createOrder() {

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Collecting Information", "Please wait...", false, true);

        HashMap<String, Object> main = new HashMap<>();

        for (ProductInvoice pv : data) {
            HashMap<String, Object> pro = new HashMap<>();
            Log.i(TAG, "updateFirebase: " + pv.getProduct_id());
            pro.put("product_id", pv.getProduct_id());
            pro.put("product_name", pv.getProduct_name());
            pro.put("sale_rate", pv.getSale_rate());
            pro.put("unit", pv.getUnit());
            pro.put("client_name", client_name);
            pro.put("cgst", pv.getCgst());
            pro.put("cess", pv.getCess());
            pro.put("in_ex", pv.getIn());
            pro.put("purchase_rate", pv.getMrp());
            main.put(pv.getProduct_id(), pro);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Order");

        IncrementPref pref = new IncrementPref(getActivity());
        String order_num = pref.getOrderId();

        Log.i(TAG, "addProduct: " + main.toString());
        reference.child(date_of).child(client_id).updateChildren(main).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public String getDate() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.short_format));

        return df.format(c);
    }

    public String getDate_() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.inv_format));

        return df.format(c);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                if (btn_val.equals("order")) {
                    Toast.makeText(getActivity(), "Order Accepted", Toast.LENGTH_SHORT).show();
                    updateFirebase(data);
                } else {
                    double total = 0.0;
                    for (int i = 0; i < data.size(); i++) {
                        Log.i(TAG, "Invoice Amount: " + data.get(i).getIn());
                        String inc = data.get(i).getIn();
                        double gst = Double.parseDouble(data.get(i).getCgst());
                        double ces = Double.parseDouble(data.get(i).getCess());
                        double rat = Double.parseDouble(data.get(i).getSale_rate());
                        double val = calculate(inc, gst, ces, rat);
                        total += val;
                    }
                    Log.i(TAG, "Invoice Amount: " + total);
                    paymentPopup(total);

                }
                break;
            case R.id.txt_close:
                this.dismiss();
                break;
            default:
                data.clear();
                productAdapter.notifyDataSetChanged();
                this.dismiss();
                break;
        }
    }

    private void paymentPopup(final double total) {
//        String credit_ = String.valueOf(getCredit(client_id));
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.payment_dialog);
        dialog.setCancelable(true);

        TextView credit = dialog.findViewById(R.id.text);
        final TextView total_amount = dialog.findViewById(R.id.dia_quantity);
        final EditText paid = dialog.findViewById(R.id.units);

        //Fin
        final RadioGroup ex = dialog.findViewById(R.id.tax_ex_in);
        final RadioButton in_ = dialog.findViewById(R.id.radio_default), ex_ = dialog.findViewById(R.id.radio_ex);
        //Fin end

        total_amount.setText(String.valueOf(total));

        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paid.getText().toString().trim().equals("") || ex_.isEnabled()) {

                    double t_amount = Double.parseDouble(total_amount.getText().toString());
                    double _paid = 0.0;

                    if (ex_.isEnabled()) {
                        _paid = t_amount;
                    } else {
                        _paid = Double.parseDouble(paid.getText().toString());
                    }


                    if (_paid > t_amount) {
                        Toast.makeText(context, "You are getting more than the total amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final DatabaseReference invoiceSingle = FirebaseDatabase.getInstance().getReference("Invoice_Single");
                    final DatabaseReference client_ref = FirebaseDatabase.getInstance().getReference("Client").child(client_id);

                    final double final_paid = _paid;
                    invoiceSingle.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                invkey = Integer.parseInt(child.getKey());
                            }
                            invkey++;

                            invoice = String.valueOf(invkey);

                            String balance = String.valueOf(Double.parseDouble(total_amount.getText().toString()) - final_paid);

                            // Pref data
                            String crede = "Credit";
                            if (balance.equals("0") || balance.equals("0.0"))
                            {
                                crede = "Cash";
                            }

                            new PrefsManager(getContext()).setBillInfo(crede, getDate(), line);

                            // Pref data end
                            if (!(Double.parseDouble(balance) <= 0.0)) {
                                String paidTxt = String.valueOf(final_paid);
                                String tot = String.valueOf(total);
                                createCredit(balance, invoice, client_id, client_name, paidTxt, tot);
                            }

                            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Generating Invoice", "Please wait...", false, true);


                            Log.i(TAG, "onClick: " + invoice);

                            HashMap<String, HashMap<String, Object>> main = new HashMap<>();


                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("client_id", client_id);
                            hashMap.put("invoice_id", invoice);
                            hashMap.put("date_of", getDate());
                            hashMap.put("amount", total);
                            hashMap.put("paid", String.valueOf(final_paid));
                            hashMap.put("balance", balance);


                            HashMap<String, Object> product = new HashMap<>();

                            for (ProductInvoice pv : data) {
                                HashMap<String, Object> pro = new HashMap<>();
                                Log.i(TAG, "updateFirebase: " + pv.getProduct_id());
                                pro.put("product_id", pv.getProduct_id());
                                pro.put("product_name", pv.getProduct_name());
                                pro.put("sale_rate", pv.getSale_rate());
                                pro.put("unit", pv.getUnit());
                                pro.put("cgst", pv.getCgst());
                                pro.put("cess", pv.getCess());
                                pro.put("in_ex", pv.getIn());
                                pro.put("purchase_rate", pv.getMrp());

                                product.put(pv.getProduct_id(), pro);
                            }

//                            Add total sold project to today's opening

                            main.put("Details", hashMap);
                            main.put("Product", product);

                            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Invoice");
                            final DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Invoice_Single");

                            Log.i(TAG, "addProduct: " + hashMap.toString());


                            reference.child(getDate()).child(invoice).setValue(main).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    reference2.child(invoice).child("date").setValue(getDate()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.i(TAG, "onComplete: " + task.toString());
                                            Toast.makeText(getActivity(), "Invoice created successfully", Toast.LENGTH_SHORT).show();
                                            reduceCount(data);

                                            client_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    Client client = dataSnapshot.getValue(Client.class);

//                                                    Log.i(TAG, "Client Snap: "+client.getName());
                                                    generateInvoice(client);
                                                    dialog.dismiss();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG, "onFailure: " + e.toString());
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "onFailure: " + e.toString());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    Toast.makeText(context, "Please enter the value", Toast.LENGTH_SHORT).show();
                }
            }

        });

        ex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (!in_.isChecked()) {
                    paid.setEnabled(false);
                } else {
                    paid.setEnabled(true);
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

    private void reduceCount(List<ProductInvoice> data) {

        for (final ProductInvoice pv : data) {

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Product")
                    .child(pv.getProduct_id());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int unit = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("unit").getValue(String.class)));
                    Log.i(TAG, "onDataChange: " + unit);
                    int balance = unit - Integer.parseInt(pv.getUnit());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("unit", String.valueOf(balance));


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
                    reference.child(pv.getProduct_id()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: " + task.toString());
//                            Toast.makeText(getActivity(), "Invoice complete", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "onFailure: " + e.toString());
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference openreference = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Opening")
                    .child(getDate())
                    .child(pv.getProduct_id());

            openreference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int sale = 0;
                    int cursale = Integer.parseInt(pv.getUnit());
                    Object obj = dataSnapshot.child("sale").getValue();

                    if (obj != null)
                        sale = Integer.parseInt(String.valueOf(obj));


                    int totsale = cursale + sale;
                    HashMap<String, Object> hashMap2 = new HashMap<>();
                    hashMap2.put("sale", String.valueOf(totsale));

                    Log.i(TAG, "onDataChange: Unit " + pv.getUnit());
                    Log.i(TAG, "onDataChange: Sale " + sale);
                    Log.i(TAG, "onDataChange: TotSale " + totsale);

                    DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Opening");
                    reference3.child(getDate()).child(pv.getProduct_id()).updateChildren(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: " + task.toString());
                            Toast.makeText(getActivity(), "Invoice complete", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "onFailure: " + e.toString());
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    private void createCredit(String balance, String invoice, String client_id, String client_name, String paidTxt, String amnt) {
        Log.i(TAG, "createCredit: ");

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("client_name", client_name);
        hashMap.put("client_id", client_id);
        hashMap.put("paid", paidTxt);
        hashMap.put("amount", amnt);
        hashMap.put("invoice_id", invoice);
        hashMap.put("date_of", getDate());
        hashMap.put("balance", balance);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Credits");

        Log.i(TAG, "addProduct: " + hashMap.toString());
        reference.child(client_id).child(invoice).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "onComplete: " + task.toString());
//                Toast.makeText(getActivity(), "Purchased on credit", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: " + e.toString());
            }
        });
    }

    private double calculate(String inc, double gst, double ces, double rat) {

        double return_value;
        if (inc.equals("inc")) {
            return_value = rat;
        } else {
            return_value = calculate_amount(rat, gst, ces);
        }

        return return_value;
    }

    private double calculate_amount(double sale_r, double gst, double ces) {
        double taxval = ces + gst;
        sale_r = (sale_r * (taxval / 100)) + /*Actual rate*/sale_r; //Adding gst + actual rate
        Log.i(TAG, "calculate_amount: " + sale_r);
        return roundDecimals(sale_r);
    }

    double roundDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#");
        return Double.parseDouble(twoDForm.format(d));
    }

    File file;

    private void generateInvoice(Client client) {
//        Uri uri = null;
//        boolean a = new File(Environment.getExternalStorageDirectory() + "/Santha Agencies" + "/Invoice").mkdirs();
//
//        if (a) {
//            Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
//        }
//
//        String path1 = Environment.getExternalStorageDirectory() + "/Santha Agencies" + "/Invoice/" + invoice + "_Inv.pdf";
//        file = new File(path1);

//        Log.i(TAG, "generateInvoice: " + Environment.getExternalStorageDirectory() + "/Santha Agencies" + "/Invoice");
        // Work Area

        ArrayList<ProductInvoice> val = new ArrayList<>(data.size());
        val.addAll(data);

        Bundle client_b = new Bundle();
        client_b.putString("name",  client.getName());
        client_b.putString("phone",  client.getPhone());
        client_b.putString("email",  client.getEmail());
        client_b.putString("add1",  client.getAdd1());
        client_b.putString("add2",  client.getAdd2());
        client_b.putString("city",  client.getCity());
        client_b.putString("state",  client.getState());
        client_b.putString("pincode",  client.getPincode());
        client_b.putString("gst",  client.getGst());

        Bundle bundle = new Bundle();
        bundle.putSerializable("valuesArray", val);
//        bundle.putBundle("client",  client_b);


//        HttpInvoice in = new HttpInvoice();
//        in.pdfcreate(file, uri, uri, uri, context, data, invoice, client, getDate_());


        Intent intent = new Intent(getActivity(), pdfreader.class);
        intent.putExtra("inv", invoice);
        intent.putExtra("inc", bundle);
        intent.putExtra("client", client_b);
        startActivity(intent);


//        startActivity(new Intent(getActivity(), PdfTabbedActivity.class).putExtra("inv", "INV").putExtra("from", "genrate"));
    }


    private void updateFirebase(List<ProductInvoice> data) {
        createOrder();
    }

}
