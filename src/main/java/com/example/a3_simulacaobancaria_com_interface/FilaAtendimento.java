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
    public void adicionarCliente(Cliente c)
    {
        fila.add(c);
        // Registra a adição no relatório
        Relatorio.getInstance().registrarAdicaoCliente(c);
        System.out.println("Cliente adicionado: " + c.getNome());
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
    public void removerAtendimentosVencidos() {
        LocalDateTime agora = LocalDateTime.now();
        List<Cliente> clientesParaManter = new ArrayList<>();
        List<Cliente> clientesRemovidos = new ArrayList<>();

        for (Cliente cliente : fila) {
            LocalDate data = cliente.getData();
            String hora = cliente.getHora();

            if (data == null || hora == null) {
                clientesParaManter.add(cliente);
                continue;
            }

            try {
                LocalTime horario = LocalTime.parse(hora);
                LocalDateTime dataHora = LocalDateTime.of(data, horario);

                if (dataHora.isAfter(agora)) {
                    clientesParaManter.add(cliente);
                } else {
                    // Cliente vencido - será removido
                    clientesRemovidos.add(cliente);
                }
            } catch (Exception e) {
                clientesParaManter.add(cliente);
            }
        }

        // Remove os clientes vencidos e registra no relatório
        for (Cliente clienteRemovido : clientesRemovidos) {
            Relatorio.getInstance().registrarRemocao(clienteRemovido, "Tempo expirado");
            Relatorio.getInstance().registrarTempoFila(clienteRemovido, clienteRemovido.getDataHoraAdicao());
        }

        fila.clear();
        fila.addAll(clientesParaManter);
    }

    public boolean removerCliente(Cliente cliente) {
        System.out.println("=== TENTANDO REMOVER CLIENTE DA FILA ===");
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Fila antes: " + fila.size() + " clientes");

        boolean removido = fila.remove(cliente);

        System.out.println("Remoção bem sucedida: " + removido);
        System.out.println("Fila depois: " + fila.size() + " clientes");

        if (removido) {
            // Registra a remoção manual no relatório
            System.out.println("Chamando relatório para registrar remoção...");
            Relatorio.getInstance().registrarRemocao(cliente, "Removido manualmente");
        } else {
            System.out.println("FALHA: Cliente não encontrado na fila para remoção");
        }

        return removido;
    }

    public boolean removerCliente(int indice) {
        List<Cliente> listaTemp = new ArrayList<>(fila);
        if (indice >= 0 && indice < listaTemp.size()) {
            Cliente clienteRemovido = listaTemp.remove(indice);
            fila.clear();
            fila.addAll(listaTemp);

            // Registra a remoção por índice no relatório
            Relatorio.getInstance().registrarRemocao(clienteRemovido, "Removido manualmente por índice");

            return true;
        }
        return false;
    }

}

