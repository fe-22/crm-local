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

            // Criar pasta database se não existir
            File databaseDir = new File("database");
            if (!databaseDir.exists()) {
                databaseDir.mkdirs();
                System.out.println("Pasta database criada.");
            }

            // Conectar e criar tabelas
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Banco de dados conectado.");

            // Iniciar aplicação
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