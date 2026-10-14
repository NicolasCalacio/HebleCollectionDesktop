package com.example.heblecollectiondesktop.model;

public class Ticket {
    private int id;
    private String assunto;
    private String descricao;
    private boolean status;
    private int funcionarioId;

    public String getFuncionarioMatricula() {
        return funcionarioMatricula;
    }

    public void setFuncionarioMatricula(String funcionarioMatricula) {
        this.funcionarioMatricula = funcionarioMatricula;
    }

    private String funcionarioMatricula;

    public Ticket(int id, String assunto, String descricao, boolean status, int funcionarioId) {
        this.id = id;
        this.assunto = assunto;
        this.descricao = descricao;
        this.status = status;
        this.funcionarioId = funcionarioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }
}