package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.database.EmpresaDAO;
import com.example.heblecollectiondesktop.database.LogsDAO;
import com.example.heblecollectiondesktop.model.Empresa;
import com.example.heblecollectiondesktop.model.Funcionario;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ControllerDeletarEmpresa {

    @FXML private Label lblFuncionarioInfo;
    @FXML private TextField txNomeEmpresa;
    @FXML private TextField txCNPJ;
    @FXML private TextField txEstilo;
    @FXML private ComboBox<String> cbMotivo;
    @FXML private TextArea txtObservacao;

    private final EmpresaDAO empresaDAO = new EmpresaDAO();
    private final LogsDAO logsDAO = new LogsDAO();

    private Empresa empresaParaDeletar;
    private ControllerGerenciarEmpresas controllerPai;
    private Funcionario funcionarioLogado;

    public void setDados(Empresa empresa, ControllerGerenciarEmpresas controllerPai, Funcionario funcionarioLogado) {
        this.empresaParaDeletar = empresa;
        this.controllerPai = controllerPai;
        this.funcionarioLogado = funcionarioLogado;

        if (empresa != null) {
            if (lblFuncionarioInfo != null) {
                lblFuncionarioInfo.setText("Excluindo: " + empresa.getNome() + " (CNPJ: " + empresa.getCnpj() + ")");
            }
            if (txNomeEmpresa != null) txNomeEmpresa.setText(empresa.getNome());
            if (txCNPJ != null) txCNPJ.setText(empresa.getCnpj());
            if (txEstilo != null) txEstilo.setText(empresa.getEstilo());
        }
    }

    public void setFuncionarioLogado(Funcionario funcionario) {
        this.funcionarioLogado = funcionario;
    }

    @FXML
    public void initialize() {
        if (cbMotivo != null) {
            cbMotivo.setItems(FXCollections.observableArrayList(
                    "Fechamento da empresa",
                    "Denúncias e tickets excederam o limite",
                    "Solicitação da própria empresa",
                    "Cadastro Duplicado / Incorreto",
                    "Violação das Políticas da Empresa",
                    "Outro"
            ));
        }
    }

    @FXML
    private void confirmarDelecao(ActionEvent event) {
        String motivo = cbMotivo.getValue();
        String observacao = txtObservacao.getText() != null ? txtObservacao.getText().trim() : "";

        if (motivo == null || motivo.isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Motivo Obrigatório", "Por favor, selecione um motivo para a exclusão.");
            return;
        }

        if (empresaParaDeletar == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nenhuma empresa selecionada para exclusão.");
            return;
        }

        try {
            boolean sucesso = empresaDAO.deletar(empresaParaDeletar.getId());

            if (sucesso) {
                String usuarioLog, autorMatricula = "SISTEMA";
                if (funcionarioLogado != null && funcionarioLogado.getMatricula() != null && !funcionarioLogado.getMatricula().isBlank()) {
                    autorMatricula = funcionarioLogado.getMatricula();
                }

                String alvoAfetado = empresaParaDeletar.getNome() + " (ID: " + empresaParaDeletar.getId() + ")";
                String detalhes = "Motivo: " + motivo + (observacao.isEmpty() ? "" : " | Obs: " + observacao);


                logsDAO.registrarLog(
                        autorMatricula,
                        "EXCLUSAO_EMPRESA",
                        alvoAfetado,
                        detalhes
                );

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Empresa removida com sucesso!");

                if (controllerPai != null) {
                    try {
                        controllerPai.carregarEmpresas();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                fecharJanela(event);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível concluir a exclusão da empresa.");
            }

        } catch (Exception e) {
            e.printStackTrace();

            String mensagemErro = e.getMessage();
            if (mensagemErro != null && (mensagemErro.contains("foreign key") || mensagemErro.contains("1451"))) {
                mostrarAlerta(Alert.AlertType.ERROR, "Violação de Integridade",
                        "Não é possível excluir a empresa '" + empresaParaDeletar.getNome() + "' pois existem funcionários, produtos ou registros vinculados a ela no sistema.");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Falha ao deletar empresa: " + mensagemErro);
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