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
import com.rabbitt.momobill.model.Invoice;
import com.rabbitt.momobill.model.ProductInvoice;
import com.rabbitt.momobill.prefsManager.IncrementPref;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CartSheet extends BottomSheetDialogFragment implements CartAdapter.OnRecyleItemListener, View.OnClickListener {

    private static final String TAG = "maluCart";
    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data;
    private CartAdapter productAdapter;
    private Fragment invoiceFrag;
    private Button button, clear;
    private String btn_val;
    private String client_id;
    private String date_of;
    Context context;
    View v;

    int lastkey = -1;

    public CartSheet(List<ProductInvoice> data, Fragment invoiceFrag, Fragment frag, String value, String order, String s, Context context) {
        this.data = data;
        this.invoiceFrag = invoiceFrag;
        this.btn_val = value;
        this.client_id = order;
        this.date_of = s;
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.cart_bottom_sheet, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        v.setVisibility(View.VISIBLE);
        invoice_recycler = v.findViewById(R.id.recycler_cart);
        button = v.findViewById(R.id.ok_button);
        clear = v.findViewById(R.id.clear_data);

        if (btn_val.equals("order")) {
            button.setText("Take order");
        }
        updateRecycler(data);
        clear.setOnClickListener(this);
        button.setOnClickListener(this);
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
                    double amount = Double.parseDouble(model.getSingle()) * Double.parseDouble(paid.getText().toString());

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
            Log.i(TAG, "updateFirebase: " + pv.getProduct_id());
            HashMap<String, Object> pro = new HashMap<>();
            pro.put("product_id", pv.getProduct_id());
            pro.put("product_name", pv.getProduct_name());
            pro.put("sale_rate", pv.getSale_rate());
            pro.put("unit", pv.getUnit());
            pro.put("cgst", pv.getCgst());
            pro.put("cess", pv.getCess());
            pro.put("in_ex", pv.getIn());

            main.put(pv.getProduct_id(), pro);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Order");

//            IncrementPref pref = new IncrementPref(getActivity());

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

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ok_button) {
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
        } else {
            data.clear();
            productAdapter.notifyDataSetChanged();
        }
    }

    String invoice;

    private void paymentPopup(final double total) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.payment_dialog);
        dialog.setCancelable(true);

        final TextView total_amount = dialog.findViewById(R.id.dia_quantity);
        final EditText paid = dialog.findViewById(R.id.units);

        total_amount.setText(String.valueOf(total));

        Button dialogButton = dialog.findViewById(R.id.ok_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!paid.getText().toString().trim().equals("")) {

                    final double t_amount = Double.parseDouble(total_amount.getText().toString());
                    final double _paid = Double.parseDouble(paid.getText().toString());

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Invoice");

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                lastkey = Integer.parseInt(child.getKey());
                            }
                            lastkey++;
                            invoice = String.valueOf(lastkey);

                            if (_paid > t_amount) {
                                Toast.makeText(context, "You are getting more than the total amount", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String balance = String.valueOf(Double.parseDouble(total_amount.getText().toString()) - Double.parseDouble(paid.getText().toString()));

                            if (!(Double.parseDouble(balance) <= 0.0)) {
                                createCredit(balance, invoice, client_id);
                            }

                            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Generating Invoice", "Please wait...", false, true);

                            Log.i(TAG, "onClick: " + invoice);

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("client_id", client_id);
                            hashMap.put("invoice_id", invoice);
                            hashMap.put("date_of", getDate());
                            hashMap.put("amount", String.valueOf(total));
                            hashMap.put("paid", paid.getText().toString().trim());
                            hashMap.put("balance", balance);

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

                                hashMap.put(pv.getProduct_id(), pro);
                            }

                            Log.i(TAG, "addProduct: " + hashMap.toString());
                            reference.child(invoice).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i(TAG, "onComplete: " + task.toString());
                                    Toast.makeText(getActivity(), "Invoice created successfully", Toast.LENGTH_SHORT).show();
                                    reduceCount(data);
                                    generateInvoice();
                                    dialog.dismiss();
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

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sale", pv.getUnit());

            DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Opening");
            reference3.child(getDate()).child(pv.getProduct_id()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
    }

    private void createCredit(String balance, String invoice, String client_id) {
        Log.i(TAG, "createCredit: ");

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("client_id", client_id);
        hashMap.put("invoice_id", invoice);
        hashMap.put("date_of", getDate());
        hashMap.put("balance", balance);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Credits");

        Log.i(TAG, "addProduct: " + hashMap.toString());
        reference.child(client_id).child(invoice).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "onComplete: " + task.toString());
                Toast.makeText(getActivity(), "Purchased on credit", Toast.LENGTH_SHORT).show();
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
        return roundDecimals(sale_r);
    }

    double roundDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return Double.parseDouble(twoDForm.format(d));
    }

    File file;

    private void generateInvoice() {
        Uri uri = null;
        String path1 = Environment.getExternalStorageDirectory() + File.separator + "INV" + "temp.pdf";
        file = new File(path1);
//        Invoice in = new Invoice(num_to_words,invoice.getText().toString(), dateString.getText().toString(), companyname, ad, user_gst, cp, user_phone, Name, Address, state, zip, Gstin, items, GST, total.getText().toString(), accno, ifsccode);
        Invoice in = new Invoice();
        in.pdfcreate(file, uri, uri, uri, context, data);
        startActivity(new Intent(getActivity(), pdfreader.class).putExtra("inv", "INV").putExtra("from", "genrate"));
    }

    private void updateFirebase(List<ProductInvoice> data) {

        createOrder();
    }

}
