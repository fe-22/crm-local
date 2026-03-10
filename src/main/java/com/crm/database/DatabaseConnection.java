package com.crm.database;

import java.sql.*;
import java.io.File;

public class DatabaseConnection {
    private static final String DB_URL;
    private static Connection connection = null;

    static {
        // Determina o diretório de dados do aplicativo
        String baseDir;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows: usa %APPDATA%
            baseDir = System.getenv("APPDATA");
            if (baseDir == null) {
                baseDir = System.getProperty("user.home");
            }
        } else {
            // Linux/macOS: usa user.home
            baseDir = System.getProperty("user.home");
        }

        // Cria a subpasta CRM-Local
        File dbDir = new File(baseDir, "CRM-Local");
        if (!dbDir.exists()) {
            boolean created = dbDir.mkdirs();
            System.out.println("Diretório de dados criado: " + created + " - " + dbDir.getAbsolutePath());
        }

        // Define o caminho completo do banco
        File dbFile = new File(dbDir, "crm_local.db");
        DB_URL = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        System.out.println("Banco de dados será criado em: " + dbFile.getAbsolutePath());
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexão estabelecida. Criando tabelas...");
            criarTabelas();
        }
        return connection;
    }

    private static void criarTabelas() throws SQLException {
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
            System.out.println("Tabela clientes criada/verificada.");
            stmt.execute(sqlContatos);
            System.out.println("Tabela contatos criada/verificada.");
            stmt.execute(sqlNegociacoes);
            System.out.println("Tabela negociacoes criada/verificada.");

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_clientes_nome ON clientes(nome)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_contatos_cliente ON contatos(cliente_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_negociacoes_cliente ON negociacoes(cliente_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_negociacoes_etapa ON negociacoes(etapa)");
            System.out.println("Índices criados/verificados.");
        } catch (SQLException e) {
            System.err.println("ERRO ao criar tabelas: " + e.getMessage());
            e.printStackTrace();
            throw e; // relança para que o chamador saiba
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão fechada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}