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
    @FXML private DatePicker campoData;
    @FXML private TextField campoHora;
    @FXML private ComboBox<String> comboTipo;

    private FilaAtendimento fila;

    public void initialize() {
        // Configurar os RadioButtons
        ToggleGroup grupoDeficiencia = new ToggleGroup();
        rbSim.setToggleGroup(grupoDeficiencia);
        rbNao.setToggleGroup(grupoDeficiencia);
        rbNao.setSelected(true);

        // Configurar o ComboBox de tipos
        comboTipo.getItems().addAll("Comum", "Corporativo");
        comboTipo.setValue("Comum");
    }

    public void setFila(FilaAtendimento fila) {
        this.fila = fila;
    }

    @FXML
    public void onAgendar() {
        String nome = campoNome.getText().trim();
        String cpf = campoCPF.getText().trim();
        String hora = campoHora.getText().trim();
        String tipoSelecionado = comboTipo.getValue();

        if (nome.isEmpty() || cpf.isEmpty() || campoData.getValue() == null || hora.isEmpty()) {
            mostrarMensagem("Erro", "Preencha todos os campos!");
            return;
        }

        int prioridade = rbSim.isSelected() ? 0 : 1;

        // Gerar ID automático baseado no tipo
        String id = GeradorID.gerarID(tipoSelecionado);

        // Criar o cliente - note que o tipo é "Comum" ou "Corporativo", não o ID
        Cliente cliente = new Cliente(id, nome, tipoSelecionado, cpf, prioridade, campoData.getValue(), hora);

        fila.adicionarCliente(cliente);

        mostrarMensagem("Sucesso", "Atendimento agendado com sucesso!\nTipo: " + tipoSelecionado + "\nID: " + id);

        // Limpar campos após agendamento bem-sucedido
        limparCampos();

        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        stage.close();
    }

    private void limparCampos() {
        campoNome.clear();
        campoCPF.clear();
        campoHora.clear();
        campoData.setValue(null);
        comboTipo.setValue("Comum");
        rbNao.setSelected(true);
    }

    private void mostrarMensagem(String titulo, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}