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
        mostrarMensagem("Chamando próximo cliente...");
    }

    @FXML
    private void onPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione um arquivo PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos PDF", "*.pdf")
        );

        File arquivo = fileChooser.showOpenDialog(null);

        if (arquivo != null) {
            try {
                List<Cliente> clientesPDF = LeitorPDF.lerClientesDoPDF(arquivo.getAbsolutePath());

                for (Cliente c : clientesPDF) {
                    Menu.filaGlobal.adicionarCliente(c);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Clientes importados do PDF com sucesso!");
                alert.showAndWait();

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Erro ao processar o PDF");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onOrdenar() {
        Menu.mudarTela("OrdenarAtendimentos.fxml");
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
