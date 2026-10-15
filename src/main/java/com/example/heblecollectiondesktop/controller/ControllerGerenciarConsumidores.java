package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.ConsumidoresDAO;

import com.example.heblecollectiondesktop.model.Consumidores;

import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;



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
    private final ConsumidoresDAO consumidoresDAO = new ConsumidoresDAO();
    private final ObservableList<Consumidores> listaConsumidores = FXCollections.observableArrayList();

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

    @FXML
    public void initialize() {
        if (colid != null) colid.setCellValueFactory(new PropertyValueFactory<>("IDconsumidor"));
        if (colNome != null) colNome.setCellValueFactory(new PropertyValueFactory<>("nomconsumidor"));
        if (colCpf != null) colCpf.setCellValueFactory(new PropertyValueFactory<>("cpfconsumidor"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("statusconsumidor"));
        carregarTabela();
    }
    public void carregarTabela() {
        try {
            listaConsumidores.setAll(consumidoresDAO.listarConsumidores());
            if (tabelaConsumidores != null) {
                tabelaConsumidores.setItems(listaConsumidores);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao remover cupom: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}