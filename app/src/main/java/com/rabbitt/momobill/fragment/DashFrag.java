package com.rabbitt.momobill.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.activity.CheckOrderActivity;
import com.rabbitt.momobill.activity.OpeningActivity;
import com.rabbitt.momobill.model.CreditModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DashFrag extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    CardView today, month, invoice, credit;

    public static DatabaseReference myRef, clientRef, productRef, creditRef, invoiceRef;
    static ArrayList<String> crnamelist, billlist, crdatelist, amntlist, paidlist, creditlist;


    static ArrayList<String> namelist, gstinlist, datelist, basicvallist, taxlist, cesslist;
    ArrayList<CreditModel> list;
    String date, billno, name, amnt, paid, cr, clientid;
    ProgressDialog progressDialog;

    public DashFrag() {
        // Required empty public constructor
    }

    public static DashFrag newInstance(String param1, String param2) {
        DashFrag fragment = new DashFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_dash, container, false);
        init(inflate);

        namelist = new ArrayList<>();
        datelist = new ArrayList<>();
        gstinlist = new ArrayList<>();
        basicvallist = new ArrayList<>();
        taxlist = new ArrayList<>();
        cesslist = new ArrayList<>();

        crnamelist = new ArrayList<>();
        crdatelist = new ArrayList<>();
        billlist = new ArrayList<>();
        amntlist = new ArrayList<>();
        paidlist = new ArrayList<>();
        creditlist = new ArrayList<>();

        list = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Order");
        clientRef = database.getReference("Client");
        productRef = database.getReference("Product");
        creditRef = database.getReference("Credits");
        invoiceRef = database.getReference("Invoice");

        return inflate;
    }

    private void init(View view) {
        today = view.findViewById(R.id.txt_today);
        month = view.findViewById(R.id.txt_month);
        credit = view.findViewById(R.id.txt_credit);
        invoice = view.findViewById(R.id.txt_invoice);

        today.setOnClickListener(this);
        month.setOnClickListener(this);
        credit.setOnClickListener(this);
        invoice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_today:
                startActivity(new Intent(getActivity(), OpeningActivity.class));
                break;
            case R.id.txt_month:
                startActivity(new Intent(getActivity(), CheckOrderActivity.class));
                break;
            case R.id.txt_credit:
                progressDialog = ProgressDialog.show(getContext(), "Please wait", "Saving", true);
                populateList();
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.txt_invoice:
                progressDialog = ProgressDialog.show(getContext(), "Please wait", "Saving", true);
                populateCreditList();
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
        }
    }

    private void populateCreditList() {


        crnamelist.clear();
        crdatelist.clear();
        billlist.clear();
        creditlist.clear();
        amntlist.clear();
        paidlist.clear();

        creditRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    clientid = child.getKey();
                    Log.i("Invoice Values", clientid);


                    for (final DataSnapshot clientChild : child.getChildren()) {


                        billno = clientChild.getKey();
                        cr = clientChild.child("balance").getValue().toString();
                        amnt = clientChild.child("amount").getValue().toString();
                        date = clientChild.child("date_of").getValue().toString();
                        name = clientChild.child("client_name").getValue().toString();
                        paid = clientChild.child("paid").getValue().toString();

                        list.add(new CreditModel(billno, cr, amnt, date, name, paid));
                        Log.i("Invoice Values", billno + "  " + cr);
                    }

                }
                saveInvoiceExcel();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

//    private String getClientName(String clientid) {
//
//
//        return name;
//    }

    private boolean saveInvoiceExcel() {

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;


        //New Sheet
        final Sheet sheet1 = wb.createSheet("Orders");


        // Generate column headings
        final Row row = sheet1.createRow(0);


        c = row.createCell(0);
        c.setCellValue("Date");
//        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Bill No");
//        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Name");
//        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Total Amount");

        c = row.createCell(4);
        c.setCellValue("Paid Amount");

        c = row.createCell(5);
        c.setCellValue("Credit");


        for (int i = 1; i <= list.size(); i++) {

            Row row1 = sheet1.createRow(i);

            Cell cell = null;

            cell = row1.createCell(0);
            cell.setCellValue(list.get(i - 1).getDate());


            cell = row1.createCell(1);
            cell.setCellValue(list.get(i - 1).getBill());

            cell = row1.createCell(2);
            cell.setCellValue(list.get(i - 1).getName());

            cell = row1.createCell(3);
            cell.setCellValue(list.get(i - 1).getAmnt());

            cell = row1.createCell(4);
            cell.setCellValue(list.get(i - 1).getPaid());

            cell = row1.createCell(5);
            cell.setCellValue(list.get(i - 1).getCr());


        }


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String filename = dateFormat.format(calendar.getTime()) + "_credit.xls";

        // Create a path where we will place our List of objects on external storage
        File file = new File(Environment.getExternalStorageDirectory() + "/Santha Agencies", filename);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            progressDialog.dismiss();
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }


    private void populateList() {

        namelist.clear();
        datelist.clear();
        gstinlist.clear();
        basicvallist.clear();
        taxlist.clear();
        cesslist.clear();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String date, productId;

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    final String fdate;
                    date = child.getKey().trim();
                    String dd, mm, yy;
                    yy = date.substring(0, date.indexOf("_"));
                    date = date.substring(date.indexOf("_") + 1);
                    mm = date.substring(0, date.indexOf("_"));
                    date = date.substring(date.indexOf("_") + 1);
                    dd = date;


                    fdate = dd + "-" + mm + "-" + yy;
//                    datelist.add(date);

                    Log.i("iteration", "1");
                    for (DataSnapshot clientChild : child.getChildren()) {


                        final String clientId = clientChild.getKey();
                        Log.i("iteration", "2");
                        for (DataSnapshot productChild : clientChild.getChildren()) {

                            productId = productChild.getKey();


                            Log.i("iteration", "3");

                            if (productChild.hasChildren()) {

                                final String basicval = productChild.child("sale_rate").getValue().toString();

                                final String tax = productChild.child("cgst").getValue().toString();

                                final String cess = productChild.child("cess").getValue().toString();


                                clientRef.child(clientId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String gstin = dataSnapshot.child("gst").getValue().toString();

                                        String name = dataSnapshot.child("name").getValue().toString();
                                        Log.i("clientid", name);
                                        gstinlist.add(gstin);
                                        namelist.add(name);
                                        datelist.add(fdate);
                                        basicvallist.add(basicval);
                                        taxlist.add(tax);
                                        cesslist.add(cess);
                                        saveExcelFile();


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }

        });


    }

    private boolean saveExcelFile() {

        // check if available and not read only
//        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
////            Log.e(TAG, "Storage not available or read only");
//            return false;
//        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;


        //New Sheet
        final Sheet sheet1 = wb.createSheet("Orders");


        // Generate column headings
        final Row row = sheet1.createRow(0);


        c = row.createCell(0);
        c.setCellValue("Company Name");
//        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Export Date");
//        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("GSTIN");
//        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Name");
//        c.setCellStyle(cs);

        c = row.createCell(4);
        c.setCellValue("Inv No");

        c = row.createCell(5);
        c.setCellValue("Date");

        c = row.createCell(6);
        c.setCellValue("Basic Value");

        c = row.createCell(7);
        c.setCellValue("Tax");

        c = row.createCell(8);
        c.setCellValue("IGST");

        c = row.createCell(9);
        c.setCellValue("CGST");

        c = row.createCell(10);
        c.setCellValue("SGST");

        c = row.createCell(11);
        c.setCellValue("Cess");

        c = row.createCell(12);
        c.setCellValue("Total");

        Log.i("clientid", String.valueOf(namelist.size()));
        for (int i = 1; i <= datelist.size(); i++) {

            Row row1 = sheet1.createRow(i);

            Cell cell = null;

            cell = row1.createCell(0);
            cell.setCellValue("Santha Agencies");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String expdate = dateFormat.format(calendar.getTime());
            cell = row1.createCell(1);
            cell.setCellValue(expdate);

            cell = row1.createCell(2);
            cell.setCellValue(gstinlist.get(i - 1));

            cell = row1.createCell(3);
            cell.setCellValue(namelist.get(i - 1));

            cell = row1.createCell(4);
            cell.setCellValue(i);

            cell = row1.createCell(5);
            cell.setCellValue(datelist.get(i - 1));

            cell = row1.createCell(6);
            cell.setCellValue(basicvallist.get(i - 1));

            cell = row1.createCell(7);
            cell.setCellValue(taxlist.get(i - 1));

            cell = row1.createCell(8);
            cell.setCellValue(taxlist.get(i - 1));

            cell = row1.createCell(9);
            cell.setCellValue(taxlist.get(i - 1));

            cell = row1.createCell(10);
            cell.setCellValue(taxlist.get(i - 1));

            cell = row1.createCell(11);
            cell.setCellValue(cesslist.get(i - 1));

            cell = row1.createCell(12);
            cell.setCellValue(basicvallist.get(i - 1));

        }


//        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        String filename = dateFormat.format(calendar.getTime()) + "_Order.xls";
        // Create a path where we will place our List of objects on external storage
        File file = new File(Environment.getExternalStorageDirectory() + "/Santha Agencies", filename);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;

            progressDialog.dismiss();

        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        return success;
    }


}