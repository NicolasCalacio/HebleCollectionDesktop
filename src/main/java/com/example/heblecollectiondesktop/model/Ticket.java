package com.example.heblecollectiondesktop.model;

public class Ticket {
    private int id;
    private String assunto;
    private String tipo;
    private String descricao;
    private String status;
    private int funcionarioId;

    public Ticket() {}

    public Ticket(int id, String assunto, String tipo, String descricao, String status, int funcionarioId) {
        this.id = id;
        this.assunto = assunto;
        this.tipo = tipo;
        this.descricao = descricao;
        this.status = status;
        this.funcionarioId = funcionarioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }
}