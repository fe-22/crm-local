package com.crm;

import com.crm.gui.TelaPrincipal;
import com.crm.database.DatabaseConnection;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Configurar aparência do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Testar conexão com banco de dados
        try {
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("Banco de dados conectado com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao conectar ao banco de dados: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Iniciar aplicação
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}