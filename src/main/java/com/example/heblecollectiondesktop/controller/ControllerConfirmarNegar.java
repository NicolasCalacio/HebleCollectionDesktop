package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.database.TicketDAO;
import com.example.heblecollectiondesktop.model.Funcionario;
import com.example.heblecollectiondesktop.model.Ticket;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ControllerConfirmarNegar {

    @FXML
    private ComboBox<String> cbMotivoNegar;
    @FXML private TextArea txtObservacao;

    private Ticket ticketRejeitar;
    private ControllerTicketsPendentes controllerPai;
    private Funcionario funcionarioLogado;

    private final LogsDAO rejeitarLog = new LogsDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    public void setTicketSelecionado(Ticket ticket) {
        this.ticketRejeitar = ticket;
    }

    public void setControllerPai(ControllerTicketsPendentes controllerPai) {
        this.controllerPai = controllerPai;
    }

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    @FXML
    public void initialize() {
        if (cbMotivoNegar != null) {
            cbMotivoNegar.setItems(FXCollections.observableArrayList(
                    "Denúncias e tickets excederam o limite",
                    "Solicitação da própria empresa",
                    "Cadastro Duplicado / Incorreto",
                    "Violação das Políticas da Empresa",
                    "Outro"
            ));
        }
    }

    @FXML
    private void negar(ActionEvent event) {
        String motivo = cbMotivoNegar.getValue();
        String observacao = txtObservacao.getText() != null ? txtObservacao.getText().trim() : "";

        if (motivo == null || motivo.isBlank()) {
            mostrarAlerta("Motivo Obrigatório", "Por favor, selecione um motivo para a negação.", Alert.AlertType.WARNING);
            return;
        }

        if (ticketRejeitar == null) {
            mostrarAlerta("Erro", "Nenhum Ticket selecionado para negação.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Alterado de false para "recusado" para corresponder ao novo sistema de status textual
            boolean sucesso = ticketDAO.atualizarStatus(ticketRejeitar.getId(), "recusado");

            if (sucesso) {
                String idModerador = (funcionarioLogado != null && funcionarioLogado.getMatricula() != null)
                        ? funcionarioLogado.getMatricula()
                        : "SISTEMA";
                rejeitarLog.registrarLog(idModerador, String.valueOf(ticketRejeitar.getId()), "Ticket retornado para análise/negado.");
                mostrarAlerta("Sucesso", "Status do Ticket #" + ticketRejeitar.getId() + " atualizado.", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Erro", "Falha ao alterar o status do ticket.", Alert.AlertType.ERROR);
            }

            if (controllerPai != null) {
                try {
                    controllerPai.carregarTickets();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            fecharJanela(event);

        } catch (Exception e) {
            e.printStackTrace();

            String mensagemErro = e.getMessage();
            if (mensagemErro != null && (mensagemErro.contains("foreign key") || mensagemErro.contains("1451"))) {
                mostrarAlerta("Violação de Integridade",
                        "Não é possível atualizar o status do ticket '" + ticketRejeitar.getAssunto() + "' pois existem registros vinculados a ele.", Alert.AlertType.ERROR);
            } else {
                mostrarAlerta("Erro de Banco de Dados", "Falha ao atualizar status: " + mensagemErro, Alert.AlertType.ERROR);
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void fecharJanela(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    public void cancelar() {
        Stage janelaAtual = (Stage) txtObservacao.getScene().getWindow();
        if (janelaAtual != null) {
            janelaAtual.close();
        }
    }
}