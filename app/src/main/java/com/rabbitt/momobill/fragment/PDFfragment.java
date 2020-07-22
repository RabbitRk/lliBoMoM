package com.rabbitt.momobill.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.joanzapata.pdfview.PDFView;
import com.rabbitt.momobill.R;

import java.io.File;

public class PDFfragment extends Fragment {

    private PDFView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pdf, container, false);
        imageView = inflate.findViewById(R.id.pdfview);

        Bundle bundle = getArguments();

        String invoice = bundle.getString("inv");

        String path1 = Environment.getExternalStorageDirectory() + "/Santha Agencies" + "/Invoice/"+ invoice + "_Inv.pdf";
        File file = new File(path1);
        imageView.fromFile(file);
        imageView.fromFile(file)
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .swipeVertical(true)
                .load();

        return inflate;
    }
}
