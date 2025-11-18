package com.example.a3_simulacaobancaria_com_interface;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ListarAtendimentosController {

    @FXML
    private TextArea txtLista;

    private FilaAtendimento fila;

    public void setFila(FilaAtendimento fila) {
        this.fila = fila;
        atualizarLista();
    }

    private void atualizarLista() {
        StringBuilder sb = new StringBuilder();

        if (fila.filaVazia()) {
            sb.append("Nenhum cliente aguardando atendimento.");
        } else {
            for (Cliente c : fila.getTodosClientes()) {
                sb.append("Nome: ").append(c.getNome())
                        .append(" | CPF: ").append(c.getCpf())
                        .append(" | Prioridade: ").append(c.getPrioridade())
                        .append("\n");
            }
        }

        txtLista.setText(sb.toString());
    }
}
