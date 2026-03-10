package com.crm.model;

import java.util.Date;

public class Contato {
    private int id;
    private int clienteId;
    private Date dataContato;
    private String tipo; // Telefone, Email, Reunião, Visita
    private String descricao;
    private String status; // Realizado, Pendente, Cancelado
    private Date proximoContato;
    
    // Construtores
    public Contato() {
        this.dataContato = new Date();
        this.status = "Pendente";
    }
    
    public Contato(int id, int clienteId, Date dataContato, String tipo, String descricao) {
        this.id = id;
        this.clienteId = clienteId;
        this.dataContato = dataContato;
        this.tipo = tipo;
        this.descricao = descricao;
        this.status = "Pendente";
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
    
    public Date getDataContato() {
        return dataContato;
    }
    
    public void setDataContato(Date dataContato) {
        this.dataContato = dataContato;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getProximoContato() {
        return proximoContato;
    }
    
    public void setProximoContato(Date proximoContato) {
        this.proximoContato = proximoContato;
    }
}
