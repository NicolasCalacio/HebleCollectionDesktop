package com.example.heblecollectiondesktop.model;

import java.math.BigDecimal;

public class ItemVenda {
    private int iditem;
    private int vendaId;
    private int produtoId;
    private String nomeProduto;
    private String descricaoProduto;
    private int quantidade;
    private BigDecimal precoUnitario;

    public ItemVenda() {}

    public ItemVenda(int produtoId, String nomeProduto,String descricaoProduto, int quantidade, BigDecimal precoUnitario) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.nomeProduto = nomeProduto;
        this.descricaoProduto = descricaoProduto;
    }


    public int getIditem() { return iditem; }
    public void setIditem(int iditem) { this.iditem = iditem; }

    public int getVendaId() { return vendaId; }
    public void setVendaId(int vendaId) { this.vendaId = vendaId; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public String getDescricaoProduto() {return descricaoProduto;}
    public void setDescricaoProduto(String descricaoProduto) {this.descricaoProduto = descricaoProduto;}
}