package com.rabbitt.momobill.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.prefsManager.IncrementPref;

import java.util.HashMap;

public class ClientActivity extends AppCompatActivity {

    private static final String TAG = "maluClient";
    EditText name, phone, email, add1, add2, city, state, pincode, gst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        name = findViewById(R.id.txt_name);
        phone = findViewById(R.id.txt_phone);
        email = findViewById(R.id.txt_email);
        add1 = findViewById(R.id.txt_add1);
        add2 = findViewById(R.id.txt_add2);
        city = findViewById(R.id.txt_cty);
        state = findViewById(R.id.txt_state);
        pincode = findViewById(R.id.txt_pincode);
        gst = findViewById(R.id.txt_gst);
    }

    public void add_client(View view) {
        if (validate()) {

            final IncrementPref incrementPref = new IncrementPref(this);

            // Client ID getting from the PrefsManager
            final String client_id = incrementPref.getClientId();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Client");

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("name", name.getText().toString().trim());
            hashMap.put("client_id", client_id);
            hashMap.put("phone", phone.getText().toString().trim());
            hashMap.put("email", email.getText().toString().trim());
            hashMap.put("add1", add1.getText().toString().trim());
            hashMap.put("add2", add2.getText().toString().trim());
            hashMap.put("city", city.getText().toString().trim());
            hashMap.put("state", state.getText().toString().trim());
            hashMap.put("pincode", pincode.getText().toString().trim());
            hashMap.put("gst", gst.getText().toString().trim());

            Log.i(TAG, "addClient: " + hashMap.toString());

            reference.child(client_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.i(TAG, "onComplete: " + task.toString());
                    name.setText("");
                    phone.setText("");
                    email.setText("");
                    add1.setText("");
                    add2.setText("");
                    city.setText("");
                    state.setText("");
                    pincode.setText("");
                    gst.setText("");

                    //Incrementing the Client ID and storing in the PrefsManager
                    incrementPref.setClientID(String.valueOf(Integer.parseInt(client_id) + 1));

                    Toast.makeText(ClientActivity.this, "Client added successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "onFailure: " + e.toString());
                }
            });
        }
    }

    private boolean validate() {
        if (name.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (phone.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (email.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (add1.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (add2.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (city.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (state.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (pincode.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }
        if (gst.getText().toString().trim().equals("")) {
            email.setError("Required");
            return false;
        }

        return true;
    }
}