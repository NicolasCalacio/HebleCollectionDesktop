package com.example.heblecollectiondesktop.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.example.heblecollectiondesktop.database.CupomDAO;
import com.example.heblecollectiondesktop.database.ProdutoDAO;
import com.example.heblecollectiondesktop.database.TicketDAO;
import com.example.heblecollectiondesktop.model.Cargo;
import com.example.heblecollectiondesktop.model.Cupom;
import com.example.heblecollectiondesktop.model.Funcionario;
import com.example.heblecollectiondesktop.model.Produto;
import com.example.heblecollectiondesktop.model.Ticket;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControllerDashboard implements Initializable {

    @FXML private Label lblUsuarioLogado;
    @FXML private Label lblTotalProdutos;
    @FXML private Label lblCuponsAtivos;
    @FXML private Label lblTicketsAbertos;
    @FXML private Label lblStatusDetalhado;

    @FXML private Button btnVisaoGeral;
    @FXML private Button btnProdutos;
    @FXML private Button btnCupons;
    @FXML private Button btnTickets;
    @FXML private Button btnModeracao;
    @FXML private Button btnSair;
    @FXML private VBox vboxProdutos;
    @FXML private VBox containerAtividades;
    @FXML private Pane painelConteudoCentral;
    @FXML private VBox vboxTickets;

    private Node vistaInicialDashboard;
    private Funcionario funcionarioLogado;


    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CupomDAO cupomDAO = new CupomDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btnModeracao != null) {
            btnModeracao.setVisible(false);
            btnModeracao.setManaged(false);
        }
    }

    public void setFuncionarioLogado(Funcionario funcionario) {
        this.funcionarioLogado = funcionario;

        if (painelConteudoCentral != null && !painelConteudoCentral.getChildren().isEmpty()) {
            this.vistaInicialDashboard = painelConteudoCentral.getChildren().get(0);
        }

        atualizarInterface();
        carregarMetricasDoBanco();
    }

    private void atualizarInterface() {
        if (funcionarioLogado != null) {
            boolean isGerente = (funcionarioLogado.getCargo() == Cargo.GERENTE);
            String papel = isGerente ? "GERENTE" : "FUNCIONÁRIO";

            lblUsuarioLogado.setText("Operador: " + funcionarioLogado.getMatricula() + " [" + papel + "]");

            if (btnModeracao != null) {
                btnModeracao.setVisible(isGerente);
                btnModeracao.setManaged(isGerente);
            }
        }
    }

    private void carregarMetricasDoBanco() {
        try {
            List<Produto> produtos = produtoDAO.listarTodos();
            lblTotalProdutos.setText(produtos.size() + " itens");

            List<Cupom> cupons = cupomDAO.listarTodos();
            long cuponsAtivos = cupons.stream().filter(Cupom::isAtivo).count();
            lblCuponsAtivos.setText(cuponsAtivos + " ativos");

            List<Ticket> tickets = ticketDAO.listarTodos();
            lblTicketsAbertos.setText(tickets.size() + " registrados");

            if (funcionarioLogado != null) {
                lblStatusDetalhado.setText(
                        "Sessão ativa para matrícula " + funcionarioLogado.getMatricula() + ".\n" +
                                "Conexão com o banco estavel.\n" +
                                "Módulos disponíveis: Produtos, Cupons e Ticketing."
                );
            }

        } catch (Exception e) {
            lblStatusDetalhado.setText("Erro ao carregar dados do banco: " + e.getMessage());
        }
    }

    @FXML
    private void handleVisaoGeral(ActionEvent event) {
        if (painelConteudoCentral != null && vistaInicialDashboard != null) {
            painelConteudoCentral.getChildren().setAll(vistaInicialDashboard);
        }
        carregarMetricasDoBanco();
    }

    @FXML
    private void acaoirprodutos(ActionEvent event) {
        irparaprodutos();
    }

    @FXML
    private void mouseirprodutos(MouseEvent event) {
        irparaprodutos();
    }

    @FXML
    private void irparaprodutos() {
        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/GerenciarProdutos.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/gerenciarProdutos.fxml");
            }

            if (url == null) {
                mostrarAlerta("Erro FXML", "Arquivo GerenciarProdutos.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent produtosView = loader.load();

            ControllerGerenciarProdutos controllerProdutos = loader.getController();
            if (controllerProdutos != null) {
                controllerProdutos.setContainerCentral(painelConteudoCentral);
                controllerProdutos.setFuncionarioLogado(funcionarioLogado);
                controllerProdutos.setAcaoVoltar(() -> handleVisaoGeral(null));
            }

            if (painelConteudoCentral != null) {
                painelConteudoCentral.getChildren().setAll(produtosView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a gestão de produtos: " + e.getMessage());
        }
    }


    @FXML
    private void mouseircupons(MouseEvent event) {
        irparacupons();
    }

    @FXML
    private void irparacupons() {
        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/GerenciarCupons.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/gerenciarCupons.fxml");
            }

            if (url == null) {
                mostrarAlerta("Erro FXML", "Arquivo GerenciarCupons.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent cuponsView = loader.load();

            ControllerGerenciarCupons controllerCupons = loader.getController();
            if (controllerCupons != null) {
                controllerCupons.setContainerCentral(painelConteudoCentral);
                controllerCupons.setFuncionarioLogado(funcionarioLogado);
            }

            if (painelConteudoCentral != null) {
                painelConteudoCentral.getChildren().setAll(cuponsView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a gestão de cupons: " + e.getMessage());
        }
    }

    @FXML
    private void abrirTicketsHub(ActionEvent event) {
        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/TicketsHub.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/ticketsHub.fxml");
            }

            if (url == null) {
                System.err.println("❌ FXML NÃO ENCONTRADO NO PATH: TicketsHub.fxml");
                mostrarAlerta("Erro FXML", "Arquivo TicketsHub.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent hubView = loader.load();

            ControllerTicketsHub controllerHub = loader.getController();
            if (controllerHub != null) {
                controllerHub.setContainerCentral(painelConteudoCentral);
                if (funcionarioLogado != null) {
                    controllerHub.setFuncionarioLogado(funcionarioLogado);
                }
            }

            if (painelConteudoCentral != null) {
                painelConteudoCentral.getChildren().setAll(hubView);
            } else {
                System.err.println("❌ ERRO CRÍTICO: painelConteudoCentral está NULL!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a central de tickets: " + e.getMessage());
        }
    }

    @FXML
    private void abrirModeração(ActionEvent event) {
        if (funcionarioLogado == null) {
            mostrarAlerta("Erro de Sessão", "Nenhum usuário logado detectado.");
            return;
        }

        if (funcionarioLogado.getCargo() == Cargo.GERENTE) {
            try {
                URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/ModeracaoHub.fxml");
                if (url == null) {
                    url = getClass().getResource("/com/example/heblecollectiondesktop/view/moderacaoHub.fxml");
                }

                if (url == null) {
                    mostrarAlerta("Erro", "Não foi possível encontrar moderacaoHub.fxml.");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(url);
                Parent hubView = loader.load();

                ControllerModeracaoHub controllerHub = loader.getController();
                if (controllerHub != null) {
                    controllerHub.setContainerCentral(painelConteudoCentral);
                    controllerHub.setFuncionarioLogado(funcionarioLogado);
                }

                if (painelConteudoCentral != null) {
                    painelConteudoCentral.getChildren().setAll(hubView);
                }

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro de Carregamento", "Falha ao carregar a tela de moderação: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Acesso Negado", "Apenas Gerentes têm acesso a esta área.");
        }
    }

    @FXML
    private void handleSair(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/heblecollectiondesktop/login-view.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Heble Collection - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void fecharAplicacao(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Encerrar Sistema");
        alerta.setHeaderText(null);
        alerta.setContentText("Deseja realmente fechar o sistema?");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void TicketsPendentes(MouseEvent event) {
        try {
            URL url = getClass().getResource(
                    "/com/example/heblecollectiondesktop/view/ticketsPendentes.fxml"
            );

            if (url == null) {
                mostrarAlerta(
                        "Erro FXML",
                        "ticketsPendentes.fxml não encontrado."
                );
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent ticketsView = loader.load();

            ControllerTicketsPendentes controller = loader.getController();

            if (controller != null) {
                controller.setContainerCentral(painelConteudoCentral);
                controller.setFuncionarioLogado(funcionarioLogado);
            }

            painelConteudoCentral.getChildren().setAll(ticketsView);

        } catch (IOException e) {
            e.printStackTrace();

            mostrarAlerta(
                    "Erro de Carregamento",
                    "Falha ao carregar os tickets: " + e.getMessage()
            );
        }
    }
}