package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alberto on 6/4/2016.
 */
public class WishListPDFCreator {

    public File generatePDF(ArrayList<OrderLine> orderLines, String fileName, Context ctx) throws Exception {
        File pdfFile;
        //check if external storage is available so that we can dump our PDF file there
        if (!Utils.isExternalStorageAvailable() || Utils.isExternalStorageReadOnly()) {
            throw new Exception(ctx.getString(R.string.external_storage_unavailable));
        } else {
            //path for the PDF file in the external storage
            pdfFile = new File(ctx.getCacheDir() + File.separator + fileName);
            try {
                pdfFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        //create a new document
        Document document = new Document(PageSize.LETTER, 40, 40, 130, 40);

        if(pdfFile.exists()) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                PdfWriter.getInstance(document, byteArrayOutputStream);
                document.open();

                //se cargan las lineas del pedido
                addDetails(document, orderLines, ctx);

                document.close();

                // Create a reader
                PdfReader reader = new PdfReader(byteArrayOutputStream.toByteArray());
                // Create a stamper
                PdfStamper stamper = new PdfStamper(reader,
                        new FileOutputStream(ctx.getCacheDir() + File.separator + fileName));

                //Se le agrega la cabecera a cada pagina
                addPageHeader(reader, stamper, ctx);

                // Close the stamper
                stamper.close();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pdfFile;
    }

    private void addPageHeader(PdfReader reader, PdfStamper stamper,
                               Context ctx) throws DocumentException, IOException {
        //Loop over the pages and add a header to each page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {
            getHeaderTable(i, n, ctx).writeSelectedRows(0, -1, 60, 780, stamper.getOverContent(i));
        }
    }

    /**
     * Create a header table with page X of Y
     * @param x the page number
     * @param y the total number of pages
     * @return a table that can be used as header
     */
    public static PdfPTable getHeaderTable(int x, int y, Context ctx)
            throws DocumentException, IOException {
        Font docNameFont;
        try{
            docNameFont = new Font(BaseFont.createFont("assets/Roboto-Italic.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED), 15f);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                docNameFont = new Font(BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED), 15f);
            }catch(Exception e) {
                e.printStackTrace();
                docNameFont = new Font(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED), 15f);
            }
        }

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidths(new float[] {230f, 250f});
        headerTable.setTotalWidth(480);

        PdfPCell companyLogoCell = new PdfPCell();
        Bitmap bmp = BitmapFactory.decodeStream(ctx.getAssets().open("companyLogo.jpg"));
        if(bmp!=null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAbsolutePosition(50, 680);
            companyLogoCell = new PdfPCell(companyLogo, true);
        }
        companyLogoCell.setPadding(3);
        companyLogoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyLogoCell.disableBorderSide(Rectangle.UNDEFINED);
        headerTable.addCell(companyLogoCell);

        PdfPCell companyDataCell = new PdfPCell();
        companyDataCell.setPadding(3);
        companyDataCell.disableBorderSide(Rectangle.UNDEFINED);
        companyDataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        companyDataCell.addElement(new Paragraph(ctx.getString(R.string.wish_list_doc_name), docNameFont));
        headerTable.addCell(companyDataCell);

        return headerTable;
    }

    private void addDetails(Document document, ArrayList<OrderLine> orderLines,
                            Context ctx) throws DocumentException, IOException {
        BaseFont bf;
        Font font;
        try{
            bf = BaseFont.createFont("assets/Roboto-Regular.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED);
        }catch (Exception ex){
            ex.printStackTrace();
            try{
                bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.EMBEDDED);
            }catch(Exception e) {
                e.printStackTrace();
                bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            }
        }
        font = new Font(bf, 7.5f);

        //document.add(new Phrase("\n"));

        PdfPTable superTable = new PdfPTable(1);
        for(OrderLine line : orderLines){
            PdfPTable borderTable = new PdfPTable(1);
            borderTable.setWidthPercentage(100);

            /****************************************/
            PdfPTable table = new PdfPTable(2);
            // Defiles the relative width of the columns
            float[] columnWidths = new float[] {30f, 120f};
            table.setWidths(columnWidths);
            table.setWidthPercentage(100);
            Bitmap bmp = null;
            if(!TextUtils.isEmpty(line.getProduct().getImageFileName())){
                bmp = Utils.getImageFromThumbDirByFileName(ctx, line.getProduct().getImageFileName());
            }
            if(bmp==null){
                bmp = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.no_image_available);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image productImage = Image.getInstance(stream.toByteArray());
            PdfPCell cell = new PdfPCell(productImage, true);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setPadding(3);
            cell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cell);

            PdfPCell cell2 = new PdfPCell();
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell2.setPadding(3);
            cell2.setBorder(Rectangle.LEFT);
            cell2.setUseVariableBorders(true);
            cell2.setBorderColorLeft(BaseColor.LIGHT_GRAY);
            cell2.addElement(new Paragraph(ctx.getString(R.string.product_internalCode, line.getProduct().getInternalCode()), font));
            cell2.addElement(new Paragraph(line.getProduct().getName(), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.brand_detail, line.getProduct().getProductBrand().getName()), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.product_description_detail, line.getProduct().getDescription()), font));
            cell2.addElement(new Paragraph(ctx.getString(R.string.product_purpose_detail, line.getProduct().getPurpose()), font));
            table.addCell(cell2);
            /*****************************************/

            PdfPCell borderTableCell = new PdfPCell();
            borderTableCell.setCellEvent(new RoundRectangle());
            borderTableCell.setBorder(PdfPCell.NO_BORDER);
            borderTableCell.addElement(table);
            borderTable.addCell(borderTableCell);

            /***************************************************************/
            PdfPCell superTableCell = new PdfPCell();
            superTableCell.setPadding(5);
            superTableCell.setBorder(PdfPCell.NO_BORDER);
            superTableCell.addElement(borderTable);
            superTable.addCell(superTableCell);
        }
        document.add(superTable);
        document.add(new Phrase("\n"));
    }

    class RoundRectangle implements PdfPCellEvent {

        public void cellLayout(PdfPCell cell, Rectangle rect,
                               PdfContentByte[] canvas) {
            PdfContentByte cb = canvas[PdfPTable.LINECANVAS];
            cb.setColorStroke(new GrayColor(0.8f));
            cb.roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), 3);
            cb.stroke();
        }
    }
}