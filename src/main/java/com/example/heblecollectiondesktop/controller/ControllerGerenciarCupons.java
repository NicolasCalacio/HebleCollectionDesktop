package com.example.heblecollectiondesktop.controller;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Optional;

import com.example.heblecollectiondesktop.database.CupomDAO;
import com.example.heblecollectiondesktop.model.Cupom;
import com.example.heblecollectiondesktop.model.Funcionario;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControllerGerenciarCupons {

    @FXML private TableView<Cupom> tabelaCupons;
    @FXML private TableColumn<Cupom, Integer> colId;
    @FXML private TableColumn<Cupom, String> colCodigo;
    @FXML private TableColumn<Cupom, String> colDesconto;
    @FXML private TableColumn<Cupom, String> colValorMinimo;
    @FXML private TableColumn<Cupom, String> colStatus;
    @FXML private TableColumn<Cupom, String> colValidade;

    @FXML private Button btnAdicionarCupom;
    @FXML private Button btnEditarCupom;
    @FXML private Button btnRemoverCupom;

    private Pane containerCentral;
    private Funcionario funcionarioLogado;
    private final CupomDAO cupomDAO = new CupomDAO();
    private final ObservableList<Cupom> listaCupons = FXCollections.observableArrayList();

    public void setContainerCentral(Pane containerCentral) {
        this.containerCentral = containerCentral;
    }

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    @FXML
    public void initialize() {
        configurarColunas();
        carregarTabela();
    }

    private void configurarColunas() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colCodigo != null) colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        if (colDesconto != null) {
            colDesconto.setCellValueFactory(cell ->
                    new SimpleStringProperty(String.format("%.1f%%", cell.getValue().getDescontoPercentual()))
            );
        }

        if (colValorMinimo != null) {
            colValorMinimo.setCellValueFactory(cell ->
                    new SimpleStringProperty(String.format("R$ %.2f", cell.getValue().getValorMinimo()))
            );
        }

        if (colStatus != null) {
            colStatus.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().isAtivo() ? "Ativo" : "Inativo")
            );
        }

        if (colValidade != null) {
            colValidade.setCellValueFactory(cell -> {
                if (cell.getValue().getValidade() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    return new SimpleStringProperty(sdf.format(cell.getValue().getValidade()));
                }
                return new SimpleStringProperty("-");
            });
        }
    }

    public void carregarTabela() {
        try {
            listaCupons.setAll(cupomDAO.listarTodos());
            if (tabelaCupons != null) {
                tabelaCupons.setItems(listaCupons);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar lista de cupons: " + e.getMessage());
        }
    }

    @FXML
    void voltarAoHub(ActionEvent event) {
        if (containerCentral == null) return;

        try {
            // Carrega o painel inicial/dashboard principal do sistema
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/Dashboard.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/dashboard.fxml");
            }

            if (url != null) {
                FXMLLoader loader = new FXMLLoader(url);
                Parent dashboardView = loader.load();

                Object controller = loader.getController();
                if (controller != null) {
                    try {
                        controller.getClass().getMethod("setContainerCentral", Pane.class).invoke(controller, containerCentral);
                        controller.getClass().getMethod("setFuncionarioLogado", Funcionario.class).invoke(controller, funcionarioLogado);
                    } catch (Exception ignored) {}
                }

                containerCentral.getChildren().setAll(dashboardView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível voltar ao Painel Principal.");
        }
    }

    @FXML
    void abrirTelaAdicionarCupom(ActionEvent event) {
        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/FormularioCupom.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/formularioCupom.fxml");
            }

            if (url == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Arquivo FormularioCupom.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent view = loader.load();

            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setContainerCentral", Pane.class).invoke(controller, containerCentral);
                    controller.getClass().getMethod("setFuncionarioLogado", Funcionario.class).invoke(controller, funcionarioLogado);
                } catch (Exception ignored) {}
            }

            if (containerCentral != null) {
                containerCentral.getChildren().setAll(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar o formulário: " + e.getMessage());
        }
    }

    @FXML
    void abrirSubjanelaEditarCupom(ActionEvent event) {
        if (tabelaCupons == null) return;

        Cupom selecionado = tabelaCupons.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção necessária", "Selecione um cupom na tabela para editar.");
            return;
        }

        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/EditarCupom.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/editarCupom.fxml");
            }

            if (url == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro FXML", "Arquivo EditarCupom.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent subtela = loader.load();

            Object controller = loader.getController();
            if (controller != null) {
                try {
                    controller.getClass().getMethod("setCupom", Cupom.class).invoke(controller, selecionado);
                } catch (Exception ignored) {}
            }

            Stage janelaAtual = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Stage subjanela = new Stage();
            subjanela.initOwner(janelaAtual);
            subjanela.initModality(Modality.WINDOW_MODAL);
            subjanela.setScene(new Scene(subtela));
            subjanela.setTitle("Editar Cupom - " + selecionado.getCodigo());
            subjanela.setResizable(false);
            subjanela.showAndWait();

            carregarTabela();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Carregamento", "Falha ao abrir subjanela de edição: " + e.getMessage());
        }
    }

    @FXML
    void acaoRemoverCupom(ActionEvent event) {
        if (tabelaCupons == null) return;

        Cupom selecionado = tabelaCupons.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção necessária", "Selecione um cupom na tabela para remover.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja realmente excluir o cupom '" + selecionado.getCodigo() + "'?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                boolean removido = cupomDAO.deletar(selecionado.getId());
                if (removido) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cupom removido com sucesso!");
                    carregarTabela();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível remover o cupom.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao remover cupom: " + e.getMessage());
            }
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