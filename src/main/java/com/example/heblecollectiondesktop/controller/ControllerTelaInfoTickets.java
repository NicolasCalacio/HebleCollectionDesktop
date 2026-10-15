package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Ticket;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ControllerTelaInfoTickets {

    @FXML
    private Label lblAssunto;
    @FXML
    private Label lblId;
    @FXML
    private Label lblStatus;
    @FXML
    private TextArea txtDescricao;
    @FXML
    private Button btnEditarSenha;



    public void setTicket(Ticket ticket) {
        if (ticket == null) return;
       if (ticket.getTipo()=="Senha"){
           Boolean isSenha=true;
       }
       if(btnEditarSenha!=null){
           btnEditarSenha.isVisible(isSenha)
       }
        lblId.setText("ID: " + ticket.getId());
        lblAssunto.setText("Assunto: " + ticket.getAssunto());

        String statusAtual = ticket.getStatus() != null ? ticket.getStatus() : "indefinido";

        lblStatus.setText("Estado: " + statusAtual);

        txtDescricao.setText(ticket.getDescricao());

    }
}