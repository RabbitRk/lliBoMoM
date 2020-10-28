package com.rabbitt.momobill.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.rabbitt.momobill.demo.Convertor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_CITY;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_GST;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC_2;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PHONE;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PIN;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PREF;

public class Invoice {
    private static final String TAG = "maluPDF";

    public void pdfcreate(File file, Uri path, Uri stamp, Uri logopath, Context context, List<ProductInvoice> data, String invoice, Client client, String date_) {

        Log.i(TAG, "pdfcreate: " + path);
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A5.rotate(), 16, 16, 16, 16);
        String outPath = file.getPath();

        try {
            SharedPreferences preference = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

            String add1 = preference.getString(USER_LOC, "Address Line 1");
            String add2 = preference.getString(USER_LOC_2, "Address Line 2");
            String city = preference.getString(USER_CITY, "City");
            String phone = preference.getString(USER_PHONE, "phone");
            String pin = preference.getString(USER_PIN, "Pin code");
            String gstin = preference.getString(USER_GST, "Gstin");

            PdfWriter.getInstance(doc, new FileOutputStream(outPath));
            doc.open();
            doc.setMargins(0, 0, 0, 0);           // setting margin
            PdfPTable innertable = new PdfPTable(3);  //first table
            innertable.setWidthPercentage(100);

            //  innertable.addCell(cell);
            PdfPTable innertable2 = new PdfPTable(3);
            innertable2.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("SANTHA AGENCIES"));////////////////////////////////////////////////////////////            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Original / Duplicate \n TAX INVOICE"));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Consignee name / Address"));
            innertable2.addCell(cell1);



            cell1 = new PdfPCell(new Phrase("Address :" +add1+"\n"+add2+ "\n" + city + "-" + pin));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Invoice No: "+invoice));////////////////////////////////////////////////////////////////
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Name :" + client.getName()));
            innertable2.addCell(cell1);


            cell1 = new PdfPCell(new Phrase("Place of supply: "));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Date: "+date_));////////////////////////////////////////////////////////////////
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Address :" + client.getAdd1() + "\n" + client.getAdd2() + "\n"+ client.getState() + "\n" + client.getPincode()));
            innertable2.addCell(cell1);



            cell1 = new PdfPCell(new Phrase("PH: " + phone));///////////////////////////////////////////////////////////////////////
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Route: "+date_));////////////////////////////////////////////////////////////////
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Phone :" + client.getPhone()));
            innertable2.addCell(cell1);



            cell1 = new PdfPCell(new Phrase("Address : \t" + "user_add"));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Term: Credit/Cash"));////////////////////////////////////////////////////////////////
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("GSTIN :" + client.getGst()));
            innertable2.addCell(cell1);


            cell1 = new PdfPCell();
            cell1.setColspan(5);
            cell1.setFixedHeight(6);
            cell1.setBorder(Rectangle.NO_BORDER);
            innertable2.addCell(cell1);
            cell1 = new PdfPCell();
            cell1.setColspan(5);
            cell1.setFixedHeight(6);
            cell1.setBorder(Rectangle.NO_BORDER);
            innertable2.addCell(cell1);
            cell1 = new PdfPCell();
            cell1.setColspan(5);
            cell1.setFixedHeight(6);
            cell1.setBorder(Rectangle.NO_BORDER);
            innertable2.addCell(cell1);

            doc.add(innertable2);


            PdfPTable innertable5 = new PdfPTable(9);
            innertable5.setWidthPercentage(100);

            PdfPCell cell5 = new PdfPCell(new Phrase("Product"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("HSN"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("M.R.P"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("Units"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("Taxable"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("SGST"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("CGST"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("CESS"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("Total"));
            innertable5.addCell(cell5);


            double amtbefore = 0.0, tax_inc = 0.0;
            double subtotalcess = 0.0, subtotalcgst = 0.0, subtot = 0.0, cess_temp = 0.0, cgst_temp = 0.0;

            double printcess = 0.0, printcgst = 0.0;//,  cess_temp = 0.0, cgst_temp = 0.0;


            for (int i = 0; i < data.size(); i++) {
                printcess = 0.0;
                printcgst = 0.0;

                ProductInvoice productInvoice = data.get(i);

                cell5 = new PdfPCell(new Phrase(productInvoice.getProduct_name()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getHsn()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getUnit()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getUnit()));
                innertable5.addCell(cell5);

                String pre_amount = "";
                double data_ = Double.parseDouble(productInvoice.getSale_rate());
                double tax = Double.parseDouble(productInvoice.getCgst());
                double cess = Double.parseDouble(productInvoice.getCess());

                Log.i(TAG, "Raw: " + data_ + "    " + tax + "    " + cess);

                if (productInvoice.getIn().equals("exc")) {


                    cgst_temp = roundDecimals(data_ * (tax / 100));
                    cess_temp = roundDecimals(data_ * (cess / 100));
                    printcgst = cgst_temp / 2;
                    printcess = cess_temp;
                    subtotalcgst += cgst_temp;
                    subtotalcess += cess_temp;

//
//                    cess_temp = data_ * (100 / (cess + 100));
//                    subtotalcess += roundDecimals(cess_temp * (cess / 100));
//
                    Log.i(TAG, "pdfcreate: cgst: " + subtotalcgst + "  " + " cess: " + subtotalcess);
//
//                    tax += cess + 100;
//
                    double amount = Double.parseDouble(productInvoice.getSale_rate()) + cess_temp + cgst_temp;
//
//                    Log.i(TAG, "pdfcreate: Amount " + roundDecimals(amount));

                    pre_amount = String.valueOf(roundDecimals(amount));
                    subtot = subtot + Double.parseDouble(productInvoice.getSale_rate());

//                    subtot = subtot + roundDecimals(amount);
//                    printcess = subtotalcgst;
//                    printcgst = subtotalcess;
                } else {

                    subtotalcess += 0;
                    subtotalcgst += 0;
                    Log.i(TAG, "pdfcreate Exc: cgst: " + subtotalcgst + "  " + " cess: " + subtotalcess);

                    pre_amount = productInvoice.getSale_rate();
                    subtot = subtot + Double.parseDouble(productInvoice.getSale_rate());
                    printcess = 0.0;
                    printcgst = 0.0;
                }


                PdfPCell activitynestedcell = new PdfPCell(new Phrase("Nested Cell 1"));
                activitynestedcell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                activitynested.addCell(activitynestedcell);
//                activity.addElement(activitynested);

                cell5 = new PdfPCell(new Phrase(productInvoice.getSale_rate()));
                innertable5.addCell(cell5);


                cell5 = new PdfPCell(new Phrase(String.valueOf(printcgst)));
                innertable5.addCell(cell5);//.addElement(activitynestedcell);



                cell5 = new PdfPCell(new Phrase(String.valueOf(printcgst)));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(String.valueOf(printcess)));
                innertable5.addCell(cell5);

//                amtbefore = amtbefore + Double.parseDouble(productInvoice.getSale_rate());
//                double singleamount = calculate(productInvoice.getIn(), Double.parseDouble(productInvoice.getCgst()), Double.parseDouble(productInvoice.getCess()), Double.parseDouble(productInvoice.getSale_rate()));
                tax_inc = tax_inc + Double.parseDouble(pre_amount);

                cell5 = new PdfPCell(new Phrase("s" + pre_amount));

                cell5.setMinimumHeight(10f);
                innertable5.addCell(cell5);
            }
            doc.add(innertable5);

//            PdfPTable t = new PdfPTable(9);
//            t.setWidthPercentage(100);
//            t.setWidths(new int[]{50, 50});
//            PdfPCell ce = new PdfPCell(new Phrase("Total"));
//            t.addCell(ce);
//            ce = new PdfPCell(new Phrase("" + tax_inc));
//            t.addCell(ce);
//            doc.add(t);

            Convertor convert_ = new Convertor();

            // rounding the double
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(0);
            String rounded = nf.format(tax_inc);
            if (rounded.contains(",")) {
                rounded = rounded.replace(",", "");
            }
            Log.i(TAG, "pdfcreate: " + rounded);

            //next step
            PdfPTable innertable6 = new PdfPTable(1);
            innertable6.setWidthPercentage(100);
            PdfPCell cell6 = new PdfPCell(new Phrase("Total Amount Paid (In Words:)"));
            cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
            innertable6.addCell(cell6);
            cell6.setMinimumHeight(50f);
            cell6 = new PdfPCell(new Phrase("" + Convertor.convert(Integer.parseInt(rounded)) + " Rupees Only"));
            cell6.setPaddingLeft(20);
            innertable6.addCell(cell6);
            cell6.setMinimumHeight(10f);
            cell6 = new PdfPCell();
            cell6.setColspan(5);
            cell6.setFixedHeight(6);
            cell6.setBorder(Rectangle.NO_BORDER);
            innertable6.addCell(cell6);
            doc.add(innertable6);

            PdfPTable innertable7 = new PdfPTable(3);
            innertable7.setWidthPercentage(100);

            PdfPTable nested5 = new PdfPTable(5);

            nested5.addCell("%");
            nested5.addCell("Taxable");
            nested5.addCell("CGST");
            nested5.addCell("SGST");
            nested5.addCell("Total");

            List<ProductInvoice> tax = new ArrayList<>();
            List<ProductInvoice> inc = new ArrayList<>();

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
                nested5.addCell("0%");
                nested5.addCell(String.valueOf(noTax));
                nested5.addCell("0.0");
                nested5.addCell("0.0");
                nested5.addCell(String.valueOf(noTax));
            }


            for (int i = 0; i < val.size(); i++) {
                double taxable = Double.parseDouble(val.get(i).getTaxable());
                double per = Double.parseDouble(val.get(i).getPercentage());
                double cgst = taxable * (per / 100.0);
                nested5.addCell(String.valueOf(per));
                nested5.addCell(String.valueOf(taxable));
                nested5.addCell(String.valueOf(cgst / 2));
                nested5.addCell(String.valueOf(cgst / 2));
                nested5.addCell(String.valueOf(cgst));
            }

            PdfPCell nesthousing5 = new PdfPCell(nested5);

            innertable7.addCell(nesthousing5);


            PdfPTable nested = new PdfPTable(1);
            nested.addCell("Taxable Amount0");
            nested.addCell("CGST");
            nested.addCell("SGST");
            nested.addCell("CESS");
            nested.addCell("TOTAL");
            PdfPCell nesthousing = new PdfPCell(nested);

            innertable7.addCell(nesthousing);
            PdfPTable nested2 = new PdfPTable(1);

            nested2.addCell("" + subtot);
            nested2.addCell("" + subtotalcgst/2);
            nested2.addCell("" + subtotalcgst/2);
            nested2.addCell("" + subtotalcess);

            nested2.addCell("" + rounded);
            PdfPCell nesthousing2 = new PdfPCell(nested2);
            innertable7.addCell(nesthousing2);

            doc.add(innertable7);
            doc.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
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
        double taxval = ces + (gst * 2);
        sale_r = (sale_r * (taxval / 100)) + /*Actual rate*/sale_r; //Adding gst + actual rate
        return roundDecimals(sale_r);
    }

    double roundDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        return Double.parseDouble(twoDForm.format(d));
    }

    String round(double val) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(0);
        return nf.format(val);
    }


}