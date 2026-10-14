package com.example.heblecollectiondesktop.model;

import java.util.Date;

public class Cupom {
    private int id;
    private String codigo;
    private double descontoPercentual;
    private double valorMinimo; // "A partir de N reais"
    private boolean ativo;
    private Date validade;

    public Cupom() {}

    public Cupom(int id, String codigo, double descontoPercentual, double valorMinimo, boolean ativo, Date validade) {
        this.id = id;
        this.codigo = codigo;
        this.descontoPercentual = descontoPercentual;
        this.valorMinimo = valorMinimo;
        this.ativo = ativo;
        this.validade = validade;
    }


    public boolean isValidoParaValor(double valorTotalCompra) {
        return this.ativo && valorTotalCompra >= this.valorMinimo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public double getDescontoPercentual() { return descontoPercentual; }
    public void setDescontoPercentual(double descontoPercentual) { this.descontoPercentual = descontoPercentual; }

    public double getValorMinimo() { return valorMinimo; }
    public void setValorMinimo(double valorMinimo) { this.valorMinimo = valorMinimo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Date getValidade() { return validade; }
    public void setValidade(Date validade) { this.validade = validade; }
}