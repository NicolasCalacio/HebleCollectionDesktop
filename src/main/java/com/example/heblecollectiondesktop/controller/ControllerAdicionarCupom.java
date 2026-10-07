package com.example.heblecollectiondesktop.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.example.heblecollectiondesktop.database.CupomDAO;
import com.example.heblecollectiondesktop.database.conexaoDB;
import com.example.heblecollectiondesktop.model.Cupom;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerAdicionarCupom {

    @FXML
    private TextField txCodigo;

    @FXML
    private TextField txDescontoPercentual;

    @FXML
    private TextField txValorMinimo;

    @FXML
    private DatePicker dpValidade;


    @FXML
    private Button btnAdicionar;

    @FXML
    private Button btnLimpar;

    @FXML
    private Button btnCancelar;

    private Cupom CupomAdicionado;
    private final CupomDAO cupomDAO = new CupomDAO();

    // Recebe o cupom selecionado na tabela e preenche os campos
    public void setCupom(Cupom cupom) {
        this.CupomAdicionado = cupom;

        if (cupom != null) {
            txCodigo.setText(cupom.getCodigo() != null ? cupom.getCodigo() : "");
            txDescontoPercentual.setText(String.valueOf(cupom.getDescontoPercentual()));
            txValorMinimo.setText(String.valueOf(cupom.getValorMinimo()));

            if (cupom.getValidade() != null) {
                LocalDate dataLocal = cupom.getValidade().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                dpValidade.setValue(dataLocal);
            } else {
                dpValidade.setValue(null);
            }
        }
    }

    @FXML
    void AdicionarCupom(ActionEvent event) {
        String codigo = txCodigo.getText().trim();
        String txtDesconto = txDescontoPercentual.getText().trim();
        String txtValorMin = txValorMinimo.getText().trim();

        // Validação de campos obrigatórios
        if (codigo.isEmpty() || txtDesconto.isEmpty() || txtValorMin.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha o código, o desconto e o valor mínimo.");
            return;
        }

        try {
            double desconto = Double.parseDouble(txtDesconto.replace(",", "."));
            double valorMin = Double.parseDouble(txtValorMin.replace(",", "."));


            LocalDate localDate = dpValidade.getValue();
            Date validade = null;
            if (localDate != null) {
                validade = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            if (CupomAdicionado == null) {
                CupomAdicionado = new Cupom();
            }




            // Atualização no banco de dados via DAO
            boolean sucesso = cupomDAO.atualizar(CupomAdicionado);

            if (sucesso) {
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "O cupom foi cadastrado com sucesso!");
                fecharJanela();
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível adicionar esse  cupom!.");
            }

        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Insira valores numéricos válidos para Desconto e Valor Mínimo.");
        } catch (SQLException e) {
            e.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao salvar as alterações: " + e.getMessage());
        }
    }

    @FXML
    void Limpar(ActionEvent event) {
        txCodigo.clear();
        txDescontoPercentual.clear();
        txValorMinimo.clear();
        dpValidade.setValue(null);

    }

    @FXML
    void Cancelar(ActionEvent event) {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = null;

        if (btnCancelar != null && btnCancelar.getScene() != null) {
            stage = (Stage) btnCancelar.getScene().getWindow();
        } else if (btnAdicionar != null && btnAdicionar.getScene() != null) {
            stage = (Stage) btnAdicionar.getScene().getWindow();
        }

        if (stage != null) {
            stage.close();
        }
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}