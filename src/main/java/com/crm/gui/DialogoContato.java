package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.dao.ContatoDAO;
import com.crm.model.Cliente;
import com.crm.model.Contato;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DialogoContato extends JDialog {
    private JComboBox<Cliente> cbCliente;
    private JTextField txtDataContato;
    private JComboBox<String> cbTipo;
    private JComboBox<String> cbStatus;
    private JTextArea txtDescricao;
    private JTextField txtProximoContato;
    private JButton btnSalvar;
    private JButton btnCancelar;
    private Contato contato;
    private ContatoDAO contatoDAO;
    private ClienteDAO clienteDAO;
    private boolean operacaoRealizada = false;

    public DialogoContato(Window parent, Contato contato, ContatoDAO contatoDAO, ClienteDAO clienteDAO) {
        super(parent, contato == null ? "Novo Contato" : "Editar Contato", ModalityType.APPLICATION_MODAL);
        this.contato = contato;
        this.contatoDAO = contatoDAO;
        this.clienteDAO = clienteDAO;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int linha = 0;

        // Cliente
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Cliente:*"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        try {
            List<Cliente> clientes = clienteDAO.listarTodos();
            cbCliente = new JComboBox<>(clientes.toArray(new Cliente[0]));
        } catch (SQLException e) {
            cbCliente = new JComboBox<>();
        }
        painel.add(cbCliente, gbc);

        linha++;
        // Data do Contato
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Data Contato:*"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtDataContato = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        painel.add(txtDataContato, gbc);

        linha++;
        // Tipo
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cbTipo = new JComboBox<>(new String[]{"Telefone", "Email", "Reunião", "Visita"});
        painel.add(cbTipo, gbc);

        linha++;
        // Status
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cbStatus = new JComboBox<>(new String[]{"Realizado", "Pendente", "Cancelado"});
        painel.add(cbStatus, gbc);

        linha++;
        // Descrição
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        painel.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtDescricao = new JTextArea(4, 20);
        txtDescricao.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(txtDescricao);
        painel.add(scroll, gbc);

        linha++;
        // Próximo Contato
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Próximo Contato:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtProximoContato = new JTextField();
        painel.add(txtProximoContato, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painel, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        if (contato != null) {
            preencherCampos();
        }

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        setModal(true);
    }

    private void preencherCampos() {
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getId() == contato.getClienteId()) {
                cbCliente.setSelectedIndex(i);
                break;
            }
        }
        txtDataContato.setText(new SimpleDateFormat("dd/MM/yyyy").format(contato.getDataContato()));
        cbTipo.setSelectedItem(contato.getTipo());
        cbStatus.setSelectedItem(contato.getStatus());
        txtDescricao.setText(contato.getDescricao());
        if (contato.getProximoContato() != null) {
            txtProximoContato.setText(new SimpleDateFormat("dd/MM/yyyy").format(contato.getProximoContato()));
        }
    }

    private void salvar() {
        try {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            if (cliente == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente.");
                return;
            }

            Date dataContato = new SimpleDateFormat("dd/MM/yyyy").parse(txtDataContato.getText());
            Date proximo = null;
            if (!txtProximoContato.getText().trim().isEmpty()) {
                proximo = new SimpleDateFormat("dd/MM/yyyy").parse(txtProximoContato.getText());
            }

            if (contato == null) contato = new Contato();
            contato.setClienteId(cliente.getId());
            contato.setDataContato(dataContato);
            contato.setTipo((String) cbTipo.getSelectedItem());
            contato.setStatus((String) cbStatus.getSelectedItem());
            contato.setDescricao(txtDescricao.getText());
            contato.setProximoContato(proximo);

            if (contato.getId() == 0) {
                contatoDAO.inserir(contato);
            } else {
                contatoDAO.atualizar(contato);
            }

            operacaoRealizada = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    public boolean isOperacaoRealizada() {
        return operacaoRealizada;
    }
}