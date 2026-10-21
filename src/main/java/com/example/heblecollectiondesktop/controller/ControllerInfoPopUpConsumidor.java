package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Consumidores;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ControllerInfoPopUpConsumidor {

    @FXML
    private Label lblId;
    @FXML
    private Label lblNome;
    @FXML
    private Label lblCpf;

    @FXML
    private Label lblEmail;
    @FXML
    private Label lblTelefone;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblCep;
    @FXML
    private Label lblEndereco;
    @FXML
    private Label lblDataNascimento;



    public void SetConsumidor(Consumidores consumidores){
        if (consumidores == null){return;}
        lblId.setText("ID: "+ consumidores.getIDconsumidor());
        lblNome.setText("NOME COMPLETO: "+ consumidores.getNomconsumidor());
        lblCpf.setText("CPF: "+ consumidores.getCpfconsumidor());
        lblCep.setText("CEP: " + consumidores.getCepconsumidor());
        lblTelefone.setText("TELEFONE: " + consumidores.getCelularconsumidor());
        lblEndereco.setText("ENDEREÇO: " + consumidores.getEnderecoconsumidor());
        lblStatus.setText("STATUS: "+ consumidores.getStatusconsumidor());
        lblEmail.setText("EMAIL: " + consumidores.getEmailconsumidor());
        lblDataNascimento.setText("DATA DE NASCIMENTO: " + consumidores.getDataNascconsumidor());

    }
}
