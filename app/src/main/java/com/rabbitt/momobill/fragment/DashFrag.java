package com.rabbitt.momobill.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
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
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DashFrag extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    int selectedItem = 0, selectedMonth = 0;
    DataSnapshot fulSnapShot;

    CardView today, month, invoice, credit, invoiceReport;

    TextView monthTxt, todayTxt;

    AlertDialog invoiceDialogBuilder;
    String monthName;

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
        myRef = database.getReference();
        clientRef = database.getReference("Client");
        productRef = database.getReference("Product");
        creditRef = database.getReference("Credits");
        invoiceRef = database.getReference("Invoice");


        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy_MM_dd");
        final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

        String today = dbFormat.format(calendar.getTime());
        final String thisMonth = monthFormat.format(calendar.getTime());
        final String thisYear = yearFormat.format(calendar.getTime());

//        To check and retrieve today's invoice total

        invoiceRef.child(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    double amnt = 0.0;

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        DataSnapshot details_child = child.child("Details").child("amount");
                        amnt += Double.parseDouble(String.valueOf(details_child.getValue()));
                    }
                    String amntTxt = String.valueOf(amnt);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en","IN"));
                    amntTxt = formatter.format(amnt);
                    todayTxt.setText(amntTxt);
                } else
                    todayTxt.setText("N/A");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        To check and retrieve this month's invoice total
        invoiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double month_amnt = 0.0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Date dbMonth = null;
                    try {
                        dbMonth = dbFormat.parse(String.valueOf(child.getKey()));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String retrievedMonth = monthFormat.format(dbMonth);
                    String retrievedYear = yearFormat.format(dbMonth);

                    if (retrievedMonth.equals(thisMonth) && retrievedYear.equals(thisYear)) {

                        for (DataSnapshot invoice_child : child.getChildren()) {

                            DataSnapshot details_child = invoice_child.child("Details");
                            month_amnt += Double.parseDouble(String.valueOf(details_child.child("amount").getValue()));

                        }
                    }
                }
                if(month_amnt == 0.0)
                    monthTxt.setText("N/A");
                else {
                    String amntTxt = String.valueOf(month_amnt);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                    amntTxt = formatter.format(month_amnt);
                    monthTxt.setText(amntTxt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return inflate;
    }


    private void init(View view) {
        todayTxt =view.findViewById(R.id.today_text);
        monthTxt = view.findViewById(R.id.month_text);
        today = view.findViewById(R.id.txt_today);
        month = view.findViewById(R.id.txt_month);
        credit = view.findViewById(R.id.txt_credit);
        invoice = view.findViewById(R.id.txt_invoice);
        invoiceReport = view.findViewById(R.id.invoice_report);

        today.setOnClickListener(this);
        month.setOnClickListener(this);
        credit.setOnClickListener(this);
        invoice.setOnClickListener(this);
        invoiceReport.setOnClickListener(this);
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
                if (checkPermission()) {
                    progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading", true);
                    populateList();
                }
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.txt_invoice:
                if (checkPermission()) {
                    progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading", true);
                    populateCreditList();
                }
//                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;

            case R.id.invoice_report:
                if (checkPermission()) {
                    selectedItem = 0;
                    selectMethod();
                }
                break;
        }
    }

    private boolean checkPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
            return false;
        }

        return true;
    }

    private void selectMethod() {

        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.invoice_report_dialog, null);

        final MaterialSpinner spinner = dialogView.findViewById(R.id.invoice_spinner);
        final Button button = dialogView.findViewById(R.id.nxt_btn);

        String[] spinnerItems = {"Date", "Month", "Date Range"};
        spinner.setItems(spinnerItems);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedItem = position;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBuilder.dismiss();
                reportMethod(selectedItem);


            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

    }

    private void reportMethod(final int method) {

        final ArrayList<String> datesOfInvoice = new ArrayList<>();
        invoiceDialogBuilder = new AlertDialog.Builder(getContext()).create();
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.invoice_report_method, null);

        LinearLayout date, month, range;
        final EditText dateTxt, fromTxt, toTxt;

        date = dialogView.findViewById(R.id.date_layout);
        month = dialogView.findViewById(R.id.month_layout);
        range = dialogView.findViewById(R.id.date_range_layout);

        dateTxt = dialogView.findViewById(R.id.date);
        fromTxt = dialogView.findViewById(R.id.from_date);
        toTxt = dialogView.findViewById(R.id.to_date);

        dateTxt.setFocusable(false);
        fromTxt.setFocusable(false);
        toTxt.setFocusable(false);


        switch (method) {

            case 0:
                date.setVisibility(View.VISIBLE);
                month.setVisibility(View.GONE);
                range.setVisibility(View.GONE);


                dateTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        c.add(Calendar.DATE, -1);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                        dateTxt.setText(df.format(date));
                                    }
                                }, mYear, mMonth, mDay);

                        datePickerDialog.show();
                    }
                });

                break;

            case 1:
                date.setVisibility(View.GONE);
                month.setVisibility(View.VISIBLE);
                range.setVisibility(View.GONE);

                MaterialSpinner spinner = dialogView.findViewById(R.id.spinner);
                String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                monthName = "January";
                selectedMonth = 0;
                spinner.setItems(months);

                spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        selectedMonth = position + 1;
                        monthName = item;
                        Log.i("month", item);
                    }
                });
                break;

            case 2:
                date.setVisibility(View.GONE);
                month.setVisibility(View.GONE);
                range.setVisibility(View.VISIBLE);


                fromTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        c.add(Calendar.DATE, -1);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                        fromTxt.setText(df.format(date));
                                    }
                                }, mYear, mMonth, mDay);

                        datePickerDialog.show();
                    }
                });

                toTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);

                        c.add(Calendar.DATE, -1);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                new DatePickerDialog.OnDateSetListener() {

                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                        toTxt.setText(df.format(date));
                                    }
                                }, mYear, mMonth, mDay);

                        datePickerDialog.show();
                    }
                });


                invoiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            datesOfInvoice.add(child.getKey());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;
        }
        Button okBtn = dialogView.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (method == 0) {
//                    EditText dateTxt = dialogView.findViewById(R.id.date);
                    String selDate;
                    selDate = dateTxt.getText().toString();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    Date myDate;
                    try {

                        myDate = df.parse(selDate);

                        SimpleDateFormat dbformat = new SimpleDateFormat("yyyy_MM_dd");
                        final String dbDate = dbformat.format(myDate);

                        invoiceRef.child(dbDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Toast.makeText(getContext(), "No Invoice found on that date", Toast.LENGTH_SHORT).show();
                                } else {
                                    invoiceDialogBuilder.dismiss();
                                    getDateData(dbDate);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

//                        invoiceDialogBuilder.dismiss();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (method == 1) {

                    SimpleDateFormat df = new SimpleDateFormat("MM");
                    Date dbDate = null;

                    try {
                        dbDate = df.parse(String.valueOf(selectedMonth));
                        String selMonth = df.format(dbDate);
                        Log.i("month_selected", selMonth);
                        getMonthData(selMonth, monthName);
//                        invoiceDialogBuilder.dismiss();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } else if (method == 2) {


                    String fromDate, toDate;
                    fromDate = fromTxt.getText().toString();
                    toDate = toTxt.getText().toString();

                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat dbformat = new SimpleDateFormat("yyyy_MM_dd");
                    Date myFromDate, myToDate;


                    try {

                        myFromDate = df.parse(fromDate);
                        myToDate = df.parse(toDate);
                        fromDate = dbformat.format(myFromDate);
                        toDate = dbformat.format(myToDate);

                        ArrayList<String> newDatesList = new ArrayList<>();
                        newDatesList = getDateRange(datesOfInvoice, fromDate, toDate);
                        if (newDatesList.isEmpty()) {
                            Toast.makeText(getContext(), "No Dates Found between the given range", Toast.LENGTH_SHORT).show();
                        } else
                            getRangedDate(newDatesList, fromDate, toDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        invoiceDialogBuilder.setView(dialogView);
        invoiceDialogBuilder.show();

        Button cancelBtn = dialogView.findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invoiceDialogBuilder.dismiss();
            }
        });
    }


    private void getRangedDate(ArrayList<String> newDatesList, final String fromDate, final String toDate) {

        progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading", true);
        final ArrayList<String> c_nameList, inv_dateList, inv_noList, amountList, gst_inList;

        c_nameList = new ArrayList<>();
        inv_dateList = new ArrayList<>();
        inv_noList = new ArrayList<>();
        amountList = new ArrayList<>();
        gst_inList = new ArrayList<>();

        for (final String dbDate : newDatesList) {

            DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
            rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot child : dataSnapshot.child("Invoice").child(dbDate).getChildren()) {

                        String inv_no = child.getKey();
                        DataSnapshot details_child = child.child("Details");
                        String clientId_ = details_child.child("client_id").getValue().toString();
                        String total = details_child.child("amount").getValue().toString();


                        DataSnapshot client_child = dataSnapshot.child("Client").child(clientId_);
                        String client_name = client_child.child("name").getValue().toString();
                        String gstin = client_child.child("gst").getValue().toString();


                        c_nameList.add(client_name);
                        inv_dateList.add(dbDate);
                        inv_noList.add(inv_no);
                        amountList.add(total);
                        gst_inList.add(gstin);
                    }

                    invoiceDialogBuilder.dismiss();
                    progressDialog.dismiss();
                    invoiceExcel(gst_inList, c_nameList, inv_noList, inv_dateList, amountList, "Invoice_between" + fromDate + "_to_" + toDate);

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private ArrayList<String> getDateRange(ArrayList<String> datesOfInvoice, String fromDate, String toDate) throws ParseException {

        ArrayList<String> newList = new ArrayList<>();
        Date d1 = null, d2 = null, d3 = null, d4 = null;
        SimpleDateFormat dbformat = new SimpleDateFormat("yyyy_MM_dd");

        d1 = dbformat.parse(fromDate);
        d2 = dbformat.parse(toDate);


        for (int i = 0; i < datesOfInvoice.size(); i++) {
            String b = datesOfInvoice.get(i);
            d3 = dbformat.parse(b);
            if (d3.compareTo(d1) >= 0 && d3.compareTo(d2) <= 0) {
                Log.i("newDates", datesOfInvoice.get(i));
                newList.add(datesOfInvoice.get(i));
            }

        }

        return newList;
    }


    private void getMonthData(final String selMonth, final String month_name) {

        progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading", true);
        final ArrayList<String> c_nameList, inv_dateList, inv_noList, amountList, gst_inList;

        c_nameList = new ArrayList<>();
        inv_dateList = new ArrayList<>();
        inv_noList = new ArrayList<>();
        amountList = new ArrayList<>();
        gst_inList = new ArrayList<>();

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.child("Invoice").getChildren()) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy_MM_dd");
                    SimpleDateFormat df = new SimpleDateFormat("MM");
                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
                    String thisYear = yearFormat.format(calendar.getTime());

                    Date dbMonth = null;
                    try {
                        dbMonth = dbFormat.parse(String.valueOf(child.getKey()));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String dbDate = dbFormat.format(dbMonth);
                    String retrievedMonth = df.format(dbMonth);
                    String retrievedYear = yearFormat.format(dbMonth);
                    if (retrievedMonth.equals(selMonth) && retrievedYear.equals(thisYear)) {

                        for (DataSnapshot invoice_child : child.getChildren()) {

                            String inv_no = invoice_child.getKey();

                            DataSnapshot details_child = invoice_child.child("Details");
                            String clientId_ = details_child.child("client_id").getValue().toString();
                            String total = details_child.child("amount").getValue().toString();


                            DataSnapshot client_child = dataSnapshot.child("Client").child(clientId_);
                            String client_name = client_child.child("name").getValue().toString();
                            String gstin = client_child.child("gst").getValue().toString();


                            c_nameList.add(client_name);
                            inv_dateList.add(dbDate);
                            inv_noList.add(inv_no);
                            amountList.add(total);
                            gst_inList.add(gstin);
                        }
                    }
                }

                if (c_nameList.isEmpty()) {
                    Toast.makeText(getContext(), "No Invoiced Found On that month", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    invoiceDialogBuilder.dismiss();
                    progressDialog.dismiss();


                    invoiceExcel(gst_inList, c_nameList, inv_noList, inv_dateList, amountList, "Invoice for " + month_name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getDateData(final String dbDate) {

        progressDialog = ProgressDialog.show(getContext(), "Please wait", "Loading", true);
        final ArrayList<String> c_nameList, inv_dateList, inv_noList, amountList, gst_inList;

        c_nameList = new ArrayList<>();
        inv_dateList = new ArrayList<>();
        inv_noList = new ArrayList<>();
        amountList = new ArrayList<>();
        gst_inList = new ArrayList<>();

        DatabaseReference rootref = FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.child("Invoice").child(dbDate).getChildren()) {

                    String inv_no = child.getKey();
                    DataSnapshot details_child = child.child("Details");
                    String clientId_ = details_child.child("client_id").getValue().toString();
                    String total = details_child.child("amount").getValue().toString();


                    DataSnapshot client_child = dataSnapshot.child("Client").child(clientId_);
                    String client_name = client_child.child("name").getValue().toString();
                    String gstin = client_child.child("gst").getValue().toString();


                    c_nameList.add(client_name);
                    inv_dateList.add(dbDate);
                    inv_noList.add(inv_no);
                    amountList.add(total);
                    gst_inList.add(gstin);
                }

                progressDialog.dismiss();
                invoiceExcel(gst_inList, c_nameList, inv_noList, inv_dateList, amountList, "Invoice for " + dbDate);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean invoiceExcel(ArrayList<String> gst_inList, ArrayList<String> c_nameList, ArrayList<String> inv_noList, ArrayList<String> inv_dateList, ArrayList<String> amountList, String filename) {

        progressDialog = ProgressDialog.show(getContext(), "Please wait", "Saving", true);
        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //New Sheet
        final Sheet sheet1 = wb.createSheet("Invoice");

        // Generate column headings
        final Row row = sheet1.createRow(0);


        c = row.createCell(0);
        c.setCellValue("GSTIN");
//        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Name");
//        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Inv No");

        c = row.createCell(3);
        c.setCellValue("Date");

        c = row.createCell(4);
        c.setCellValue("Basic Value");

        c = row.createCell(5);
        c.setCellValue("Tax");

        c = row.createCell(6);
        c.setCellValue("IGST");

        c = row.createCell(7);
        c.setCellValue("CGST");

        c = row.createCell(8);
        c.setCellValue("SGST");

        c = row.createCell(9);
        c.setCellValue("Cess");

        c = row.createCell(10);
        c.setCellValue("Total");


        for (int i = 1; i <= c_nameList.size(); i++) {

            Row row1 = sheet1.createRow(i);

            Cell cell = null;

            cell = row1.createCell(0);
            cell.setCellValue(gst_inList.get(i - 1));

            cell = row1.createCell(1);
            cell.setCellValue(c_nameList.get(i - 1));

            cell = row1.createCell(2);
            cell.setCellValue(inv_noList.get(i - 1));

            cell = row1.createCell(3);
            cell.setCellValue(inv_dateList.get(i - 1));

            cell = row1.createCell(4);
            cell.setCellValue(amountList.get(i - 1));

            cell = row1.createCell(5);
            cell.setCellValue(amountList.get(i - 1));

            cell = row1.createCell(6);
            cell.setCellValue(amountList.get(i - 1));

            cell = row1.createCell(7);
            cell.setCellValue(amountList.get(i - 1));

            cell = row1.createCell(8);
            cell.setCellValue(amountList.get(i - 1));

            cell = row1.createCell(9);
            cell.setCellValue(amountList.get(i - 1));

            cell = row1.createCell(10);
            cell.setCellValue(amountList.get(i - 1));

        }


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
//        String filename = dateFormat.format(calendar.getTime()) + "_Invoice_by_" + method + ".xls";
        // Create a path where we will place our List of objects on external storage
        File file = new File(Environment.getExternalStorageDirectory() + "/Santha Agencies", filename + ".xls");

        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();

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

                if (list.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Credit Found", Toast.LENGTH_SHORT).show();
                } else
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
        final Sheet sheet1 = wb.createSheet("Credit");


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
            Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
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

                for (DataSnapshot child : dataSnapshot.child("Order").getChildren()) {

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


                                DataSnapshot client_child = dataSnapshot.child("Client").child(clientId);

                                String gstin = client_child.child("gst").getValue().toString();

                                String name = client_child.child("name").getValue().toString();
                                Log.i("clientid", name);
                                gstinlist.add(gstin);
                                namelist.add(name);
                                datelist.add(fdate);
                                basicvallist.add(basicval);
                                taxlist.add(tax);
                                cesslist.add(cess);

                            }
                        }
                    }
                }

                if (namelist.isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
                } else
                    saveExcelFile();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }

        });


    }

    private boolean saveExcelFile() {

        if (namelist.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "No Orders Found", Toast.LENGTH_SHORT).show();
            return false;
        }


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
            Toast.makeText(getContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
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