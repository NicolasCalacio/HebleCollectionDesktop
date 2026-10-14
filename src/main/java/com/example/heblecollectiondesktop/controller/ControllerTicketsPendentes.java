package com.example.heblecollectiondesktop.controller;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class ControllerTicketsPendentes {

    @FXML
    private TableView<Ticket> TabelaTickets;
    @FXML
    private TableColumn<Ticket, Integer> colID;
    @FXML
    private TableColumn<Ticket, String> colAssunto;
    @FXML
    private TableColumn<Ticket, String> colDescricao;
    @FXML
    private TableColumn<Ticket, String> colStatus; // Alterado de Boolean para String

    private Pane containerCentral;
    private Funcionario funcionarioLogado;
    private final TicketDAO ticketsPendentesDao = new TicketDAO();

    public void setContainerCentral(Pane containerCentral) {
        this.containerCentral = containerCentral;
    }

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    @FXML
    public void initialize() {
        if (colID != null) colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colAssunto != null) colAssunto.setCellValueFactory(new PropertyValueFactory<>("assunto"));
        if (colDescricao != null) colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        configurarDuploClique();
        carregarTickets();
    }

    private void configurarDuploClique() {
        if (TabelaTickets == null) return;

        TabelaTickets.setRowFactory(tv -> {
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
            List<Ticket> listaBanco = ticketsPendentesDao.listarTodos();

            List<Ticket> pendentes = listaBanco.stream()
                    .filter(t -> t.getStatus() != null && t.getStatus().equalsIgnoreCase("pendente"))
                    .collect(Collectors.toList());

            ObservableList<Ticket> lista = FXCollections.observableArrayList(pendentes);
            if (TabelaTickets != null) {
                TabelaTickets.setItems(lista);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Conexão", "Não foi possível carregar os tickets: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void AprovarTicket() {
        Ticket selecionado = TabelaTickets.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Atenção", "Selecione um ticket na tabela.", Alert.AlertType.WARNING);
            return;
        }

        // Atualiza o status para "andamento" (ou "fechado", conforme sua preferência de fluxo)
        boolean sucesso = ticketsPendentesDao.atualizarStatus(selecionado.getId(), "andamento");

        if (sucesso) {
            mostrarAlerta("Sucesso", "Ticket #" + selecionado.getId() + " aprovado/movido para andamento com sucesso!", Alert.AlertType.INFORMATION);
            carregarTickets();
        } else {
            mostrarAlerta("Erro", "Não foi possível aprovar o ticket selecionado.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void RejeitarTicket(ActionEvent event) {
        Ticket selecionado = TabelaTickets.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Atenção", "Selecione um ticket na tabela.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/ConfirmarRejeicao.fxml"));
            Parent subtela1 = loader.load();

            ControllerConfirmarRejeicao controllerPai = loader.getController();
            controllerPai.setTicketSelecionado(selecionado);

            Button btnClicado1 = (Button) event.getSource();
            Stage janelaAtual = (Stage) btnClicado1.getScene().getWindow();

            Stage subjanela1 = new Stage();
            subjanela1.initOwner(janelaAtual);
            subjanela1.initModality(Modality.WINDOW_MODAL);
            subjanela1.setScene(new Scene(subtela1));
            subjanela1.setTitle("Rejeitar Ticket");
            subjanela1.setResizable(false);
            subjanela1.showAndWait();

            carregarTickets();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a sub-tela: " + e.getMessage(), Alert.AlertType.ERROR);
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
                mostrarAlerta("Erro FXML", "Arquivo TicketsHub.fxml não encontrado.", Alert.AlertType.ERROR);
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
            mostrarAlerta("Erro de Navegação", "Falha ao retornar ao hub: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void subjanela(ActionEvent event){
        Ticket TicketSelecionado = TabelaTickets.getSelectionModel().getSelectedItem();
        if (TicketSelecionado == null) {
            mostrarAlerta("Aviso", "Por favor, selecione um ticket na tabela para Rejeitar.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/RejeitarTickets.fxml"));
            Parent subtela = loader.load();
            ControllerConfirmarRejeicao ControllerRejeicao = loader.getController();
            ControllerRejeicao.setTicketSelecionado(TicketSelecionado);

            Button Btnclicado1 = (Button) event.getSource();
            Stage JanelaAtual = (Stage) Btnclicado1.getScene().getWindow();

            Stage SubJanela1 = new Stage();
            SubJanela1.initOwner(JanelaAtual);
            SubJanela1.initModality(Modality.WINDOW_MODAL);
            SubJanela1.setScene(new Scene(subtela));
            SubJanela1.setTitle("Rejeitar Tickets");
            SubJanela1.setResizable(false);
            SubJanela1.showAndWait();
            carregarTickets();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a sub-tela: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}