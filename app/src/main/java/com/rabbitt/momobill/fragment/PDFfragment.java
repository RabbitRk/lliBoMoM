package com.rabbitt.momobill.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.joanzapata.pdfview.PDFView;
import com.rabbitt.momobill.BuildConfig;
import com.rabbitt.momobill.R;

import java.io.File;

public class PDFfragment extends Fragment {

    private PDFView imageView;
    Button shareBtn;

    File file;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pdf, container, false);
        imageView = inflate.findViewById(R.id.pdfview);
        shareBtn = inflate.findViewById(R.id.share_btn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePdf();
            }
        });


        Bundle bundle = getArguments();

        String invoice = bundle.getString("inv");

        String path1 = Environment.getExternalStorageDirectory() + "/Santha Agencies" + "/Invoice/"+ invoice + "_Inv.pdf";
        file = new File(path1);
        imageView.fromFile(file);
        imageView.fromFile(file)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .swipeVertical(true)
                .load();

        return inflate;
    }

    private void sharePdf() {

        // create new Intent
        Intent intent = new Intent(Intent.ACTION_SEND);

        // set flag to give temporary permission to external app to use your FileProvider
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // generate URI, I defined authority as the application ID in the Manifest, the last param is file I want to open
        Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, file);


        intent.putExtra(Intent.EXTRA_STREAM,uri);

        //giving intent a valid MIME type
        intent.setType("application/pdf");


        startActivity(Intent.createChooser(intent,"Share With"));

    }
}
