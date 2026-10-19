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
    @FXML private ComboBox CbMudarStatus;
    @FXML private TextArea txtObservacao;

    public void initialize(){
        if (CbMudarStatus != null){
            CbMudarStatus.setItems(FXCollections.observableArrayList("Permitido","Banido" ));

        }
    }
    public void SetLabel(){
        LabelNome.setText(ConsumidorEditar.getNomconsumidor());
        LabelID.setText(String.valueOf(ConsumidorEditar.getIDconsumidor()));
    }

    public void setConsumidorEditar(Consumidores consumidor){
        this.ConsumidorEditar = consumidor;
        if (consumidor != null){
            SetLabel();
        }
    }
    public void EditarConsumidor(){
        String selecionado = CbMudarStatus.getValue().toString();
        if (selecionado == null || selecionado.toString().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Status Obrigatório", "Por favor, selecione um Status para a edição.");
            return;
        }
        if (ConsumidorEditar == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhumo Consumidor  selecionado para Edição.");
            return;
        }
        try {
            if (selecionado.equalsIgnoreCase("Permitido")) {
                boolean sucesso = consumidorDAO.atualizarStatusConsumidor(ConsumidorEditar.getIDconsumidor(), "Permitido");
                String Observacao = txtObservacao.getText()!= null ? txtObservacao.getText().trim() : "";
                if (sucesso) {
                    String detalhes = "Motivo: " + selecionado + (Observacao.isEmpty() ? "" : " | Obs: " + Observacao);
                    String alvoAfetado = ConsumidorEditar.getNomconsumidor();
                    logsDAO.registrarLog("Suporte", "RETIRANDO_BANIMENTO", alvoAfetado,detalhes);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Banimento do consumidor retirado e ação registrada no log da moderação!");
                    Stage janelaAtual = (Stage) txtObservacao.getScene().getWindow();
                    janelaAtual.close();
                }else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir o Desbanimento do Consumidor.");
                }
            }
            if (selecionado.equalsIgnoreCase("Banido")){
                boolean sucesso = consumidorDAO.atualizarStatusConsumidor(ConsumidorEditar.getIDconsumidor(), "Banido");
                String Observacao = txtObservacao.getText()!= null ? txtObservacao.getText().trim() : "";
                if (sucesso) {
                    String detalhes = "Motivo: " + selecionado + (Observacao.isEmpty() ? "" : " | Obs: " + Observacao);
                    String alvoAfetado = ConsumidorEditar.getNomconsumidor();
                    logsDAO.registrarLog("Suporte", "MANTENDO_BANIMENTO", alvoAfetado,detalhes);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Banimento do consumidor Mantido e ação registrada no log da moderação!");
                    Stage janelaAtual = (Stage) txtObservacao.getScene().getWindow();
                    janelaAtual.close();
                }else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir Banimento do Consumidor.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao Editar consumidor: " + e.getMessage());
        }


    }

    @FXML
    public void Limpar() {
        CbMudarStatus.getSelectionModel().clearSelection();
    }
    @FXML
    public void Cancelar() {
        Stage janelaAtual = (Stage) LabelNome.getScene().getWindow();
        janelaAtual.close();
    }
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();

    }
}
