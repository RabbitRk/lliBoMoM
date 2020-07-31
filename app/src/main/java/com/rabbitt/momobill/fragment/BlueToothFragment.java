package com.rabbitt.momobill.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.DeviceListActivity;
import com.rabbitt.momobill.demo.UnicodeFormatter;
import com.rabbitt.momobill.model.ProductInvoice;
import com.rabbitt.momobill.prefsManager.PrefsManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_CITY;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_GST;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC_2;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PHONE;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PIN;

public class BlueToothFragment extends Fragment implements View.OnClickListener,Runnable {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

    //Additionally added
    protected static final String TAG = "TAG";
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    Button mScan, mPrint, mDisc;
    BluetoothAdapter mBluetoothAdapter;
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog mBluetoothConnectProgressDialog;
    private BluetoothSocket mBluetoothSocket;
    BluetoothDevice mBluetoothDevice;


    String BILL = "";
    List<ProductInvoice> data = null;

    int totalQty;
    double totalVal;

    TextView txt,txt2;
    public BlueToothFragment() {
        // Required empty public constructor
    }

//    public static BlueToothFragment newInstance(String param1, String param2) {
//        BlueToothFragment fragment = new BlueToothFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

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

        Bundle bundle = getArguments();
        data = (List<ProductInvoice>) bundle.getSerializable("data");
        if (data != null) {
            Log.i(TAG, "onCreate: "+data.size());
        }
        else
        {
            Log.i(TAG, "onCreate: "+"bundle null");
        }
//        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
//
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_blue_tooth, container, false);
        init(inflate);
        return inflate;
    }

    private void init(View inflate) {
        mScan = inflate.findViewById(R.id.scan);
        mPrint = inflate.findViewById(R.id.print);
        txt = inflate.findViewById(R.id.bill_fomat);


        SharedPreferences preference = getContext().getSharedPreferences(PrefsManager.USER_PREF,0);
        String add1 = preference.getString(USER_LOC,"Address Line 1");
        String add2 = preference.getString(USER_LOC_2,"Address Line 2");
        String city = preference.getString(USER_CITY,"City");
        String phone = preference.getString(USER_PHONE,"state");
        String pin = preference.getString(USER_PIN,"Pin code");

        String gstin = preference.getString(USER_GST,"gstin");




//        Initializing total value and quantity to 0
        totalQty = 0;
        totalVal = 0.0;

//        get invoice data from argument
        List<ProductInvoice> data = (List<ProductInvoice>) getArguments().getSerializable("data");

        String invId = getArguments().getString("inv");

        //
//        String BILL = "";

//        BILL =  "                   XXXX MART    \n" +
//                "                 XX.AA.BB.CC.     \n " +
//                "               NO 25 ABC ABCDE    \n" +
//                "                 XXXXX YYYYYY      \n" +
//                "                MMM 590019091      \n";

        BILL =  "SANTHA AGENCIES\n" +
                add1.toUpperCase()+",\n " +
                add2.toUpperCase()+",\n" +
                city.toUpperCase()+"-" +
                pin.toUpperCase()+",\n" +
                "PH : "+phone+",\n" +
                "GSTIN : "+gstin.toUpperCase()+",\n" ;

        BILL = BILL
                + "----------------------------------------------------------------------------\n";
//        BILL = BILL + String.format("%1$-12s %2$20s", "Bill No :" , invId);
//        BILL = BILL + "\n";

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df =new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat tf =new SimpleDateFormat("hh:mm a");
        String date_txt =  df.format(date);

        BILL = BILL + String.format("%1$-10s %2$-10s %3$-10s %4$-10s","Bill No :",invId, "Date : " , date_txt);
        BILL = BILL + "\n";
        BILL = BILL + String.format("%1$-10s %2$-10s %3$-10s %4$-10s","Counter :","Counter", "Time : " , tf.format(date));
        BILL = BILL + "\n";


        BILL = BILL
                + "----------------------------------------------------------------------------\n";


        BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "PRODUCT", "QTY", "RATE", "AMT");
        BILL = BILL + "\n";
        BILL = BILL
                + "----------------------------------------------------------------------------";

//generating bill
        if (data != null) {

            for(ProductInvoice ob : data)
            {
                String product;
                double total,rate;
                int quantity;


                product = ob.getProduct_name();
                quantity = Integer.parseInt(ob.getUnit());
                total = Double.parseDouble(ob.getSale_rate());
                rate = total/quantity;

                totalQty = totalQty + quantity;
                totalVal = totalVal + total;

                BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$13s %4$10s", product, quantity, rate, total);
                Log.i(TAG, "Item "+product);
                Log.i(TAG, "Quantity "+quantity);
                Log.i(TAG, "Total "+total);

            }
        }


//        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-002", "10", "5", "50.00");
//        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-003", "20", "10", "200.00");
//        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-004", "50", "10", "500.00");

        BILL = BILL
                + "\n----------------------------------------------------------------------------";
        BILL = BILL + "\n"+String.format("%1$-10s %2$-50s","TOTAL ITEMS :", totalQty );
        BILL = BILL
                + "\n----------------------------------------------------------------------------";
//        BILL = BILL + "\n\n ";
//        String BILL ="";
        BILL = BILL + "           TOTAL :" + "          " + totalVal + "\n";
        BILL = BILL + "NET AMOUNT :" + "          " + totalVal + "\n";

        BILL = BILL
                + "----------------------------------------------------------------------------\n";
        BILL = BILL + "GST SUMMARY DETAILS";
        BILL = BILL + "\n ";
        BILL = BILL
                + "----------------------------------------------------------------------------\n";

        BILL = BILL + String.format("%1$5s %2$8s %3$8s %4$8s %5$8s", "TAX % ", "Taxable Val", "SGST", "CGST","Total");
        BILL = BILL + "\n ";
        BILL = BILL + String.format("%1$-10s %2$8s %3$15s %4$12s %5$12s", "0", "37.00", "0.00", "0.00","37.00");
        BILL = BILL + "\n ";
        BILL = BILL + String.format("%1$-10s %2$8s %3$15s %4$12s %5$12s", "5", "9.52", "0.24", "0.24","10.00");
        BILL = BILL + "\n ";
        BILL = BILL + String.format("%1$-10s %2$8s %3$15s %4$12s %5$12s", "12", "153.57", "9.21", "9.21","172.00");
        BILL = BILL + "\n ";

        BILL = BILL
                + "----------------------------------------------------------------------------\n";
        BILL = BILL + String.format("%1$-10s %2$-50s","TOTAL SAVINGS :", "RS. 7.00" );
        BILL = BILL
                + "----------------------------------------------------------------------------\n";

        BILL = BILL + String.format("%1$10s %2$10s","  Given Amount :", "RS. 250.00" );
        BILL = BILL + "\n ";
        BILL = BILL + String.format("%1$10s %2$10s","Balance Amount :", "RS. 31.00 " );
        BILL = BILL + "\n ";

        BILL = BILL
                + "----------------------------------------------------------------------------\n";

        BILL = BILL + "THANK YOU FOR PURCHASING";


        //This is printer specific code you can comment ==== > Start

        txt.setText(BILL);

        mScan.setOnClickListener(this);
        mPrint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.scan:
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

                break;
            case R.id.print:
                print(BILL);
                break;
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
            Toast.makeText(getContext(), "DeviceConnected", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket.close();
        } catch (Exception e) {
            Log.e("Tag", "Exe ", e);
        }
    }



    private void print(final String BILL) {
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = mBluetoothSocket.getOutputStream();

                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

//                    txt.setText(BILL);


                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));


                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        t.start();
    }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle mExtra = data.getExtras();
                    String mDeviceAddress = mExtra.getString("DeviceAddress");
                    Log.v(TAG, "Coming incoming address " + mDeviceAddress);
                    mBluetoothDevice = mBluetoothAdapter
                            .getRemoteDevice(mDeviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(getContext(),
                            "Connecting...", mBluetoothDevice.getName() + " : "
                                    + mBluetoothDevice.getAddress(), true, false);
                    Thread mBlutoothConnectThread = new Thread(this);
                    mBlutoothConnectThread.start();
                    // pairToDevice(mBluetoothDevice); This method is replaced by
                    // progress dialog with thread
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
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
}