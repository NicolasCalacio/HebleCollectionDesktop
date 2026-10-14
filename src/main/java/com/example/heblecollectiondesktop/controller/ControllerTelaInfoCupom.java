package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Cupom;
import javafx.fxml.FXML;
import javafx.scene.control.Label;



public class ControllerTelaInfoCupom {

    @FXML
    private Label lblCodigoCupom;
    @FXML
    private Label lblId;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblValorDesconto;
    @FXML
    private Label lblValorMinimo;
    @FXML
    private Label lblValidade;


    public void setCupom(Cupom cupom) {
        if (cupom == null) return;

        lblCodigoCupom.setText("Codigo do Cupom: " + cupom.getCodigo());
        lblId.setText("ID: " + cupom.getId());

        String statusTexto = cupom.isAtivo() ? "Ativo" : "Inativo";
        lblStatus.setText("Status: " + statusTexto);
        lblValorDesconto.setText("Valor do Desconto:" + cupom.getDescontoPercentual());
        lblValidade.setText("Validade:" + cupom.getValidade());
        lblValorMinimo.setText("Valor Minimo:" + cupom.getValorMinimo());



    }

}