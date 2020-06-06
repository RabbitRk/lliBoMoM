package com.rabbitt.momobill.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rabbitt.momobill.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = "maluProduct";
    RoundedImageView imageView;
    Uri imageUri;
    RadioGroup tax, ex;
    EditText cgst, sgst, cess, product_name, quantity, sale_rate, final_rate;
    double amount;
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

        cess.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                {    cess.setText("");}
                else
                {
                    calculateCess(s);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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

        int gst = Integer.parseInt(s) * 2;
        int sale_r = Integer.parseInt(sale_rate.getText().toString().trim());

        //calulate GST

        amount = 0.0;

    }

    private void calculateCess(CharSequence s) {

        int cess = Integer.parseInt(String.valueOf(s));
        int gst = Integer.parseInt(cgst.getText().toString().trim()) * 2;
        int sale_r = Integer.parseInt(sale_rate.getText().toString().trim());

        Log.i(TAG, "calculateCess: "+cess);

        //calulate GST

        amount = 0.0;

    }

    public void add_product(View view) {
        //Firebase functionality

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

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            } catch (IOException | NullPointerException ex) {
                Log.i(TAG, "Exception: " + ex.getMessage());
            }

        }
    }

}