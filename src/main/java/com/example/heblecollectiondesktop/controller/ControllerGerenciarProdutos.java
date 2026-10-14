package com.example.heblecollectiondesktop.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.database.ProdutoDAO;
import com.example.heblecollectiondesktop.model.Funcionario;
import com.example.heblecollectiondesktop.model.Produto;

import com.example.heblecollectiondesktop.model.Ticket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControllerGerenciarProdutos implements Initializable {

    @FXML private Button btnEditarProduto;
    @FXML private Button btnDeletarProduto;
    @FXML private Button btnBuscar;
    @FXML private TextField txtPesquisaProduto;

    @FXML private TableView<Produto> tvProdutos;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colEmpresa;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, String> colCategoria;
    @FXML private TableColumn<Produto, Double> colPreco;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, String> colStatus;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final LogsDAO logsDAO = new LogsDAO();
    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();

    private Pane containerCentral;
    private Funcionario funcionarioLogado;
    private Runnable acaoVoltar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabela();
        carregarProdutos();
    }

    public void setContainerCentral(Pane containerCentral) {
        this.containerCentral = containerCentral;
    }

    public void setFuncionarioLogado(Funcionario funcionario) {
        this.funcionarioLogado = funcionario;
    }

    public void setAcaoVoltar(Runnable acaoVoltar) {
        this.acaoVoltar = acaoVoltar;
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("nomeEmpresa"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tvProdutos.setItems(listaProdutos);
        configurarDuploClique();
    }

    public void carregarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.listarTodos();
            listaProdutos.setAll(produtos);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao carregar produtos: " + e.getMessage());
        }
    }

    @FXML
    void acaoDeletarProduto(ActionEvent event) {
        Produto selecionado = tvProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um produto para excluir.");
            return;
        }

        // Subtela modal com motivo
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Justificativa de Exclusão");
        dialog.setHeaderText("Informe o motivo para a exclusão do produto: " + selecionado.getNome());

        ButtonType btnConfirmar = new ButtonType("Confirmar Exclusão", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextArea txtMotivo = new TextArea();
        txtMotivo.setPromptText("Escreva a razão da exclusão aqui...");
        txtMotivo.setPrefRowCount(4);
        content.getChildren().addAll(new Label("Motivo da Exclusão:"), txtMotivo);

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnConfirmar) {
                return txtMotivo.getText().trim();
            }
            return null;
        });

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(motivo -> {
            if (motivo.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "É obrigatório fornecer uma justificativa para excluir o produto.");
                return;
            }

            try {
                if (produtoDAO.deletar(selecionado.getId())) {
                    listaProdutos.remove(selecionado);


                    String acao = "EXCLUSAO_PRODUTO";
                    String alvoAfetado = "PRODUTO_ID_" + selecionado.getId();
                    String detalhes = "Produto: " + selecionado.getNome() + " | Motivo: " + motivo;

                    logsDAO.registrarLog(funcionarioLogado, acao, alvoAfetado, detalhes);

                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto excluído e registrado no log de moderação.");
                }
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao deletar produto: " + e.getMessage());
            }
        });
    }

   @FXML
    void irParaAprovacoes(ActionEvent event) {
        try {
            // Garantido com a barra '/' no início do caminho do pacote
            URL fxmlUrl = getClass().getResource("/com/example/heblecollectiondesktop/view/AprovarProdutos.fxml");
            if (fxmlUrl == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Arquivo AprovarProdutos.fxml não foi encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent view = loader.load();

            ControllerAprovarProdutos controller = loader.getController();
            controller.setContainerCentral(containerCentral);
            controller.setFuncionarioLogado(funcionarioLogado);
            controller.setAcaoVoltar(() -> {
                if (containerCentral != null) {
                    containerCentral.getChildren().clear();

                    try {
                        URL reloadUrl = getClass().getResource("/com/example/heblecollectiondesktop/GerenciarProdutos.fxml");
                        if (reloadUrl != null) {
                            FXMLLoader reloadLoader = new FXMLLoader(reloadUrl);
                            Parent reloadView = reloadLoader.load();
                            ControllerGerenciarProdutos reloadCtrl = reloadLoader.getController();
                            reloadCtrl.setContainerCentral(containerCentral);
                            reloadCtrl.setFuncionarioLogado(funcionarioLogado);
                            containerCentral.getChildren().add(reloadView);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (containerCentral != null) {
                containerCentral.getChildren().clear();
                containerCentral.getChildren().add(view);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de aprovações: " + e.getMessage());
        }
    }

    @FXML
    void abrirModalEdicao(ActionEvent event) {
        Produto selecionado = tvProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Selecione um produto para editar.");
            return;
        }

        Dialog<Produto> dialog = new Dialog<>();
        dialog.setTitle("Editar Produto");
        dialog.setHeaderText("Altere as informações do produto: " + selecionado.getNome());

        ButtonType btnSalvar = new ButtonType("Salvar Alterações", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(btnSalvar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtNome = new TextField(selecionado.getNome());
        TextField txtDescricao = new TextField(selecionado.getDescricao());
        TextField txtPreco = new TextField(String.valueOf(selecionado.getPreco()));
        TextField txtEstoque = new TextField(String.valueOf(selecionado.getQuantidadeEstoque()));

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(txtNome, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(txtDescricao, 1, 1);
        grid.add(new Label("Preço (R$):"), 0, 2);
        grid.add(txtPreco, 1, 2);
        grid.add(new Label("Estoque:"), 0, 3);
        grid.add(txtEstoque, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSalvar) {
                try {
                    selecionado.setNome(txtNome.getText());
                    selecionado.setDescricao(txtDescricao.getText());
                    selecionado.setPreco(Double.parseDouble(txtPreco.getText()));
                    selecionado.setQuantidadeEstoque(Integer.parseInt(txtEstoque.getText()));
                    return selecionado;
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Erro de Entrada", "Insira valores numéricos válidos para Preço e Estoque.");
                }
            }
            return null;
        });

        Optional<Produto> resultado = dialog.showAndWait();
        resultado.ifPresent(produtoEditado -> {
            try {
                boolean atualizado = produtoDAO.atualizarProduto(produtoEditado);
                if (atualizado) {
                    tvProdutos.refresh();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
                }
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro ao Salvar", e.getMessage());
            }
        });
    }

    @FXML
    void acaoBuscarProduto(ActionEvent event) {
        String termo = txtPesquisaProduto.getText().trim();
        try {
            if (termo.isEmpty()) {
                carregarProdutos();
            } else {
                List<Produto> filtrados = produtoDAO.buscarPorTermo(termo);
                listaProdutos.setAll(filtrados);
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro na Busca", "Erro ao filtrar produtos: " + e.getMessage());
        }
    }

    @FXML
    void voltarAoHub(ActionEvent event) {
        if (acaoVoltar != null) {
            acaoVoltar.run();
        }
    }
    private void configurarDuploClique() {

        if ( tvProdutos== null) return;

        tvProdutos.setRowFactory(tv -> {
            TableRow<Produto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Produto selecionado = row.getItem();
                    abrirTelaInfo(selecionado);
                }
            });
            return row;
        });
    }
    @FXML
    private void abrirTelaInfo(Produto produto) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/view/InfoPopUpProdutos.fxml"));
            Parent root = loader.load();


            ControllerTelaInfoProdutos modalController = loader.getController();


            modalController.SetProduto(produto);


            Stage stage = new Stage();
            stage.setTitle("Detalhes do Produto #" + produto.getId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
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