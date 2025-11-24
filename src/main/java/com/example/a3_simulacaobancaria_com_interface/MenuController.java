package com.example.a3_simulacaobancaria_com_interface;



import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.util.List;
public class MenuController {

    public static FilaAtendimento filaGlobal = new FilaAtendimento();
    @FXML
    private void onAgendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AgendarAtendimentos.fxml"));
            Parent root = loader.load();

            AgendarAtendimentoController controller = loader.getController();
            controller.setFila(Menu.filaGlobal); // fila compartilhada

            Stage stage = new Stage();
            stage.setTitle("Agendar Atendimento");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onListar() {
        try {
            List<Cliente> lista = Menu.filaGlobal.getTodosClientes();
            int quantidadeClientes = lista.size();

            // Mede o tempo da ordenação
            long tempoOrdenacao = OrdenarAtendimentos.medirTempoApenas(() -> {
                OrdenarAtendimentos.ordenarCompleto(lista);
            });

            Menu.filaGlobal.ordenarFila(lista);

            // Registra no relatório com mais detalhes
            if (quantidadeClientes > 0) {
                Relatorio.getInstance().registrarOrdenacao(tempoOrdenacao, quantidadeClientes);


                double tempoMedio = (double) tempoOrdenacao / quantidadeClientes;
                String detalhes = String.format("Tempo médio por cliente: %.2f ms", tempoMedio);
                System.out.println("📊 " + detalhes);
            }

            System.out.printf("⏱️ Ordenação de %d clientes concluída em %d ms%n",
                    quantidadeClientes, tempoOrdenacao);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListarAtendimentos.fxml"));
            Parent root = loader.load();

            ListarAtendimentosController controller = loader.getController();
            controller.setFila(Menu.filaGlobal);

            Stage stage = new Stage();
            stage.setTitle("Lista de Atendimentos Pendentes");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onChamar() {
        try {
            // Verificar se há clientes na fila
            if (Menu.filaGlobal.filaVazia()) {
                mostrarMensagem("Não há clientes na fila de atendimento.");
                return;
            }

            // Chamar o próximo cliente (remove da fila)
            Cliente clienteChamado = Menu.filaGlobal.chamarProximo();

            if (clienteChamado != null) {
                // Abrir tela de atendimento
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AtendimentoCliente.fxml"));
                Parent root = loader.load();

                AtendimentoClienteController controller = loader.getController();
                controller.setCliente(clienteChamado);

                Stage stage = new Stage();
                stage.setTitle("Atendimento - " + clienteChamado.getNome());
                stage.setScene(new Scene(root));
                controller.setStage(stage);

                // Impedir que fechem a janela principal enquanto atende
                stage.setOnCloseRequest(e -> {
                    controller.onCancelar();
                });

                stage.show();

                // Atualizar a lista global se necessário
                mostrarMensagem("Cliente " + clienteChamado.getNome() + " chamado para atendimento!");

            } else {
                mostrarMensagem("Erro ao chamar próximo cliente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensagem("Erro ao chamar próximo cliente: " + e.getMessage());
        }
    }

    @FXML
    private void onPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um arquivo");

        // Aceita PDF e CSV
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF e CSV", "*.pdf", "*.csv"),
                new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Arquivos CSV", "*.csv")
        );

        File arquivo = fileChooser.showOpenDialog(null);
        if (arquivo == null) return;

        try {
            List<Cliente> clientesImportados;

            // Descobre automaticamente o tipo do arquivo
            String nomeArquivo = arquivo.getName().toLowerCase();

            if (nomeArquivo.endsWith(".pdf")) {
                clientesImportados = LeitorPDF.lerClientesDoPDF(arquivo.getAbsolutePath());
            }
            else if (nomeArquivo.endsWith(".csv")) {
                // ALTERAÇÃO AQUI: Usando o metodo que detecta automaticamente o formato
                clientesImportados = LeitorPDF.lerClientesDoCSVAuto(arquivo.getAbsolutePath());
            }
            else {
                throw new IllegalArgumentException("Formato desconhecido: apenas PDF ou CSV.");
            }

            // Adiciona à fila global
            for (Cliente c : clientesImportados) {
                Menu.filaGlobal.adicionarCliente(c);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Clientes importados com sucesso!");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao processar arquivo");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onOrdenar() {
        Menu.mudarTela("OrdenarAtendimentos.fxml");
    }
    @FXML
    private void onRelatorio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Relatorio.fxml"));
            Parent root = loader.load();

            RelatorioController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Relatório do Sistema");
            stage.setScene(new Scene(root));
            controller.setStage(stage);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarMensagem("Erro ao abrir relatório: " + e.getMessage());
        }
    }
    @FXML
    private void onSair() {

        // Salva a lista antes de fechar
        ArquivoClientes.SalvarClientes(Menu.filaGlobal.getTodosClientes());

        // Alerta visual opcional (pode remover se não quiser)
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText("Lista de clientes salva com sucesso!");
        a.showAndWait();
        System.exit(0);
    }

    private void mostrarMensagem(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

}
