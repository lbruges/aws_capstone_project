package com.immune.capstone.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

@Log4j2
@Component
public class PdfDocumentGenerator {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
    private static final Font BASE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

    /**
     * https://www.youtube.com/watch?v=_FUCwZ8Hf8w
     * @param fileContent
     * @param filename
     */
    public void generateDocument(Map<String, String> fileContent, String filename) {

        if (fileContent == null  || fileContent.isEmpty()  || StringUtils.isBlank(filename)) {
            log.warn("Empty content sent or no filename was sent. Skipping generation.");
            return;
        }

        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(filename + ".pdf"));
            doc.open();
            Paragraph par = new Paragraph(filename, TITLE_FONT);
            doc.add(par);
            PdfPTable table = new PdfPTable(fileContent.size());
            generateRowCellsStream(fileContent.keySet(), true).forEach(table::addCell);
            generateRowCellsStream(fileContent.values(), false).forEach(table::addCell);
            doc.add(table);
            doc.close();
        } catch (Exception e) {
            log.error("Unable to create pdf file.", e);
        }

    }

    private Stream<PdfPCell> generateRowCellsStream(Collection<String> rowCells, boolean isHeader) {
        return rowCells.stream()
                .map(row -> {
                    PdfPCell cell = new PdfPCell();
                    cell.setPhrase(new Phrase(row, isHeader ? TITLE_FONT : BASE_FONT));
                    return cell;
                });
    }

}
