package com.example.a3_simulacaobancaria_com_interface;

//import java.util.Collection;
import java.util.List;
public class OrdenarAtendimentos {

    //======Buble Sort por nome=====

    public static void bubleSortPorNome(List<Cliente> lista)
    {
        int n = lista.size();
        for(int i = 0; i < n - 1; i++)
        {
            for(int j = 0; j < n - i - 1; j++){
                if(lista.get(j).getNome().compareToIgnoreCase(lista.get(j + 1).getNome()) > 0 ){
                    //swap
                    Cliente tmp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, tmp);
                }
            }
        }
    }
    // ============== Selection Sort por CPF ============
    public static void selectionSortPorCPF(List<Cliente> lista)
    {
        int n = lista.size();
        for(int i = 0; i < n - 1; i++){
            int menor = i;
            for(int j = i + 1; j < n; j++){
                if(lista.get(j).getCpf().compareTo(lista.get(menor).getCpf()) < 0){
                    menor = j;
                }
            }
            //swap
            Cliente tmp = lista.get(i);
            lista.set(i, lista.get(menor));
            lista.set(menor, tmp);
        }
    }
    // ========== quickSort por prioridade =========
    public static void quickSortPorPrioridade(List<Cliente> lista, int inicio, int fim){
        if(inicio < fim){
            int pivo = particionar(lista, inicio, fim);
            quickSortPorPrioridade(lista, inicio, pivo - 1);
            quickSortPorPrioridade(lista, pivo + 1, fim);
        }
    }
    private static int particionar(List<Cliente> lista, int inicio, int fim){
        int pivo = lista.get(fim).getPrioridade();
        int i = inicio - 1;

        for(int j = inicio; j < fim; j++){
            if(lista.get(j).getPrioridade() < pivo){
                i++;
                Cliente tmp = lista.get(i);
                lista.set(i, lista.get(j));
                lista.set(j, tmp);
            }
        }
        Cliente tmp = lista.get(i + 1);
        lista.set(i + 1, lista.get(fim));
        lista.set(fim, tmp);

        return i + 1;
    }
}
