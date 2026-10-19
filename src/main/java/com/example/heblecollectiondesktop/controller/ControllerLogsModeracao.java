package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Cupom;
import com.example.heblecollectiondesktop.model.Funcionario;
import com.example.heblecollectiondesktop.model.LogModeracao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.time.LocalDateTime;

public class ControllerLogsModeracao {

    @FXML private TableView<LogModeracao> tabelaLogs;
    @FXML private TableColumn<LogModeracao, Integer> colId;
    @FXML private TableColumn<LogModeracao, LocalDateTime> colDataHora;
    @FXML private TableColumn<LogModeracao, String> colModerador;
    @FXML private TableColumn<LogModeracao, String> colAcao;
    @FXML private TableColumn<LogModeracao, String> colAlvoAfetado;
    @FXML private TableColumn<LogModeracao, String> colDetalhes;
    @FXML private TextField txtFiltro;

    private final LogsDAO logsDAO = new LogsDAO();
    private ObservableList<LogModeracao> listaLogs = FXCollections.observableArrayList();
    private FilteredList<LogModeracao> logsFiltrados;

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
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDataHora.setCellValueFactory(new PropertyValueFactory<>("dataAcao"));
        colModerador.setCellValueFactory(new PropertyValueFactory<>("funcionarioId"));
        colAcao.setCellValueFactory(new PropertyValueFactory<>("acao"));
        colAlvoAfetado.setCellValueFactory(new PropertyValueFactory<>("alvoAfetado"));
        colDetalhes.setCellValueFactory(new PropertyValueFactory<>("detalhes"));

        logsFiltrados = new FilteredList<>(listaLogs, p -> true);

        if (txtFiltro != null) {
            txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
                logsFiltrados.setPredicate(log -> {
                    if (newValue == null || newValue.isBlank()) {
                        return true;
                    }

                    String termo = newValue.toLowerCase();

                    boolean bateModerador = log.getFuncionarioId() != null && log.getFuncionarioId().toLowerCase().contains(termo);
                    boolean bateAcao = log.getAcao() != null && log.getAcao().toLowerCase().contains(termo);
                    boolean bateAlvo = log.getAlvoAfetado() != null && log.getAlvoAfetado().toLowerCase().contains(termo);
                    boolean bateDetalhes = log.getDetalhes() != null && log.getDetalhes().toLowerCase().contains(termo);

                    return bateModerador || bateAcao || bateAlvo || bateDetalhes;
                });
            });
        }

        tabelaLogs.setItems(logsFiltrados);
        carregarLogs();
        configurarDuploClique();
    }

    @FXML
    public void atualizarLogs() {
        carregarLogs();
    }

    private void carregarLogs() {
        try {
            listaLogs.setAll(logsDAO.listarTodos());
        } catch (Exception e) {
            e.printStackTrace();
            exibirAlerta("Erro ao carregar logs", "Não foi possível carregar os registros de auditoria: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void voltarAoHub(ActionEvent event) {
        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/moderacaoHub.fxml");
            if (url == null) {
                url = getClass().getResource("/view/moderacaoHub.fxml");
            }

            if (url == null) {
                exibirAlerta("Erro FXML", "Arquivo moderacaoHub.fxml não encontrado.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent hubView = loader.load();

            ControllerModeracaoHub controllerHub = loader.getController();
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
            exibirAlerta("Erro de Navegação", "Falha ao retornar ao painel principal: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void abrirTelaInfoLog(LogModeracao log) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/InfoPopUpLog.fxml"));
            Parent root = loader.load();

            ControllerTelaInfoLog modalController = loader.getController();
            if (modalController != null) {
                modalController.SetLog(log);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Informações do Log");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta( "Erro", "Falha ao abrir detalhes do cupom: ",Alert.AlertType.ERROR);
        }
    }
    private void configurarDuploClique() {
        if (tabelaLogs == null) return;

        tabelaLogs.setRowFactory(tv -> {
            TableRow<LogModeracao> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    LogModeracao logSelecionado = row.getItem();
                   abrirTelaInfoLog(logSelecionado);
                }
            });
            return row;
        });
    }

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}