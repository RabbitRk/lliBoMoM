package com.rabbitt.momobill.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.adapter.LineAdapter;
import com.rabbitt.momobill.model.Line;

import java.util.ArrayList;
import java.util.HashMap;

public class EditClientActivity extends AppCompatActivity {

    EditText name, phone, email, add1, add2, city, state, pincode, gst;

    String[] ar;

    AutoCompleteTextView line;
    LineAdapter adapter;
    ArrayList<Line> linelist;
    DatabaseReference lineReference;

    ProgressDialog progressDialog;

    String clientid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        name = findViewById(R.id.txt_name);
        phone = findViewById(R.id.txt_phone);
        email = findViewById(R.id.txt_email);
        add1 = findViewById(R.id.txt_add1);
        add2 = findViewById(R.id.txt_add2);
        city = findViewById(R.id.txt_cty);
        state = findViewById(R.id.txt_state);
        pincode = findViewById(R.id.txt_pincode);
        gst = findViewById(R.id.txt_gst);

        line = findViewById(R.id.txt_line);

        linelist = new ArrayList<>();
        line.setEnabled(true);
        line.setEms(15);

        clientid = getIntent().getStringExtra("clientid");

        progressDialog = ProgressDialog.show(EditClientActivity.this, "Please wait", "Loading", true);
        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("Client");

        clientRef.child(clientid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name.setText(dataSnapshot.child("name").getValue().toString());
                phone.setText(dataSnapshot.child("phone").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                add1.setText(dataSnapshot.child("add1").getValue().toString());
                add2.setText(dataSnapshot.child("add2").getValue().toString());
                city.setText(dataSnapshot.child("city").getValue().toString());
                state.setText(dataSnapshot.child("state").getValue().toString());
                pincode.setText(dataSnapshot.child("pincode").getValue().toString());
                gst.setText(dataSnapshot.child("gst").getValue().toString());
                line.setText(dataSnapshot.child("line").getValue().toString());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        lineReference = FirebaseDatabase.getInstance().getReference("Line");

        updateLine();
    }

    private void updateLine() {

        linelist.clear();
        lineReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;
                ar = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    ar[i] = child.getKey();

                    //Updates to Customer on every single changes
                    linelist.add(new Line(ar[i]));
                    adapter = new LineAdapter(EditClientActivity.this, linelist);
                    line.setAdapter(adapter);
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private boolean validate() {

        if (line.getText().toString().trim().equals("")) {
            line.setError("Required");
            return false;
        }
        if (name.getText().toString().trim().equals("")) {
            name.setError("Required");
            return false;
        }
        if (phone.getText().toString().trim().equals("")) {
            phone.setError("Required");
            return false;
        }
//        if (email.getText().toString().trim().equals("")) {
//            email.setError("Required");
//            return false;
//        }
        if (add1.getText().toString().trim().equals("")) {
            add1.setError("Required");
            return false;
        }
        if (add2.getText().toString().trim().equals("")) {
            add2.setError("Required");
            return false;
        }
        if (city.getText().toString().trim().equals("")) {
            city.setError("Required");
            return false;
        }
        if (state.getText().toString().trim().equals("")) {
            state.setError("Required");
            return false;
        }
        if (pincode.getText().toString().trim().equals("")) {
            pincode.setError("Required");
            return false;
        }
//        if (gst.getText().toString().trim().equals("")) {
//            gst.setError("Required");
//            return false;
//        }

        return true;
    }

    public void add_client(View view) {
        if (validate()) {

            progressDialog = ProgressDialog.show(EditClientActivity.this, "Please wait", "Saving", true);
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Client");


            reference.child(clientid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                    Stores line value with client id in line tree

                    lineReference.child(line.getText().toString().trim()).child(String.valueOf(clientid)).setValue(name.getText().toString().trim());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", name.getText().toString().trim());
                    hashMap.put("client_id", String.valueOf(clientid));
                    hashMap.put("phone", phone.getText().toString().trim());
                    hashMap.put("email", email.getText().toString().trim());
                    hashMap.put("add1", add1.getText().toString().trim());
                    hashMap.put("add2", add2.getText().toString().trim());
                    hashMap.put("city", city.getText().toString().trim());
                    hashMap.put("state", state.getText().toString().trim());
                    hashMap.put("pincode", pincode.getText().toString().trim());
                    hashMap.put("line", line.getText().toString().trim());
                    hashMap.put("gst", gst.getText().toString().trim());


                    reference.child(String.valueOf(clientid)).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            //Incrementing the Client ID and storing in the PrefsManager
//                    incrementPref.setClientID(String.valueOf(Integer.parseInt(client_id) + 1));

                            progressDialog.dismiss();
                            Toast.makeText(EditClientActivity.this, "Edited the client successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditClientActivity.this, "Failed ! Please Check your Internet Connection and retry", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}