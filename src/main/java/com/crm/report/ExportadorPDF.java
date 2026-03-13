package com.crm.report;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileNotFoundException;

public class ExportadorPDF {

    public static void exportarTabelaParaPDF(JTable tabela, File arquivo) throws FileNotFoundException {
        // Cria o PDF
        PdfWriter writer = new PdfWriter(arquivo.getAbsolutePath());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        document.add(new Paragraph("Relatório CRM Local")
                .setBold()
                .setFontSize(16));

        // Cria tabela com número de colunas igual ao da JTable
        TableModel model = tabela.getModel();
        Table pdfTable = new Table(UnitValue.createPercentArray(model.getColumnCount()));
        pdfTable.setWidth(UnitValue.createPercentValue(100));

        // Cabeçalhos
        for (int i = 0; i < model.getColumnCount(); i++) {
            pdfTable.addCell(new Cell().add(new Paragraph(model.getColumnName(i)).setBold()));
        }

        // Linhas
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row, col);
                pdfTable.addCell(new Cell().add(new Paragraph(value != null ? value.toString() : "")));
            }
        }

        document.add(pdfTable);
        document.close();
    }
}