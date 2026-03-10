package com.crm.model;

import java.util.Date;

public class Negociacao {
    private int id;
    private int clienteId;
    private String titulo;
    private String descricao;
    private double valor;
    private String etapa; // Lead, Qualificado, Proposta, Negociação, Fechado, Perdido
    private int probabilidade; // 0 a 100
    private Date dataInicio;
    private Date dataPrevista;
    private Date dataFechamento;
    private String motivoPerda;
    
    // Construtores
    public Negociacao() {
        this.dataInicio = new Date();
        this.etapa = "Lead";
        this.probabilidade = 10;
    }
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public double getValor() {
        return valor;
    }
    
    public void setValor(double valor) {
        this.valor = valor;
    }
    
    public String getEtapa() {
        return etapa;
    }
    
    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }
    
    public int getProbabilidade() {
        return probabilidade;
    }
    
    public void setProbabilidade(int probabilidade) {
        this.probabilidade = probabilidade;
    }
    
    public Date getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public Date getDataPrevista() {
        return dataPrevista;
    }
    
    public void setDataPrevista(Date dataPrevista) {
        this.dataPrevista = dataPrevista;
    }
    
    public Date getDataFechamento() {
        return dataFechamento;
    }
    
    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }
    
    public String getMotivoPerda() {
        return motivoPerda;
    }
    
    public void setMotivoPerda(String motivoPerda) {
        this.motivoPerda = motivoPerda;
    }
}
