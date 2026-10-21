package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.LogModeracao;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ControllerTelaInfoLog {
    @FXML
    Label lblId;
    @FXML
    Label lblData;
    @FXML
    Label lblModerador;
    @FXML
    Label lblAcao;
    @FXML
    Label lblAlvoAfetado;
    @FXML
    TextArea txtDetalhes;

    public void SetLog(LogModeracao log){
        if (log == null ){return;}
        lblId.setText("Id: "+ log.getId());
        lblData.setText("Data/Hora: "+ log.getDataAcao());
        lblModerador.setText("Moderador: "+ log.getFuncionarioId());
        lblAcao.setText("Ação: "+ log.getAcao());
        lblAlvoAfetado.setText("Alvo/Afetado: "+ log.getAlvoAfetado());
        txtDetalhes.setText(log.getDetalhes());
    }


}
