package com.rabbitt.momobill.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.model.Product;
import com.rabbitt.momobill.prefsManager.IncrementPref;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class EditProductActivity extends AppCompatActivity {

    private static final String TAG = "maluProduct";
    Uri imageUri, final_uri = null, resultUri;
    RadioGroup ex;
    RadioButton in_, ex_;
    TextInputEditText cgst, sgst, cess, product_name, sale_rate, purchase_rate, hsn_code, units;

    Product product;

    double amount;
    private StorageReference storageRef;

    String product_id, unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eproduct);

        //initialization
        ex = findViewById(R.id.tax_ex_in);

        cgst = findViewById(R.id.cgst_txt);
        sgst = findViewById(R.id.sgst_txt);
        cess = findViewById(R.id.cess_txt);

        units = findViewById(R.id.txt_units);

        //  Newly added
        purchase_rate = findViewById(R.id.txt_pur);
        hsn_code = findViewById(R.id.txt_hsn);

        product_name = findViewById(R.id.txt_name);
        sale_rate = findViewById(R.id.txt_sale);

        in_ = findViewById(R.id.radio_default);
        ex_ = findViewById(R.id.radio_ex);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        //creates a storage reference
        storageRef = storage.getReference();


        //Setting value from the intent
        product = (Product) getIntent().getSerializableExtra("Pro");

        Log.i(TAG, "onCreate: "+product.getProduct_name());

        cgst.setText(product.getGst());
        sgst.setText(product.getGst());
        cess.setText(product.getCess());
        purchase_rate.setText(product.getPuchase());
        hsn_code.setText(product.getHsn());
        product_name.setText(product.getProduct_name());
        sale_rate.setText(product.getSale_rate());
        product_id = product.getProduct_id();
        units.setText(product.getUnit());
        final_uri = Uri.parse(product.getImg_url());

        if (product.getInc().equals("inc"))
        {
            in_.setChecked(true);
        }
        else
        {
            ex_.setChecked(true);
        }
        //

        cgst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    sgst.setText(s);
                } else {
                    cgst.setText("");
                    sgst.setText("");
                }
            }
        });
    }

    public void add_product(View view) {

        if (validate()) {
            try {
                //Firebase functionality
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("product_id", product_id);
                hashMap.put("img_url", String.valueOf(final_uri));
                hashMap.put("product_name", product_name.getText().toString().trim());
                hashMap.put("sale_rate", sale_rate.getText().toString().trim());
                hashMap.put("purchase_rate", purchase_rate.getText().toString().trim());
                hashMap.put("hsn_code", hsn_code.getText().toString().trim());
                hashMap.put("date_added", getDate());
                hashMap.put("cgst_sgst", cgst.getText().toString().trim());
                hashMap.put("cess", cess.getText().toString().trim());
                hashMap.put("in_ex", in_.isChecked() ? "inc" : "exc");
                hashMap.put("unit", units.getText().toString().trim());

                Log.i(TAG, "addProduct: " + hashMap.toString());
                reference.child(product_id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "onComplete: " + task.toString());
                        Toast.makeText(EditProductActivity.this, "Product Editted successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: " + e.toString());
                    }
                });

            } catch (Exception e) {
                Toast.makeText(this, "Please check all the values", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Exception: " + e.toString());
            }
        }
    }

    public String getDate() {
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.date_format));
        return df.format(c);
    }

    public boolean validate() {
        if (final_uri == null) {
            Toast.makeText(this, "Photo required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (product_name.getText().toString().trim().equals("")) {
            product_name.setError("Required");
            return false;
        }
        if (hsn_code.getText().toString().trim().equals("")) {
            hsn_code.setError("Required");
            return false;
        }
        if (sale_rate.getText().toString().equals("")) {
            sale_rate.setError("Required");
            return false;
        }
        if (purchase_rate.getText().toString().trim().equals("")) {
            purchase_rate.setError("Required");
            return false;
        }
        if (!in_.isChecked() && !ex_.isChecked()) {
            Toast.makeText(this, "Select tax type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cgst.getText().toString().trim().equals("") || sgst.getText().toString().trim().equals("") || cess.getText().toString().trim().equals("")) {
            Toast.makeText(this, "GST Percentage required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}