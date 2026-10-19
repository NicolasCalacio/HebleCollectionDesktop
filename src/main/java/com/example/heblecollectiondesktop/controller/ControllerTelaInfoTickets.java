package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.FuncionarioDAO;
import com.example.heblecollectiondesktop.database.TicketDAO;
import com.example.heblecollectiondesktop.model.Ticket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ControllerTelaInfoTickets {

    @FXML
    private Label lblAssunto;
    @FXML
    private Label lblId;
    @FXML
    private Label lblStatus;
    @FXML
    private TextArea txtDescricao;
    @FXML
    private Button btnEditarSenha;

    private Ticket ticketAtual;
    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final TicketDAO ticketDAO = new TicketDAO(); // Instanciado para atualizar o status

    public void setTicket(Ticket ticket) {
        this.ticketAtual = ticket;
        if (ticket == null) return;

        boolean isSenha = "Senha".equalsIgnoreCase(ticket.getTipo());
        boolean emAndamento = ticket.getStatus() != null && ticket.getStatus().equalsIgnoreCase("andamento");
        boolean podeEditar = isSenha && emAndamento;

        if (btnEditarSenha != null) {
            btnEditarSenha.setVisible(podeEditar);
            btnEditarSenha.setManaged(podeEditar);
        }

        lblId.setText("ID: " + ticket.getId());
        lblAssunto.setText("Assunto: " + ticket.getAssunto());

        String statusAtual = ticket.getStatus() != null ? ticket.getStatus() : "indefinido";
        lblStatus.setText("Estado: " + statusAtual);

        txtDescricao.setText(ticket.getDescricao());
    }

    @FXML
    private void alterarSenhaFuncionario(ActionEvent event) {
        if (ticketAtual == null) return;

        String matriculaAlvo = ticketAtual.getAssunto();

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Redefinir Senha");
        dialog.setHeaderText("Funcionário (Matrícula): " + matriculaAlvo);
        dialog.setContentText("Digite a nova senha:");

        dialog.showAndWait().ifPresent(novaSenha -> {
            if (novaSenha.trim().isEmpty()) {
                exibirAlerta("Aviso", "A senha não pode estar vazia.", Alert.AlertType.WARNING);
                return;
            }

            try {
                boolean senhaAtualizada = funcionarioDAO.atualizarSenhaPorMatricula(matriculaAlvo, novaSenha.trim());

                if (senhaAtualizada) {
                    ticketDAO.atualizarStatus(ticketAtual.getId(), "Fechado");

                    exibirAlerta("Sucesso", "Senha alterada e ticket resolvido com sucesso!", Alert.AlertType.INFORMATION);

                    Stage stage = (Stage) btnEditarSenha.getScene().getWindow();
                    stage.close();
                } else {
                    exibirAlerta("Erro", "Nenhum funcionário encontrado com a matrícula: " + matriculaAlvo, Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                exibirAlerta("Erro de Banco", "Erro ao atualizar a senha: " + e.getMessage(), Alert.AlertType.ERROR);
            }
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