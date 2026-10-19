package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.ConsumidoresDAO;

import com.example.heblecollectiondesktop.model.Consumidores;

import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


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
    @FXML
    public void IrBanirConsumidor(ActionEvent event){
        Consumidores consumidorselecionado = tabelaConsumidores.getSelectionModel().getSelectedItem();
        if (consumidorselecionado == null ){
            mostrarAlerta(Alert.AlertType.WARNING,"Aviso", "Por favor, selecione um consumidor na tabela para Banir.");
            return;
        }
        try {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/BanirConsumidor.fxml"));
            Parent subtela = loader.load();

            ControllerBanirConsumidor controllerBanir1 = loader.getController();
            controllerBanir1.Setconsumidor(consumidorselecionado);

            Button btnClicado1 = (Button) event.getSource();
            Stage janelaAtual = (Stage) btnClicado1.getScene().getWindow();

            Stage SubJanela = new Stage();
            SubJanela.setScene(new Scene(subtela));
            SubJanela.initModality(Modality.APPLICATION_MODAL);
            SubJanela.initOwner(janelaAtual);
            SubJanela.setTitle("Banir Consumidor");
            SubJanela.setResizable(false);
            SubJanela.showAndWait();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,"Erro de Carregamento", "Falha ao carregar a sub-tela: " + e.getMessage());
        }
    }
    @FXML
    public void IrEditarConsumidor(ActionEvent event){
        Consumidores consumidorselecionado = tabelaConsumidores.getSelectionModel().getSelectedItem();
        if (consumidorselecionado == null ){
            mostrarAlerta(Alert.AlertType.WARNING,"Aviso", "Por favor, selecione um consumidor na tabela para Banir.");
            return;
        }
<<<<<<< HEAD
        String SomenteBanido = consumidorselecionado.getStatusconsumidor();
        if (SomenteBanido.equalsIgnoreCase("Banido")){
=======

        if (consumidorselecionado.getStatusconsumidor().equalsIgnoreCase("Banido")){
>>>>>>> origin/feature/branches-atualizadas
            try {


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/EditarConsumidor.fxml"));
                Parent subtela1 = loader.load();

                ControllerEditarConsumidor controllerEditar = loader.getController();
                controllerEditar.setConsumidorEditar(consumidorselecionado);

                Button btnClicado2 = (Button) event.getSource();
                Stage janelaAtual1 = (Stage) btnClicado2.getScene().getWindow();

                Stage SubJanela1 = new Stage();
                SubJanela1.setScene(new Scene(subtela1));
                SubJanela1.initModality(Modality.APPLICATION_MODAL);
                SubJanela1.initOwner(janelaAtual1);
                SubJanela1.setTitle("Editar Consumidor");
                SubJanela1.setResizable(false);
                SubJanela1.showAndWait();
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR,"Erro de Carregamento", "Falha ao carregar a sub-tela: " + e.getMessage());
            }
        }else {
            mostrarAlerta(Alert.AlertType.WARNING,"Somente Consumidores Banidos", "Selecione somente um consumidor que esteja Banido");
            return;
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