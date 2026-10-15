package com.example.heblecollectiondesktop.controller;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControllerTicketsPendentes {

    @FXML
    private TableView<Ticket> TabelaTickets;
    @FXML
    private TableColumn<Ticket, Integer> colID;
    @FXML
    private TableColumn<Ticket, String> colTipo;
    @FXML
    private TableColumn<Ticket, String> colAssunto;
    @FXML
    private TableColumn<Ticket, String> colDescricao;
    @FXML
    private TableColumn<Ticket, String> colStatus;

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
        if (colTipo != null) colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        if (colAssunto != null) colAssunto.setCellValueFactory(new PropertyValueFactory<>("assunto"));
        if (colDescricao != null) colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        if (colStatus != null) colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        configurarDuploClique();
        carregarTickets();
    }

    @FXML
    public void voltarAoHub(ActionEvent event) {
        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/ticketsHub.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/TicketsHub.fxml");
            }
            if (url == null) {
                mostrarAlerta("Erro FXML", "Arquivo ticketsHub.fxml não encontrado nos recursos.", Alert.AlertType.ERROR);
                return;
            }
            System.out.println(url.getPath());
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
            } else if (TabelaTickets != null && TabelaTickets.getScene() != null) {
                Stage palcoAtual = (Stage) TabelaTickets.getScene().getWindow();
                palcoAtual.setScene(new Scene(hubView));
                palcoAtual.setTitle("Hub de Tickets");
                palcoAtual.show();
            } else {
                mostrarAlerta("Erro de Navegação", "Não foi possível identificar o container ou a janela atual.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Navegação", "Falha ao retornar ao hub: " + e.getMessage(), Alert.AlertType.ERROR);
        }
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

    public void carregarTickets() {
        try {
            List<Ticket> listaBanco = ticketsPendentesDao.listarTodos();

            List<Ticket> pendentes = listaBanco.stream()
                    .filter(t -> t != null && t.getStatus() != null && t.getStatus().equalsIgnoreCase("pendente"))
                    .collect(Collectors.toList());

            ObservableList<Ticket> lista = FXCollections.observableArrayList(pendentes);
            if (TabelaTickets != null) {
                TabelaTickets.setItems(lista);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Conexão", "Não foi possível carregar os tickets do banco de dados: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void AprovarTicket() {
        Ticket selecionado = TabelaTickets.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Atenção", "Selecione um ticket na tabela.", Alert.AlertType.WARNING);
            return;
        }

        boolean sucesso = ticketsPendentesDao.atualizarStatus(selecionado.getId(), "andamento");

        if (sucesso) {
            mostrarAlerta("Sucesso", "Ticket #" + selecionado.getId() + " movido para 'Em Andamento' com sucesso!", Alert.AlertType.INFORMATION);
            carregarTickets();
        } else {
            mostrarAlerta("Erro", "Não foi possível atualizar o status do ticket selecionado.", Alert.AlertType.ERROR);
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
            Parent subtela = loader.load();

            ControllerConfirmarRejeicao controllerRejeicao = loader.getController();
            if (controllerRejeicao != null) {
                controllerRejeicao.setTicketSelecionado(selecionado);
            }

            Button btnClicado = (Button) event.getSource();
            Stage janelaAtual = (Stage) btnClicado.getScene().getWindow();

            Stage subjanela = new Stage();
            subjanela.initOwner(janelaAtual);
            subjanela.initModality(Modality.WINDOW_MODAL);
            subjanela.setScene(new Scene(subtela));
            subjanela.setTitle("Rejeitar Ticket");
            subjanela.setResizable(false);
            subjanela.showAndWait();

            carregarTickets();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a sub-tela de rejeição: " + e.getMessage(), Alert.AlertType.ERROR);
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