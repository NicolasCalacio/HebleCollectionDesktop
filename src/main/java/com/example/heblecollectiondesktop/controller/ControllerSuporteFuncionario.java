package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.TicketDAO;
import com.example.heblecollectiondesktop.model.Ticket;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ControllerSuporteFuncionario {
    @FXML
    private Button btnAbrirChamado;
    @FXML
    private TextField txMatricula;

    @FXML
    private TextArea txDescricao;

    TicketDAO ticketDAO= new TicketDAO();

    @FXML
    private void abrirChamado(ActionEvent event){
    String matricula=txMatricula.getText() != null ? txMatricula.getText().trim() : "";
    String detalhes=txDescricao.getText() != null ? txDescricao.getText().trim() : "";


    if(matricula.isEmpty()){
        System.out.println("Preencha todos os campos");
        return;
        }
        Ticket novoTicket= new Ticket(1,matricula,detalhes,false,1);
        ticketDAO.Adicionar(novoTicket);
        aleta();
        voltar(event);
    }
    @FXML
    private void voltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/heblecollectiondesktop/view/login.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                    .getScene()
                    .getWindow();


            Scene novaCena = new Scene(root);
            stage.setScene(novaCena);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void aleta(){
        Alert alert= new Alert(Alert.AlertType.INFORMATION, "O ticket foi criado espere o seu problema ser resolvido");
    }
}
