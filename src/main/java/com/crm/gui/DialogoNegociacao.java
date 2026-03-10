package com.crm.gui;

import com.crm.dao.ClienteDAO;
import com.crm.dao.NegociacaoDAO;
import com.crm.model.Cliente;
import com.crm.model.Negociacao;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DialogoNegociacao extends JDialog {
    private JComboBox<Cliente> cbCliente;
    private JTextField txtTitulo;
    private JTextField txtValor;
    private JTextField txtDataPrevista;
    private JComboBox<String> cbEtapa;
    private JSpinner spProbabilidade;
    private JTextArea txtDescricao;
    private JButton btnSalvar;
    private JButton btnCancelar;
    private Negociacao negociacao;
    private NegociacaoDAO negociacaoDAO;
    private ClienteDAO clienteDAO;
    private boolean operacaoRealizada = false;

    public DialogoNegociacao(Window parent, Negociacao negociacao, NegociacaoDAO negociacaoDAO, ClienteDAO clienteDAO) {
        super(parent, negociacao == null ? "Nova Negociação" : "Editar Negociação", ModalityType.APPLICATION_MODAL);
        this.negociacao = negociacao;
        this.negociacaoDAO = negociacaoDAO;
        this.clienteDAO = clienteDAO;

        setSize(500, 450);
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
        // Título
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Título:*"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtTitulo = new JTextField(20);
        painel.add(txtTitulo, gbc);

        linha++;
        // Valor
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Valor (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtValor = new JTextField(20);
        painel.add(txtValor, gbc);

        linha++;
        // Etapa
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Etapa:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cbEtapa = new JComboBox<>(new String[]{"Lead", "Qualificado", "Proposta", "Negociação", "Fechado", "Perdido"});
        painel.add(cbEtapa, gbc);

        linha++;
        // Probabilidade
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Probabilidade (%):"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        spProbabilidade = new JSpinner(new SpinnerNumberModel(10, 0, 100, 5));
        painel.add(spProbabilidade, gbc);

        linha++;
        // Data Prevista
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Data Prevista:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtDataPrevista = new JTextField(10);
        painel.add(txtDataPrevista, gbc);

        linha++;
        // Descrição
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        painel.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(txtDescricao);
        painel.add(scroll, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painel, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        if (negociacao != null) {
            preencherCampos();
        }

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        setModal(true); // Garantir que é modal
    }

    private void preencherCampos() {
        for (int i = 0; i < cbCliente.getItemCount(); i++) {
            if (cbCliente.getItemAt(i).getId() == negociacao.getClienteId()) {
                cbCliente.setSelectedIndex(i);
                break;
            }
        }
        txtTitulo.setText(negociacao.getTitulo());
        txtValor.setText(String.valueOf(negociacao.getValor()));
        cbEtapa.setSelectedItem(negociacao.getEtapa());
        spProbabilidade.setValue(negociacao.getProbabilidade());
        if (negociacao.getDataPrevista() != null) {
            txtDataPrevista.setText(new SimpleDateFormat("dd/MM/yyyy").format(negociacao.getDataPrevista()));
        }
        txtDescricao.setText(negociacao.getDescricao());
    }

    private void salvar() {
        try {
            Cliente cliente = (Cliente) cbCliente.getSelectedItem();
            if (cliente == null || txtTitulo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cliente e Título são obrigatórios.");
                return;
            }

            if (negociacao == null) {
                negociacao = new Negociacao();
                negociacao.setDataInicio(new Date());
            }

            negociacao.setClienteId(cliente.getId());
            negociacao.setTitulo(txtTitulo.getText().trim());
            try {
                String valorStr = txtValor.getText().replace(",", ".").trim();
                negociacao.setValor(valorStr.isEmpty() ? 0.0 : Double.parseDouble(valorStr));
            } catch (NumberFormatException e) {
                negociacao.setValor(0.0);
            }
            negociacao.setEtapa((String) cbEtapa.getSelectedItem());
            negociacao.setProbabilidade((Integer) spProbabilidade.getValue());
            negociacao.setDescricao(txtDescricao.getText());

            if (!txtDataPrevista.getText().trim().isEmpty()) {
                negociacao.setDataPrevista(new SimpleDateFormat("dd/MM/yyyy").parse(txtDataPrevista.getText()));
            } else {
                negociacao.setDataPrevista(null);
            }

            if (negociacao.getId() == 0) {
                negociacaoDAO.inserir(negociacao);
            } else {
                negociacaoDAO.atualizar(negociacao);
            }

            operacaoRealizada = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    public boolean isOperacaoRealizada() {
        return operacaoRealizada;
    }
}