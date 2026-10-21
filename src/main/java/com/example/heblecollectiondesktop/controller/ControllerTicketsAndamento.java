package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.LogsDAO;
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

public class ControllerTicketsAndamento {

    @FXML private TableView<Ticket> ticketsEmAndamento;
    @FXML private TableColumn<Ticket, Integer> colID;
    @FXML private TableColumn<Ticket, String> colAssunto;
    @FXML private TableColumn<Ticket, String> colDescricao;
    @FXML private TableColumn<Ticket, String> colStatus;
    @FXML private TableColumn<Ticket, Integer> colFuncionarioId;

    private final TicketDAO ticketDAO = new TicketDAO();
    private final LogsDAO logsDAO = new LogsDAO();
    private final ObservableList<Ticket> listaTickets = FXCollections.observableArrayList();

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
        if (colID != null) colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colAssunto != null) colAssunto.setCellValueFactory(new PropertyValueFactory<>("assunto"));
        if (colDescricao != null) colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (colFuncionarioId != null) colFuncionarioId.setCellValueFactory(new PropertyValueFactory<>("funcionarioId"));
        configurarDuploClique();
        carregarTickets();
    }

    public void carregarTickets() {
        try {
            List<Ticket> todos = ticketDAO.listarTodos();

            List<Ticket> emAndamento = todos.stream()
                    .filter(t -> t.getStatus() != null && t.getStatus().equalsIgnoreCase("andamento"))
                    .collect(Collectors.toList());

            listaTickets.setAll(emAndamento);
            if (ticketsEmAndamento != null) {
                ticketsEmAndamento.setItems(listaTickets);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível carregar os tickets: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void aprovarTicket(ActionEvent event) {
        Ticket ticketSelecionado = ticketsEmAndamento.getSelectionModel().getSelectedItem();

        if (ticketSelecionado == null) {
            mostrarAlerta("Atenção", "Selecione um ticket na tabela para concluir/aprovar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            boolean sucesso = ticketDAO.atualizarStatus(ticketSelecionado.getId(), "fechado");

            if (sucesso) {
                registrarLog("CONCLUSAO_TICKET", ticketSelecionado, "Ticket finalizado com sucesso.");
                mostrarAlerta("Sucesso", "Ticket #" + ticketSelecionado.getId() + " finalizado com sucesso!", Alert.AlertType.INFORMATION);
                carregarTickets();
            } else {
                mostrarAlerta("Erro", "Falha ao atualizar o status do ticket.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao aprovar ticket: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void negarTicket(ActionEvent event) {
        Ticket ticketSelecionado = ticketsEmAndamento.getSelectionModel().getSelectedItem();

        if (ticketSelecionado == null) {
            mostrarAlerta("Atenção", "Selecione um ticket na tabela para recusar.", Alert.AlertType.WARNING);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/ConfirmarNegaçãoTicket.fxml"));
            Parent subtela1 = loader.load();

            ControllerConfirmarNegar controllerPai = loader.getController();
            controllerPai.setTicketSelecionado(ticketSelecionado);

            Button btnClicado1 = (Button) event.getSource();
            Stage janelaAtual = (Stage) btnClicado1.getScene().getWindow();

            Stage subjanela1 = new Stage();
            subjanela1.initOwner(janelaAtual);
            subjanela1.initModality(Modality.WINDOW_MODAL);
            subjanela1.setScene(new Scene(subtela1));
            subjanela1.setTitle("Negar Ticket");
            subjanela1.setResizable(false);
            subjanela1.showAndWait();

            carregarTickets();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a sub-tela: ", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void deletarTicket(ActionEvent event) {
        Ticket ticketSelecionado = ticketsEmAndamento.getSelectionModel().getSelectedItem();

        if (ticketSelecionado == null) {
            mostrarAlerta("Atenção", "Selecione um ticket na tabela para deletar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            boolean sucesso = ticketDAO.deletar(ticketSelecionado.getId());

            if (sucesso) {
                registrarLog("EXCLUSAO_TICKET", ticketSelecionado, "Ticket removido do sistema.");
                mostrarAlerta("Sucesso", "Ticket #" + ticketSelecionado.getId() + " excluído com sucesso!", Alert.AlertType.INFORMATION);
                carregarTickets();
            } else {
                mostrarAlerta("Erro", "Não foi possível deletar o ticket.", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao deletar ticket: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void configurarDuploClique() {
        if (ticketsEmAndamento == null) return;

        ticketsEmAndamento.setRowFactory(tv -> {
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
            if (modalController != null) {
                modalController.setTicket(ticket);
            }

            Stage stage = new Stage();
            stage.setTitle("Detalhes do Ticket #" + ticket.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


            carregarTickets();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao abrir detalhes do ticket: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void registrarLog(String acao, Ticket ticket, String observacao) {
        String alvo = "Ticket #" + ticket.getId() + " - " + ticket.getAssunto();

        try {
            if (funcionarioLogado != null) {
                logsDAO.registrarLog(funcionarioLogado, acao, alvo, observacao);
            } else {
                logsDAO.registrarLog("SISTEMA", acao, alvo, observacao);
            }
        } catch (Exception e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
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

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}