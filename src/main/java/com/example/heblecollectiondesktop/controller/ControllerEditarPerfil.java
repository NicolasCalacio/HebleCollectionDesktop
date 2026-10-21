package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.FuncionarioDAO;
import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerEditarPerfil {
    @FXML
    private TextField txNome;
    @FXML
    private TextField txSenha;

    private Funcionario funcionarioEditando;

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final LogsDAO logsDAOFuncionario = new LogsDAO();

    public void setPerfil(Funcionario funcionario){
        this.funcionarioEditando = funcionario;
        if(funcionario != null) {

            txNome.setText(funcionario.getMatricula() != null ? funcionario.getMatricula() : "");
            txSenha.setText(funcionario.getSenha() != null ? funcionario.getSenha() : "");
        }
    }

    @FXML
    public void EditarPerfil(ActionEvent event) {
        String matriculaDigitada = txNome.getText();
        String senhaDigitada = txSenha.getText();

        if (matriculaDigitada == null || matriculaDigitada.trim().isEmpty() ||
                senhaDigitada == null || senhaDigitada.trim().isEmpty() ){
            mostrarAlerta1("Campos Obrigatórios", "Por favor, preencha a matrícula e a senha.", "Alerta");
            return;
        }

        if (funcionarioEditando == null) {
            mostrarAlerta1("Erro", "Nenhum funcionário foi selecionado para edição.", "Erro");
            return;
        }

        funcionarioEditando.setMatricula(matriculaDigitada);
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
                    "Dados do funcionário atualizados com sucesso."
            );

            mostrarAlerta1("Sucesso", "Seus dados foram atualizados", "Informação");

            Stage janelaAtual = (Stage) txNome.getScene().getWindow();
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
        txNome.clear();
        txSenha.clear();
    }

    @FXML
    public void Cancelar() {
        Stage janelaAtual = (Stage) txSenha.getScene().getWindow();
        janelaAtual.close();
    }
}