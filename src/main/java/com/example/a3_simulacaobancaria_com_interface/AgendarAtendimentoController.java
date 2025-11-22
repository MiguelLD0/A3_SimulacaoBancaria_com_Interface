package com.example.a3_simulacaobancaria_com_interface;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AgendarAtendimentoController {

    @FXML private TextField campoNome;
    @FXML private TextField campoCPF;
    @FXML private RadioButton rbSim;
    @FXML private RadioButton rbNao;
    @FXML private Button btnConfirmar;
    @FXML private DatePicker campoData;      // ADICIONADO
    @FXML private TextField campoHora;       // ADICIONADO

    private FilaAtendimento fila; // receberá a fila do Menu

    public void initialize() {
        ToggleGroup grupoDeficiencia = new ToggleGroup();
        rbSim.setToggleGroup(grupoDeficiencia);
        rbNao.setToggleGroup(grupoDeficiencia);

        rbNao.setSelected(true); // padrão = não
    }

    public void setFila(FilaAtendimento fila) {
        this.fila = fila;
    }

    @FXML
    public void onAgendar() {
        String nome = campoNome.getText().trim();
        String cpf = campoCPF.getText().trim();
        String hora = campoHora.getText().trim();

        // ----- Validações -----
        if (nome.isEmpty() || cpf.isEmpty() || campoData.getValue() == null || hora.isEmpty()) {
            mostrarMensagem("Erro", "Preencha todos os campos (nome, CPF, data e horário)!");
            return;
        }

        // prioridade: 0 = deficiente / 1 = normal
        int prioridade = rbSim.isSelected() ? 0 : 1;

        // Criar o cliente (agora com data/hora também)
        Cliente cliente = new Cliente(nome, cpf, prioridade, campoData.getValue(), hora);

        // Adiciona à fila global
        fila.adicionarCliente(cliente);

        mostrarMensagem("Sucesso", "Atendimento agendado com sucesso!");

        Stage stage = (Stage) btnConfirmar.getScene().getWindow();

        stage.close();
    }

    private void mostrarMensagem(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
