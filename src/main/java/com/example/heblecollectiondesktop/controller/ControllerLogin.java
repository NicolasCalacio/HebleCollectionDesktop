package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.FuncionarioDAO;
import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.w3c.dom.events.MouseEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ControllerLogin implements Initializable {

    @FXML
    private ImageView imgLogo;

    @FXML
    private TextField txMatricula;

    @FXML
    private PasswordField txSenha;

    @FXML
    private Button btnLogin;


    private final FuncionarioDAO funcionarioDAO;


    public ControllerLogin() {
        this.funcionarioDAO = new FuncionarioDAO();
    }


    public ControllerLogin(FuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarLogo();
    }

    private void carregarLogo() {
        if (imgLogo == null) return;

        String[] caminhos = {
                "/com/example/heblecollectiondesktop/images/logo.png",
                "/images/logo.png",
                "/logo.png"
        };

        for (String caminho : caminhos) {
            try (InputStream stream = getClass().getResourceAsStream(caminho)) {
                if (stream != null) {
                    imgLogo.setImage(new Image(stream));
                    return;
                }
            } catch (IOException e) {

            }
        }
    }

    @FXML
    void fazerLogin(ActionEvent event) {
        String matricula = txMatricula.getText() != null ? txMatricula.getText().trim() : "";
        String senha = txSenha.getText() != null ? txSenha.getText() : "";

        if (matricula.isEmpty() || senha.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha a matrícula e a senha.");
            return;
        }

        try {
            Funcionario funcionarioLogado = funcionarioDAO.autenticar(matricula, senha);

            if (funcionarioLogado != null) {
                abrirDashboard(funcionarioLogado);
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Acesso Negado", "Matrícula ou senha incorretos.");
                txSenha.clear();
                txSenha.requestFocus();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível conectar ao banco de dados.");
        }
    }

    private void abrirDashboard(Funcionario funcionarioLogado) {
        URL dashboardLocation = getClass().getResource("/com/example/heblecollectiondesktop/view/dashboard.fxml");

        if (dashboardLocation == null) {
            dashboardLocation = getClass().getResource("/view/dashboard.fxml");
        }

        if (dashboardLocation == null) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Configuração", "O arquivo 'dashboard.fxml' não foi encontrado nas pastas de recursos.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(dashboardLocation);
            Parent root = fxmlLoader.load();


            Object controller = fxmlLoader.getController();
            if (controller instanceof ControllerDashboard controllerDashboard) {
                controllerDashboard.setFuncionarioLogado(funcionarioLogado);
            }


            Stage stageAtual = (Stage) btnLogin.getScene().getWindow();

            Stage stageDashboard = new Stage();
            stageDashboard.setTitle("Heble Collection - Dashboard");
            stageDashboard.setScene(new Scene(root, 1200, 760));
            stageDashboard.setResizable(true);
            stageDashboard.centerOnScreen();

            stageDashboard.show();
            stageAtual.close();

        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Carregamento", "Falha ao carregar a tela principal: " + e.getMessage());
        }
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
    @FXML
    private void abrirsuporte(MouseEvent event){
        URL dashboardLocation = getClass().getResource("/com/example/heblecollectiondesktop/view/TelaSuporte.fxml");

        if (dashboardLocation == null) {
            dashboardLocation = getClass().getResource("/view/TelaSuporte.fxml");
        }

        if (dashboardLocation == null) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Configuração", "O arquivo 'TelaSuporte.fxml' não foi encontrado nas pastas de recursos.");
            return;
        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(dashboardLocation);
            Parent root = fxmlLoader.load();


            Object controller = fxmlLoader.getController();



            Stage stageAtual = (Stage) btnLogin.getScene().getWindow();

            Stage stageDashboard = new Stage();
            stageDashboard.setTitle("Heble Collection - Suport");
            stageDashboard.setScene(new Scene(root, 1200, 760));
            stageDashboard.setResizable(true);
            stageDashboard.centerOnScreen();

            stageDashboard.show();
            stageAtual.close();
        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Carregamento", "Falha ao carregar a tela principal: " + e.getMessage());
        }
    }
}