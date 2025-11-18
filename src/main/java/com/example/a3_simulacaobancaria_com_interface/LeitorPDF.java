package com.example.a3_simulacaobancaria_com_interface;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LeitorPDF {
    public static List<Cliente> lerClientesDoPDF(String caminhoPDF) throws IOException {
        List<Cliente> lista = new ArrayList<>();
        PDDocument documento = Loader.loadPDF(new File(caminhoPDF));
        PDFTextStripper stripper = new PDFTextStripper();
        String texto = stripper.getText(documento);
        documento.close();

        String[] linhas = texto.split("\n");

        String nome = null;
        String cpf = null;
        Integer prioridade = null;

        for (String linha : linhas) {
            linha = linha.trim();

            //detector Nome
            if (linha.toLowerCase().contains("nome")) {
                nome = linha.replaceAll("(?i).*nome[: ]*", "").trim();

            }
            // Detectar CPF
            if (linha.toLowerCase().contains("cpf")) {
                cpf = linha.replaceAll("(?i).*cpf[: ]*", "").trim();

            }
            if (linha.toLowerCase().contains("prioridade")) {
                String prio = linha.replaceAll("(?i).*prioridade[: ]*", "").trim();
                try {
                    prioridade = Integer.parseInt(prio);
                } catch (NumberFormatException ignored) {
                }
            }
            //se ja temos os 3 campos cria cliente e resetar
            if (nome != null && cpf != null && prioridade != null) {
                lista.add(new Cliente(nome, cpf, prioridade));

                nome = null;
                cpf = null;
                prioridade = null;
            }
        }
        return lista;
    }
}

