package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.TicketDAO;
import com.example.heblecollectiondesktop.model.Ticket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ControllerSuporteFuncionario {
    @FXML
    private Button btnAbrirChamado;
    @FXML
    private TextField txMatricula;
    @FXML
    private ComboBox<String> cbTipoSuporte;
    @FXML
    private TextField txDescricao;
    TicketDAO ticketDAO= new TicketDAO();
    private void Initializable(){
        cbTipoSuporte.getItems().setAll(
                "Perdeu a senha","Problemas no login","Faltando login do funcionario"
        );
    }
    private void AbrirTicket(ActionEvent event){
    String matricula=txMatricula.getText() != null ? txMatricula.getText().trim() : "";
    String detalhes=txDescricao.getText() != null ? txDescricao.getText().trim() : "";
    String tipo=cbTipoSuporte.getValue() != null ? cbTipoSuporte.getValue().trim(): "";
    if(matricula.isEmpty() || tipo == null){
        System.out.println("Preencha todos os campos");
        return;
        }
        Ticket novoTicket= new Ticket(1,matricula,detalhes,false,1);
        ticketDAO.Adicionar(novoTicket);
    }
}
