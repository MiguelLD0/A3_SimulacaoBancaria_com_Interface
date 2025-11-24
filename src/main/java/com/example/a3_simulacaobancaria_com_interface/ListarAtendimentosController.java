package com.example.a3_simulacaobancaria_com_interface;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListarAtendimentosController {

    @FXML
    private ListView<Cliente> listaAtendimentos;

    @FXML
    private Button btnRemover;

    @FXML
    private TextField campoPesquisa;

    private FilaAtendimento fila;
    private Stage stage;
    private List<Cliente> todosClientes;

    public void setFila(FilaAtendimento fila) {
        this.fila = fila;
        atualizarLista();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        todosClientes = new ArrayList<>();

        // Configurar a ListView para permitir seleção única
        listaAtendimentos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Configurar células personalizadas com design bonito
        listaAtendimentos.setCellFactory(new Callback<ListView<Cliente>, ListCell<Cliente>>() {
            @Override
            public ListCell<Cliente> call(ListView<Cliente> listView) {
                return new ListCell<Cliente>() {
                    @Override
                    protected void updateItem(Cliente cliente, boolean empty) {
                        super.updateItem(cliente, empty);

                        if (empty || cliente == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            // Criar o card personalizado
                            VBox cardContainer = new VBox();
                            cardContainer.getStyleClass().add("card-container");

                            // Linha do nome e prioridade
                            HBox nomeLine = new HBox();
                            nomeLine.getStyleClass().add("nome-line");

                            Label nomeLabel = new Label(cliente.getNome());
                            nomeLabel.getStyleClass().add("nome-text");

                            Label prioridadeBadge = new Label("Prioridade " + cliente.getPrioridade());
                            prioridadeBadge.getStyleClass().add("prioridade-badge");
                            prioridadeBadge.getStyleClass().add("prioridade-" + Math.min(cliente.getPrioridade(), 3));

                            nomeLine.getChildren().addAll(nomeLabel, prioridadeBadge);

                            // Linha de detalhes (CPF)
                            HBox detalhesLine = new HBox();
                            detalhesLine.getStyleClass().add("detalhes-line");

                            VBox cpfItem = new VBox();
                            cpfItem.getStyleClass().add("detalhe-item");
                            Label cpfLabel = new Label("CPF:");
                            cpfLabel.getStyleClass().add("detalhe-label");
                            Label cpfValue = new Label(cliente.getCpf());
                            cpfValue.getStyleClass().add("detalhe-value");
                            cpfItem.getChildren().addAll(cpfLabel, cpfValue);

                            detalhesLine.getChildren().add(cpfItem);

                            // Linha de data/hora
                            HBox dataHoraLine = new HBox();
                            dataHoraLine.getStyleClass().add("datahora-line");

                            VBox dataItem = new VBox();
                            dataItem.getStyleClass().add("datahora-item");
                            Label dataLabel = new Label("Data:");
                            dataLabel.getStyleClass().add("datahora-label");
                            Label dataValue = new Label(cliente.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            dataValue.getStyleClass().add("datahora-value");
                            dataItem.getChildren().addAll(dataLabel, dataValue);

                            VBox horaItem = new VBox();
                            horaItem.getStyleClass().add("datahora-item");
                            Label horaLabel = new Label("Hora:");
                            horaLabel.getStyleClass().add("datahora-label");
                            Label horaValue = new Label(cliente.getHora());
                            horaValue.getStyleClass().add("datahora-value");
                            horaItem.getChildren().addAll(horaLabel, horaValue);

                            dataHoraLine.getChildren().addAll(dataItem, horaItem);

                            // Adicionar tudo ao card
                            cardContainer.getChildren().addAll(nomeLine, detalhesLine, dataHoraLine);

                            // Container final do card
                            VBox finalCard = new VBox(cardContainer);
                            finalCard.getStyleClass().add("cliente-card");
                            finalCard.setPrefWidth(550);

                            setGraphic(finalCard);
                            setText(null);
                        }
                    }
                };
            }
        });

        // Listener para habilitar/desabilitar o botão baseado na seleção
        listaAtendimentos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    btnRemover.setDisable(newValue == null);
                }
        );
    }

    private void atualizarLista() {
        if (fila != null) {
            // REMOVE AUTOMATICAMENTE OS ATENDIMENTOS VENCIDOS
            fila.removerAtendimentosVencidos();

            todosClientes = fila.getTodosClientes();
            listaAtendimentos.getItems().setAll(todosClientes);

            // Se não há clientes, desabilita o botão de remover
            if (todosClientes.isEmpty()) {
                btnRemover.setDisable(true);
            }
        }
    }

    @FXML
    private void onPesquisar() {
        String termoPesquisa = campoPesquisa.getText().trim().toLowerCase();

        if (termoPesquisa.isEmpty()) {
            listaAtendimentos.getItems().setAll(todosClientes);
        } else {
            List<Cliente> clientesFiltrados = new ArrayList<>();
            for (Cliente cliente : todosClientes) {
                if (cliente.getNome().toLowerCase().contains(termoPesquisa) ||
                        cliente.getCpf().toLowerCase().contains(termoPesquisa) ||
                        String.valueOf(cliente.getPrioridade()).contains(termoPesquisa)) {
                    clientesFiltrados.add(cliente);
                }
            }
            listaAtendimentos.getItems().setAll(clientesFiltrados);

            if (clientesFiltrados.isEmpty()) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Pesquisa");
                info.setHeaderText(null);
                info.setContentText("Nenhum cliente encontrado com: " + termoPesquisa);
                info.showAndWait();
            }
        }
    }

    @FXML
    private void onLimparPesquisa() {
        campoPesquisa.clear();
        listaAtendimentos.getItems().setAll(todosClientes);
    }

    @FXML
    private void onRemoverSelecionado() {
        Cliente clienteSelecionado = listaAtendimentos.getSelectionModel().getSelectedItem();

        if (clienteSelecionado != null) {
            System.out.println("=== BOTÃO REMOVER CLICADO ===");
            System.out.println("Cliente selecionado: " + clienteSelecionado.getNome());
            System.out.println("DataHoraAdicao: " + clienteSelecionado.getDataHoraAdicao());

            // Confirmar remoção
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Remoção");
            confirmacao.setHeaderText("Remover Atendimento");
            confirmacao.setContentText(String.format("Tem certeza que deseja remover %s da fila?", clienteSelecionado.getNome()));

            // Mostrar diálogo e esperar resposta
            Optional<ButtonType> resultado = confirmacao.showAndWait();

            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                System.out.println("Usuário confirmou a remoção");

                // Remover da fila
                boolean removido = fila.removerCliente(clienteSelecionado);

                if (removido) {
                    System.out.println("Cliente removido com sucesso da fila");
                    // Atualizar a lista
                    atualizarLista();

                    // Mostrar mensagem de sucesso
                    Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                    sucesso.setTitle("Sucesso");
                    sucesso.setHeaderText(null);
                    sucesso.setContentText("Atendimento removido com sucesso!\nVerifique o relatório para detalhes.");
                    sucesso.showAndWait();
                } else {
                    System.out.println("FALHA: Não foi possível remover o cliente da fila");
                    // Mostrar mensagem de erro
                    Alert erro = new Alert(Alert.AlertType.ERROR);
                    erro.setTitle("Erro");
                    erro.setHeaderText(null);
                    erro.setContentText("Erro ao remover o atendimento. O cliente não foi encontrado na fila.");
                    erro.showAndWait();
                }
            } else {
                System.out.println("Usuário cancelou a remoção");
            }
        } else {
            System.out.println("Nenhum cliente selecionado para remover");
        }
    }

    @FXML
    private void onFechar() {
        if (stage != null) {
            stage.close();
        } else {
            Stage stageAtual = (Stage) listaAtendimentos.getScene().getWindow();
            stageAtual.close();
        }
    }
    @FXML
    public void ordenarPorNome2() {
        List<Cliente> lista = fila.getTodosClientes();
        OrdenarAtendimentos.bubleSortPorNome(lista);
        fila.ordenarFila(lista);
        atualizarLista();
        mensagem("Lista ordenada por Nome!");
    }

    @FXML
    public void ordenarPorCPF2() {
        List<Cliente> lista = fila.getTodosClientes();
        OrdenarAtendimentos.selectionSortPorCPF(lista);
        fila.ordenarFila(lista);
        atualizarLista();
        mensagem("Lista ordenada por CPF!");
    }

    @FXML
    public void ordenarPorPrioridade2() {
        List<Cliente> lista = fila.getTodosClientes();

        // Agora o método recebe apenas a lista
        OrdenarAtendimentos.mergeSortPorPrioridade(lista);

        fila.ordenarFila(lista);
        atualizarLista();
        mensagem("Lista ordenada por Prioridade!");
    }
    @FXML
    public void ordenardata()
    {
        List<Cliente> lista = fila.getTodosClientes();

        OrdenarAtendimentos.ordenarCompleto(lista);
        fila.ordenarFila(lista);
        atualizarLista();
        mensagem("Lista ordenada por Prioridade!");
    }

    private void mensagem(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(texto);
        alert.showAndWait();
    }
}