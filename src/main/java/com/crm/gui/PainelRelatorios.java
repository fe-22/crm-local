package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.dao.ContatoDAO;
import com.crm.dao.NegociacaoDAO;
import com.crm.model.Cliente;
import com.crm.model.Contato;
import com.crm.model.Negociacao;
import com.crm.report.ExportadorCSV;
import com.crm.report.ExportadorPDF;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PainelRelatorios extends JPanel {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private ClienteDAO clienteDAO;
    private ContatoDAO contatoDAO;
    private NegociacaoDAO negociacaoDAO;

    public PainelRelatorios() {
        try {
            clienteDAO = new ClienteDAO();
            contatoDAO = new ContatoDAO();
            negociacaoDAO = new NegociacaoDAO();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de botões
        JPanel painelBotoes = new JPanel(new GridLayout(0, 3, 10, 10));
        painelBotoes.setBorder(BorderFactory.createTitledBorder("Relatórios Disponíveis"));

        JButton btnClientes = new JButton("Lista de Clientes");
        JButton btnContatos = new JButton("Histórico de Contatos");
        JButton btnPipeline = new JButton("Pipeline de Vendas");
        JButton btnClientesPeriodo = new JButton("Clientes por Período");
        JButton btnExportPDF = new JButton("Exportar para PDF");
        JButton btnExportExcel = new JButton("Exportar para Excel (CSV)");

        painelBotoes.add(btnClientes);
        painelBotoes.add(btnContatos);
        painelBotoes.add(btnPipeline);
        painelBotoes.add(btnClientesPeriodo);
        painelBotoes.add(btnExportPDF);
        painelBotoes.add(btnExportExcel);

        // Tabela para exibir resultados
        modeloTabela = new DefaultTableModel();
        tabela = new JTable(modeloTabela);
        tabela.setFillsViewportHeight(true);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resultado"));

        add(painelBotoes, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Ações dos botões
        btnClientes.addActionListener(e -> gerarRelatorioClientes());
        btnContatos.addActionListener(e -> gerarRelatorioContatos());
        btnPipeline.addActionListener(e -> gerarRelatorioPipeline());
        btnClientesPeriodo.addActionListener(e -> gerarRelatorioClientesPeriodo());
        btnExportPDF.addActionListener(e -> exportarPDF());
        btnExportExcel.addActionListener(e -> exportarExcel());
    }

    private void gerarRelatorioClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            String[] colunas = {"ID", "Nome", "Email", "Telefone", "Empresa", "Data Cadastro"};
            modeloTabela.setColumnIdentifiers(colunas);
            modeloTabela.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Cliente c : clientes) {
                Object[] linha = {
                    c.getId(),
                    c.getNome(),
                    c.getEmail(),
                    c.getTelefone(),
                    c.getEmpresa(),
                    sdf.format(c.getDataCadastro())
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void gerarRelatorioContatos() {
        try {
            List<Contato> contatos = contatoDAO.listarTodos();
            String[] colunas = {"ID", "Cliente", "Data", "Tipo", "Descrição", "Status", "Próximo Contato"};
            modeloTabela.setColumnIdentifiers(colunas);
            modeloTabela.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Contato ct : contatos) {
                Cliente cli = clienteDAO.buscarPorId(ct.getClienteId());
                String nomeCli = (cli != null) ? cli.getNome() : "Desconhecido";
                Object[] linha = {
                    ct.getId(),
                    nomeCli,
                    sdf.format(ct.getDataContato()),
                    ct.getTipo(),
                    ct.getDescricao(),
                    ct.getStatus(),
                    ct.getProximoContato() != null ? sdf.format(ct.getProximoContato()) : ""
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void gerarRelatorioPipeline() {
        try {
            List<Negociacao> lista = negociacaoDAO.listarTodos();
            String[] colunas = {"ID", "Cliente", "Título", "Valor", "Etapa", "Prob.", "Data Início", "Data Prev."};
            modeloTabela.setColumnIdentifiers(colunas);
            modeloTabela.setRowCount(0);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Negociacao n : lista) {
                Cliente cli = clienteDAO.buscarPorId(n.getClienteId());
                String nomeCli = (cli != null) ? cli.getNome() : "Desconhecido";
                Object[] linha = {
                    n.getId(),
                    nomeCli,
                    n.getTitulo(),
                    String.format("R$ %.2f", n.getValor()),
                    n.getEtapa(),
                    n.getProbabilidade() + "%",
                    sdf.format(n.getDataInicio()),
                    n.getDataPrevista() != null ? sdf.format(n.getDataPrevista()) : ""
                };
                modeloTabela.addRow(linha);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void gerarRelatorioClientesPeriodo() {
        // Exemplo simples: todos os clientes (poderia adicionar filtro de data depois)
        gerarRelatorioClientes();
    }

    private void exportarPDF() {
        if (tabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum dado para exportar.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("relatorio.pdf"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo PDF", "pdf");
        chooser.setFileFilter(filter);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            if (!arquivo.getName().toLowerCase().endsWith(".pdf")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".pdf");
            }
            try {
                ExportadorPDF.exportarTabelaParaPDF(tabela, arquivo);
                JOptionPane.showMessageDialog(this, "PDF exportado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar PDF: " + ex.getMessage());
            }
        }
    }

    private void exportarExcel() {
        if (tabela.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum dado para exportar.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("relatorio.csv"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivo CSV", "csv");
        chooser.setFileFilter(filter);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();
            if (!arquivo.getName().toLowerCase().endsWith(".csv")) {
                arquivo = new File(arquivo.getAbsolutePath() + ".csv");
            }
            try {
                ExportadorCSV.exportarTabelaParaCSV(tabela, arquivo);
                JOptionPane.showMessageDialog(this, "Arquivo CSV exportado com sucesso!\nPode ser aberto no Excel.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar CSV: " + ex.getMessage());
            }
        }
    }
}