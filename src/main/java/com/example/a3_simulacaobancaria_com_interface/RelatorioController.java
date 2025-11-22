package com.example.a3_simulacaobancaria_com_interface;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.util.Optional;

public class RelatorioController {

    @FXML
    private TextArea txtRelatorio;

    @FXML
    private Label lblInfo;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        carregarRelatorio();
    }

    private void carregarRelatorio() {
        String relatorio = Relatorio.getInstance().gerarRelatorioCompleto();
        txtRelatorio.setText(relatorio);
        lblInfo.setText("Registros carregados do arquivo: relatorio.dat");
    }

    @FXML
    private void onAtualizar() {
        carregarRelatorio();
    }

    @FXML
    private void onExportarTXT() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salvar Relatório como TXT");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivos TXT", "*.txt")
            );
            fileChooser.setInitialFileName("relatorio_sistema.txt");

            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(txtRelatorio.getText());
                }
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Sucesso");
                success.setHeaderText(null);
                success.setContentText("Relatório exportado com sucesso para: " + file.getAbsolutePath());
                success.showAndWait();
            }
        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erro");
            error.setHeaderText(null);
            error.setContentText("Erro ao exportar relatório: " + e.getMessage());
            error.showAndWait();
        }
    }

    @FXML
    private void onLimparRelatorio() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Limpeza");
        confirmacao.setHeaderText("Limpar Relatório Completo");
        confirmacao.setContentText("Tem certeza que deseja limpar todo o histórico do relatório? Esta ação não pode ser desfeita.");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Relatorio.getInstance().limparRelatorio();
            carregarRelatorio();
        }
    }

    @FXML
    private void onFechar() {
        if (stage != null) {
            stage.close();
        } else {
            Stage stageAtual = (Stage) txtRelatorio.getScene().getWindow();
            stageAtual.close();
        }
    }
}