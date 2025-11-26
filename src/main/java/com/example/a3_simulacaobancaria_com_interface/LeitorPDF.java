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

    private static String gerarId() {
        return java.util.UUID.randomUUID().toString();
    }

    // =====================================================
    //  LER CLIENTES DO PDF
    // =====================================================
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

            if (linha.toLowerCase().contains("nome"))
                nome = linha.replaceAll("(?i).*nome[: ]*", "").trim();

            if (linha.toLowerCase().contains("cpf"))
                cpf = linha.replaceAll("(?i).*cpf[: ]*", "").trim();

            if (linha.toLowerCase().contains("prioridade")) {
                try {
                    prioridade = Integer.parseInt(linha.replaceAll("(?i).*prioridade[: ]*", "").trim());
                } catch (Exception ignored) {}
            }

            if (linha.toLowerCase().contains("data")) {
                try {
                    data = LocalDate.parse(linha.replaceAll("(?i).*data[: ]*", "").trim(), FORMATADOR);
                } catch (Exception ignored) {}
            }

            if (linha.toLowerCase().contains("hora"))
                hora = linha.replaceAll("(?i).*hora[: ]*", "").trim();

            // Se temos um cliente completo → cria
            if (nome != null && cpf != null && prioridade != null && data != null && hora != null) {

                lista.add(new Cliente(
                        gerarId(),
                        nome,
                        "Normal", // tipo padrão
                        cpf,
                        prioridade,
                        data,
                        hora
                ));

                // Reset
                nome = cpf = hora = null;
                prioridade = null;
                data = null;
            }
        }

        return lista;
    }

    // =====================================================
    //  LER CLIENTES DO CSV (formato antigo)
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
                if (nome != null && cpf != null && prioridade != null && data != null && hora != null) {
                    lista.add(new Cliente(
                            gerarId(),
                            nome,
                            "Normal",
                            cpf,
                            prioridade,
                            data,
                            hora
                    ));
                }
                nome = cpf = hora = null;
                prioridade = null;
                data = null;
                continue;
            }

            if (linha.toLowerCase().startsWith("nome"))
                nome = linha.replaceAll("(?i).*nome[: ]*", "").trim();

            else if (linha.toLowerCase().startsWith("cpf"))
                cpf = linha.replaceAll("(?i).*cpf[: ]*", "").trim();

            else if (linha.toLowerCase().startsWith("prioridade"))
                prioridade = Integer.parseInt(linha.replaceAll("(?i).*prioridade[: ]*", "").trim());

            else if (linha.toLowerCase().startsWith("data"))
                data = LocalDate.parse(linha.replaceAll("(?i).*data[: ]*", "").trim(), FORMATADOR);

            else if (linha.toLowerCase().startsWith("hora"))
                hora = linha.replaceAll("(?i).*hora[: ]*", "").trim();
        }

        if (nome != null && cpf != null && prioridade != null && data != null && hora != null)
            lista.add(new Cliente(gerarId(), nome, "Normal", cpf, prioridade, data, hora));

        br.close();
        return lista;
    }

    // =====================================================
    //  LER CSV FORMATO 2 (uma linha por cliente)
    // =====================================================
    public static List<Cliente> lerClientesDoCSVFormato2(String caminhoCSV) throws IOException {
        List<Cliente> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCSV))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }

                if (linha.isEmpty())
                    continue;

                String[] dados = linha.split(",");

                if (dados.length >= 5) {
                    try {
                        String nome = dados[0].trim();
                        String cpf = dados[1].trim();
                        int prioridade = Integer.parseInt(dados[2].trim());
                        LocalDate data = LocalDate.parse(dados[3].trim(), FORMATADOR);
                        String hora = dados[4].trim();

                        lista.add(new Cliente(
                                gerarId(),
                                nome,
                                "Normal",
                                cpf,
                                prioridade,
                                data,
                                hora
                        ));

                    } catch (Exception e) {
                        System.err.println("Erro ao processar linha: " + linha);
                    }
                }
            }
        }
        return lista;
    }

    // =====================================================
//  DETECTAR FORMATO AUTOMATICAMENTE (AGORA COM 3 TIPOS)
// =====================================================
    public static List<Cliente> lerClientesDoCSVAuto(String caminhoCSV) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCSV))) {

            String primeiraLinha = br.readLine();

            if (primeiraLinha == null)
                return new ArrayList<>();

            String lower = primeiraLinha.toLowerCase();

            // -------------------------------------------
            // FORMATO 1
            // id,nome,tipo,tempo,hora
            // -------------------------------------------
            if (primeiraLinha.contains(",")
                    && lower.contains("id")
                    && lower.contains("nome")
                    && lower.contains("tipo")
                    && lower.contains("tempo")
                    && lower.contains("hora")) {

                return lerClientesDoCSVFormatoImagem(caminhoCSV);
            }

            // -------------------------------------------
            // FORMATO 2 (uma linha por cliente)
            // nome,cpf,prioridade,data,hora
            // -------------------------------------------
            if (primeiraLinha.contains(",")
                    && lower.contains("nome")
                    && lower.contains("cpf")
                    && lower.contains("prioridade")) {

                return lerClientesDoCSVFormato2(caminhoCSV);
            }

            // -------------------------------------------
            // FORMATO ANTIGO (várias linhas por cliente)
            // -------------------------------------------
            return lerClientesDoCSV(caminhoCSV);
        }
    }
    public static List<Cliente> lerClientesDoCSVFormatoImagem(String caminhoCSV) throws IOException {

        List<Cliente> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoCSV))) {

            String linha;
            boolean primeira = true;

            while ((linha = br.readLine()) != null) {

                if (linha.trim().isEmpty()) continue;

                // Pula a primeira linha (cabeçalho)
                if (primeira) {
                    primeira = false;
                    continue;
                }

                String[] partes = linha.split(",");

                if (partes.length < 5) {
                    System.err.println("Linha inválida → " + linha);
                    continue;
                }

                // Dados conforme a imagem
                String id = partes[0].trim();
                String nome = partes[1].trim();
                String tipo = partes[2].trim().toLowerCase();
                String tempoStr = partes[3].trim();
                String hora = partes[4].trim();

                int tempoAtendimento = 0;
                try { tempoAtendimento = Integer.parseInt(tempoStr); }
                catch (Exception ignored) {}

                // Converter tipo → prioridade
                // preferencial = 0
                // corporativo = 1
                // comum = 2
                int prioridade;
                switch (tipo) {
                    case "preferencial":
                        prioridade = 0;
                        break;
                    case "corporativo":
                        prioridade = 1;
                        break;
                    default:
                        prioridade = 2;
                }

                // Criar cliente — usando data atual (já que não existe no CSV)
                Cliente cliente = new Cliente(
                        id,
                        nome,
                        tipo,
                        "000.000.000-00",
                        prioridade,
                        LocalDate.now().plusDays(1),
                        hora
                );

                lista.add(cliente);
            }
        }

        return lista;
    }
}
