package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.ConsumidoresDAO;
import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Consumidores;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ControllerBanirConsumidor {
    @FXML
    private ComboBox<String> cbMotivo;
    @FXML
    private TextArea txtObservacao;
    private Consumidores consumidorBanir;
    private ConsumidoresDAO consumidorDAO = new ConsumidoresDAO();
    private LogsDAO logsDAO = new LogsDAO();

    public void initialize() {
        if (cbMotivo != null) {
            cbMotivo.setItems(FXCollections.observableArrayList(
                    "Violação dos Termos de Uso",
                    "Comportamento Abusivo ou Ofensivo",
                    "Fraude ou Atividade Suspeita",
                    "Spam ou Propaganda não autorizada",
                    "Solicitação do próprio Usuário",
                    "Inatividade prolongada / Outros"
            ));
        }
    }

    public void Setconsumidor(Consumidores consumidor) {
        this.consumidorBanir = consumidor;
    }

    @FXML
    public void confirmarBanimento() {
        String observacao = txtObservacao.getText() != null ? txtObservacao.getText().trim() : "";

        if (cbMotivo.getValue() == null || cbMotivo.getValue().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Motivo Obrigatório", "Por favor, selecione um motivo para o banimento.");
            return;
        }

        String motivo = cbMotivo.getValue();

        if (consumidorBanir == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhum consumidor selecionado para banimento.");
            return;
        }

        try {
            boolean sucesso = consumidorDAO.atualizarStatusConsumidor(consumidorBanir.getIDconsumidor(), "Banido");
            if (sucesso) {
                String alvoAfetado = consumidorBanir.getNomconsumidor();
                String detalhes = "Motivo: " + motivo + (observacao.isEmpty() ? "" : " | Obs: " + observacao);

                logsDAO.registrarLog("Suporte", "BAN_CONSUMIDOR", alvoAfetado, detalhes);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Consumidor banido e ação registrada no log da moderação!");

                Stage janelaAtual = (Stage) txtObservacao.getScene().getWindow();
                janelaAtual.close();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir o banimento do consumidor.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao banir consumidor: " + e.getMessage());
        }
    }

    @FXML
    public void BanimentoPermanente() {
        String observacao = txtObservacao.getText() != null ? txtObservacao.getText().trim() : "";

        if (cbMotivo.getValue() == null || cbMotivo.getValue().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Motivo Obrigatório", "Por favor, selecione um motivo para o banimento.");
            return;
        }

        String motivo = cbMotivo.getValue();

        if (consumidorBanir == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhum consumidor selecionado para exclusão.");
            return;
        }

        try {
            boolean sucesso = consumidorDAO.deletar(consumidorBanir.getIDconsumidor());
            if (sucesso) {
                String alvoAfetado = consumidorBanir.getNomconsumidor();
                String detalhes = "Motivo: " + motivo + (observacao.isEmpty() ? "" : " | Obs: " + observacao);

                logsDAO.registrarLog("Suporte", "BAN_PERMANENTE_CONSUMIDOR", alvoAfetado, detalhes);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Consumidor removido e ação registrada no log da moderação!");

                Stage janelaAtual = (Stage) txtObservacao.getScene().getWindow();
                janelaAtual.close();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir a exclusão do consumidor.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao deletar consumidor: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void cancelar(ActionEvent event) {
        Stage janelaAtual = (Stage) txtObservacao.getScene().getWindow();
        janelaAtual.close();
    }
}