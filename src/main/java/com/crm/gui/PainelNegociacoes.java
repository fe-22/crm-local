package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.dao.NegociacaoDAO;
import com.crm.model.Cliente;
import com.crm.model.Negociacao;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PainelNegociacoes extends JPanel {
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JComboBox<Cliente> cbClientes;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnAtualizar;
    private NegociacaoDAO negociacaoDAO;
    private ClienteDAO clienteDAO;

    public PainelNegociacoes() throws SQLException {
        negociacaoDAO = new NegociacaoDAO();
        clienteDAO = new ClienteDAO();
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        inicializarComponentes();
        configurarEventos();
        carregarNegociacoes();
    }

    private void inicializarComponentes() throws SQLException {
        // Painel de filtro
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltro.setBorder(BorderFactory.createTitledBorder("Filtrar"));

        List<Cliente> clientes = clienteDAO.listarTodos();
        cbClientes = new JComboBox<>();
        cbClientes.addItem(null); // item para "Todos"
        for (Cliente c : clientes) {
            cbClientes.addItem(c);
        }
        cbClientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) {
                    setText("Todos os clientes");
                } else {
                    setText(((Cliente) value).getNome());
                }
                return this;
            }
        });

        JButton btnFiltrar = new JButton("Filtrar");
        painelFiltro.add(new JLabel("Cliente:"));
        painelFiltro.add(cbClientes);
        painelFiltro.add(btnFiltrar);

        // Tabela
        String[] colunas = {"ID", "Cliente", "Título", "Valor", "Etapa", "Prob.", "Data Início", "Data Prev."};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(tabela);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Negociações"));

        // Botões
        JPanel painelBotoes = new JPanel();
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnAtualizar = new JButton("Atualizar");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);

        add(painelFiltro, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        btnFiltrar.addActionListener(e -> filtrar());
    }

    private void configurarEventos() {
        btnAdicionar.addActionListener(e -> abrirDialogoNegociacao(null));

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modeloTabela.getValueAt(linha, 0);
                try {
                    Negociacao n = negociacaoDAO.buscarPorId(id);
                    abrirDialogoNegociacao(n);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma negociação.");
            }
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modeloTabela.getValueAt(linha, 0);
                int resp = JOptionPane.showConfirmDialog(this, "Excluir negociação?");
                if (resp == JOptionPane.YES_OPTION) {
                    try {
                        negociacaoDAO.excluir(id);
                        carregarNegociacoes();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                    }
                }
            }
        });

        btnAtualizar.addActionListener(e -> carregarNegociacoes());
    }

    private void filtrar() {
        Cliente sel = (Cliente) cbClientes.getSelectedItem();
        try {
            List<Negociacao> lista;
            if (sel == null) {
                lista = negociacaoDAO.listarTodos();
            } else {
                lista = negociacaoDAO.listarPorCliente(sel.getId());
            }
            atualizarTabela(lista);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void carregarNegociacoes() {
        try {
            List<Negociacao> lista = negociacaoDAO.listarTodos();
            atualizarTabela(lista);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void atualizarTabela(List<Negociacao> lista) {
        modeloTabela.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Negociacao n : lista) {
            String nomeCliente = "";
            try {
                Cliente c = clienteDAO.buscarPorId(n.getClienteId());
                if (c != null) nomeCliente = c.getNome();
            } catch (SQLException e) {
                // ignora
            }
            Object[] row = {
                n.getId(),
                nomeCliente,
                n.getTitulo(),
                String.format("R$ %.2f", n.getValor()),
                n.getEtapa(),
                n.getProbabilidade() + "%",
                sdf.format(n.getDataInicio()),
                n.getDataPrevista() != null ? sdf.format(n.getDataPrevista()) : ""
            };
            modeloTabela.addRow(row);
        }
    }

    // Método público para ser chamado pela toolbar
    public void abrirDialogoNegociacao(Negociacao negociacao) {
        DialogoNegociacao dialog = new DialogoNegociacao(
            SwingUtilities.getWindowAncestor(this),
            negociacao,
            negociacaoDAO,
            clienteDAO
        );
        dialog.setVisible(true);
        if (dialog.isOperacaoRealizada()) {
            carregarNegociacoes();
        }
    }
}