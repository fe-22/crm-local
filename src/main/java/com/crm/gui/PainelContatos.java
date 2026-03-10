package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.dao.ContatoDAO;
import com.crm.model.Cliente;
import com.crm.model.Contato;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PainelContatos extends JPanel {
    private JTable tabelaContatos;
    private DefaultTableModel modeloTabela;
    private JComboBox<Cliente> cbClientes;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnAtualizar;
    private ContatoDAO contatoDAO;
    private ClienteDAO clienteDAO;

    public PainelContatos() throws SQLException {
        contatoDAO = new ContatoDAO();
        clienteDAO = new ClienteDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inicializarComponentes();
        configurarEventos();
        carregarContatos();
    }

    private void inicializarComponentes() throws SQLException {
        // Painel de filtro por cliente
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFiltro.setBorder(BorderFactory.createTitledBorder("Filtrar por Cliente"));

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

        // Tabela de contatos
        String[] colunas = {"ID", "Cliente", "Data", "Tipo", "Descrição", "Status", "Próximo Contato"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaContatos = new JTable(modeloTabela);
        tabelaContatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaContatos.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(tabelaContatos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Contatos"));

        // Painel de botões
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
        btnAdicionar.addActionListener(e -> abrirDialogoContato(null));
        btnEditar.addActionListener(e -> {
            int linha = tabelaContatos.getSelectedRow();
            if (linha >= 0) {
                int id = (int) tabelaContatos.getValueAt(linha, 0);
                try {
                    Contato c = contatoDAO.buscarPorId(id);
                    abrirDialogoContato(c);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um contato.");
            }
        });
        btnExcluir.addActionListener(e -> {
            int linha = tabelaContatos.getSelectedRow();
            if (linha >= 0) {
                int id = (int) tabelaContatos.getValueAt(linha, 0);
                int resp = JOptionPane.showConfirmDialog(this, "Excluir contato?");
                if (resp == JOptionPane.YES_OPTION) {
                    try {
                        contatoDAO.excluir(id);
                        carregarContatos();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
                    }
                }
            }
        });
        btnAtualizar.addActionListener(e -> carregarContatos());
    }

    private void filtrar() {
        Cliente selecionado = (Cliente) cbClientes.getSelectedItem();
        try {
            List<Contato> contatos;
            if (selecionado == null) {
                contatos = contatoDAO.listarTodos();
            } else {
                contatos = contatoDAO.listarPorCliente(selecionado.getId());
            }
            atualizarTabela(contatos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao filtrar: " + ex.getMessage());
        }
    }

    private void carregarContatos() {
        try {
            List<Contato> contatos = contatoDAO.listarTodos();
            atualizarTabela(contatos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void atualizarTabela(List<Contato> contatos) {
        modeloTabela.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Contato c : contatos) {
            String nomeCliente = "";
            try {
                Cliente cli = clienteDAO.buscarPorId(c.getClienteId());
                if (cli != null) nomeCliente = cli.getNome();
            } catch (SQLException e) {}
            Object[] row = {
                c.getId(),
                nomeCliente,
                sdf.format(c.getDataContato()),
                c.getTipo(),
                c.getDescricao(),
                c.getStatus(),
                c.getProximoContato() != null ? sdf.format(c.getProximoContato()) : ""
            };
            modeloTabela.addRow(row);
        }
    }

    public void abrirDialogoContato(Contato contato) {
        DialogoContato dialog = new DialogoContato(SwingUtilities.getWindowAncestor(this), contato, contatoDAO, clienteDAO);
        dialog.setVisible(true);
        if (dialog.isOperacaoRealizada()) {
            carregarContatos();
        }
    }
}