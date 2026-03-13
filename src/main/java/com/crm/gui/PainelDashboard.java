package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.dao.ContatoDAO;
import com.crm.dao.NegociacaoDAO;
import com.crm.model.Negociacao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PainelDashboard extends JPanel {
    private ClienteDAO clienteDAO;
    private ContatoDAO contatoDAO;
    private NegociacaoDAO negociacaoDAO;

    private JLabel lblTotalClientes;
    private JLabel lblTotalContatos;
    private JLabel lblTotalNegociacoes;
    private JLabel lblTotalValorPipeline;
    private JTable tabelaPipeline;
    private DefaultTableModel modeloTabela;

    public PainelDashboard() throws SQLException {
        System.out.println("[DEBUG] Construtor do PainelDashboard iniciado");

        clienteDAO = new ClienteDAO();
        contatoDAO = new ContatoDAO();
        negociacaoDAO = new NegociacaoDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior com cards de estatísticas
        JPanel painelCards = new JPanel(new GridLayout(1, 4, 10, 10));
        painelCards.setPreferredSize(new Dimension(800, 80));
        painelCards.add(criarCard("Total Clientes", "0", new Color(52, 152, 219)));
        painelCards.add(criarCard("Total Contatos", "0", new Color(46, 204, 113)));
        painelCards.add(criarCard("Negociações", "0", new Color(155, 89, 182)));
        painelCards.add(criarCard("Valor Total (R$)", "0,00", new Color(230, 126, 34)));

        // Painel central com tabela de pipeline
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("Pipeline de Vendas por Etapa"));

        String[] colunas = {"Etapa", "Quantidade", "Valor Total (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaPipeline = new JTable(modeloTabela);
        tabelaPipeline.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(tabelaPipeline);
        painelTabela.add(scroll, BorderLayout.CENTER);

        add(painelCards, BorderLayout.NORTH);
        add(painelTabela, BorderLayout.CENTER);

        // Carregar dados iniciais
        carregarDados();

        System.out.println("[DEBUG] PainelDashboard construído com sucesso");
    }

    private JPanel criarCard(String titulo, String valorInicial, Color cor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cor);
        card.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

        JLabel lblTitulo = new JLabel(titulo, JLabel.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblValor = new JLabel(valorInicial, JLabel.CENTER);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Arial", Font.BOLD, 20));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        // Armazenar referência para atualizar depois
        if (titulo.equals("Total Clientes")) {
            lblTotalClientes = lblValor;
        } else if (titulo.equals("Total Contatos")) {
            lblTotalContatos = lblValor;
        } else if (titulo.equals("Negociações")) {
            lblTotalNegociacoes = lblValor;
        } else if (titulo.equals("Valor Total (R$)")) {
            lblTotalValorPipeline = lblValor;
        }

        return card;
    }

    private void carregarDados() {
        try {
            // Totais
            int totalClientes = clienteDAO.listarTodos().size();
            int totalContatos = contatoDAO.listarTodos().size();
            List<Negociacao> negociacoes = negociacaoDAO.listarTodos();

            double valorTotal = negociacoes.stream()
                    .mapToDouble(Negociacao::getValor)
                    .sum();

            lblTotalClientes.setText(String.valueOf(totalClientes));
            lblTotalContatos.setText(String.valueOf(totalContatos));
            lblTotalNegociacoes.setText(String.valueOf(negociacoes.size()));
            lblTotalValorPipeline.setText(String.format("R$ %.2f", valorTotal).replace(".", ","));

            // Agrupar por etapa
            Map<String, Integer> qtdPorEtapa = new HashMap<>();
            Map<String, Double> valorPorEtapa = new HashMap<>();
            for (Negociacao n : negociacoes) {
                String etapa = n.getEtapa();
                qtdPorEtapa.put(etapa, qtdPorEtapa.getOrDefault(etapa, 0) + 1);
                valorPorEtapa.put(etapa, valorPorEtapa.getOrDefault(etapa, 0.0) + n.getValor());
            }

            modeloTabela.setRowCount(0);
            for (String etapa : qtdPorEtapa.keySet()) {
                Object[] linha = {
                    etapa,
                    qtdPorEtapa.get(etapa),
                    String.format("R$ %.2f", valorPorEtapa.get(etapa)).replace(".", ",")
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dashboard: " + e.getMessage());
        }
    }
}