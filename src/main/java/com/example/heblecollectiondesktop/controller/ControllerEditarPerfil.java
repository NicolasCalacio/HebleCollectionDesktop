package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.FuncionarioDAO;
import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ControllerEditarPerfil {
    @FXML
    private Label lbNome;
    @FXML
    private PasswordField txSenha;

    private Funcionario funcionarioEditando;

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final LogsDAO logsDAOFuncionario = new LogsDAO();

    public void setPerfil(Funcionario funcionario){
        this.funcionarioEditando = funcionario;
        if(funcionario != null) {
            // Apenas exibe a matrícula/usuário no Label (não editável)
            lbNome.setText(funcionario.getMatricula() != null ? funcionario.getMatricula() : "");

            // Opcional: pré-carrega a senha atual ou deixa limpo para redigitar
            txSenha.setText(funcionario.getSenha() != null ? funcionario.getSenha() : "");
        }
    }

    @FXML
    public void EditarPerfil(ActionEvent event) {
        String senhaDigitada = txSenha.getText();

        if (senhaDigitada == null || senhaDigitada.trim().isEmpty()){
            mostrarAlerta1("Campo Obrigatório", "Por favor, preencha a nova senha.", "Alerta");
            return;
        }

        if (funcionarioEditando == null) {
            mostrarAlerta1("Erro", "Nenhum funcionário foi selecionado para edição.", "Erro");
            return;
        }

        // Mantém a matrícula original e altera apenas a senha
        funcionarioEditando.setSenha(senhaDigitada);

        boolean sucesso = funcionarioDAO.atualizar(funcionarioEditando);

        if (sucesso) {
            String matriculaLog = (funcionarioEditando.getMatricula() != null)
                    ? funcionarioEditando.getMatricula() : "SISTEMA";
            String alvo = matriculaLog + " (ID: " + funcionarioEditando.getId() + ")";

            logsDAOFuncionario.registrarLog(
                    matriculaLog,
                    "EDICAO_FUNCIONARIO",
                    alvo,
                    "Senha do funcionário atualizada com sucesso."
            );

            mostrarAlerta1("Sucesso", "Sua senha foi atualizada com sucesso!", "Informação");

            Stage janelaAtual = (Stage) lbNome.getScene().getWindow();
            janelaAtual.close();
        } else {
            mostrarAlerta1("Erro de Persistência", "Não foi possível atualizar os dados no banco de dados.", "Erro");
        }
    }

    private void mostrarAlerta1(String titulo, String mensagem, String TipoAlerta) {
        Alert.AlertType tipo = Alert.AlertType.INFORMATION;

        if ("Erro".equalsIgnoreCase(TipoAlerta)) {
            tipo = Alert.AlertType.ERROR;
        } else if ("Alerta".equalsIgnoreCase(TipoAlerta)) {
            tipo = Alert.AlertType.WARNING;
        } else if ("Confirmação".equalsIgnoreCase(TipoAlerta)) {
            tipo = Alert.AlertType.CONFIRMATION;
        }

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    public void Limpar() {
        // Como o usuário não altera mais a matrícula, o botão limpar limpa apenas o campo de senha
        txSenha.clear();
    }

    @FXML
    public void Cancelar() {
        Stage janelaAtual = (Stage) txSenha.getScene().getWindow();
        janelaAtual.close();
    }
}