package com.rabbitt.momobill.demo;

import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.joanzapata.pdfview.PDFView;
import com.rabbitt.momobill.R;

import java.io.File;
import java.io.IOException;

public class pdfreader extends AppCompatActivity {

    private PDFView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);
        imageView = (PDFView) findViewById(R.id.pdfview);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Invoice Preview");

        try {
            openPDF();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Something Wrong: " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }

    private void openPDF() throws IOException {

        try
        {
            if(getIntent().getStringExtra("from").equals("genrate")) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + getIntent().getStringExtra("inv") + "temp.pdf");
                imageView.fromFile(file);
                imageView.fromFile(file)
                        .defaultPage(1)
                        .showMinimap(false)
                        .enableSwipe(true)
                        .swipeVertical(true)
                        .load();
            }
            else {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + getIntent().getStringExtra("inv") + ".pdf");
                if(file.exists()) {
                    imageView.fromFile(file);
                    imageView.fromFile(file)
                            .defaultPage(1)
                            .showMinimap(false)
                            .enableSwipe(true)
                            .swipeVertical(true)
                            .load();
                }
                else
                {
                    Toast.makeText(this, "File Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e)
        {
            Log.i("maluPdf", "Exception: "+e.toString());
        }

    }

    /**
     * this method will delete the file after previewing it
     */


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().getStringExtra("from").equals("generate")) {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + getIntent().getStringExtra("inv") + "temp.pdf");
            file.delete();
        }
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        onBackPressed();
        if(getIntent().getStringExtra("from").equals("generate")) {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + getIntent().getStringExtra("inv") + "temp.pdf");
            file.delete();
        }
        return null;

    }
}
