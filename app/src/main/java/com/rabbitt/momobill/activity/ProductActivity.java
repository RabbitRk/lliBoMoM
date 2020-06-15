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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rabbitt.momobill.R;
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

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "maluProduct";
    RoundedImageView imageView;
    Uri imageUri, final_uri;
    RadioGroup tax, ex;
    RadioButton in_, ex_;
    EditText cgst, sgst, cess, product_name, quantity, sale_rate, final_rate;
    double amount;
    private StorageReference storageRef;

    IncrementPref incrementPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        //initialization
        imageView = findViewById(R.id.product_image);
        tax = findViewById(R.id.radioGroup_tax);
        ex = findViewById(R.id.tax_ex_in);

        cgst = findViewById(R.id.cgst_txt);
        sgst = findViewById(R.id.sgst_txt);
        cess = findViewById(R.id.cess_txt);

        product_name = findViewById(R.id.txt_name);
        quantity = findViewById(R.id.txt_quantity);
        sale_rate = findViewById(R.id.txt_sale);
        final_rate = findViewById(R.id.final_rate);

        in_ = findViewById(R.id.radio_default);
        ex_ = findViewById(R.id.radio_ex);

        in_.setChecked(true);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        //creates a storage reference
        storageRef = storage.getReference();

        //incrementation preference declaration
        incrementPref = new IncrementPref(this);

        cess.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    calculateCess(s);
                } else {
                    cess.setText("");
                }
            }
        });

        tax.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.i(TAG, "onCheckedChanged: " + checkedId);
                switch (checkedId) {
                    case 6:
                        if (validate()) {
                            cgst.setText("1.5");
                            sgst.setText("1.5");
                            calculateTax("1.5");


                        } else {
                            Toast.makeText(ProductActivity.this, "Please enter the previous values", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 7:
                        if (validate()) {
                            cgst.setText("2.5");
                            sgst.setText("2.5");
                            calculateTax("2.5");

                        } else {
                            Toast.makeText(ProductActivity.this, "Please enter the previous values", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 8:
                        if (validate()) {
                            cgst.setText("6");
                            sgst.setText("6");
                            calculateTax("6");

                        } else {
                            Toast.makeText(ProductActivity.this, "Please enter the previous values", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 9:
                        if (validate()) {
                            cgst.setText("9");
                            sgst.setText("9");
                            calculateTax("9");

                        } else {
                            Toast.makeText(ProductActivity.this, "Please enter the previous values", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 10:
                        if (validate()) {
                            cgst.setText("14");
                            sgst.setText("14");
                            calculateTax("14");
                        } else {
                            Toast.makeText(ProductActivity.this, "Please enter the previous values", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
    }

    private void calculateTax(String s) {

        double gst = Double.parseDouble(s) * 2;
        double sale_r = Integer.parseInt(sale_rate.getText().toString().trim());

        //calulate GST
        if (in_.isChecked()) {

            double in_gst = gst + 100;
            double av_val = sale_r * (100 / in_gst);//it gives the actual value without GST
            double gst_val = (av_val * gst) / 100;//calculating the the tax

            av_val = roundDecimals(av_val);
            gst_val = roundDecimals(gst_val);

            Log.i(TAG, "calculateTax: av: " + av_val + "   gst: " + gst_val);

            amount = av_val + gst_val;

            Log.i(TAG, "calculateTax: amount: " + Math.round(amount));
        } else {
            sale_r = (sale_r * (gst / 100)) + /*Actual rate*/sale_r; //Adding gst + actual rate

            amount = roundDecimals(sale_r);

        }

        final_rate.setText(String.valueOf(Math.round(amount)));
        amount = 0.0;
    }

    private void calculateCess(CharSequence s) {

        double cess = Double.parseDouble(String.valueOf(s));
        double gst = Double.parseDouble(cgst.getText().toString().trim()) * 2;

        double taxval = cess + gst;

        double sale_r = Integer.parseInt(sale_rate.getText().toString().trim());

        Log.i(TAG, "calculateCess: " + cess);

        //calulate GST
        if (in_.isChecked()) {

            double in_gst = taxval + 100;
            double av_val = sale_r * (100 / in_gst);//it gives the actual value without GST
            double gst_val = (av_val * taxval) / 100;//calculating the the tax

            av_val = roundDecimals(av_val);
            gst_val = roundDecimals(gst_val);

            Log.i(TAG, "calculateTax: av: "+av_val+"   gst: "+gst_val);
            amount = av_val + gst_val;

            Log.i(TAG, "calculateTax: amount: "+amount);
        } else {
            sale_r = (sale_r * (taxval / 100)) + /*Actual rate*/sale_r; //Adding gst + actual rate
            amount = roundDecimals(sale_r);
        }

        final_rate.setText(String.valueOf(Math.round(amount)));
        amount = 0.0;
    }

    double roundDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return Double.parseDouble(twoDForm.format(d));
    }

    public void add_product(View view) {

        try
        {
            Log.i(TAG, "uploadImage_fire: " + getFileExtension(final_uri));
            final StorageReference riversRef = storageRef.child("ProductImage/" + product_name.getText().toString().trim());

            assert final_uri != null;

            riversRef.putFile(final_uri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.i(TAG, "onFailure: " + exception.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.i(TAG, "onSuccess: " + uri.toString());
                            imageUri = uri;
                            //Firebase functionality
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");

                            final String newKey = incrementPref.getProductId();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("product_id", newKey);
                            hashMap.put("img_url", String.valueOf(imageUri));
                            hashMap.put("product_name", product_name.getText().toString().trim());
                            hashMap.put("quantity", quantity.getText().toString().trim());
                            hashMap.put("sale_rate", sale_rate.getText().toString().trim());
                            hashMap.put("date_added", getDate());
                            hashMap.put("cgst_sgst", cgst.getText().toString().trim());
                            hashMap.put("cess", cess.getText().toString().trim());
                            hashMap.put("in_ex", in_.isChecked() ? "inc" : "exc");
                            hashMap.put("unit", "0");

                            Log.i(TAG, "addProduct: " + hashMap.toString());
                            reference.child(newKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.i(TAG, "onComplete: " + task.toString());

                                    //  product_name.setText("");
                                    //  quantity.setText("");
                                    //  sale_rate.setText("");
                                    //  cgst.setText("");
                                    //  cess.setText("");
                                    //  final_rate.setText("");
                                    //  imageView.setImageBitmap(null);

                                    //Updating product ID
                                    incrementPref.setProductID(String.valueOf(Integer.parseInt(newKey) + 1));

                                    Toast.makeText(ProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "onFailure: " + e.toString());
                                }
                            });
                        }
                    });
                }
            });
        }
        catch(Exception e)
        {
            Toast.makeText(this, "Please check all the values", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Exception: "+e.toString());
        }
    }

    public String IdFormattor(String string) {
        string = string.toLowerCase();
        string = string.replaceAll("-", "_");
        return string;
    }

    public String getDate() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.date_format));

        return df.format(c);
    }

    public void add_image(View view) {
        Pix.start(this, Options.init().setRequestCode(100));
    }

    public boolean validate() {
        if (product_name.getText().toString().trim().equals("")) {
            return false;
        } else if (quantity.getText().toString().trim().equals("")) {
            return false;
        } else return !sale_rate.getText().toString().equals("");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult: Requestcode: " + requestCode + " " + resultCode);

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            ArrayList<String> returnValue = null;
            if (data != null) {
                returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            }
            Log.i(TAG, "onActivityResult: " + returnValue);
            if (returnValue != null) {

                try {
                    imageUri = Uri.fromFile(new File(returnValue.get(0)));

                    CropImage.activity(imageUri)
                            .setAspectRatio(100, 100)
                            .start(this);

                } catch (Exception e) {
                    Log.i(TAG, "Exception: " + e.toString());
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            try {
                if (resultCode == RESULT_OK) {
                    Uri resultUri = null;
                    if (result != null) {
                        resultUri = result.getUri();
                    }

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    imageView.setImageBitmap(bitmap);

                    //Final uri of cropped image
                    final_uri = resultUri;

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            } catch (IOException | NullPointerException ex) {
                Log.i(TAG, "Exception: " + ex.getMessage());
            }

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = Objects.requireNonNull(this).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}