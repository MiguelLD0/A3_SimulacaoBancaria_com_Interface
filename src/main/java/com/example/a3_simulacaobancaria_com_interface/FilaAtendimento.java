package com.example.a3_simulacaobancaria_com_interface;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

public class FilaAtendimento {

    private Queue<Cliente> fila = new LinkedList<>();

    public void adicionarCliente(Cliente c) {
        fila.add(c);
        Relatorio.getInstance().registrarAdicaoCliente(c);
        System.out.println("Cliente adicionado: " + c.getNome());
    }

    public Cliente chamarProximo() {
        return fila.poll();
    }

    public boolean filaVazia() {
        return fila.isEmpty();
    }

    // 🔵 LISTAR CLIENTES — agora exibe ID e Tipo
    public void listarClientes() {
        if (fila.isEmpty()) {
            System.out.println("Nenhum cliente aguardando atendimento.");
        } else {
            for (Cliente c : fila) {
                System.out.println(
                        "ID: " + c.getId() +
                                " | Nome: " + c.getNome() +
                                " | Tipo: " + c.getTipo() +
                                " | CPF: " + c.getCpf() +
                                " | Prioridade: " + c.getPrioridade()
                );
            }
        }
    }

    public List<Cliente> getTodosClientes() {
        return new ArrayList<>(fila);
    }

    public void ordenarFila(List<Cliente> listaOrdenada) {
        fila.clear();
        fila.addAll(listaOrdenada);
    }

    public void removerAtendimentosVencidos() {
        LocalDateTime agora = LocalDateTime.now();
        List<Cliente> manter = new ArrayList<>();
        List<Cliente> removidos = new ArrayList<>();

        for (Cliente cliente : fila) {
            LocalDate data = cliente.getData();
            String hora = cliente.getHora();

            if (data == null || hora == null) {
                manter.add(cliente);
                continue;
            }

            try {
                LocalTime horario = LocalTime.parse(hora);
                LocalDateTime dataHora = LocalDateTime.of(data, horario);

                if (dataHora.isAfter(agora)) {
                    manter.add(cliente);
                } else {
                    removidos.add(cliente);
                }

            } catch (Exception e) {
                manter.add(cliente);
            }
        }

        for (Cliente r : removidos) {
            Relatorio.getInstance().registrarRemocao(r, "Tempo expirado");
            Relatorio.getInstance().registrarTempoFila(r, r.getDataHoraAdicao());
        }

        fila.clear();
        fila.addAll(manter);
    }

    public boolean removerCliente(Cliente cliente) {
        boolean removido = fila.remove(cliente);

        if (removido) {
            Relatorio.getInstance().registrarRemocao(cliente, "Removido manualmente");
        }

        return removido;
    }

    public boolean removerCliente(int indice) {
        List<Cliente> temp = new ArrayList<>(fila);

        if (indice >= 0 && indice < temp.size()) {
            Cliente removido = temp.remove(indice);
            fila.clear();
            fila.addAll(temp);

            Relatorio.getInstance().registrarRemocao(removido, "Removido por índice");
            return true;
        }
        return false;
    }

}
