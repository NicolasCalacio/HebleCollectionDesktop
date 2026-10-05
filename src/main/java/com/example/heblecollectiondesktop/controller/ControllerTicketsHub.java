package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

public class ControllerTicketsHub {

    @FXML
    private Pane containerCentral;

    private Funcionario funcionarioLogado;

    public void setContainerCentral(Pane containerCentral) {
        this.containerCentral = containerCentral;
    }

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    @FXML
    private void abrirTicketsPendentes(ActionEvent event) {

        carregarView("/com/example/heblecollectiondesktop/view/GerenciarTicketsPendentes.fxml", loader -> {
            ControllerTicketsPendentes controller = loader.getController();
            if (controller != null) {
                controller.setContainerCentral(containerCentral);
                controller.setFuncionarioLogado(funcionarioLogado);
            }
        });
    }

    @FXML
    private void abrirTicketsEmAndamento(ActionEvent event) {
        carregarView("/com/example/heblecollectiondesktop/view/TicketsAndamento.fxml", loader -> {
            ControllerTicketsAndamento controller = loader.getController();
            if (controller != null) {
                controller.setContainerCentral(containerCentral);
                controller.setFuncionarioLogado(funcionarioLogado);
            }
        });
    }

    @FXML
    private void abrirTicketsFechados(ActionEvent event) {
        carregarView("/com/example/heblecollectiondesktop/view/ticketFechados.fxml", loader -> {
            ControllerTicketFechados controller = loader.getController();
            if (controller != null) {
                controller.setContainerCentral(containerCentral);
                controller.setFuncionarioLogado(funcionarioLogado);
            }
        });
    }

    private void carregarView(String caminhoFxml, ControllerInitializer initializer) {
        try {
            URL resource = getClass().getResource(caminhoFxml);
            if (resource == null) {
                System.err.println("ERRO: O arquivo FXML não foi encontrado no caminho: " + caminhoFxml);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();

            if (initializer != null) {
                initializer.initialize(loader);
            }

            if (containerCentral != null) {
                containerCentral.getChildren().setAll(view);
            } else {
                System.err.println("AVISO: containerCentral está nulo.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface ControllerInitializer {
        void initialize(FXMLLoader loader);
    }
}