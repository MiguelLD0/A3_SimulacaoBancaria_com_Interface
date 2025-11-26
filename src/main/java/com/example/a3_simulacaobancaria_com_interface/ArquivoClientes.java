package com.example.a3_simulacaobancaria_com_interface;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ArquivoClientes {

    private static final String ARQUIVO = "clientes.dat";
    private static final DateTimeFormatter FORMATAR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ================================
    // SALVAR CLIENTES
    // ================================
    public static void SalvarClientes(List<Cliente> clientes) {
        try {
            StringBuilder sb = new StringBuilder();

            for (Cliente c : clientes) {
                sb.append("ID: ").append(c.getId()).append(" | ")
                        .append("Tipo: ").append(c.getTipo()).append(" | ") // Aqui deve salvar "Comum" ou "Corporativo"
                        .append("Nome: ").append(c.getNome()).append(" | ")
                        .append("CPF: ").append(c.getCpf()).append(" | ")
                        .append("Prioridade: ").append(c.getPrioridade()).append(" | ")
                        .append("Data: ").append(c.getData().format(FORMATAR_DATA)).append(" | ")
                        .append("Hora: ").append(c.getHora())
                        .append("\n");
            }

            String textoCriptografado = CriptografiaUtil.criptografar(sb.toString());

            try (FileWriter fw = new FileWriter(ARQUIVO)) {
                fw.write(textoCriptografado);
            }

        } catch (Exception e) {
            System.out.println("Erro ao salvar clientes: " + e.getMessage());
        }
    }

    // ================================
    // CARREGAR CLIENTES
    // ================================
    public static List<Cliente> carregarClientes() {

        List<Cliente> clientes = new ArrayList<>();

        try {
            File file = new File(ARQUIVO);
            if (!file.exists()) return clientes;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linha;
                while ((linha = br.readLine()) != null) sb.append(linha).append("\n");
            }

            String textoDescriptografado = CriptografiaUtil.descriptografar(sb.toString());

            // DEBUG: Mostrar conteúdo do arquivo
            System.out.println("=== CONTEÚDO DO ARQUIVO DESCRIPTOGRAFADO ===");
            System.out.println(textoDescriptografado);
            System.out.println("============================================");

            String[] linhas = textoDescriptografado.split("\n");

            for (String l : linhas) {
                if (l.trim().isEmpty()) continue;

                try {
                    // DEBUG: Mostrar linha sendo processada
                    System.out.println("Processando linha: " + l);

                    // Dividir a linha pelas barras verticais
                    String[] partes = l.split("\\|");

                    if (partes.length < 7) {
                        System.out.println("Linha com formato inválido (menos de 7 partes): " + l);
                        continue;
                    }

                    // Extrair valores de forma direta
                    String id = partes[0].replace("ID:", "").trim();
                    String tipo = partes[1].replace("Tipo:", "").trim();
                    String nome = partes[2].replace("Nome:", "").trim();
                    String cpf = partes[3].replace("CPF:", "").trim();
                    String prioridadeStr = partes[4].replace("Prioridade:", "").trim();
                    String dataStr = partes[5].replace("Data:", "").trim();
                    String hora = partes[6].replace("Hora:", "").trim();

                    // DEBUG: Mostrar valores extraídos
                    System.out.println("Valores extraídos - ID: '" + id + "', Tipo: '" + tipo + "', Nome: '" + nome + "'");

                    // Verificar se todos os campos obrigatórios foram encontrados
                    if (id.isEmpty() || tipo.isEmpty() || nome.isEmpty() || cpf.isEmpty() || prioridadeStr.isEmpty() || dataStr.isEmpty() || hora.isEmpty()) {
                        System.out.println("Campos obrigatórios faltando na linha: " + l);
                        continue;
                    }

                    int prioridade = Integer.parseInt(prioridadeStr.trim());
                    LocalDate data = LocalDate.parse(dataStr.trim(), FORMATAR_DATA);

                    clientes.add(new Cliente(id, nome, tipo, cpf, prioridade, data, hora.trim()));

                } catch (NumberFormatException e) {
                    System.out.println("Erro ao converter prioridade na linha: " + l);
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Erro ao processar linha: " + l);
                    e.printStackTrace();
                }
            }

            // =====================================================
            // ORDENAR + MEDIR TEMPO
            // =====================================================

            if (!clientes.isEmpty()) {
                long tempoOrdenacao = OrdenarAtendimentos.medirTempoApenas(() ->
                        OrdenarAtendimentos.ordenarCompleto(clientes)
                );

                int quantidadeClientes = clientes.size();

                Relatorio.getInstance().registrarOrdenacao(tempoOrdenacao, quantidadeClientes);

                double tempoMedio = (double) tempoOrdenacao / quantidadeClientes;
                System.out.printf("📊 Tempo médio por cliente: %.2f ms%n", tempoMedio);

                System.out.printf(
                        "⏱️ Ordenação de %d clientes concluída em %d ms%n",
                        quantidadeClientes, tempoOrdenacao
                );
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return clientes;
    }
}