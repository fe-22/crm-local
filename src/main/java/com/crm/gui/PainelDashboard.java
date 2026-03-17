package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.dao.ContatoDAO;
import com.crm.dao.NegociacaoDAO;
import com.crm.model.Negociacao;
import com.crm.util.AlertaSistema;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

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
    private AlertaSistema alertaSistema;

    private JLabel lblTotalClientes;
    private JLabel lblTotalContatos;
    private JLabel lblTotalNegociacoes;
    private JLabel lblTotalValorPipeline;
    private JTable tabelaPipeline;
    private DefaultTableModel modeloTabela;
    private JList<String> listaAlertas;
    private DefaultListModel<String> modeloAlertas;
    private ChartPanel chartPanel;

    public PainelDashboard() throws SQLException {
        System.out.println("[DEBUG] Construtor do PainelDashboard iniciado");

        clienteDAO = new ClienteDAO();
        contatoDAO = new ContatoDAO();
        negociacaoDAO = new NegociacaoDAO();
        alertaSistema = new AlertaSistema();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel superior com cards de estatísticas
        JPanel painelCards = new JPanel(new GridLayout(1, 4, 10, 10));
        painelCards.setPreferredSize(new Dimension(800, 80));
        painelCards.add(criarCard("Total Clientes", "0", new Color(52, 152, 219)));
        painelCards.add(criarCard("Total Contatos", "0", new Color(46, 204, 113)));
        painelCards.add(criarCard("Negociações", "0", new Color(155, 89, 182)));
        painelCards.add(criarCard("Valor Total (R$)", "0,00", new Color(230, 126, 34)));

        // Painel central dividido: gráfico em cima, tabela embaixo
        JPanel painelCentral = new JPanel(new GridLayout(2, 1, 5, 5));

        // Gráfico de barras
        JPanel painelGrafico = new JPanel(new BorderLayout());
        painelGrafico.setBorder(BorderFactory.createTitledBorder("Valor por Etapa"));
        chartPanel = criarGraficoBarras();
        painelGrafico.add(chartPanel, BorderLayout.CENTER);

        // Tabela de pipeline
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

        painelCentral.add(painelGrafico);
        painelCentral.add(painelTabela);

        // Painel de alertas (sul)
        JPanel painelAlertas = new JPanel(new BorderLayout());
        painelAlertas.setBorder(BorderFactory.createTitledBorder("Alertas Próximos"));
        modeloAlertas = new DefaultListModel<>();
        listaAlertas = new JList<>(modeloAlertas);
        listaAlertas.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scrollAlertas = new JScrollPane(listaAlertas);
        scrollAlertas.setPreferredSize(new Dimension(800, 100));
        painelAlertas.add(scrollAlertas, BorderLayout.CENTER);

        // Adicionar componentes
        add(painelCards, BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
        add(painelAlertas, BorderLayout.SOUTH);

        // Carregar dados iniciais e alertas
        carregarDados();
        carregarAlertas();

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

    private ChartPanel criarGraficoBarras() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(
                "Valor por Etapa",
                "Etapa",
                "Valor (R$)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(600, 200));
        return panel;
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

            // Atualizar tabela
            modeloTabela.setRowCount(0);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (String etapa : qtdPorEtapa.keySet()) {
                Object[] linha = {
                    etapa,
                    qtdPorEtapa.get(etapa),
                    String.format("R$ %.2f", valorPorEtapa.get(etapa)).replace(".", ",")
                };
                modeloTabela.addRow(linha);
                dataset.addValue(valorPorEtapa.get(etapa), "Valor", etapa);
            }

            // Atualizar gráfico
            JFreeChart chart = chartPanel.getChart();
            chart.getCategoryPlot().setDataset(dataset);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dashboard: " + e.getMessage());
        }
    }

    private void carregarAlertas() {
        List<String> alertas = alertaSistema.gerarAlertas();
        modeloAlertas.clear();
        if (alertas.isEmpty()) {
            modeloAlertas.addElement("Nenhum alerta próximo.");
        } else {
            for (String a : alertas) {
                modeloAlertas.addElement(a);
            }
        }
    }
}