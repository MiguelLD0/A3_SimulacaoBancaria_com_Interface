package com.example.a3_simulacaobancaria_com_interface;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import java.util.List;

public class OrdenarAtendimentosController {

    private final FilaAtendimento fila = Menu.filaGlobal;

    @FXML
    public void ordenarPorNome() {
        List<Cliente> lista = fila.getTodosClientes();
        OrdenarAtendimentos.bubleSortPorNome(lista);
        fila.ordenarFila(lista);
        mensagem("Lista ordenada por Nome!");
    }

    @FXML
    public void ordenarPorCPF() {
        List<Cliente> lista = fila.getTodosClientes();
        OrdenarAtendimentos.selectionSortPorCPF(lista);
        fila.ordenarFila(lista);
        mensagem("Lista ordenada por CPF!");
    }

    @FXML
    public void ordenarPorPrioridade() {
        List<Cliente> lista = fila.getTodosClientes();

        // Agora o método recebe apenas a lista
        OrdenarAtendimentos.mergeSortPorPrioridade(lista);

        fila.ordenarFila(lista);
        mensagem("Lista ordenada por Prioridade!");
    }

    private void mensagem(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(texto);
        alert.showAndWait();
    }

    @FXML
    public void voltar() {
        Menu.mudarTela("menu-view.fxml");
    }
}
