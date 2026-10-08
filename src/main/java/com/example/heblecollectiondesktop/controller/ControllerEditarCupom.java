package com.example.heblecollectiondesktop.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.example.heblecollectiondesktop.database.CupomDAO;
import com.example.heblecollectiondesktop.model.Cupom;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerEditarCupom {

    @FXML
    private TextField txCodigo;

    @FXML
    private TextField txDescontoPercentual;

    @FXML
    private TextField txValorMinimo;

    @FXML
    private DatePicker dpValidade;

    @FXML
    private CheckBox chkAtivo;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnLimpar;

    @FXML
    private Button btnCancelar;

    private Cupom cupomEmEdicao;
    private final CupomDAO cupomDAO = new CupomDAO();

    // Recebe o cupom selecionado na tabela e preenche os campos
    public void setCupom(Cupom cupom) {
        this.cupomEmEdicao = cupom;

        if (cupom != null) {
            txCodigo.setText(cupom.getCodigo() != null ? cupom.getCodigo() : "");
            txDescontoPercentual.setText(String.valueOf(cupom.getDescontoPercentual()));
            txValorMinimo.setText(String.valueOf(cupom.getValorMinimo()));
            chkAtivo.setSelected(cupom.isAtivo());

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
    void EditarCupom(ActionEvent event) {
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
            boolean ativo = chkAtivo.isSelected();

            LocalDate localDate = dpValidade.getValue();
            Date validade = null;
            if (localDate != null) {
                validade = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            if (cupomEmEdicao == null) {
                cupomEmEdicao = new Cupom();
            }

            cupomEmEdicao.setCodigo(codigo);
            cupomEmEdicao.setDescontoPercentual(desconto);
            cupomEmEdicao.setValorMinimo(valorMin);
            cupomEmEdicao.setAtivo(ativo);
            cupomEmEdicao.setValidade(validade);

            // Atualização no banco de dados via DAO
            boolean sucesso = cupomDAO.atualizar(cupomEmEdicao);

            if (sucesso) {
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Cupom atualizado com sucesso!");
                fecharJanela();
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível atualizar o cupom no banco de dados.");
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
        chkAtivo.setSelected(false);
    }

    @FXML
    void Cancelar(ActionEvent event) {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = null;

        if (btnCancelar != null && btnCancelar.getScene() != null) {
            stage = (Stage) btnCancelar.getScene().getWindow();
        } else if (btnEditar != null && btnEditar.getScene() != null) {
            stage = (Stage) btnEditar.getScene().getWindow();
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