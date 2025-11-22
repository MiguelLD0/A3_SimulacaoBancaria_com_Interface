package com.example.a3_simulacaobancaria_com_interface;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdenarAtendimentos {

    // -------------------------------------------------------------------------
    // ========================= BUBBLE SORT POR NOME ==========================
    // -------------------------------------------------------------------------
    public static void bubleSortPorNome(List<Cliente> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getNome().compareToIgnoreCase(lista.get(j + 1).getNome()) > 0) {
                    Cliente tmp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, tmp);
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // ======================= SELECTION SORT POR CPF ==========================
    // -------------------------------------------------------------------------
    public static void selectionSortPorCPF(List<Cliente> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < n; j++) {
                if (lista.get(j).getCpf().compareTo(lista.get(menor).getCpf()) < 0) {
                    menor = j;
                }
            }
            Cliente tmp = lista.get(i);
            lista.set(i, lista.get(menor));
            lista.set(menor, tmp);
        }
    }

    // -------------------------------------------------------------------------
    // ====================== MERGE SORT POR PRIORIDADE ========================
    // ---------- (substitui o QuickSort para manter estabilidade) ------------
    // -------------------------------------------------------------------------

    public static void mergeSortPorPrioridade(List<Cliente> lista) {
        if (lista == null || lista.size() <= 1) return;
        mergeSortPrioridade(lista, 0, lista.size() - 1);
    }

    private static void mergeSortPrioridade(List<Cliente> lista, int inicio, int fim) {
        if (inicio < fim) {
            int meio = (inicio + fim) / 2;
            mergeSortPrioridade(lista, inicio, meio);
            mergeSortPrioridade(lista, meio + 1, fim);
            mergePrioridade(lista, inicio, meio, fim);
        }
    }

    private static void mergePrioridade(List<Cliente> lista, int inicio, int meio, int fim) {
        List<Cliente> temp = new ArrayList<>(lista.subList(inicio, fim + 1));

        int i = 0;
        int j = meio - inicio + 1;
        int k = inicio;

        while (i <= meio - inicio && j <= fim - inicio) {
            if (temp.get(i).getPrioridade() <= temp.get(j).getPrioridade()) {
                lista.set(k++, temp.get(i++));
            } else {
                lista.set(k++, temp.get(j++));
            }
        }

        while (i <= meio - inicio)
            lista.set(k++, temp.get(i++));

        while (j <= fim - inicio)
            lista.set(k++, temp.get(j++));
    }

    // -------------------------------------------------------------------------
    // ======= MÉTODO FINAL — ORDENAR POR DATA → HORA → PRIORIDADE ============
    // -------------------------------------------------------------------------

    public static void ordenarAtendimentos(List<Cliente> lista) {
        if (lista == null || lista.size() <= 1) return;
        mergeSortDataHoraPrioridade(lista, 0, lista.size() - 1);
    }

    private static void mergeSortDataHoraPrioridade(List<Cliente> lista, int inicio, int fim) {
        if (inicio < fim) {
            int meio = (inicio + fim) / 2;
            mergeSortDataHoraPrioridade(lista, inicio, meio);
            mergeSortDataHoraPrioridade(lista, meio + 1, fim);
            mergeDataHoraPrioridade(lista, inicio, meio, fim);
        }
    }

    private static void mergeDataHoraPrioridade(List<Cliente> lista, int inicio, int meio, int fim) {
        List<Cliente> temp = new ArrayList<>(lista.subList(inicio, fim + 1));

        int i = 0;
        int j = meio - inicio + 1;
        int k = inicio;

        while (i <= meio - inicio && j <= fim - inicio) {
            if (compararClientes(temp.get(i), temp.get(j)) <= 0) {
                lista.set(k++, temp.get(i++));
            } else {
                lista.set(k++, temp.get(j++));
            }
        }

        while (i <= meio - inicio)
            lista.set(k++, temp.get(i++));

        while (j <= fim - inicio)
            lista.set(k++, temp.get(j++));
    }

    // -------------------------------------------------------------------------
    // ===================== CRITÉRIO DE COMPARAÇÃO FINAL ======================
    // -------------------------------------------------------------------------

    private static int compararClientes(Cliente a, Cliente b) {

        // 1) DATA
        int cmpData = a.getData().compareTo(b.getData());
        if (cmpData != 0) return cmpData;

        // 2) HORA
        int cmpHora = a.getHora().compareTo(b.getHora());
        if (cmpHora != 0) return cmpHora;

        // 3) PRIORIDADE (menor = mais urgente)
        int cmpPrioridade = Integer.compare(a.getPrioridade(), b.getPrioridade());
        if (cmpPrioridade != 0) return cmpPrioridade;

        // 4) Estabilidade garantida pelo Merge Sort

        return 0;
    }
    public static void ordenarCompleto(List<Cliente> lista) {
        lista.sort(Comparator
                .comparing(Cliente::getData)
                .thenComparing(Cliente::getHora)
                .thenComparing(Cliente::getPrioridade)
        );
    }

}
