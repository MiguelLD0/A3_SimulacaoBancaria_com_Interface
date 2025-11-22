package com.example.a3_simulacaobancaria_com_interface;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class AtendimentoClienteController {

    @FXML private Label lblNome;
    @FXML private Label lblCpf;
    @FXML private Label lblPrioridade;
    @FXML private Label lblData;
    @FXML private Label lblHora;
    @FXML private Label lblTempoAtendimento;
    @FXML private Button btnFinalizar;

    private Cliente cliente;
    private Stage stage;
    private LocalDateTime inicioAtendimento;
    private Timeline timeline;
    private long segundosDecorridos = 0;

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
        this.inicioAtendimento = LocalDateTime.now();
        exibirDadosCliente();
        iniciarTimer();

        // Registrar no relatório que o atendimento começou
        Relatorio.getInstance().registrarInicioAtendimento(cliente);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void exibirDadosCliente() {
        if (cliente != null) {
            lblNome.setText(cliente.getNome());
            lblCpf.setText(cliente.getCpf());
            lblPrioridade.setText(String.valueOf(cliente.getPrioridade()));

            if (cliente.getData() != null) {
                lblData.setText(cliente.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                lblData.setText("N/A");
            }

            lblHora.setText(cliente.getHora() != null ? cliente.getHora() : "N/A");
        }
    }

    private void iniciarTimer() {

        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> atualizarTimer()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void atualizarTimer() {
        segundosDecorridos++;
        long horas = segundosDecorridos / 3600;
        long minutos = (segundosDecorridos % 3600) / 60;
        long segundos = segundosDecorridos % 60;

        String tempoFormatado = String.format("%02d:%02d:%02d", horas, minutos, segundos);
        lblTempoAtendimento.setText(tempoFormatado);
    }

    @FXML
    private void onFinalizarAtendimento() {
        if (timeline != null) {
            timeline.stop();
        }

        // Calcular tempo total de atendimento
        LocalDateTime fimAtendimento = LocalDateTime.now();
        java.time.Duration tempoAtendimento = java.time.Duration.between(inicioAtendimento, fimAtendimento);

        // Registrar no relatório o tempo de atendimento
        Relatorio.getInstance().registrarFimAtendimento(cliente, tempoAtendimento);

        // Fechar a tela
        if (stage != null) {
            stage.close();
        } else {
            // Fechar a janela atual se stage não foi definido
            Stage currentStage = (Stage) btnFinalizar.getScene().getWindow();
            currentStage.close();
        }
    }

    @FXML
    void onCancelar() {
        if (timeline != null) {
            timeline.stop();
        }

        // Registrar cancelamento no relatório
        Relatorio.getInstance().registrarCancelamentoAtendimento(cliente, java.time.Duration.ofSeconds(segundosDecorridos));

        if (stage != null) {
            stage.close();
        } else {
            // Fechar a janela atual se stage não foi definido
            Stage currentStage = (Stage) btnFinalizar.getScene().getWindow();
            currentStage.close();
        }
    }
}