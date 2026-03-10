package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.model.Cliente;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PainelClientes extends JPanel {
    private JTable tabelaClientes;
    private DefaultTableModel modeloTabela;
    private JTextField txtBusca;
    private JComboBox<String> cbFiltro;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnAtualizar;
    private ClienteDAO clienteDAO;
    
    public PainelClientes() throws SQLException {
        clienteDAO = new ClienteDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        inicializarComponentes();
        configurarEventos();
        carregarClientes();
    }
    
    private void inicializarComponentes() {
        // Painel de busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelBusca.setBorder(BorderFactory.createTitledBorder("Buscar Clientes"));
        
        txtBusca = new JTextField(20);
        String[] filtros = {"Nome", "Email", "Empresa", "Telefone"};
        cbFiltro = new JComboBox<>(filtros);
        JButton btnBuscar = new JButton("Buscar");
        btnAtualizar = new JButton("Atualizar");
        
        painelBusca.add(new JLabel("Filtrar por:"));
        painelBusca.add(cbFiltro);
        painelBusca.add(new JLabel("Termo:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnAtualizar);
        
        // Tabela de clientes
        String[] colunas = {"ID", "Nome", "Email", "Telefone", "Empresa", "Data Cadastro"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelaClientes = new JTable(modeloTabela);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaClientes.getTableHeader().setReorderingAllowed(false);
        tabelaClientes.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Clientes"));
        
        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoes.setBorder(BorderFactory.createEtchedBorder());
        
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        
        add(painelBusca, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }
    
    private void configurarEventos() {
        btnAdicionar.addActionListener(e -> abrirDialogoCliente(null));
        
        btnEditar.addActionListener(e -> {
            int linha = tabelaClientes.getSelectedRow();
            if (linha >= 0) {
                int id = (int) tabelaClientes.getValueAt(linha, 0);
                try {
                    Cliente cliente = clienteDAO.buscarPorId(id);
                    abrirDialogoCliente(cliente);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Erro ao carregar cliente: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Selecione um cliente para editar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnExcluir.addActionListener(e -> {
            int linha = tabelaClientes.getSelectedRow();
            if (linha >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Tem certeza que deseja excluir este cliente?",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tabelaClientes.getValueAt(linha, 0);
                    try {
                        clienteDAO.excluir(id);
                        carregarClientes();
                        JOptionPane.showMessageDialog(this,
                            "Cliente excluído com sucesso!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this,
                            "Erro ao excluir cliente: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Selecione um cliente para excluir.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnAtualizar.addActionListener(e -> carregarClientes());
        
        tabelaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnEditar.doClick();
                }
            }
        });
    }
    
    public void abrirDialogoCliente(Cliente cliente) {
        DialogoCliente dialog = new DialogoCliente(
            SwingUtilities.getWindowAncestor(this), 
            cliente,
            clienteDAO
        );
        dialog.setVisible(true);
        
        if (dialog.isOperacaoRealizada()) {
            carregarClientes();
        }
    }
    
    private void carregarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            atualizarTabela(clientes);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar clientes: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void atualizarTabela(List<Cliente> clientes) {
        modeloTabela.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Cliente c : clientes) {
            Object[] row = {
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getTelefone(),
                c.getEmpresa(),
                c.getDataCadastro() != null ? sdf.format(c.getDataCadastro()) : ""
            };
            modeloTabela.addRow(row);
        }
    }
}