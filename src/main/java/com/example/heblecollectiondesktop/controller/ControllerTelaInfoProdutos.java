package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ControllerTelaInfoProdutos {

    @FXML
    private Label lblId;
    @FXML
    private Label lblEmpresaProduto;
    @FXML
    private Label lblNome;

    @FXML
    private Label lblPreco;
    @FXML
    private Label lblEstoque;
    @FXML
    private Label lblStatus;
    @FXML
    private TextArea txtDescricao;

    public void SetProduto(Produto produto){
        if (produto == null){return;}
        lblId.setText("ID: "+ produto.getId());
        lblEmpresaProduto.setText("Empresa do Produto: "+ produto.getNomeEmpresa());
        lblNome.setText("Nome: "+ produto.getNome());
        txtDescricao.setText(produto.getDescricao());
        lblPreco.setText("Preço: "+produto.getPreco());
        lblEstoque.setText("Quantidade em Estoque: "+ produto.getQuantidadeEstoque());
        lblStatus.setText("Status do Produto: "+produto.getStatus());

    }
}
