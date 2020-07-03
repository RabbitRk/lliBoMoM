package com.rabbitt.momobill.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.demo.pdfreader;
import com.rabbitt.momobill.model.Invoice;
import com.rabbitt.momobill.model.ProductInvoice;
import com.rabbitt.momobill.prefsManager.IncrementPref;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CartSheet extends BottomSheetDialogFragment implements CartAdapter.OnRecyleItemListener, View.OnClickListener {

    private static final String TAG = "maluCart";
    private RecyclerView invoice_recycler;
    private List<ProductInvoice> data = new ArrayList<>();
    private CartAdapter productAdapter;
    private Fragment invoiceFrag;
    private cartDelete cartDelete;
    private Button button, clear;
    private String btn_val;
    private String client_id;
    private String date_of;
    Context context;
    View v;

    public CartSheet(List<ProductInvoice> data, Fragment invoiceFrag, Fragment frag, cartDelete cartDelete, String value, String order, String s, Context context) {
        this.data = data;
        this.invoiceFrag = invoiceFrag;
        this.cartDelete = cartDelete;
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

//        String product_id = model.getProduct_id();
//        String unit = model.getUnit();
        String quantity = model.getQuantity();
        String name = model.getProduct_name();

        Log.i(TAG, "OnItemClick: " + quantity + "  " + name);

//        openDialog(position, quantity, name);
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.invoice_dialog);
//        dialog.setCancelable(true);

    }

    private void createOrder() {

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "Collecting Information", "Please wait...", false, true);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("client_id", client_id);

        for (ProductInvoice pv : data) {
            HashMap<String, Object> pro = new HashMap<>();
            Log.i(TAG, "updateFirebase: " + pv.getProduct_id());
            pro.put("product_id", pv.getProduct_id());
            pro.put("product_name", pv.getProduct_name());
            pro.put("quantity", pv.getQuantity());
            pro.put("sale_rate", pv.getSale_rate());
            pro.put("unit", pv.getUnit());
            pro.put("cgst", pv.getCgst());
            pro.put("cess", pv.getCess());
            pro.put("in_ex", pv.getIn());

            hashMap.put(pv.getProduct_id(), pro);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Order");

        IncrementPref pref = new IncrementPref(getActivity());
        String order_num = pref.getOrderId();

        Log.i(TAG, "addProduct: " + hashMap.toString());
        reference.child(date_of).child(client_id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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

//    @SuppressLint("SetTextI18n")
//    public void openDialog(final int position, String quantity, String name_) {
//
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.delete_dialog);
//        dialog.setCancelable(true);
//
//        TextView name = dialog.findViewById(R.id.text);
//        TextView quanitiy = dialog.findViewById(R.id.dia_quantity);
////        TextView unit = dialog.findViewById(R.id.dia_lbl);
//
//        name.setText(name_);
//        quanitiy.setText(quantity + " ml");
////        unit.setText(ex_unit);
//
//        Button dialogButton = dialog.findViewById(R.id.ok_button);
//        dialogButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v_) {
//
//                try {
//                    Log.i(TAG, "Sheeeeeetttt: " + position);
//                    data.clear();
////                    cartDelete.OnDelete(position);
//
//                    productAdapter.notifyDataSetChanged();
//                } catch (Exception e) {
////                    v.setVisibility(View.GONE);
//                    Log.i(TAG, "Exception: " + e.getMessage() + "    " + e.toString());
//                }
//
////                if (units.getText().toString().trim().equals("")) {
////                    Toast.makeText(getActivity(), "Please enter units", Toast.LENGTH_SHORT).show();
////                }
////                else
////                {
////                    double sale_ = Double.parseDouble(model.getSale_rate()) * Double.parseDouble(units.getText().toString().trim());
////                    ProductInvoice product = new ProductInvoice();
////                    product.setProduct_name(model.getProduct_name());
////                    product.setQuantity(model.getQuantity());
////                    product.setSale_rate(String.valueOf(sale_));
////                    product.setUnit(units.getText().toString().trim());
////                    product.setProduct_id(model.getProduct_id());
////                    product.setCgst(model.getCgst());
////                    product.setCess(model.getCess());
////
////                    cart.add(product);
//                dialog.dismiss();
//////                     createInvoice(dialog, ex_unit, product_id, units.getText().toString().trim());
////                }
//            }
//        });
//
//        try {
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.show();
//        } catch (WindowManager.BadTokenException e) {
//            e.printStackTrace();
//        }
//    }

    public String getDate() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.short_format));

        return df.format(c);
    }
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ok_button) {
            if (btn_val.equals("order")){
                Toast.makeText(getActivity(), "Order Accepted", Toast.LENGTH_SHORT).show();
                updateFirebase(data);
            }
            else
            {
                generateInvoice();
            }
        } else {
            data.clear();
            productAdapter.notifyDataSetChanged();
        }
    }

    File file;

    private void generateInvoice() {
        Uri uri = null;
//        for (ProductInvoice pv : data) {
//            uri = Uri.parse(pv.getImg_url());
//            break;
//        }
//        File file4 = new File("https://firebasestorage.googleapis.com/v0/b/invent-23ce1.appspot.com/o/ProductImage%2FCoke?alt=media&token=cb955945-bd38-4461-8960-1e20f05bbb2e");
//        = Uri.parse(file4.getPath());

        String path1 = Environment.getExternalStorageDirectory() + File.separator + "INV" + "temp.pdf";
        file = new File(path1);
//        Invoice in = new Invoice(num_to_words,invoice.getText().toString(), dateString.getText().toString(), companyname, ad, user_gst, cp, user_phone, Name, Address, state, zip, Gstin, items, GST, total.getText().toString(), accno, ifsccode);
        Invoice in = new Invoice();
        in.pdfcreate(file, uri, uri, uri, context);
        startActivity(new Intent(getActivity(), pdfreader.class).putExtra("inv", "INV").putExtra("from","genrate"));
    }

    private void updateFirebase(List<ProductInvoice> data) {

        createOrder();
    }

    public interface cartDelete {
        void OnDelete(int position);
    }
}
