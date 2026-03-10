package com.crm.gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class TelaPrincipal extends JFrame {
    private JTabbedPane tabbedPane;
    private PainelClientes painelClientes;
    private PainelContatos painelContatos;
    private PainelNegociacoes painelNegociacoes;
    private PainelRelatorios painelRelatorios;
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel footerPanel;

    public TelaPrincipal() {
        setTitle("CRM Local - Sistema de Gestão de Clientes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);

        try {
            inicializarComponentes();
            criarMenu();
            criarToolbar();
            criarFooter(); // <-- adicionado
            setJMenuBar(menuBar);
            
            // Organizando os componentes na janela
            add(toolBar, BorderLayout.NORTH);
            add(tabbedPane, BorderLayout.CENTER);
            add(footerPanel, BorderLayout.SOUTH); // <-- footer na parte inferior
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao inicializar: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inicializarComponentes() throws SQLException {
        tabbedPane = new JTabbedPane();

        painelClientes = new PainelClientes();
        painelContatos = new PainelContatos();
        painelNegociacoes = new PainelNegociacoes();
        painelRelatorios = new PainelRelatorios();

        tabbedPane.addTab("Clientes", painelClientes);
        tabbedPane.addTab("Contatos", painelContatos);
        tabbedPane.addTab("Negociações", painelNegociacoes);
        tabbedPane.addTab("Relatórios", painelRelatorios);
    }

    private void criarMenu() {
        menuBar = new JMenuBar();

        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemBackup = new JMenuItem("Fazer Backup");
        JMenuItem itemRestaurar = new JMenuItem("Restaurar Backup");
        JMenuItem itemSair = new JMenuItem("Sair");

        itemSair.addActionListener(e -> System.exit(0));

        menuArquivo.add(itemBackup);
        menuArquivo.add(itemRestaurar);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);

        // Menu Relatórios
        JMenu menuRelatorios = new JMenu("Relatórios");
        JMenuItem itemClientes = new JMenuItem("Lista de Clientes");
        JMenuItem itemContatos = new JMenuItem("Histórico de Contatos");
        JMenuItem itemVendas = new JMenuItem("Pipeline de Vendas");

        menuRelatorios.add(itemClientes);
        menuRelatorios.add(itemContatos);
        menuRelatorios.add(itemVendas);

        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");

        itemSobre.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "CRM Local v1.0\nDesenvolvido em Java\n\n" +
                "Sistema de gerenciamento de clientes para uso local.\n" +
                "Desenvolvedora: Fthec Sistemas e Automação\n" +
                "CNPJ: 64.212.742/0001-63\n" +
                "Contato: (11) 9.8217-0425\nE-mail: fernando.fernandes@fthec.com.br",
                "Sobre", JOptionPane.INFORMATION_MESSAGE);
        });

        menuAjuda.add(itemSobre);

        menuBar.add(menuArquivo);
        menuBar.add(menuRelatorios);
        menuBar.add(menuAjuda);
    }

    private void criarToolbar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnNovoCliente = new JButton("Novo Cliente");
        JButton btnNovoContato = new JButton("Novo Contato");
        JButton btnNovaNegociacao = new JButton("Nova Negociação");

        btnNovoCliente.addActionListener(e -> {
            if (painelClientes != null) {
                painelClientes.abrirDialogoCliente(null);
            }
        });

        btnNovoContato.addActionListener(e -> {
            if (painelContatos != null) {
                painelContatos.abrirDialogoContato(null);
            }
        });

        btnNovaNegociacao.addActionListener(e -> {
            if (painelNegociacoes != null) {
                painelNegociacoes.abrirDialogoNegociacao(null);
            }
        });

        toolBar.add(btnNovoCliente);
        toolBar.addSeparator();
        toolBar.add(btnNovoContato);
        toolBar.addSeparator();
        toolBar.add(btnNovaNegociacao);
    }

    private void criarFooter() {
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(240, 240, 240)); // cinza claro
        footerPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel labelFooter = new JLabel(
            "Fthec Sistemas e Automação - CNPJ: 64.212.742/0001-63 | " +
            "Contato: (11) 9.8217-0425 | E-mail: fernando.fernandes@fthec.com.br"
            
        );
        labelFooter.setFont(new Font("Arial", Font.PLAIN, 11));
        labelFooter.setForeground(Color.DARK_GRAY);

        footerPanel.add(labelFooter);
    }
}