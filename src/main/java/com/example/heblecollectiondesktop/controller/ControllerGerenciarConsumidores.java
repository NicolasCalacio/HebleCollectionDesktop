package com.example.heblecollectiondesktop.controller;

import com.example.heblecollectiondesktop.model.Consumidores;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ControllerGerenciarConsumidores {
    @FXML
    private TableView <Consumidores> tabelaConsumidores;
    @FXML
    private TableColumn<Consumidores , Integer> colid;
    @FXML
    private TableColumn<Consumidores , String> colNome;
    @FXML
    private TableColumn<Consumidores , Integer> colCpf;
    @FXML
    private TableColumn<Consumidores , String> colStatus;



}
