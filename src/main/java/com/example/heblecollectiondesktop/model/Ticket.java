package com.example.heblecollectiondesktop.model;

public class Ticket {
    private int id;
    private String assunto;
    private Tipo tipo;
    private String descricao;
    private status status;
    private int funcionarioId;

    public Ticket() {}

    public Ticket(int id, String assunto, Tipo tipo, String descricao, status status, int funcionarioId) {
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

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public status getStatus() { return status; }
    public void setStatus(status status) { this.status = status; }

    public int getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(int funcionarioId) { this.funcionarioId = funcionarioId; }
}