package com.crm.dao;

import com.crm.model.Negociacao;
import com.crm.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NegociacaoDAO {
    private Connection connection;

    public NegociacaoDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    public void inserir(Negociacao neg) throws SQLException {
        String sql = "INSERT INTO negociacoes (cliente_id, titulo, descricao, valor, etapa, probabilidade, data_inicio, data_prevista) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, neg.getClienteId());
            stmt.setString(2, neg.getTitulo());
            stmt.setString(3, neg.getDescricao());
            stmt.setDouble(4, neg.getValor());
            stmt.setString(5, neg.getEtapa());
            stmt.setInt(6, neg.getProbabilidade());
            stmt.setDate(7, new java.sql.Date(neg.getDataInicio().getTime()));
            if (neg.getDataPrevista() != null) {
                stmt.setDate(8, new java.sql.Date(neg.getDataPrevista().getTime()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            stmt.executeUpdate();
        }
    }

    public void atualizar(Negociacao neg) throws SQLException {
        String sql = "UPDATE negociacoes SET cliente_id=?, titulo=?, descricao=?, valor=?, etapa=?, probabilidade=?, data_inicio=?, data_prevista=?, data_fechamento=?, motivo_perda=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, neg.getClienteId());
            stmt.setString(2, neg.getTitulo());
            stmt.setString(3, neg.getDescricao());
            stmt.setDouble(4, neg.getValor());
            stmt.setString(5, neg.getEtapa());
            stmt.setInt(6, neg.getProbabilidade());
            stmt.setDate(7, new java.sql.Date(neg.getDataInicio().getTime()));
            if (neg.getDataPrevista() != null) {
                stmt.setDate(8, new java.sql.Date(neg.getDataPrevista().getTime()));
            } else {
                stmt.setNull(8, Types.DATE);
            }
            if (neg.getDataFechamento() != null) {
                stmt.setDate(9, new java.sql.Date(neg.getDataFechamento().getTime()));
            } else {
                stmt.setNull(9, Types.DATE);
            }
            stmt.setString(10, neg.getMotivoPerda());
            stmt.setInt(11, neg.getId());
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM negociacoes WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Negociacao buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM negociacoes WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarNegociacao(rs);
                }
            }
        }
        return null;
    }

    public List<Negociacao> listarTodos() throws SQLException {
        List<Negociacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM negociacoes ORDER BY data_inicio DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(criarNegociacao(rs));
            }
        }
        return lista;
    }

    public List<Negociacao> listarPorCliente(int clienteId) throws SQLException {
        List<Negociacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM negociacoes WHERE cliente_id=? ORDER BY data_inicio DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(criarNegociacao(rs));
                }
            }
        }
        return lista;
    }

    private Negociacao criarNegociacao(ResultSet rs) throws SQLException {
        Negociacao n = new Negociacao();
        n.setId(rs.getInt("id"));
        n.setClienteId(rs.getInt("cliente_id"));
        n.setTitulo(rs.getString("titulo"));
        n.setDescricao(rs.getString("descricao"));
        n.setValor(rs.getDouble("valor"));
        n.setEtapa(rs.getString("etapa"));
        n.setProbabilidade(rs.getInt("probabilidade"));
        n.setDataInicio(rs.getDate("data_inicio"));
        n.setDataPrevista(rs.getDate("data_prevista"));
        n.setDataFechamento(rs.getDate("data_fechamento"));
        n.setMotivoPerda(rs.getString("motivo_perda"));
        return n;
    }
}