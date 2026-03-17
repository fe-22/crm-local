package com.crm;

import com.crm.gui.TelaPrincipal;
import com.crm.database.DatabaseConnection;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            File databaseDir = new File(System.getenv("APPDATA") + "/CRM-Local");
            if (!databaseDir.exists()) {
                databaseDir.mkdirs();
            }

            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Banco de dados conectado com sucesso!");

            SwingUtilities.invokeLater(() -> {
                try {
                    TelaPrincipal tela = new TelaPrincipal();
                    tela.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Erro ao abrir tela: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro fatal: " + e.getMessage());
        }
    }
}