package com.rabbitt.momobill.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;

public class DeleteSheet extends BottomSheetDialogFragment {

    private static final String TAG = "maluDelete";
    View v;
    String product_id;

    public DeleteSheet(String product_id)
    {
        this.product_id = product_id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.delete_bottom_sheet, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        TextView textView = v.findViewById(R.id.product_name);
        Button btn = v.findViewById(R.id.clear_data);


        textView.setText(product_id);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(product_id);
            }
        });
    }

    private void deleteData(String product_id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Product").child(product_id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }
}
