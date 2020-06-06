package com.rabbitt.momobill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rabbitt.momobill.demo.DemoFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "maluHomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.home));
        loadFragment(new DemoFragment());

        //Append the data in the Post branch
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Test");

        String postid = reference.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid","postid");
        hashMap.put("postimage","postimage");
        hashMap.put("description","description");
        hashMap.put("publisher","publisher");

        Log.i(TAG, "uploadImage_10: " + hashMap.toString());

        reference.child(postid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "onComplete: "+task.toString());
            }
        });
    }

    public Boolean loadFragment(final Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            Log.i(TAG, "Current thread: " + Thread.currentThread().getId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentcontainer, fragment)
                    .addToBackStack(null)
                    .commit();

            return true;
        }

        return false;
    }
}