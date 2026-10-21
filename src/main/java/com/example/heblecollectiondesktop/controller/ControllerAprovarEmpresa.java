package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.EmpresaDAO;
import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Empresa;
import com.example.heblecollectiondesktop.model.Funcionario;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;

import java.util.Optional;

public class ControllerAprovarEmpresa {

    @FXML private TableView<Empresa> tabelaEmpresasPendentes;
    @FXML private TableColumn<Empresa, Integer> colId;
    @FXML private TableColumn<Empresa, String> colNome;
    @FXML private TableColumn<Empresa, String> colCnpj;
    @FXML private TableColumn<Empresa, String> colEstilo;
    @FXML private Button btnAprovarEmpresa;
    @FXML private Button btnRejeitarEmpresa;

    private Pane containerCentral;
    private Funcionario funcionarioLogado;
    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    private final LogsDAO logsDAO = new LogsDAO();

    public void setContainerCentral(Pane containerCentral) {
        this.containerCentral = containerCentral;
    }

    public void setFuncionarioLogado(Funcionario funcionario) {
        this.funcionarioLogado = funcionario;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        colEstilo.setCellValueFactory(new PropertyValueFactory<>("estilo"));

        carregarEmpresasPendentes();
    }

    public void carregarEmpresasPendentes() {
        try {
            ObservableList<Empresa> listaPendentes = FXCollections.observableArrayList(empresaDAO.listarPendentes());
            tabelaEmpresasPendentes.setItems(listaPendentes);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao carregar empresas pendentes: " + e.getMessage());
        }
    }

    @FXML
    public void aprovarEmpresa() {
        Empresa selecionada = tabelaEmpresasPendentes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            exibirAlerta("Atenção", "Selecione uma empresa na tabela para aprovar.", Alert.AlertType.WARNING);
            return;
        }

        boolean sucesso = empresaDAO.atualizarStatusAprovacao(selecionada.getId(), true);

        if (sucesso) {
            String matricula = (funcionarioLogado != null && funcionarioLogado.getMatricula() != null)
                    ? funcionarioLogado.getMatricula() : "SISTEMA";
            String alvo = selecionada.getNome() + " (CNPJ: " + selecionada.getCnpj() + ")";

            logsDAO.registrarLog(
                    matricula,
                    "APROVACAO_EMPRESA",
                    alvo,
                    "Solicitação de cadastro da empresa aprovada."
            );

            exibirAlerta("Sucesso", "A empresa '" + selecionada.getNome() + "' foi aprovada!", Alert.AlertType.INFORMATION);
            carregarEmpresasPendentes();
        } else {
            exibirAlerta("Erro", "Não foi possível aprovar a empresa selecionada.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void rejeitarEmpresa() {
        Empresa selecionada = tabelaEmpresasPendentes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            exibirAlerta("Atenção", "Selecione uma empresa na tabela para rejeitar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Rejeição");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja rejeitar e excluir o cadastro de '" + selecionada.getNome() + "'?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean sucesso = empresaDAO.deletar(selecionada.getId());

                if (sucesso) {
                    String matricula = (funcionarioLogado != null && funcionarioLogado.getMatricula() != null)
                            ? funcionarioLogado.getMatricula() : "SISTEMA";
                    String alvo = selecionada.getNome() + " (CNPJ: " + selecionada.getCnpj() + ")";

                    logsDAO.registrarLog(
                            matricula,
                            "REJEICAO_EMPRESA",
                            alvo,
                            "Solicitação de cadastro da empresa rejeitada e excluída."
                    );

                    exibirAlerta("Sucesso", "Solicitação de empresa rejeitada com sucesso.", Alert.AlertType.INFORMATION);
                    carregarEmpresasPendentes();
                } else {
                    exibirAlerta("Erro", "Não foi possível excluir o cadastro da empresa.", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exibirAlerta("Erro Inesperado", "Falha ao processar a exclusão: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void voltarParaGerenciarEmpresas(ActionEvent event) {
        if (containerCentral == null) {
            exibirAlerta("Erro de Navegação", "O container central não está configurado.", Alert.AlertType.ERROR);
            return;
        }

        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/GerenciarEmpresas.fxml");
            if (url == null) {
                url = getClass().getResource("/view/GerenciarEmpresas.fxml");
            }

            if (url == null) {
                exibirAlerta("Erro FXML", "Arquivo GerenciarEmpresas.fxml não encontrado.", Alert.AlertType.ERROR);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent gerenciarView = loader.load();

            ControllerGerenciarEmpresas controllerGerenciar = loader.getController();
            if (controllerGerenciar != null) {
                controllerGerenciar.setContainerCentral(containerCentral);
                if (funcionarioLogado != null) {
                    controllerGerenciar.setFuncionarioLogado(funcionarioLogado);
                }
            }

            containerCentral.getChildren().setAll(gerenciarView);
        } catch (IOException e) {
            e.printStackTrace();
            exibirAlerta("Erro de Navegação", "Não foi possível retornar à Gestão de Empresas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}