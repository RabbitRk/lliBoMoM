package com.rabbitt.momobill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    EditText phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialization
        phone = findViewById(R.id.phone);
    }

    public void verify(View view) {

        String number = phone.getText().toString();
        if(number.isEmpty()|| number.length()<10)
        {
            phone.setError("Give Valid Number");
            phone.requestFocus();
            return;
        }
        String phonenumber = "+91"+number;//Only indian mobile numbers are allowed

//        //Preference files
//        PrefsManager prefsManager = new PrefsManager(this);
//        prefsManager.setPhoneNumber(phonenumber);

        //Once the validation process is finished go to OTP activity
        Intent intent = new Intent(this, OtpActivity.class);
        intent.putExtra("phone_no", phonenumber);
        startActivity(intent);
        finish();
    }
}