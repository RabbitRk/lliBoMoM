package com.rabbitt.momobill.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.rabbitt.momobill.BuildConfig;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.Volley.ServerCallback;
import com.rabbitt.momobill.Volley.VolleyAdapter;
import com.rabbitt.momobill.model.ProductInvoice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.rabbitt.momobill.prefsManager.PrefsManager.CREDIT;
import static com.rabbitt.momobill.prefsManager.PrefsManager.DATE;
import static com.rabbitt.momobill.prefsManager.PrefsManager.LINE;
import static com.rabbitt.momobill.prefsManager.PrefsManager.PREF_NAME;
import static com.rabbitt.momobill.prefsManager.PrefsManager.URL;

public class PDFfragment extends Fragment {

    Button shareBtn;
    WebView printWeb;
    File file;
    List<ProductInvoice> data = null;

    private static final String TAG  = "pdf";
    String invoice;
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
        invoice = bundle.getString("inv");
        Bundle client = bundle.getBundle("client");
        data = (List<ProductInvoice>) bundle.getSerializable("data");

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


        SharedPreferences shrp = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        HashMap<String, String> misc = new HashMap<>();
        misc.put("inv_no", invoice);
        misc.put("date", shrp.getString(DATE, ""));
        misc.put("route", shrp.getString(LINE, ""));
        misc.put("term", shrp.getString(CREDIT, ""));
        misc.put("url", shrp.getString(URL, ""));

//        map.put("gst", String.valueOf(client.get("gst")));
//        String path1 = Environment.getExternalStorageDirectory() + "/Santha Agencies" + "/Invoice/"+ invoice + "_Inv.pdf";
//        file = new File(path1);
//        imageView.fromFile(file);
//        imageView.fromFile(file)
//                .defaultPage(1)
//                .showMinimap(false)
//                .enableSwipe(true)
//                .swipeVertical(true)
//                .load();

        HashMap<String, String> product;
        HashMap<String, HashMap<String, String>> pro = new HashMap<>();

        for (int i = 0; i < data.size(); i++) {

            product = new HashMap<>();
            ProductInvoice productInvoice = data.get(i);
            product.put("Product_name", productInvoice.getProduct_name());
            product.put("Sale_rate", productInvoice.getSale_rate());
            product.put("Unit", productInvoice.getUnit());
            product.put("Product_id", productInvoice.getProduct_id());
            product.put("Cgst", productInvoice.getCgst());
            product.put("Cess", productInvoice.getCess());
            product.put("Img_url", productInvoice.getImg_url());
            product.put("In", productInvoice.getIn());
            product.put("Mrp", productInvoice.getMrp());
            product.put("Hsn", productInvoice.getHsn());

            pro.put(String.valueOf(i), product);
            Log.i(TAG, "onCreateView: ================================> "+product);
        }
        JSONObject jObject = new JSONObject(pro);
        JSONArray jarray_product, jsonArray_client, jarray_misc;

        //Product
        jarray_product = new JSONArray();
        jarray_product.put(jObject);


        //Client
        jsonArray_client = new JSONArray();
        jsonArray_client.put(new JSONObject(map));

        //Misc
        jarray_misc = new JSONArray();
        jarray_misc.put(new JSONObject(misc));


        HashMap<String, String> main = new HashMap<>();
        main.put("client", String.valueOf(jsonArray_client));
        main.put("product", String.valueOf(jarray_product));
        main.put("misc", String.valueOf(jarray_misc));

        Log.i(TAG, "onCreateView: Main ********************* "+main);


        // Initializing the WebView
        final WebView webView = (WebView) inflate.findViewById(R.id.webViewMain);
        webView.getSettings().setBuiltInZoomControls(true);
        // Initializing the Button
        Button savePdfBtn = (Button) inflate.findViewById(R.id.print);

        webView.getSettings().setDefaultTextEncodingName("utf-8");

        // Setting we View Client
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // initializing the printWeb Object
                printWeb = webView;
            }
        });

        VolleyAdapter volleyAdapter = new VolleyAdapter(getContext());



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Server");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String details_child = "http://192.168.43.34:80/mark45/invoice";
                Log.i("check", "onDataChange: "+dataSnapshot);
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    details_child = child.getValue(String.class);
                    Log.i("check", "onDataChange: "+details_child);
                }

                volleyAdapter.getData(main, details_child, new ServerCallback() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i(TAG, "onSuccess: "+s);
                        webView.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);
                    }

                    @Override
                    public void onError(String s) {
                        Toast.makeText(getContext(), "Server not responding...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
//    PrintJob printJob;

    // a boolean to check the status of printing
//    boolean printBtnPressed = false;
    private void PrintTheWebPage(WebView webView) {
//        // set printBtnPressed true
//        printBtnPressed = true;
//
//        // Creating PrintManager instance
//        PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);
//
//        // setting the name of job
//        String jobName = getString(R.string.app_name) + " webpage" + webView.getUrl();
//
//        // Creating PrintDocumentAdapter instance
//        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
//
//        // Create a print job with name and adapter instance
//        assert printManager != null;
//        printJob = printManager.print(jobName, printAdapter,
//                new PrintAttributes.Builder().build());


        PrintManager printManager = (PrintManager) requireActivity().getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        String jobName = "Invoice_"+invoice;
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5.asLandscape());
        PrintJob printJob = printManager.print(jobName, printAdapter, builder.build());

        if(printJob.isCompleted()){
            Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
        }
        else if(printJob.isFailed()){
            Toast.makeText(getContext(), "PDF Failed", Toast.LENGTH_LONG).show();
        }
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
