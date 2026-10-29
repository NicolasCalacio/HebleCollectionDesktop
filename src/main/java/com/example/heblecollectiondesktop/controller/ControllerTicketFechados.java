package com.example.heblecollectiondesktop.controller;

import java.util.List;
import java.util.stream.Collectors;
import com.example.heblecollectiondesktop.database.TicketDAO;
import com.example.heblecollectiondesktop.model.Funcionario;
import com.example.heblecollectiondesktop.model.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ControllerTicketFechados {

    @FXML
    private TableView<Ticket> tbTicketsFechados;


    @FXML
    private TableColumn<Ticket, Integer> colId;

    @FXML
    private TableColumn<Ticket, String> colAssunto;

    @FXML
    private TableColumn<Ticket, String> colDescricao;

    @FXML
    private TableColumn<Ticket, String> colStatus;

    private final TicketDAO ticketDAO = new TicketDAO();
    private Pane containerCentral;
    private Funcionario funcionarioLogado;

    public void setContainerCentral(Pane containerCentral) {
        this.containerCentral = containerCentral;
    }

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    @FXML
    public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colAssunto != null) colAssunto.setCellValueFactory(new PropertyValueFactory<>("assunto"));
        if (colDescricao != null) colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        configurarDuploClique();
        carregarTickets();
    }

    private void configurarDuploClique() {
        if (tbTicketsFechados == null) return;

        tbTicketsFechados.setRowFactory(tv -> {
            TableRow<Ticket> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Ticket ticketSelecionado = row.getItem();
                    abrirTelaInfo(ticketSelecionado);
                }
            });
            return row;
        });
    }

    private void abrirTelaInfo(Ticket ticket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/InfoPopUp.fxml"));
            Parent root = loader.load();

            ControllerTelaInfoTickets modalController = loader.getController();
            modalController.setTicket(ticket);

            Stage stage = new Stage();
            stage.setTitle("Detalhes do Ticket #" + ticket.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void carregarTickets() {
        try {
            List<Ticket> listaBanco = ticketDAO.listarTodos();

            List<Ticket> finalizados = listaBanco.stream()
                    .filter(t -> t != null && t.getStatus() != null &&
                            (t.getStatus().name().equalsIgnoreCase("finalizado") ||
                                    t.getStatus().name().equalsIgnoreCase("recusado")))
                    .collect(Collectors.toList());

            ObservableList<Ticket> listaObservable = FXCollections.observableArrayList(finalizados);
            if (tbTicketsFechados != null) {
                tbTicketsFechados.setItems(listaObservable);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro ao carregar tickets", "Não foi possível carregar os dados do banco: " + e.getMessage());
        }
    }

    @FXML
    private void voltarAoHub(ActionEvent event) {
        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/TicketsHub.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/ticketsHub.fxml");
            }
            if (url == null) {
                mostrarAlertaErro("Erro FXML", "Arquivo TicketsHub.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent hubView = loader.load();

            ControllerTicketsHub controllerHub = loader.getController();
            if (controllerHub != null) {
                controllerHub.setContainerCentral(containerCentral);
                if (funcionarioLogado != null) {
                    controllerHub.setFuncionarioLogado(funcionarioLogado);
                }
            }

            if (containerCentral != null) {
                containerCentral.getChildren().setAll(hubView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaErro("Erro de Navegação", "Falha ao retornar ao hub: " + e.getMessage());
        }
    }

    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}