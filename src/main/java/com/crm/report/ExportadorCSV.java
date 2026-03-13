package com.crm.report;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ExportadorCSV {

    public static void exportarTabelaParaCSV(JTable tabela, File arquivo) throws IOException {
        TableModel model = tabela.getModel();

        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            // Cabeçalhos
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.print(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) writer.print(";");
            }
            writer.println();

            // Linhas
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    writer.print(value != null ? value.toString() : "");
                    if (col < model.getColumnCount() - 1) writer.print(";");
                }
                writer.println();
            }
        }
    }
}