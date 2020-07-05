package com.rabbitt.momobill;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.activity.ClientActivity;
import com.rabbitt.momobill.model.Client;
import com.rabbitt.momobill.prefsManager.PrefsManager;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "maluSign";
    EditText name, phone, email, add1, add2, city, state, pincode, gst;
    String key;

    RadioGroup ex;
    RadioButton in_, ex_;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);


        name = findViewById(R.id.txt_name);
        phone = findViewById(R.id.txt_phone);
        email = findViewById(R.id.txt_email);
        add1 = findViewById(R.id.txt_add1);
        add2 = findViewById(R.id.txt_add2);
        city = findViewById(R.id.txt_cty);
        state = findViewById(R.id.txt_state);
        pincode = findViewById(R.id.txt_pincode);
        gst = findViewById(R.id.txt_gst);

        ex = findViewById(R.id.tax_ex_in);
        in_ = findViewById(R.id.radio_default);
        ex_ = findViewById(R.id.radio_ex);

        phone.setText(getIntent().getStringExtra("phone"));
        phone.setEnabled(false);

        ex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (in_.isChecked())
                {
                    email.setVisibility(View.VISIBLE);
                    add1.setVisibility(View.VISIBLE);
                    add2.setVisibility(View.VISIBLE);
                    city.setVisibility(View.VISIBLE);
                    state.setVisibility(View.VISIBLE);
                    pincode.setVisibility(View.VISIBLE);
                    gst.setVisibility(View.VISIBLE);
                }
                else
                {
                    email.setVisibility(View.GONE);
                    add1.setVisibility(View.GONE);
                    add2.setVisibility(View.GONE);
                    city.setVisibility(View.GONE);
                    state.setVisibility(View.GONE);
                    pincode.setVisibility(View.GONE);
                    gst.setVisibility(View.GONE);
                }
            }
        });
    }

    public void register(View view) {
        String design = "";

        if (validate())
        {
            if (in_.isChecked())
            {
                design = "Owner";
            }
            else{
                design = "Employee";
            }

            if (design.equals("Owner"))
            {
                if (!checkOwner())
                {
                    Toast.makeText(this, "Owner Already Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Agency");

            HashMap<String, Object> hashMap = new HashMap<>();

            if (in_.isChecked()) {
                hashMap.put("name", name.getText().toString().trim());
                hashMap.put("designation", design);
                hashMap.put("phone", phone.getText().toString().trim());
                hashMap.put("email", email.getText().toString().trim());
                hashMap.put("add1", add1.getText().toString().trim());
                hashMap.put("add2", add2.getText().toString().trim());
                hashMap.put("city", city.getText().toString().trim());
                hashMap.put("state", state.getText().toString().trim());
                hashMap.put("pincode", pincode.getText().toString().trim());
                hashMap.put("gst", gst.getText().toString().trim());
            }
            else
            {
                hashMap.put("name", name.getText().toString().trim());
                hashMap.put("phone", phone.getText().toString().trim());
            }
            Log.i(TAG, "addClient: " + hashMap.toString());

            key = reference.child(design).push().getKey();

            final String finalDesign = design;

            reference.child(design).child(key).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i(TAG, "onComplete: " + task.toString());

                    storePreference(
                            key,
                            name.getText().toString().trim(),
                            phone.getText().toString().trim(),
                            email.getText().toString().trim(),
                            add1.getText().toString().trim(),
                            add2.getText().toString().trim(),
                            city.getText().toString().trim(),
                            state.getText().toString().trim(),
                            pincode.getText().toString().trim(),
                            gst.getText().toString().trim(),
                            finalDesign);

                    name.setText("");
                    phone.setText("");
                    email.setText("");
                    add1.setText("");
                    add2.setText("");
                    city.setText("");
                    state.setText("");
                    pincode.setText("");
                    gst.setText("");

                    Toast.makeText(SignUpActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: " + e.toString());
                }
            });
        }
    }

    private void storePreference(String myKey,String name, String phone, String email, String add1, String add2, String city, String state, String pincode, String gst, String d) {
        new PrefsManager(this).userPreferences_(
                myKey,
                name,
                phone,
                email,
                add1,
                add2,
                city,
                state,
                pincode,
                gst
        );
        if (d.equals("Owner"))
        {
            new PrefsManager(this).setOwner(true);
        }
        else
        {
            new PrefsManager(this).setOwner(false);
        }

        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
    }

    private boolean checkOwner() {
        final Boolean[] isExist = {true};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Agency").child("Owner");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: "+dataSnapshot);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                }
                if (dataSnapshot.exists())
                {
                    Log.i(TAG, "onDataChange: false");
                }
                else
                {
                    isExist[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.i(TAG, "checkOwner: "+ isExist[0]);
        return isExist[0];
    }

    private boolean validate() {
        if (name.getText().toString().trim().equals("")) {
            name.setError("Required");
            return false;
        }
        if (phone.getText().toString().trim().equals("")) {
            phone.setError("Required");
            return false;
        }
        if (email.getText().toString().trim().equals("") && email.getVisibility() == View.VISIBLE) {
            email.setError("Required");
            return false;
        }
        if (add1.getText().toString().trim().equals("") && add1.getVisibility() == View.VISIBLE) {
            add1.setError("Required");
            return false;
        }
        if (add2.getText().toString().trim().equals("") && add2.getVisibility() == View.VISIBLE) {
            add2.setError("Required");
            return false;
        }
        if (city.getText().toString().trim().equals("") && city.getVisibility() == View.VISIBLE) {
            city.setError("Required");
            return false;
        }
        if (state.getText().toString().trim().equals("") && state.getVisibility() == View.VISIBLE) {
            state.setError("Required");
            return false;
        }
        if (pincode.getText().toString().trim().equals("") && pincode.getVisibility() == View.VISIBLE) {
            pincode.setError("Required");
            return false;
        }
        if (gst.getText().toString().trim().equals("") && gst.getVisibility() == View.VISIBLE) {
            gst.setError("Required");
            return false;
        }
        if (!in_.isChecked() && !ex_.isChecked())
        {
            Toast.makeText(this, "Select Owner or Employee", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
