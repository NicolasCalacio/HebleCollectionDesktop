package com.example.heblecollectiondesktop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Venda {
    private int idvenda;
    private int consumidorId;
    private Integer cupomId;
    private BigDecimal valorSubtotal;
    private BigDecimal valorFrete;
    private BigDecimal valorDesconto;
    private BigDecimal valorTotal;
    private String statusVenda;
    private LocalDateTime criadoEm;
    private List<ItemVenda> itens;

    public Venda() {}

    public Venda(int consumidorId, Integer cupomId, BigDecimal valorSubtotal, BigDecimal valorFrete, BigDecimal valorDesconto, BigDecimal valorTotal, String statusVenda, List<ItemVenda> itens) {
        this.consumidorId = consumidorId;
        this.cupomId = cupomId;
        this.valorSubtotal = valorSubtotal;
        this.valorFrete = valorFrete;
        this.valorDesconto = valorDesconto;
        this.valorTotal = valorTotal;
        this.statusVenda = statusVenda;
        this.itens = itens;
    }


    public int getIdvenda() { return idvenda; }
    public void setIdvenda(int idvenda) { this.idvenda = idvenda; }

    public int getConsumidorId() { return consumidorId; }
    public void setConsumidorId(int consumidorId) { this.consumidorId = consumidorId; }

    public Integer getCupomId() { return cupomId; }
    public void setCupomId(Integer cupomId) { this.cupomId = cupomId; }

    public BigDecimal getValorSubtotal() { return valorSubtotal; }
    public void setValorSubtotal(BigDecimal valorSubtotal) { this.valorSubtotal = valorSubtotal; }

    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getStatusVenda() { return statusVenda; }
    public void setStatusVenda(String statusVenda) { this.statusVenda = statusVenda; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }
}