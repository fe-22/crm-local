package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.model.Cliente;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Date;

public class DialogoCliente extends JDialog {
    private JTextField txtNome, txtEmail, txtTelefone, txtEmpresa;
    private JTextArea txtObservacoes;
    private JButton btnSalvar, btnCancelar;
    private Cliente cliente;
    private ClienteDAO clienteDAO;
    private boolean operacaoRealizada = false;

    public DialogoCliente(Window parent, Cliente cliente, ClienteDAO clienteDAO) {
        super(parent, cliente == null ? "Novo Cliente" : "Editar Cliente", 
              ModalityType.APPLICATION_MODAL);
        
        this.cliente = cliente;
        this.clienteDAO = clienteDAO;
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        
        inicializarComponentes();
        if (cliente != null) {
            preencherCampos();
        }
    }
    
    private void inicializarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Nome:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtNome = new JTextField(30);
        painel.add(txtNome, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtEmail = new JTextField(30);
        painel.add(txtEmail, gbc);
        
        // Telefone
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Telefone:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtTelefone = new JTextField(30);
        painel.add(txtTelefone, gbc);
        
        // Empresa
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Empresa:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtEmpresa = new JTextField(30);
        painel.add(txtEmpresa, gbc);
        
        // Observações
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Observações:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtObservacoes = new JTextArea(5, 30);
        txtObservacoes.setLineWrap(true);
        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        painel.add(scrollObs, gbc);
        
        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
        
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        painel.add(painelBotoes, gbc);
        
        add(painel);
        
        getRootPane().setDefaultButton(btnSalvar);
    }
    
    private void preencherCampos() {
        txtNome.setText(cliente.getNome());
        txtEmail.setText(cliente.getEmail());
        txtTelefone.setText(cliente.getTelefone());
        txtEmpresa.setText(cliente.getEmpresa());
        txtObservacoes.setText(cliente.getObservacoes());
    }
    
    private void salvar() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "O campo Nome é obrigatório.",
                "Validação", JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            return;
        }
        
        try {
            if (cliente == null) {
                cliente = new Cliente();
                cliente.setDataCadastro(new Date());
            }
            
            cliente.setNome(txtNome.getText().trim());
            cliente.setEmail(txtEmail.getText().trim());
            cliente.setTelefone(txtTelefone.getText().trim());
            cliente.setEmpresa(txtEmpresa.getText().trim());
            cliente.setObservacoes(txtObservacoes.getText().trim());
            
            if (cliente.getId() == 0) {
                clienteDAO.inserir(cliente);
            } else {
                clienteDAO.atualizar(cliente);
            }
            
            operacaoRealizada = true;
            dispose();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao salvar cliente: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isOperacaoRealizada() {
        return operacaoRealizada;
    }
}