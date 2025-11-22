package com.example.a3_simulacaobancaria_com_interface;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LeitorPDF {

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        LocalDate data = null;
        String hora = null;

        for (String linha : linhas) {
            linha = linha.trim();

            // Nome
            if (linha.toLowerCase().contains("nome")) {
                nome = linha.replaceAll("(?i).*nome[: ]*", "").trim();
            }

            // CPF
            if (linha.toLowerCase().contains("cpf")) {
                cpf = linha.replaceAll("(?i).*cpf[: ]*", "").trim();
            }

            // Prioridade
            if (linha.toLowerCase().contains("prioridade")) {
                try {
                    String prio = linha.replaceAll("(?i).*prioridade[: ]*", "").trim();
                    prioridade = Integer.parseInt(prio);
                } catch (Exception ignored) {}
            }

            // Data
            if (linha.toLowerCase().contains("data")) {
                try {
                    String dataStr = linha.replaceAll("(?i).*data[: ]*", "").trim();
                    data = LocalDate.parse(dataStr, FORMATADOR);
                } catch (Exception ignored) {}
            }

            // Hora
            if (linha.toLowerCase().contains("hora")) {
                hora = linha.replaceAll("(?i).*hora[: ]*", "").trim();
            }

            // Se encontrou tudo → cria cliente
            if (nome != null && cpf != null && prioridade != null && data != null && hora != null) {

                lista.add(new Cliente(nome, cpf, prioridade, data, hora));

                // reset
                nome = null;
                cpf = null;
                prioridade = null;
                data = null;
                hora = null;
            }
        }
        return lista;

    }
    // =====================================================
    //  LER CLIENTES DO CSV
    // =====================================================
    public static List<Cliente> lerClientesDoCSV(String caminhoCSV) throws IOException {

        List<Cliente> lista = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(caminhoCSV));

        String linha;

        String nome = null;
        String cpf = null;
        Integer prioridade = null;
        LocalDate data = null;
        String hora = null;

        while ((linha = br.readLine()) != null) {
            linha = linha.trim();

            if (linha.isEmpty()) {
                // Se tiver um cliente completo, salva
                if (nome != null && cpf != null && prioridade != null && data != null && hora != null) {
                    lista.add(new Cliente(nome, cpf, prioridade, data, hora));
                }
                // reset
                nome = cpf = hora = null;
                prioridade = null;
                data = null;
                continue;
            }

            if (linha.toLowerCase().startsWith("nome")) {
                nome = linha.replaceAll("(?i).*nome[: ]*", "").trim();
            }
            else if (linha.toLowerCase().startsWith("cpf")) {
                cpf = linha.replaceAll("(?i).*cpf[: ]*", "").trim();
            }
            else if (linha.toLowerCase().startsWith("prioridade")) {
                prioridade = Integer.parseInt(linha.replaceAll("(?i).*prioridade[: ]*", "").trim());
            }
            else if (linha.toLowerCase().startsWith("data")) {
                data = LocalDate.parse(linha.replaceAll("(?i).*data[: ]*", "").trim(), FORMATADOR);
            }
            else if (linha.toLowerCase().startsWith("hora")) {
                hora = linha.replaceAll("(?i).*hora[: ]*", "").trim();
            }
        }

        // adiciona o último cliente se o arquivo não terminar com linha em branco
        if (nome != null && cpf != null && prioridade != null && data != null && hora != null) {
            lista.add(new Cliente(nome, cpf, prioridade, data, hora));
        }

        br.close();
        return lista;
    }
    // =====================================================
    //  NOVO MÉTODO: LER CSV FORMATO 2 (uma linha por cliente)
    // =====================================================
    public static List<Cliente> lerClientesDoCSVFormato2(String caminhoCSV) throws IOException {
        List<Cliente> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCSV))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                // Pula a primeira linha (cabeçalho)
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                // Pula linhas vazias
                if (linha.isEmpty()) {
                    continue;
                }

                // Divide a linha por vírgulas
                String[] dados = linha.split(",");

                // Verifica se tem pelo menos 5 campos: Nome, CPF, Prioridade, Data, Hora
                if (dados.length >= 5) {
                    try {
                        String nome = dados[0].trim();
                        String cpf = dados[1].trim();
                        int prioridade = Integer.parseInt(dados[2].trim());
                        LocalDate data = LocalDate.parse(dados[3].trim(), FORMATADOR);
                        String hora = dados[4].trim();

                        lista.add(new Cliente(nome, cpf, prioridade, data, hora));
                    } catch (Exception e) {
                        System.err.println("Erro ao processar linha: " + linha);
                        e.printStackTrace();
                    }
                }
            }
        }
        return lista;
    }
    // =====================================================
    //  MÉTODO QUE DETECTA AUTOMATICAMENTE O FORMATO DO CSV
    // =====================================================
    public static List<Cliente> lerClientesDoCSVAuto(String caminhoCSV) throws IOException {
        // Primeiro, vamos verificar o formato do arquivo
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCSV))) {
            String primeiraLinha = br.readLine();

            if (primeiraLinha == null) {
                return new ArrayList<>(); // Arquivo vazio
            }

            // Se a primeira linha contém vírgulas e parece ser um cabeçalho com os campos
            if (primeiraLinha.contains(",") &&
                    primeiraLinha.toLowerCase().contains("nome") &&
                    primeiraLinha.toLowerCase().contains("cpf") &&
                    primeiraLinha.toLowerCase().contains("prioridade")) {

                System.out.println("Detectado: Formato 2 (uma linha por cliente)");
                return lerClientesDoCSVFormato2(caminhoCSV);
            } else {
                System.out.println("Detectado: Formato 1 (linha por campo)");
                return lerClientesDoCSV(caminhoCSV);
            }
        }
    }

}


