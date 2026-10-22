package com.example.heblecollectiondesktop.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import com.example.heblecollectiondesktop.database.FuncionarioDAO;
import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Cargo;
import com.example.heblecollectiondesktop.model.Funcionario;
import com.example.heblecollectiondesktop.model.Gerente;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerCadastroFuncionarios implements Initializable {

    @FXML private TextField txtMatricula;
    @FXML private PasswordField txtSenha;
    @FXML private PasswordField txtConfirmarSenha;
    @FXML private ComboBox<Cargo> cbCargo;

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final LogsDAO logsDAO = new LogsDAO();
    private ControllerGerenciarFuncionarios controllerPai;
    private Funcionario funcionarioLogado;

    public void setControllerPai(ControllerGerenciarFuncionarios controllerPai) {
        this.controllerPai = controllerPai;
    }

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (cbCargo != null) {
            cbCargo.setItems(FXCollections.observableArrayList(Cargo.values()));
        }
    }

    @FXML
    private void salvarFuncionario(ActionEvent event)throws SQLException {
        String matricula = txtMatricula.getText() != null ? txtMatricula.getText().trim() : "";
        String senha = txtSenha.getText() != null ? txtSenha.getText().trim() : "";
        String confirmarSenha = txtConfirmarSenha.getText() != null ? txtConfirmarSenha.getText().trim() : "";
        Cargo cargo = cbCargo.getValue();

        if (matricula.isEmpty() || senha.isEmpty() || cargo == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha a matrícula, senha e selecione um cargo.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Senha Incompatível", "A senha e a confirmação de senha não coincidem.");
            return;
        }
        List<Funcionario> funcionarios= funcionarioDAO.pesquisar(matricula);
        boolean existe = funcionarios.stream()
                .anyMatch(f -> f.getMatricula().equalsIgnoreCase(matricula));
        if(existe){
            mostrarAlerta(Alert.AlertType.ERROR,"Funcionario já cadastrado","Escolha outro nome para a matricula, ou coloque o sborenome junto");
            return;
        }else {
            try {
                Funcionario novoFuncionario;
                if (cargo == Cargo.GERENTE) {
                    novoFuncionario = new Gerente(0, matricula, senha);
                } else {
                    novoFuncionario = new Funcionario(0, matricula, senha, cargo);
                }

                funcionarioDAO.salvar(novoFuncionario);

                String idModerador = (funcionarioLogado != null && funcionarioLogado.getMatricula() != null)
                        ? funcionarioLogado.getMatricula() : "SISTEMA";
                String alvo = "Matrícula: " + matricula;

                logsDAO.registrarLog(
                        idModerador,
                        "CADASTRO_FUNCIONARIO",
                        alvo,
                        "Novo funcionário cadastrado com o cargo: " + cargo
                );

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Funcionário cadastrado com sucesso!");

                if (controllerPai != null) {
                    controllerPai.carregarFuncionarios();
                }

                fecharJanela(event);

            } catch (SQLException e) {
                e.printStackTrace();
                if (e.getMessage() != null && e.getMessage().contains("Duplicate entry")) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Matrícula Existente", "Já existe um funcionário cadastrado com esta matrícula.");
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro no Banco de Dados", "Falha ao cadastrar funcionário: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        fecharJanela(event);
    }

    private void fecharJanela(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        if (stage != null) {
            stage.close();
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