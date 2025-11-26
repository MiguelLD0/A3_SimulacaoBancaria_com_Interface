package com.example.a3_simulacaobancaria_com_interface;

import java.util.HashMap;

public class GeradorID {
    private static HashMap<String, Integer> contador = new HashMap<>();

    public static String gerarID(String tipo) {
        contador.putIfAbsent(tipo, 0);
        int novoNumero = contador.get(tipo) + 1;
        contador.put(tipo, novoNumero);

        String prefixo;
        switch (tipo.toLowerCase()) {
            case "preferencial":
                prefixo = "PRF";
                break;
            case "corporativo":
                prefixo = "CRP";
                break;
            case "comum":
                prefixo = "COM";
                break;
            default:
                prefixo = "GEN"; // Genérico para tipos não especificados
                break;
        }

        return prefixo + String.format("%04d", novoNumero);
    }
}