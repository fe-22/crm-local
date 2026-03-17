package com.crm.model;

import java.util.Date;

public class Cliente {
    private int id;
    private String nome;
    private String email;
    private String telefone;
    private String empresa;
    private Date dataCadastro;
    private String observacoes;
    private boolean ativo;
    
    // Construtores
    public Cliente() {
        this.ativo = true;
        this.dataCadastro = new Date();
    }
    
    public Cliente(int id, String nome, String email, String telefone, String empresa) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.empresa = empresa;
        this.ativo = true;
        this.dataCadastro = new Date();
    }
    
    // Getters e Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getEmpresa() {
        return empresa;
    }
    
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
    
    public Date getDataCadastro() {
        return dataCadastro;
    }
    
    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public boolean isAtivo() {
        return ativo;
    }
    
    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
@Override
public String toString() {
    if (empresa != null && !empresa.trim().isEmpty()) {
        return empresa + " - " + nome;
    }
    return nome;
}
}
