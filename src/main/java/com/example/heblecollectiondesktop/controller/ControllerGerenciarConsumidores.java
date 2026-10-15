package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Consumidores;
import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class ControllerGerenciarConsumidores {
    @FXML
    private TableView <Consumidores> tabelaConsumidores;
    @FXML
    private TableColumn<Consumidores , Integer> colid;
    @FXML
    private TableColumn<Consumidores , String> colNome;
    @FXML
    private TableColumn<Consumidores , Integer> colCpf;
    @FXML
    private TableColumn<Consumidores , String> colStatus;
    private Pane containerCentral;

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    public void setContainerCentral(Pane containerCentral) {
        this.containerCentral = containerCentral;
    }

    private Funcionario funcionarioLogado;
    private Runnable acaovoltar;
    public void setAcaovoltar(Runnable acaovoltar){
        this.acaovoltar=acaovoltar;
    }
    @FXML
    private void voltarAoHub(ActionEvent event) {
       if(acaovoltar!=null){
           acaovoltar.run();
       }
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}


