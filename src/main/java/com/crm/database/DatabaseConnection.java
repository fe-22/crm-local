package com.crm.database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:database/crm_local.db";
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            criarTabelas();
        }
        return connection;
    }
    
    private static void criarTabelas() throws SQLException {
        // Versão SEM text blocks (compatível com Java 11)
        String sqlClientes = "CREATE TABLE IF NOT EXISTS clientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT, " +
                "telefone TEXT, " +
                "empresa TEXT, " +
                "data_cadastro DATE NOT NULL, " +
                "observacoes TEXT, " +
                "ativo BOOLEAN DEFAULT 1, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        String sqlContatos = "CREATE TABLE IF NOT EXISTS contatos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cliente_id INTEGER NOT NULL, " +
                "data_contato DATE NOT NULL, " +
                "tipo VARCHAR(20) NOT NULL, " +
                "descricao TEXT, " +
                "status VARCHAR(20), " +
                "proximo_contato DATE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE" +
                ")";
        
        String sqlNegociacoes = "CREATE TABLE IF NOT EXISTS negociacoes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cliente_id INTEGER NOT NULL, " +
                "titulo VARCHAR(100) NOT NULL, " +
                "descricao TEXT, " +
                "valor DECIMAL(10,2), " +
                "etapa VARCHAR(30) NOT NULL, " +
                "probabilidade INTEGER DEFAULT 0, " +
                "data_inicio DATE NOT NULL, " +
                "data_prevista DATE, " +
                "data_fechamento DATE, " +
                "motivo_perda TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE" +
                ")";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlClientes);
            stmt.execute(sqlContatos);
            stmt.execute(sqlNegociacoes);
            
            // Criar índices
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_clientes_nome ON clientes(nome)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_contatos_cliente ON contatos(cliente_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_negociacoes_cliente ON negociacoes(cliente_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_negociacoes_etapa ON negociacoes(etapa)");
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}