package com.rabbitt.momobill.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.pdf.PdfPCell;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.DeviceListActivity;
import com.rabbitt.momobill.adapter.BillAdapter;
import com.rabbitt.momobill.adapter.GstAdapter;
import com.rabbitt.momobill.demo.PrinterCommands;
import com.rabbitt.momobill.demo.UnicodeFormatter;
import com.rabbitt.momobill.demo.Utils;
import com.rabbitt.momobill.model.BillModel;
import com.rabbitt.momobill.model.GstModel;
import com.rabbitt.momobill.model.ProductInvoice;
import com.rabbitt.momobill.model.Summary;
import com.zj.btsdk.PrintPic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_CITY;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_GST;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC_2;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PHONE;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PIN;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PREF;

public class BlueToothFragment extends Fragment implements Runnable {

    RecyclerView billRecycler, gstRecycler;
    Button mPrint, mScan;


//    Print declarations

    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;
    List<ProductInvoice> data = null;
    String inv_no;

    public static DatabaseReference myRef, invoiceRef;

    TextView add1Txt, add2Txt, cityTxt, phoneTxt, gstinTxt;
    TextView billNoTxt, dateTxt, timeTxt, counterTxt;
    TextView totalItem, netAmnt;

    public static Fragment newInstance(Bundle bundle1) {
        BlueToothFragment fragment = new BlueToothFragment();
        Bundle args = new Bundle();
        args = bundle1;
//        data = (data)
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_blue_tooth, container, false);
        init(inflate);
        return inflate;
    }

    private void init(final View inflate) {
        billRecycler = inflate.findViewById(R.id.bill_recycler);
        gstRecycler = inflate.findViewById(R.id.gst_recycler);
        mScan = inflate.findViewById(R.id.scan);
        mPrint = inflate.findViewById(R.id.print);

//        Company details

        add1Txt = inflate.findViewById(R.id.add_line_1);
        add2Txt = inflate.findViewById(R.id.add_line_2);
        cityTxt = inflate.findViewById(R.id.add_line_3);
        phoneTxt = inflate.findViewById(R.id.phone);
        gstinTxt = inflate.findViewById(R.id.gstin);


        SharedPreferences preference = getContext().getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

        String add1 = preference.getString(USER_LOC, "Address Line 1");
        String add2 = preference.getString(USER_LOC_2, "Address Line 2");
        String city = preference.getString(USER_CITY, "City");
        String phone = preference.getString(USER_PHONE, "phone");
        String pin = preference.getString(USER_PIN, "Pin code");

        String gstin = preference.getString(USER_GST, "gstin");

        add1Txt.setText(add1);
        add2Txt.setText(add2);
        cityTxt.setText(city + "-" + pin);
        phoneTxt.setText("PH - " + phone);
        gstinTxt.setText("GSTIN : " + gstin);


//      Bill No, Date ,time

        Bundle bundle = getArguments();
        data = (List<ProductInvoice>) bundle.getSerializable("data");
        inv_no = bundle.getString("inv");
        if (data != null) {
            Log.i(TAG, "onCreate: " + data.size());
        } else {
            Log.i(TAG, "onCreate: " + "bundle null");
        }


        billNoTxt = inflate.findViewById(R.id.bill);
        dateTxt = inflate.findViewById(R.id.date);
        timeTxt = inflate.findViewById(R.id.time);

        billNoTxt.setText(inv_no);
        dateTxt.setText(getDate_());
        timeTxt.setText(getTime());

//        Total item, amount,net amnt

        totalItem = inflate.findViewById(R.id.total_item);
        netAmnt = inflate.findViewById(R.id.net_amnt);


//        Recyclers for gst and bill

//        billRecycler.setHasFixedSize(true);
//        gstRecycler.setHasFixedSize(true);

        billRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        gstRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        billRecycler.setNestedScrollingEnabled(false);
        gstRecycler.setNestedScrollingEnabled(false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        invoiceRef = database.getReference("Invoice");


        populateBill();
        populateGst();

        mScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getContext(), "Message1", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent,
                                REQUEST_ENABLE_BT);
                    } else {
                        ListPairedDevices();
                        Intent connectIntent = new Intent(getContext(),
                                DeviceListActivity.class);
                        startActivityForResult(connectIntent,
                                REQUEST_CONNECT_DEVICE);
                    }
                }
            }
        });


        mPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewToImg(inflate);
            }
        });
    }

    // <================================================== Scan and print code starts Here ==================================================> //


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }

    public void onActivityResult(int mRequestCode, int mResultCode,
                                 Intent mDataIntent) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

        switch (mRequestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (mResultCode == Activity.RESULT_OK) {
                    Bundle mExtra = mDataIntent.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);

                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(getContext(),
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, true);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (mResultCode == Activity.RESULT_OK) {
                    ListPairedDevices();
                    Intent connectIntent = new Intent(getContext(),
                            DeviceListActivity.class);
                    startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(getContext(), "Message", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ListPairedDevices() {
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
                .getBondedDevices();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
                        + mDevice.getAddress());
            }
        }
    }

    public void run() {
        try {
            mBluetoothSocket = mBluetoothDevice
                    .createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            mHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
            return;
        }
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(getContext(), "Device Connected", Toast.LENGTH_SHORT).show();
        }
    };

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    public byte[] sel(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putInt(val);
        buffer.flip();
        return buffer.array();
    }

    private void print(String file) {


        PrintPic pg = new PrintPic();
        pg.initCanvas(490);
        pg.initPaint();
        pg.drawImage(0, 0, file);
        byte[] sendData = pg.printDraw();

        OutputStream os = null;
        try {
            os = mBluetoothSocket.getOutputStream();
            os.write(PrinterCommands.ESC_ALIGN_LEFT);
            os.write(sendData);
        } catch (IOException e) {
            e.printStackTrace();
        }

//
//        Thread t = new Thread() {
//            public void run() {
//                try {
//                    OutputStream os = mBluetoothSocket
//                            .getOutputStream();
//
//                    os.write(img);
//                    //This is printer specific code you can comment ==== > Start
//
//                    // Setting height
//                    int gs = 29;
//                    os.write(intToByteArray(gs));
//                    int h = 104;
//                    os.write(intToByteArray(h));
//                    int n = 162;
//                    os.write(intToByteArray(n));
//
//                    // Setting Width
//                    int gs_width = 29;
//                    os.write(intToByteArray(gs_width));
//                    int w = 119;
//                    os.write(intToByteArray(w));
//                    int n_width = 2;
//                    os.write(intToByteArray(n_width));
//
//
//                } catch (Exception e) {
//                    Log.e("MainActivity", "Exe ", e);
//                }
//            }
//        };
//        t.start();
    }

// <================================================== Scan and print code ends Here ==================================================> //


    private void viewToImg(View inflate) {
        View view = inflate.findViewById(R.id.frame);
//        ScrollView scrollView = findViewById(R.id.frame);
        LinearLayout linearLayout = inflate.findViewById(R.id.linear);

        int width = linearLayout.getWidth();
        int height = linearLayout.getHeight();

        Bitmap bitmap = getBitmapFromView(linearLayout, width, height);

        Log.i(TAG, "viewToImg: " + bitmap.getWidth() + bitmap.getHeight());

        bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() / 2), (bitmap.getHeight() / 2), true);

        String extr = Environment.getExternalStorageDirectory() + "/Santha Agencies/";
        String filename = "Bill" + inv_no + ".jpg";
        File path = new File(extr, filename);
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(path);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            byte[] img = Utils.decodeBitmap(bitmap);
            print(extr + filename);
//            MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Screen","Screen");
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private Bitmap getBitmapFromView(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }


    private void populateBill() {

        ArrayList<BillModel> billData = new ArrayList<>();

        double amtbefore = 0.0, tax_inc = 0.0;
        int size = data.size();
        totalItem.setText(String.valueOf(size));
        for (int i = 0; i < data.size(); i++) {
            ProductInvoice productInvoice = data.get(i);

            String pre_amount = "";
            String product_name = productInvoice.getProduct_name();
            double qty = Double.parseDouble(productInvoice.getUnit());

            amtbefore = amtbefore + Double.parseDouble(productInvoice.getSale_rate());
            double total = calculate(productInvoice.getIn(), Double.parseDouble(productInvoice.getCgst()), Double.parseDouble(productInvoice.getCess()), Double.parseDouble(productInvoice.getSale_rate()));
            tax_inc = tax_inc + total;

            double mrp = total / qty;

            billData.add(new BillModel(product_name, productInvoice.getMrp(), String.valueOf((int) qty), String.valueOf(total)));
        }
        netAmnt.setText(String.valueOf(tax_inc));
        BillAdapter billAdapter = new BillAdapter(billData);
        billRecycler.setAdapter(billAdapter);
    }

    private void populateGst() {

//        TODO To feed gst data into recycler

        ArrayList<GstModel> gstData = new ArrayList<>();
        List<ProductInvoice> inc = new ArrayList<>();

        List<ProductInvoice> tax = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {  // Filtering the model which the tax are not inclusive
            if (data.get(i).getIn().equals("exc")) {
                tax.add(data.get(i));
                Log.i(TAG, "parseData: " + data.get(i).toString());
            } else {
                // Inclusive data collection
                inc.add(data.get(i));
                Log.i(TAG, "INC: " + data.get(i).toString());
            }
        }

        //Getting tax for 0%
        double noTax = 0.0;
        for (int i = 0; i < inc.size(); i++) {
            noTax += Double.parseDouble(inc.get(i).getSale_rate());
        }

        ArrayList<Integer> tax_val = new ArrayList<>(); //Split up the taxes for the EXCLUSIVE products

        for (int i = 0; i < tax.size(); i++) {
            Log.i(TAG, "parseData: 2: " + tax.get(i).getCgst());
            tax_val.add(Integer.valueOf(tax.get(i).getCgst()));
        }

        Set<Integer> set = new HashSet<Integer>();
        set.addAll(tax_val); //Getting the unique values

        List<Summary> val = new ArrayList<>(); //Getting taxable and the gst percentages

        for (Iterator<Integer> it = set.iterator(); it.hasNext(); ) { //3
            int f = it.next();
            double taxable = 0.0;
            for (int i = 0; i < tax.size(); i++) { //4
                Log.i(TAG, "Iteration: " + i);
                if (tax.get(i).getCgst().equals(String.valueOf(f))) {
                    taxable += Double.parseDouble(tax.get(i).getSale_rate());
                }
            }
            Log.i(TAG, "Taxable: " + taxable + "    " + f);

            //Custom model
            Summary summary = new Summary();
            summary.setPercentage(String.valueOf(f));
            summary.setTaxable(String.valueOf(taxable));

            val.add(summary);
            Log.i(TAG, "parseData:" + f);
        }


        if (inc.size() != 0) {
            gstData.add(new GstModel("0%", String.valueOf(noTax), "0.00", "0.00", String.valueOf(noTax)));
        }

        for (int i = 0; i < val.size(); i++) {
            double taxable = Double.parseDouble(val.get(i).getTaxable());
            double per = Double.parseDouble(val.get(i).getPercentage());
            double cgst = taxable * ((per / 2.0) / 100.0);
            String percentage = String.valueOf(per);
            String taxTxt = String.valueOf(taxable);
            String cgstTxt = String.valueOf(roundDecimals(cgst));
            double total = taxable + (cgst * 2);
            total = roundDecimals(total);
            String totalTxt = String.valueOf(total);
            gstData.add(new GstModel(percentage, taxTxt, cgstTxt, cgstTxt, totalTxt));
        }


//        gstData.add(new GstModel("0","37.00","0.00","0.00","37.00"));
//        gstData.add(new GstModel("5","9.52","0.24","0.24","10.00"));
//        gstData.add(new GstModel("12","153.57","9.21","9.21","172.00"));

        GstAdapter gstAdapter = new GstAdapter(gstData);
        gstRecycler.setAdapter(gstAdapter);
    }

    public String getDate() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.short_format));

        return df.format(c);
    }

    public String getDate_() {
        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        return df.format(c);
    }

    private String getTime() {

        Date c = Calendar.getInstance().getTime();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");

        return df.format(c);
    }

    private double calculate(String inc, double gst, double ces, double rat) {

        double return_value;
        if (inc.equals("inc")) {
            return_value = rat;
        } else {
            return_value = calculate_amount(rat, gst, ces);
        }

        return return_value;
    }

    private double calculate_amount(double sale_r, double gst, double ces) {
        double taxval = ces + gst;
        sale_r = (sale_r * (taxval / 100)) + /*Actual rate*/sale_r; //Adding gst + actual rate
        return roundDecimals(sale_r);
    }

    double roundDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Double.parseDouble(twoDForm.format(d));
    }


}