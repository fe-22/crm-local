package com.crm.dao;

import com.crm.model.Contato;
import com.crm.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContatoDAO {
    private Connection connection;

    public ContatoDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public void inserir(Contato contato) throws SQLException {
        String sql = "INSERT INTO contatos (cliente_id, data_contato, tipo, descricao, status, proximo_contato) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, contato.getClienteId());
            stmt.setDate(2, new java.sql.Date(contato.getDataContato().getTime()));
            stmt.setString(3, contato.getTipo());
            stmt.setString(4, contato.getDescricao());
            stmt.setString(5, contato.getStatus());
            if (contato.getProximoContato() != null) {
                stmt.setDate(6, new java.sql.Date(contato.getProximoContato().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.executeUpdate();
        }
    }

    public void atualizar(Contato contato) throws SQLException {
        String sql = "UPDATE contatos SET cliente_id=?, data_contato=?, tipo=?, descricao=?, status=?, proximo_contato=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, contato.getClienteId());
            stmt.setDate(2, new java.sql.Date(contato.getDataContato().getTime()));
            stmt.setString(3, contato.getTipo());
            stmt.setString(4, contato.getDescricao());
            stmt.setString(5, contato.getStatus());
            if (contato.getProximoContato() != null) {
                stmt.setDate(6, new java.sql.Date(contato.getProximoContato().getTime()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.setInt(7, contato.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM contatos WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Contato buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM contatos WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarContato(rs);
                }
            }
        }
        return null;
    }

    public List<Contato> listarTodos() throws SQLException {
        List<Contato> lista = new ArrayList<>();
        String sql = "SELECT * FROM contatos ORDER BY data_contato DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(criarContato(rs));
            }
        }
        return lista;
    }

    public List<Contato> listarPorCliente(int clienteId) throws SQLException {
        List<Contato> lista = new ArrayList<>();
        String sql = "SELECT * FROM contatos WHERE cliente_id=? ORDER BY data_contato DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(criarContato(rs));
                }
            }
        }
        return lista;
    }

    private Contato criarContato(ResultSet rs) throws SQLException {
        Contato c = new Contato();
        c.setId(rs.getInt("id"));
        c.setClienteId(rs.getInt("cliente_id"));
        c.setDataContato(rs.getDate("data_contato"));
        c.setTipo(rs.getString("tipo"));
        c.setDescricao(rs.getString("descricao"));
        c.setStatus(rs.getString("status"));
        c.setProximoContato(rs.getDate("proximo_contato"));
        return c;
    }
}