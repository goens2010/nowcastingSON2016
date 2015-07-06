/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 *
 * @author Dai
 */
public class PDFModel {

    static public boolean createPdf(ArrayList<ImageModel> inputFile, String outputFile, boolean isPictureFile) {
        /**
         * Set the page size for the image
         */
        Rectangle pageSize = new Rectangle(595, 841);
        Document pdfDocument = new Document(pageSize);
        String pdfFilePath = outputFile;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pdfFilePath);
            PdfWriter writer = null;
            writer = PdfWriter.getInstance(pdfDocument, fileOutputStream);
            writer.open();
            pdfDocument.open();
            /**
             * Proceed if the file given is a picture file
             */
            if (isPictureFile) {
                pdfDocument.add(new Paragraph("Images after cluster"));
                for (int i = 0; i < inputFile.size(); i++) {
                    pdfDocument.add(Image.getInstance(inputFile.get(i).getPath()));
                    pdfDocument.add(new Paragraph("Name: " + inputFile.get(i).getName() + "- Time: " + inputFile.get(i).getTime()));
                }
            } else {
                //pdfDocument.add(new Paragraph(org.apache.commons.io.FileUtils.readFileToString(file)));
            }

            pdfDocument.close();
            writer.close();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

 
}
