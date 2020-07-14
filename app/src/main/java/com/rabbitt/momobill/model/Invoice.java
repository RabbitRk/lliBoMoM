package com.rabbitt.momobill.model;

import android.annotation.SuppressLint;
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
import com.rabbitt.momobill.R;
import com.rabbitt.momobill.demo.Convertor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_GST;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_LOC_2;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PHONE;
import static com.rabbitt.momobill.prefsManager.PrefsManager.USER_PREF;

public class Invoice {
    private static final String TAG = "maluPDF";

    public void pdfcreate(File file, Uri path, Uri stamp, Uri logopath, Context context, List<ProductInvoice> data, String invoice, Client client, String date_) {

        Log.i(TAG, "pdfcreate: " + path);
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document(PageSize.A4, 0f, 0f, 0f, 0f);
        String outPath = file.getPath();

        try {
            SharedPreferences shrp = context.getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);

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

            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell(new Paragraph("" + "Santha Agency", FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);

// column 3

//            if (logopath != null && logo.exists()) {
//                cell = new PdfPCell(image3);
//            } else {
            cell = new PdfPCell(new Phrase(""));
//            }
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell(new Paragraph("" + shrp.getString(USER_LOC, "") + " " + shrp.getString(USER_LOC_2, ""), FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);

// column 4

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell(new Paragraph("Phone :" + shrp.getString(USER_PHONE, ""), FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
            //cell.setPaddingLeft(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);

            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell(new Paragraph("GSTIN " + shrp.getString(USER_GST, ""), FontFactory.getFont(FontFactory.TIMES_BOLD, 17, Font.NORMAL, BaseColor.BLACK)));
            //cell.setPaddingLeft(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
            cell = new PdfPCell();
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable.addCell(cell);
// spacing
            cell = new PdfPCell();
            cell.setColspan(5);
            cell.setFixedHeight(6);
            cell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(cell);
            cell = new PdfPCell();
            cell.setColspan(5);
            cell.setFixedHeight(6);
            cell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(cell);
            cell = new PdfPCell();
            cell.setColspan(5);
            cell.setFixedHeight(6);
            cell.setBorder(Rectangle.NO_BORDER);
            innertable.addCell(cell);
            doc.add(innertable);

//Second table

            PdfPTable inner = new PdfPTable(1);
            inner.setWidthPercentage(100);
            PdfPCell cel = new PdfPCell(new Phrase("PAYMENT VOUCHER",
                    FontFactory.getFont(FontFactory.COURIER_BOLD, 25, Font.NORMAL, BaseColor.BLACK)));

            cel.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cel.setHorizontalAlignment(Element.ALIGN_CENTER);
            inner.addCell(cel);
            cel = new PdfPCell();
            cel.setColspan(5);
            cel.setFixedHeight(6);
            cel.setBorder(Rectangle.NO_BORDER);
            inner.addCell(cel);

            doc.add(inner);

            //  innertable.addCell(cell);
            PdfPTable innertable2 = new PdfPTable(2);
            innertable2.setWidthPercentage(100);
            innertable2.setWidths(new int[]{50, 50});
            PdfPCell cell1 = new PdfPCell(new Phrase("Voucher no. :" + invoice));
            //cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Details of Supplier"));
            //cell.setBorder(Rectangle.NO_BORDER);
            cell1.setPaddingLeft(20);
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Voucher Date: " + date_));

            innertable2.addCell(cell1);


            cell1 = new PdfPCell(new Phrase("Name: " + client.getName()));
            //cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingLeft(2);

            innertable2.addCell(cell1);
            cell1 = new PdfPCell(new Phrase("Place of supply: "));
            //cell1.setBorder(Rectangle.NO_BORDER);
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase("Address :" + client.getAdd1() + " " + client.getAdd2()));
            innertable2.addCell(cell1);

            cell1 = new PdfPCell(new Phrase(""));
            innertable2.addCell(cell1);


            cell1 = new PdfPCell(new Phrase("GSTIN :" + client.getGst()));
            //cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingLeft(2);

            innertable2.addCell(cell1);
            cell1 = new PdfPCell(new Phrase("Address : \t" + "user_add"));

            cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
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

            doc.add(innertable2);


            PdfPTable innertable5 = new PdfPTable(7);
            innertable5.setWidthPercentage(100);
            // innertable5.setWidths(new int[]{11,4,7,5,5,5});

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

            for (int i = 0; i < data.size(); i++) {
                ProductInvoice productInvoice = data.get(i);
//                String gsco[] = GST.get(i);

                cell5 = new PdfPCell(new Phrase(productInvoice.getProduct_name()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getHsn()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getUnit()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getSale_rate()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getCgst()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getCgst()));
                innertable5.addCell(cell5);
                cell5 = new PdfPCell(new Phrase(productInvoice.getCess()));
                innertable5.addCell(cell5);


//                PdfPTable nested4 = new PdfPTable(1);
//                nested4.addCell("R: " + productInvoice.getQuantity());
//                nested4.addCell("A: " + "gsco");
//                PdfPCell nesthousing4 = new PdfPCell(nested4);
//                innertable5.addCell(nesthousing4);
//                PdfPTable nested5 = new PdfPTable(1);
//                nested5.addCell("R: " + productInvoice.getProduct_name());
//                nested5.addCell("A: " + "gsco");
//                PdfPCell nesthousing5 = new PdfPCell(nested5);
//                innertable5.addCell(nesthousing5);
//
//                amtbefore = amtbefore + (Double.parseDouble(productInvoice.getSale_rate()) * Double.parseDouble(productInvoice.getUnit()));
                amtbefore = amtbefore + Double.parseDouble(productInvoice.getSale_rate());
                //Total column  productInvoice.getSale_rate()
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
         /*   ce = new PdfPCell(new Phrase("0"));
            t.addCell(ce);
            ce = new PdfPCell(new Phrase("0"));
            t.addCell(ce);
            ce = new PdfPCell(new Phrase("0"));
            t.addCell(ce);*/
            doc.add(t);

            Convertor convert_ = new Convertor();

            // rounding the double
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(0);
            String rounded = nf.format(tax_inc);
            Log.i(TAG, "pdfcreate: "+rounded);

            //next step
            PdfPTable innertable6 = new PdfPTable(1);
            innertable6.setWidthPercentage(100);
            PdfPCell cell6 = new PdfPCell(new Phrase("Total Amount Paid (In Words:)"));
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            innertable6.addCell(cell6);
            cell6.setMinimumHeight(50f);
            cell6 = new PdfPCell(new Phrase("" + Convertor.convert(Integer.parseInt(rounded))));
            cell6.setPaddingLeft(20);
            innertable6.addCell(cell6);
            cell6.setMinimumHeight(10f);
            cell6 = new PdfPCell();
            cell6.setColspan(5);
            cell6.setFixedHeight(6);
            cell6.setBorder(Rectangle.NO_BORDER);
            innertable6.addCell(cell6);
            doc.add(innertable6);

            PdfPTable innertable7 = new PdfPTable(4);
            innertable7.setWidthPercentage(100);
            //innertable6.setWidths(new int[]{20,20,20});
            PdfPTable nested4 = new PdfPTable(1);
//            if (path != null) {
//                PdfPCell cell65 = new PdfPCell(image);
//                cell65.setHorizontalAlignment(Element.ALIGN_CENTER);
//                // cell65.setFixedHeight(150);
//                nested4.addCell(cell65);
//            } else {
            nested4.addCell("");
//            }
            nested4.addCell("Authorised Signatory");
            PdfPCell nesthousing4 = new PdfPCell(nested4);
            innertable7.addCell(nesthousing4);
            PdfPTable nested5 = new PdfPTable(1);
//            if (stamp != null) {
//                PdfPCell cell55 = new PdfPCell(image2);
//                cell55.setHorizontalAlignment(Element.ALIGN_CENTER);
//                //   cell55.setFixedHeight(150);
//                nested5.addCell(cell55);
//
//            } else {
            nested5.addCell("");
//            }
            nested5.addCell("Common Seal");
            PdfPCell nesthousing5 = new PdfPCell(nested5);

            innertable7.addCell(nesthousing5);


            PdfPTable nested = new PdfPTable(1);
            nested.addCell("Total Amount before tax:");
            nested.addCell("Add: CGST");
            nested.addCell("Add: SGST");
            nested.addCell("Total Tax Amount (GST)");
            nested.addCell("Total Amount After Tax");
            PdfPCell nesthousing = new PdfPCell(nested);

            innertable7.addCell(nesthousing);
            PdfPTable nested2 = new PdfPTable(1);

            Double subtotalsgst = 0.0, subtotalcgst = 0.0, subtot = 0.0;
//            for(int i=0;i<items.size();i++)
//            {
//                String item[]=items.get(i);
//                String gs[]=GST.get(i);
//                subtot=subtot+(Double.parseDouble(item[4])*Double.parseDouble(item[5]));
//                subtotalsgst=subtotalsgst+Double.parseDouble(gs[0]);
//                subtotalcgst=subtotalcgst+Double.parseDouble(gs[1]);
//
//            }
            nested2.addCell("" + subtot);
            nested2.addCell("" + subtotalcgst);
            nested2.addCell("" + subtotalsgst);
            nested2.addCell("" + subtotalcgst + subtotalsgst);
            nested2.addCell("" + "total");
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
        double taxval = ces + gst;
        sale_r = (sale_r * (taxval / 100)) + /*Actual rate*/sale_r; //Adding gst + actual rate
        return roundDecimals(sale_r);
    }

    double roundDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        return Double.parseDouble(twoDForm.format(d));
    }


}