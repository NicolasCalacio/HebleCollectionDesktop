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

   public void AdicionarCupom(){
        String txcodigo = txCodigo.getText().trim();
       String  txtDescontoPercentual= txDescontoPercentual.getText().trim();
       String  txValorMin = txValorMinimo.getText().trim();

       if (txcodigo.isEmpty() || txtDescontoPercentual.isEmpty() || txValorMin.isEmpty()) {
           exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha o código, o desconto e o valor mínimo.");
           return;
       }
       try {

           double desconto = Double.parseDouble(txtDescontoPercentual.replace(",", "."));
           double valorMin = Double.parseDouble(txValorMin.replace(",", "."));


           if (desconto <= 0 || desconto > 100) {
               exibirAlerta(Alert.AlertType.WARNING, "Valor Inválido", "O desconto deve ser entre 0% e 100%.");
               return;
           }

           if (valorMin < 0) {
               exibirAlerta(Alert.AlertType.WARNING, "Valor Inválido", "O valor mínimo não pode ser negativo.");
               return;
           }


           LocalDate localDate = dpValidade.getValue();
           Date validade = null;
           if (localDate != null) {
               validade = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
           }


           Cupom novoCupom = new Cupom();
           novoCupom.setCodigo(txcodigo);
           novoCupom.setDescontoPercentual(desconto);
           novoCupom.setValorMinimo(valorMin);
           novoCupom.setValidade(validade);


           boolean sucesso = cupomDAO.salvar(novoCupom);

           if (sucesso) {
               exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "O cupom foi cadastrado com sucesso!");
               fecharJanela();
           } else {
               exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível cadastrar o cupom.");
           }

       } catch (NumberFormatException e) {
           exibirAlerta(Alert.AlertType.ERROR, "Erro de Formato", "Informe números válidos nos campos de Desconto e Valor Mínimo.");
       } catch (SQLException e) {
           exibirAlerta(Alert.AlertType.ERROR, "Erro no Banco de Dados", "Falha ao gravar cupom: " + e.getMessage());
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