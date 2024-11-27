package com.immune.capstone.utils.impl;

import com.immune.capstone.utils.ByteDocumentGenerator;
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

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Log4j2
@Component
public class PdfDocumentGenerator implements ByteDocumentGenerator {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);
    private static final Font BASE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

    @Override
    public Optional<byte[]> generateDocument(Map<String, String> fileContent, String title) {
        if (fileContent == null  || fileContent.isEmpty()  || StringUtils.isBlank(title)) {
            log.warn("Empty content sent or no filename was sent. Skipping generation.");
            return Optional.empty();
        }

        // Original source: https://www.youtube.com/watch?v=_FUCwZ8Hf8w
        Document doc = new Document();
        try {
            var outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, outputStream);
            doc.open();
            Paragraph titlePar = new Paragraph(title, TITLE_FONT);
            doc.add(titlePar);
            PdfPTable table = new PdfPTable(fileContent.size());
            generateRowCellsStream(fileContent.keySet(), true).forEach(table::addCell);
            generateRowCellsStream(fileContent.values(), false).forEach(table::addCell);
            doc.add(table);

            Paragraph tsPar = new Paragraph("Generation timestamp: %s".formatted(LocalDateTime.now().toString()), BASE_FONT);
            doc.add(tsPar);

            doc.close();
            return Optional.of(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Unable to create pdf file.", e);
            return Optional.empty();
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
