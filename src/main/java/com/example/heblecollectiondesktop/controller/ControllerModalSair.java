package com.example.heblecollectiondesktop.controller;

import java.io.IOException;
import java.net.URL;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControllerModalSair {

    @FXML
    private void acaoDeslogar(ActionEvent event) {
        try {

            Stage modalStage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            Stage dashboardStage = (Stage) modalStage.getOwner();


            URL url = getClass().getResource("/com/example/heblecollectiondesktop/login.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/Login.fxml");
            }


            if (url == null) {
                System.err.println("Erro: Arquivo FXML de login não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            modalStage.close();

            if (dashboardStage != null) {
                dashboardStage.setScene(new Scene(root));
                dashboardStage.setTitle("Heble Collection - Login");
                dashboardStage.show();
            } else {
                modalStage.setScene(new Scene(root));
                modalStage.setTitle("Heble Collection - Login");
                modalStage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acaoSairSistema(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void acaoCancelar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}