package com.example.heblecollectiondesktop.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.heblecollectiondesktop.database.VendaDAO;
import com.example.heblecollectiondesktop.model.ItemVenda;
import com.example.heblecollectiondesktop.model.Venda;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControllerVendas {

    @FXML
    private TableView<Venda> tvVendas;

    @FXML
    private TableColumn<Venda, Integer> colIdVenda;

    @FXML
    private TableColumn<Venda, Integer> colCliente;

    @FXML
    private TableColumn<Venda, LocalDateTime> colData;

    @FXML
    private TableColumn<Venda, BigDecimal> colTotal;

    @FXML
    private TableColumn<Venda, String> colStatusVenda;

    @FXML
    private Button btnRelatorioMes;

    @FXML
    private Button btnDetalhesVenda;

    private VendaDAO vendaDAO;

    @FXML
    public void initialize() {
        vendaDAO = new VendaDAO();

        colIdVenda.setCellValueFactory(new PropertyValueFactory<>("idvenda"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("consumidorId"));
        colData.setCellValueFactory(new PropertyValueFactory<>("criadoEm"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valorTotal"));
        colStatusVenda.setCellValueFactory(new PropertyValueFactory<>("statusVenda"));

        carregarVendas();
    }

    private void carregarVendas() {
        try {
            List<Venda> listaVendas = vendaDAO.listarTodas();
            ObservableList<Venda> observableVendas = FXCollections.observableArrayList(listaVendas);
            tvVendas.setItems(observableVendas);
        } catch (SQLException e) {
            exibirAlerta("Erro", "Não foi possível carregar as vendas do banco de dados: " + e.getMessage());
        }
    }

    @FXML
    private void acaoRelatorioMes(ActionEvent event) {
        try {
            List<Venda> todasVendas = vendaDAO.listarTodas();
            YearMonth mesAtual = YearMonth.now();

            BigDecimal valorTotalMes = BigDecimal.ZERO;
            int quantidadeVendasMes = 0;

            for (Venda v : todasVendas) {
                if (v.getCriadoEm() != null) {
                    YearMonth mesVenda = YearMonth.from(v.getCriadoEm());
                    if (mesVenda.equals(mesAtual)) {
                        if (v.getValorTotal() != null) {
                            valorTotalMes = valorTotalMes.add(v.getValorTotal());
                        }
                        quantidadeVendasMes++;
                    }
                }
            }

            BigDecimal mediaValor = BigDecimal.ZERO;
            if (quantidadeVendasMes > 0) {
                mediaValor = valorTotalMes.divide(BigDecimal.valueOf(quantidadeVendasMes), 2, RoundingMode.HALF_UP);
            }

            DateTimeFormatter formatterMes = DateTimeFormatter.ofPattern("MMMM / yyyy");
            String nomeMesAno = mesAtual.format(formatterMes).toUpperCase();

            abrirPopupRelatorio(nomeMesAno, quantidadeVendasMes, valorTotalMes, mediaValor);

        } catch (SQLException e) {
            exibirAlerta("Erro", "Não foi possível gerar o relatório: " + e.getMessage());
        }
    }

    @FXML
    private void acaoDetalhesVenda(ActionEvent event) {
        Venda vendaSelecionada = tvVendas.getSelectionModel().getSelectedItem();
        if (vendaSelecionada == null) {
            exibirAlerta("Seleção Necessária", "Por favor, selecione uma venda na tabela para visualizar os detalhes.");
            return;
        }

        try {
            List<ItemVenda> itens = vendaDAO.listarItensPorVenda(vendaSelecionada.getIdvenda());
            abrirPopupDetalhes(vendaSelecionada, itens);
        } catch (SQLException e) {
            exibirAlerta("Erro", "Falha ao carregar os itens da venda: " + e.getMessage());
        }
    }

    private void abrirPopupRelatorio(String mesAno, int qtdVendas, BigDecimal faturamentoTotal, BigDecimal mediaVenda) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Relatório Mensal de Vendas");

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 12;");
        layout.setPrefWidth(420);

        Label lblTitulo = new Label("📊 Relatório Financeiro");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #0F172A;");

        Label lblSubtitulo = new Label("Período: " + mesAno);
        lblSubtitulo.setStyle("-fx-text-fill: #64748B;");

        VBox cardTotal = criarCardMetrica("Faturamento Total do Mês", "R$ " + String.format("%.2f", faturamentoTotal), "#10B981");
        VBox cardQuantidade = criarCardMetrica("Total de Transações", qtdVendas + " vendas realizadas", "#3B82F6");
        VBox cardMedia = criarCardMetrica("Média por Venda (Ticket Médio)", "R$ " + String.format("%.2f", mediaVenda), "#D6B667");

        Button btnFechar = new Button("Fechar Relatório");
        btnFechar.setPrefHeight(38);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        // Aplicando o padrão de cor amarelo/dourado (#d6b667)
        btnFechar.setStyle("-fx-background-color: #d6b667; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        btnFechar.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(lblTitulo, lblSubtitulo, cardTotal, cardQuantidade, cardMedia, btnFechar);

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private void abrirPopupDetalhes(Venda venda, List<ItemVenda> itens) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Detalhes da Venda #" + venda.getIdvenda());

        VBox layout = new VBox(16);
        layout.setPadding(new Insets(24));
        layout.setStyle("-fx-background-color: #F8FAFC;");
        layout.setPrefSize(520, 500);

        Label lblTitulo = new Label("🧾 Detalhes da Venda #" + venda.getIdvenda());
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #0F172A;");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String dataStr = venda.getCriadoEm() != null ? venda.getCriadoEm().format(fmt) : "N/A";

        Label lblInfo = new Label(String.format("Cliente ID: %d  |  Data: %s  |  Status: %s",
                venda.getConsumidorId(), dataStr, venda.getStatusVenda()));
        lblInfo.setStyle("-fx-text-fill: #64748B; -fx-font-size: 13px;");

        TableView<ItemVenda> tvItens = new TableView<>();
        tvItens.setStyle("-fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");
        tvItens.setPrefHeight(200);

        TableColumn<ItemVenda, Integer> colProdId = new TableColumn<>("Prod. ID");
        colProdId.setCellValueFactory(new PropertyValueFactory<>("produtoId"));
        colProdId.setPrefWidth(90);

        TableColumn<ItemVenda, Integer> colQtd = new TableColumn<>("Qtd.");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setPrefWidth(80);

        TableColumn<ItemVenda, BigDecimal> colPreco = new TableColumn<>("Preço Unit.");
        colPreco.setCellValueFactory(new PropertyValueFactory<>("precoUnitario"));
        colPreco.setPrefWidth(120);

        tvItens.getColumns().addAll(colProdId, colQtd, colPreco);
        tvItens.setItems(FXCollections.observableArrayList(itens));

        VBox resumoValores = new VBox(6);
        resumoValores.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        Label lblSubtotal = new Label("Subtotal: R$ " + String.format("%.2f", venda.getValorSubtotal() != null ? venda.getValorSubtotal() : BigDecimal.ZERO));
        Label lblFrete = new Label("Frete: R$ " + String.format("%.2f", venda.getValorFrete() != null ? venda.getValorFrete() : BigDecimal.ZERO));
        Label lblDesconto = new Label("Desconto: R$ " + String.format("%.2f", venda.getValorDesconto() != null ? venda.getValorDesconto() : BigDecimal.ZERO));
        Label lblTotal = new Label("Total Final: R$ " + String.format("%.2f", venda.getValorTotal() != null ? venda.getValorTotal() : BigDecimal.ZERO));
        lblTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #10B981; -fx-font-size: 15px;");

        resumoValores.getChildren().addAll(lblSubtotal, lblFrete, lblDesconto, lblTotal);

        Button btnFechar = new Button("Fechar");
        btnFechar.setPrefHeight(38);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        // Aplicando o padrão de cor amarelo/dourado (#d6b667)
        btnFechar.setStyle("-fx-background-color: #d6b667; -fx-text-fill: white; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-weight: bold;");
        btnFechar.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(lblTitulo, lblInfo, tvItens, resumoValores, btnFechar);

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private VBox criarCardMetrica(String titulo, String valor, String corDestaque) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #E2E8F0; -fx-border-radius: 8;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        Label lblValor = new Label(valor);
        lblValor.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblValor.setStyle("-fx-text-fill: " + corDestaque + ";");

        card.getChildren().addAll(lblTitulo, lblValor);
        return card;
    }

    @FXML
    private void voltarAoHub(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/heblecollectiondesktop/view/dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            exibirAlerta("Erro", "Não foi possível retornar ao painel: " + e.getMessage());
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}