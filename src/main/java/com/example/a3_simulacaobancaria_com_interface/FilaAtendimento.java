package com.example.a3_simulacaobancaria_com_interface;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import java.util.List;
public class FilaAtendimento {
    private Queue<Cliente> fila = new LinkedList<>();
    public void adicionarCliente(Cliente c)
    {
        fila.add(c);
    }

    public Cliente chamarProximo()
    {
        return fila.poll();
    }
    public boolean filaVazia()
    {
        return fila.isEmpty();
    }
    public void listarClientes() {
        if (fila.isEmpty()) {
            System.out.println("Nenhum cliente aguardando atendimento.");
        } else {
            for (Cliente c : fila) {
                System.out.println("Nome: " + c.getNome() + " | CPF: " + c.getCpf() + " | Prioridade: " + c.getPrioridade());
            }
        }
    }
    public List<Cliente> getTodosClientes()
    {
        return new ArrayList<>(fila);
    }
    public void ordenarFila(List<Cliente> listaOrdenada) {
        fila.clear();
        for (Cliente c : listaOrdenada) {
            fila.add(c);
        }
    }
}
