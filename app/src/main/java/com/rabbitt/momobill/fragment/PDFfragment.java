package com.rabbitt.momobill.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.joanzapata.pdfview.PDFView;
import com.rabbitt.momobill.BuildConfig;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.Volley.ServerCallback;
import com.rabbitt.momobill.Volley.VolleyAdapter;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PDFfragment extends Fragment {

//    private PDFView imageView;
    Button shareBtn;
    WebView printWeb;
    File file;

    private static final String TAG  = "pdf";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pdf, container, false);
//        imageView = inflate.findViewById(R.id.pdfview);
        shareBtn = inflate.findViewById(R.id.share_btn);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePdf();
            }
        });

//
        Bundle bundle = getArguments();
//
        String invoice = bundle.getString("inv");
        Bundle client = bundle.getBundle("client");

        HashMap<String, String> map = new HashMap<>();
        map.put("name", String.valueOf(client.get("name")));
        map.put("phone", String.valueOf(client.get("phone")));
        map.put("email", String.valueOf(client.get("email")));
        map.put("add1", String.valueOf(client.get("add1")));
        map.put("add2", String.valueOf(client.get("add2")));
        map.put("city", String.valueOf(client.get("city")));
        map.put("state", String.valueOf(client.get("state")));
        map.put("pincode", String.valueOf(client.get("pincode")));
        map.put("gst", String.valueOf(client.get("gst")));
//        map.put("gst", String.valueOf(client.get("gst")));
//
//        String path1 = Environment.getExternalStorageDirectory() + "/Santha Agencies" + "/Invoice/"+ invoice + "_Inv.pdf";
//        file = new File(path1);
//        imageView.fromFile(file);
//        imageView.fromFile(file)
//                .defaultPage(1)
//                .showMinimap(false)
//                .enableSwipe(true)
//                .swipeVertical(true)
//                .load();

        // Initializing the WebView
        final WebView webView = (WebView) inflate.findViewById(R.id.webViewMain);
        webView.getSettings().setBuiltInZoomControls(true);
        // Initializing the Button
        Button savePdfBtn = (Button) inflate.findViewById(R.id.print);

        webView.getSettings().setDefaultTextEncodingName("utf-8");


        HashMap<String, String> main = new HashMap<>();
        main.put("client", String.valueOf(new JSONObject(map)));

        VolleyAdapter volleyAdapter = new VolleyAdapter(getContext());

        volleyAdapter.getData(main, "http://192.168.43.34/mark45/invoice", new ServerCallback() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "onSuccess: "+s);
//                webView.loadUrl(s);
                webView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
            }

            @Override
            public void onError(String s) {

            }
        });
        // Setting we View Client
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // initializing the printWeb Object
                printWeb = webView;
            }
        });
        // setting clickListener for Save Pdf Button
        savePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (printWeb != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Calling createWebPrintJob()
                        PrintTheWebPage(printWeb);
                    } else {
                        // Showing Toast message to user
                        Toast.makeText(getContext(), "Not available for device below Android LOLLIPOP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Showing Toast message to user
                    Toast.makeText(getContext(), "WebPage not fully loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return inflate;
    }
    // object of print job
    PrintJob printJob;

    // a boolean to check the status of printing
    boolean printBtnPressed = false;
    private void PrintTheWebPage(WebView webView) {
        // set printBtnPressed true
        printBtnPressed = true;

        // Creating PrintManager instance
        PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);

        // setting the name of job
        String jobName = getString(R.string.app_name) + " webpage" + webView.getUrl();

        // Creating PrintDocumentAdapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        assert printManager != null;
        printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
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
