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
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A5.rotate(), 0f, 0f, 0f, 0f);
        String outPath = file.getPath();

        try {
            SharedPreferences preference = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

            String add1 = preference.getString(USER_LOC, "Address Line 1");
            String add2 = preference.getString(USER_LOC_2, "Address Line 2");
            String city = preference.getString(USER_CITY, "City");
            String phone = preference.getString(USER_PHONE, "phone");
            String pin = preference.getString(USER_PIN, "Pin code");

            String gstin = preference.getString(USER_GST, "gstin");

            PdfWriter.getInstance(doc, new FileOutputStream(outPath));
            doc.open();
            doc.setMargins(0, 0, 0, 0);           // setting margin
            PdfPTable innertable = new PdfPTable(3);  //first table
            innertable.setWidthPercentage(100);

//            Image image = null, image2 = null, image3 = null;
//            if (path != null) {
//                Bitmap bmp = BitmapFactory.decodeFile(path.toString());
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 10, stream);
//                image = Image.getInstance(stream.toByteArray());
//                image.scaleToFit(100, 100);
//            }
//            if (stamp != null) {
//                Bitmap bmp = BitmapFactory.decodeFile(stamp.toString());
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 10, stream);
//                image2 = Image.getInstance(stream.toByteArray());
//                image2.scaleToFit(100, 100);
//            }
//            File logo = null;
//            if (logopath != null)
//                logo = new File(logopath.toString());
//            if (logopath != null && logo.exists()) {
//                Bitmap bmp = BitmapFactory.decodeFile(logopath.toString());
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.PNG, 10, stream);
//                image3 = Image.getInstance(stream.toByteArray());
//                image3.scaleToFit(50, 50);
//            }
            //innertable.setWidths(new int[]{40});
            //Topic
//            PdfPCell cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell(new Paragraph("" + "Santha Agency", FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//
//            cell = new PdfPCell(new Phrase(""));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell(new Paragraph("" + shrp.getString(USER_LOC, "") + " " + shrp.getString(USER_LOC_2, ""), FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//
//            cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell(new Paragraph("Phone :" + shrp.getString(USER_PHONE, ""), FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//
//            cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell(new Paragraph("GSTIN " + shrp.getString(USER_GST, ""), FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
//            //cell.setPaddingLeft(2);
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//            cell = new PdfPCell();
//            cell.setBorder(Rectangle.NO_BORDER);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            innertable.addCell(cell);
//
//            cell = new PdfPCell();
//            cell.setColspan(5);
//            cell.setFixedHeight(6);
//            cell.setBorder(Rectangle.NO_BORDER);
//            innertable.addCell(cell);
//            cell = new PdfPCell();
//            cell.setColspan(5);
//            cell.setFixedHeight(6);
//            cell.setBorder(Rectangle.NO_BORDER);
//            innertable.addCell(cell);
//            cell = new PdfPCell();
//            cell.setColspan(5);
//            cell.setFixedHeight(6);
//            cell.setBorder(Rectangle.NO_BORDER);
//            innertable.addCell(cell);
//            doc.add(innertable);
            //Topic End
//Second table

//            PdfPTable inner = new PdfPTable(1);
//            inner.setWidthPercentage(100);
//            PdfPCell cel = new PdfPCell(new Phrase("PAYMENT VOUCHER",
//                    FontFactory.getFont(FontFactory.COURIER_BOLD, 25, Font.NORMAL, BaseColor.BLACK)));
//
//            cel.setBackgroundColor(BaseColor.LIGHT_GRAY);
//            cel.setHorizontalAlignment(Element.ALIGN_CENTER);
//            inner.addCell(cel);
//            cel = new PdfPCell();
//            cel.setColspan(5);
//            cel.setFixedHeight(6);
//            cel.setBorder(Rectangle.NO_BORDER);
//            inner.addCell(cel);
//
//            doc.add(inner);

            //  innertable.addCell(cell);
            PdfPTable innertable2 = new PdfPTable(3);
            innertable2.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("Voucher no. :" + invoice));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("SANTHA AGENCIES"));/////////////////////////////////////////////////////////////////////////
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Details of Supplier"));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Voucher Date: " + date_));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase(add1));/////////////////////////////////////////////////////////////////////////
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Name: " + client.getName()));

            innertable2.addCell(cell1);
            cell1 = new PdfPCell(new Phrase("Place of supply: "));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase(add2+"\n"+city+"-"+pin));/////////////////////////////////////////////////////////////////////////
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);


            cell1 = new PdfPCell(new Phrase("Address :" + client.getAdd1() + " " + client.getAdd2()));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase(""));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("PH: "+phone));/////////////////////////////////////////////////////////////////////////
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);


            cell1 = new PdfPCell(new Phrase("GSTIN :" + client.getGst()));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Address : \t" + "user_add"));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("GSTIN : "+gstin));/////////////////////////////////////////////////////////////////////////
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("State: " + client.getState() + "    Code: " + client.getPincode()));
            //cell.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

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


            PdfPTable innertable5 = new PdfPTable(8);
            innertable5.setWidthPercentage(100);

            PdfPCell cell5 = new PdfPCell(new Phrase("Product Description"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("HSN Code"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("Units"));
            innertable5.addCell(cell5);

            cell5 = new PdfPCell(new Phrase("Taxable Value"));
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

            for (int i = 0; i < data.size(); i++) {
                ProductInvoice productInvoice = data.get(i);

                cell5 = new PdfPCell(new Phrase(productInvoice.getProduct_name()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getHsn()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getUnit()));
                innertable5.addCell(cell5);

                String pre_amount = "";
                double data_ = Double.parseDouble(productInvoice.getSale_rate());
                double tax = Double.parseDouble(productInvoice.getCgst()) * 2;
                double cess = Double.parseDouble(productInvoice.getCess());

                if (productInvoice.getIn().equals("inc")) {

                    cgst_temp = roundDecimals(data_ * (100 / (tax + 100)));
                    cgst_temp = roundDecimals(cgst_temp * (tax / 100));
                    subtotalcgst += cgst_temp / 2;

                    cess_temp = data_ * (100 / (cess + 100));
                    subtotalcess += roundDecimals(cess_temp * (cess / 100));

                    Log.i(TAG, "pdfcreate: cgst: " + subtotalcgst + "  " + " cess: " + subtotalcess);

                    tax += cess + 100;

                    double amount = data_ * (100 / tax);

                    Log.i(TAG, "pdfcreate: Amount " + roundDecimals(amount));

                    pre_amount = String.valueOf(roundDecimals(amount));
                    subtot = subtot + roundDecimals(amount);

                } else {

                    subtotalcess += data_ * (cess / 100);
                    subtotalcgst += (data_ * (tax / 100)) / 2;
                    Log.i(TAG, "pdfcreate Exc: cgst: " + subtotalcgst + "  " + " cess: " + subtotalcess);

                    pre_amount = productInvoice.getSale_rate();
                    subtot = subtot + Double.parseDouble(productInvoice.getSale_rate());
                }

                cell5 = new PdfPCell(new Phrase(pre_amount));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getCgst()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getCgst()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getCess()));
                innertable5.addCell(cell5);

                amtbefore = amtbefore + Double.parseDouble(productInvoice.getSale_rate());
                double singleamount = calculate(productInvoice.getIn(), Double.parseDouble(productInvoice.getCgst()), Double.parseDouble(productInvoice.getCess()), Double.parseDouble(productInvoice.getSale_rate()));
                tax_inc = tax_inc + singleamount;

                cell5 = new PdfPCell(new Phrase("" + singleamount));

                cell5.setMinimumHeight(10f);
                innertable5.addCell(cell5);
            }
            doc.add(innertable5);

            PdfPTable t = new PdfPTable(2);
            t.setWidthPercentage(100);
            t.setWidths(new int[]{50, 50});
            PdfPCell ce = new PdfPCell(new Phrase("Total"));
            t.addCell(ce);
            ce = new PdfPCell(new Phrase("" + tax_inc));
            t.addCell(ce);
            doc.add(t);

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
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
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

            PdfPTable nested5 = new PdfPTable(4);

            nested5.addCell("%");
            nested5.addCell("Taxable");
            nested5.addCell("CGST");
            nested5.addCell("SGST");


            List<ProductInvoice> tax = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {  // Filtering the model which the tax are not inclusive
                if (data.get(i).getIn().equals("exc")) {
                    tax.add(data.get(i));
                    Log.i(TAG, "parseData: " + data.get(i).toString());
                }
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


            for(int i=0;i<val.size();i++) {
                double taxable = Double.parseDouble(val.get(i).getTaxable());
                double per = Double.parseDouble(val.get(i).getPercentage());
                double cgst = taxable * ((per / 2.0) / 100.0);
                nested5.addCell(String.valueOf(per));
                nested5.addCell(String.valueOf(taxable));
                nested5.addCell(String.valueOf(cgst));
                nested5.addCell(String.valueOf(cgst));
            }

//
//            nested5.addCell("3");
//            nested5.addCell("34");
//            nested5.addCell("3");
//            nested5.addCell("34");
//
//            nested5.addCell("3");
//            nested5.addCell("35");
//            nested5.addCell("3");
//            nested5.addCell("35");

            PdfPCell nesthousing5 = new PdfPCell(nested5);

            innertable7.addCell(nesthousing5);


            PdfPTable nested = new PdfPTable(1);
            nested.addCell("Total Amount before tax:");
            nested.addCell("Add: CGST");
            nested.addCell("Add: SGST");
            nested.addCell("Add: CESS");
            nested.addCell("Grand Total");
            PdfPCell nesthousing = new PdfPCell(nested);

            innertable7.addCell(nesthousing);
            PdfPTable nested2 = new PdfPTable(1);

//            for (int i = 0; i < data.size(); i++) {
//                ProductInvoice productInvoice = data.get(i);
//
//                String tax_ = productInvoice.getIn();
//                if (tax_.equals("inc")) {
//                    double _sale = Double.parseDouble(productInvoice.getSale_rate());
//                    double _tax = Double.parseDouble(productInvoice.getCgst()) * 2;
//                    double _in_tax = Double.parseDouble(productInvoice.getCgst()) + 100;
//                    double _cess = Double.parseDouble(productInvoice.getCess());
//                    double _in_cess = Double.parseDouble(productInvoice.getCess()) + 100;
//
//                    double _sub = _sale * (100 / _in_tax);
//                    double _sub_cess = _sale * (100 / _in_cess);
//
//                    subtotalcgst += Double.parseDouble(round(_sub * (_tax / 100)));
//                    subtotalcess += Double.parseDouble(round(_sub_cess * (_cess / 100)));
//                    subtot += Double.parseDouble(round(_sub));
//                    Log.i(TAG, "pdfcreate: INC " + "_sale: " + _sale + "_tax: " + _tax + "_in_tax: " + _in_tax + "_cess: " + _cess + "_in_cess: " + _in_cess + "_sub: " + _sub + "_sub_cess: " + _sub_cess);
//
//                } else {
//
//                    double _sale = Double.parseDouble(productInvoice.getSale_rate());
//                    double _tax = Double.parseDouble(productInvoice.getCgst()) * 2;
//                    double _cess = Double.parseDouble(productInvoice.getCess());
//
//                    subtotalcgst += Double.parseDouble(round(_sale * (_tax / 100)));
//                    subtotalcess += Double.parseDouble(round(_sale * (_cess / 100)));
//
//                    subtot += Double.parseDouble(round(_sale));
//
//                    Log.i(TAG, "pdfcreate: EXC  " + subtot);
//
//                }
//            }
            nested2.addCell("" + subtot);

            nested2.addCell("" + subtotalcgst);
            nested2.addCell("" + subtotalcgst);
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