package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Empresa;
import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ControllerExibirPerfil {

    @FXML
    private Label lblPerfil;
    @FXML
    private Label lblId;
    @FXML
    private Label lblCargo;
    private Funcionario funcionario;
    private Runnable acaovoltar;
    public void setAcaovoltar(Runnable acaovoltar){
        this.acaovoltar=acaovoltar;
    }
    public void setFuncionario(Funcionario funcionario){
        this.funcionario=funcionario;
        setLabel();
    }
    private void setLabel(){
        lblPerfil.setText("Perfil de "+ funcionario.getMatricula());
        lblId.setText("Id: "+ funcionario.getId());
        lblCargo.setText("Cargo:"+funcionario.getCargo().toString());
    }

    public void irparaEditarPerfil(ActionEvent event){


        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/EditarPerfil.fxml");
            if (url == null) {
                url = getClass().getResource("/view/editarPerfil.fxml");
            }

            if (url == null) {
                mostrarAlerta("Erro FXML", "Arquivo EditarEmpresas.fxml não encontrado.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent subtela1 = loader.load();

            ControllerEditarPerfil controllerEditar = loader.getController();
            controllerEditar.setPerfil(funcionario);

            Button btnClicado1 = (Button) event.getSource();
            Stage janelaAtual = (Stage) btnClicado1.getScene().getWindow();

            Stage subjanela1 = new Stage();
            subjanela1.initOwner(janelaAtual);
            subjanela1.initModality(Modality.WINDOW_MODAL);
            subjanela1.setScene(new Scene(subtela1));
            subjanela1.setTitle("Editar Perfil");
            subjanela1.setResizable(false);
            subjanela1.showAndWait();


        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a sub-tela: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void Voltar(ActionEvent event) {
        if(acaovoltar!= null){
            acaovoltar.run();
        }
    }
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}