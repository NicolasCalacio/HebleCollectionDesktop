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
    private ComboBox cbMotivo;
    @FXML
    private  TextArea txtObservacao;
    private Consumidores consumidorBanir;
    private ConsumidoresDAO consumidorDAO = new ConsumidoresDAO();
    private LogsDAO logsDAO = new LogsDAO();

    public void initialize (){
       if( cbMotivo != null){
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
    public void Setconsumidor(Consumidores consumidor){
        this.consumidorBanir = consumidor;
    }

    public  void confirmarBanimento(){
        String Observacao = txtObservacao.getText()!= null ? txtObservacao.getText().trim() : "";;
        String motivo = cbMotivo.getValue().toString();

        if (motivo == null || motivo.toString().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Motivo Obrigatório", "Por favor, selecione um motivo para o banimento.");
            return;
        }
        if (consumidorBanir == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhumo Consumidor  selecionado para exclusão.");
            return;
        }
        try {
            boolean sucesso = consumidorDAO.deletar(consumidorBanir.getIDconsumidor());
            if (sucesso){
                String AlvoAfetado = consumidorBanir.getNomconsumidor();
                String detalhes = "Motivo: " + motivo + (Observacao.isEmpty() ? "" : " | Obs: " + Observacao);

                logsDAO.registrarLog("Suporte", "BANIR_CONSUMIDOR",AlvoAfetado, detalhes);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Consumidor removido e ação registrada no log da moderação!");

                Stage janelaAtual = (Stage) txtObservacao.getScene().getWindow();
                janelaAtual.close();
            }
            else{
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir a exclusão do Consumidor.");
        }

        }catch (Exception e){
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
