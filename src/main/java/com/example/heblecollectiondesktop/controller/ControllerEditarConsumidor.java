package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.ConsumidoresDAO;
import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Consumidores;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ControllerEditarConsumidor {
    private ConsumidoresDAO consumidorDAO = new ConsumidoresDAO();
    private LogsDAO logsDAO = new LogsDAO();
    private Consumidores ConsumidorEditar;

    @FXML private Label LabelNome;
    @FXML private Label LabelID;
    @FXML private ComboBox<String> CbMudarStatus;
    @FXML private TextArea txtObservacao;

    public void initialize() {
        if (CbMudarStatus != null) {
            CbMudarStatus.setItems(FXCollections.observableArrayList("Permitido", "Banido"));
        }
    }

    public void SetLabel() {
        if (ConsumidorEditar != null) {
            LabelNome.setText(ConsumidorEditar.getNomconsumidor());
            LabelID.setText(String.valueOf(ConsumidorEditar.getIDconsumidor()));
        }
    }

    public void setConsumidorEditar(Consumidores consumidor) {
        this.ConsumidorEditar = consumidor;
        if (consumidor != null) {
            SetLabel();
        }
    }

    @FXML
    public void EditarConsumidor() {
        // 1. Pegamos o valor diretamente como String (sem chamar .toString() antes da checagem)
        String selecionado = (CbMudarStatus != null) ? CbMudarStatus.getValue() : null;

        // 2. Validação se algo foi selecionado
        if (selecionado == null || selecionado.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Status Obrigatório", "Por favor, selecione um Status para a edição.");
            return;
        }

        if (ConsumidorEditar == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhum consumidor selecionado para edição.");
            return;
        }

        try {
            String observacao = (txtObservacao.getText() != null) ? txtObservacao.getText().trim() : "";

            if (selecionado.equalsIgnoreCase("Permitido")) {
                boolean sucesso = consumidorDAO.atualizarStatusConsumidor(ConsumidorEditar.getIDconsumidor(), "Permitido");
                if (sucesso) {
                    String detalhes = "Motivo: " + selecionado + (observacao.isEmpty() ? "" : " | Obs: " + observacao);
                    String alvoAfetado = ConsumidorEditar.getNomconsumidor();
                    logsDAO.registrarLog("Suporte", "RETIRANDO_BANIMENTO", alvoAfetado, detalhes);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Banimento do consumidor retirado e ação registrada no log da moderação!");

                    fecharJanela();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir o desbanimento do consumidor.");
                }
            } else if (selecionado.equalsIgnoreCase("Banido")) {
                boolean sucesso = consumidorDAO.atualizarStatusConsumidor(ConsumidorEditar.getIDconsumidor(), "Banido");
                if (sucesso) {
                    String detalhes = "Motivo: " + selecionado + (observacao.isEmpty() ? "" : " | Obs: " + observacao);
                    String alvoAfetado = ConsumidorEditar.getNomconsumidor();
                    logsDAO.registrarLog("Suporte", "MANTENDO_BANIMENTO", alvoAfetado, detalhes);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Banimento do consumidor mantido e ação registrada no log da moderação!");

                    fecharJanela();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir o banimento do consumidor.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao editar consumidor: " + e.getMessage());
        }
    }

    @FXML
    public void Limpar() {
        if (CbMudarStatus != null) {
            CbMudarStatus.getSelectionModel().clearSelection();
        }
        if (txtObservacao != null) {
            txtObservacao.clear();
        }
    }

    @FXML
    public void Cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        if (LabelNome != null && LabelNome.getScene() != null) {
            Stage janelaAtual = (Stage) LabelNome.getScene().getWindow();
            janelaAtual.close();
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