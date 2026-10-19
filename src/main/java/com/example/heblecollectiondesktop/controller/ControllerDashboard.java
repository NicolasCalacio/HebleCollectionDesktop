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

    @FXML
    private Label lblUsuarioLogado;
    @FXML
    private Label lblTotalProdutos;
    @FXML
    private Label lblCuponsAtivos;
    @FXML
    private Label lblTicketsAbertos;
    @FXML
    private Label lblStatusDetalhado;

    @FXML
    private Button btnVisaoGeral;
    @FXML
    private Button btnProdutos;
    @FXML
    private Button btnVendas;
    @FXML
    private Button btnCupons;
    @FXML
    private Button btnTickets;
    @FXML
    private Button btnConsumidores;
    @FXML
    private Button btnModeracao;
    @FXML
    private Button btnSair;

    @FXML
    private VBox vboxProdutos;
    @FXML
    private VBox containerAtividades;
    @FXML
    private Pane painelConteudoCentral;
    @FXML
    private VBox vboxTickets;

    private Node vistaInicialDashboard;
    private Funcionario funcionarioLogado;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final CupomDAO cupomDAO = new CupomDAO();
    private final TicketDAO ticketDAO = new TicketDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializações padrão, se necessário
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
            Cargo cargo = funcionarioLogado.getCargo();
            String papel = cargo != null ? cargo.name() : "FUNCIONARIO";

            lblUsuarioLogado.setText("Operador: " + funcionarioLogado.getMatricula() + " [" + papel + "]");

            boolean isGerente = (cargo == Cargo.GERENTE);
            boolean isSuporte = (cargo == Cargo.SUPORTE);
            boolean isFuncionario = (cargo == Cargo.FUNCIONARIO);

            if (btnModeracao != null) {
                btnModeracao.setVisible(isGerente);
                btnModeracao.setManaged(isGerente);
            }

            if (btnCupons != null) {
                btnCupons.setVisible(isGerente);
                btnCupons.setManaged(isGerente);
            }

            if (btnTickets != null) {
                btnTickets.setVisible(isGerente || isSuporte);
                btnTickets.setManaged(isGerente || isSuporte);
            }

            if (btnConsumidores != null) {
                btnConsumidores.setVisible(isGerente || isSuporte);
                btnConsumidores.setManaged(isGerente || isSuporte);
            }

            // FUNCIONÁRIO e GERENTE veem Produtos e Vendas
            if (btnProdutos != null) {
                btnProdutos.setVisible(isGerente || isFuncionario);
                btnProdutos.setManaged(isGerente || isFuncionario);
            }

            if (btnVendas != null) {
                btnVendas.setVisible(isGerente || isFuncionario);
                btnVendas.setManaged(isGerente || isFuncionario);
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
                                "Conexão com o banco estável.\n" +
                                "Cargo atual: " + funcionarioLogado.getCargo()
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
        Cargo cargo = funcionarioLogado != null ? funcionarioLogado.getCargo() : null;
        if (cargo != Cargo.GERENTE && cargo != Cargo.FUNCIONARIO) {
            mostrarAlerta("Acesso Negado", "Apenas Funcionários e Gerentes podem acessar o estoque/produtos.");
            return;
        }

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
    private void irparaVendas(ActionEvent event) {
        Cargo cargo = funcionarioLogado != null ? funcionarioLogado.getCargo() : null;
        if (cargo != Cargo.GERENTE && cargo != Cargo.FUNCIONARIO) {
            mostrarAlerta("Acesso Negado", "Apenas Funcionários e Gerentes podem acessar as vendas.");
            return;
        }

        // Implemente a navegação de vendas aqui conforme o seu projeto
        mostrarAlerta("Informação", "Módulo de Vendas em desenvolvimento.");
    }

    @FXML
    private void mouseircupons(MouseEvent event) {
        irparacupons();
    }

    @FXML
    private void irparacupons() {
        Cargo cargo = funcionarioLogado != null ? funcionarioLogado.getCargo() : null;
        if (cargo != Cargo.GERENTE) {
            mostrarAlerta("Acesso Negado", "Apenas Gerentes podem gerir cupons.");
            return;
        }

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
        Cargo cargo = funcionarioLogado != null ? funcionarioLogado.getCargo() : null;
        if (cargo != Cargo.GERENTE && cargo != Cargo.SUPORTE) {
            mostrarAlerta("Acesso Negado", "Apenas perfis de Suporte ou Gerência podem acessar os tickets.");
            return;
        }

        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/TicketsHub.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/ticketsHub.fxml");
            }

            if (url == null) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a central de tickets: " + e.getMessage());
        }
    }
    @FXML
    private void ExibirPerfil(ActionEvent event) {
        if (funcionarioLogado == null) {
            mostrarAlerta("Erro de Sessão", "Nenhum usuário logado detectado.");
            return;}
            try {
                URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/ExibirPerfil.fxml");
                if (url == null) {
                    url = getClass().getResource("/com/example/heblecollectiondesktop/view/exibirPerfil.fxml");
                }

                if (url == null) {
                    mostrarAlerta("Erro", "Não foi possível encontrar ExibirPerfil.fxml");
                    return;
                }

                FXMLLoader loader = new FXMLLoader(url);
                Parent hubView = loader.load();

                ControllerExibirPerfil controllerHub = loader.getController();
                if(controllerHub!=null){
                    controllerHub.setFuncionario(funcionarioLogado);
                }

                if (painelConteudoCentral != null) {
                    painelConteudoCentral.getChildren().setAll(hubView);
                }


            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro de Carregamento", "Falha ao carregar a tela de moderação: " + e.getMessage());
            }
        }


    @FXML
    private void abrirModeração(ActionEvent event) {
        Cargo cargo = funcionarioLogado != null ? funcionarioLogado.getCargo() : null;
        if (cargo != Cargo.GERENTE) {
            mostrarAlerta("Acesso Negado", "Apenas Gerentes têm acesso a esta área.");
            return;
        }

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
    }

    @FXML
    private void irConsumidores(ActionEvent event) {
        Cargo cargo = funcionarioLogado != null ? funcionarioLogado.getCargo() : null;
        if (cargo != Cargo.GERENTE && cargo != Cargo.SUPORTE) {
            mostrarAlerta("Acesso Negado", "Apenas perfis de Suporte ou Gerência podem gerir consumidores.");
            return;
        }

        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/telaConsumidores.fxml");
            if (url == null) {
                url = getClass().getResource("/com/example/heblecollectiondesktop/view/telaconsumidores.fxml");
            }

            if (url == null) {
                mostrarAlerta("Erro FXML", "Arquivo telaConsumidores.fxml não encontrado.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent hubView = loader.load();

            ControllerGerenciarConsumidores controllerconsumidores = loader.getController();
            if (controllerconsumidores != null) {
                controllerconsumidores.setContainerCentral(painelConteudoCentral);
                controllerconsumidores.setAcaovoltar(() -> handleVisaoGeral(null));
                if (funcionarioLogado != null) {
                    controllerconsumidores.setFuncionarioLogado(funcionarioLogado);
                }
            }

            if (painelConteudoCentral != null) {
                painelConteudoCentral.getChildren().setAll(hubView);
            }
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar a central de consumidores: " + e.getMessage());
        }
    }

    @FXML
    private void TicketsPendentes(MouseEvent event) {
        Cargo cargo = funcionarioLogado != null ? funcionarioLogado.getCargo() : null;
        if (cargo != Cargo.GERENTE && cargo != Cargo.SUPORTE) {
            mostrarAlerta("Acesso Negado", "Apenas perfis de Suporte ou Gerência podem acessar os tickets pendentes.");
            return;
        }

        try {
            URL url = getClass().getResource("/com/example/heblecollectiondesktop/view/ticketsPendentes.fxml");
            if (url == null) {
                mostrarAlerta("Erro FXML", "ticketsPendentes.fxml não encontrado.");
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
            mostrarAlerta("Erro de Carregamento", "Falha ao carregar os tickets: " + e.getMessage());
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
}