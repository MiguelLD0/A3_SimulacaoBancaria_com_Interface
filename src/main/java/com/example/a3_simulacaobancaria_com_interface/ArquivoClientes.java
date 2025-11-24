package com.example.a3_simulacaobancaria_com_interface;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArquivoClientes {
    private static final String ARQUIVO = "clientes.dat";

    public static void SalvarClientes(List<Cliente> clientes)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            for (Cliente c : clientes){
                sb.append(c.getNome()).append(";")
                        .append(c.getCpf()).append(";")
                        .append(c.getPrioridade()).append(";")
                        .append(c.getData()).append(";") // LocalDate é salvo como texto (YYYY-MM-DD)
                        .append(c.getHora()).append("\n");
            }

            String textoCriptografado = CriptografiaUtil.criptografar(sb.toString());

            FileWriter fw = new FileWriter(ARQUIVO);
            fw.write(textoCriptografado);
            fw.close();
        } catch (Exception e){
            System.out.println("Erro ao Salvar clientes: " + e.getMessage());
        }
    }

    public static List<Cliente> carregarClientes()
    {
        List<Cliente> clientes = new ArrayList<>();

        try{
            File file = new File(ARQUIVO);
            if(!file.exists()) return clientes;

            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
            br.close();

            String textoDescriptografado = CriptografiaUtil.descriptografar(sb.toString());
            String[] linhas = textoDescriptografado.split("\n");

            for(String l : linhas){
                if(l.trim().isEmpty()) continue;

                String[] partes = l.split(";");

                String nome = partes[0];
                String cpf = partes[1];
                int prioridade = Integer.parseInt(partes[2]);

                LocalDate data = LocalDate.parse(partes[3]);
                String hora = partes[4];

                clientes.add(new Cliente(nome, cpf, prioridade, data, hora));
            }
            OrdenarAtendimentos.ordenarCompleto(clientes);
            // Mede o tempo da ordenação
            long tempoOrdenacao = OrdenarAtendimentos.medirTempoApenas(() -> {
                OrdenarAtendimentos.ordenarCompleto(clientes);
            });
            int quantidadeClientes = clientes.size();
            if (quantidadeClientes > 0) {
                Relatorio.getInstance().registrarOrdenacao(tempoOrdenacao, quantidadeClientes);


                double tempoMedio = (double) tempoOrdenacao / quantidadeClientes;
                String detalhes = String.format("Tempo médio por cliente: %.2f ms", tempoMedio);
                System.out.println("📊 " + detalhes);
            }

            System.out.printf("⏱️ Ordenação de %d clientes concluída em %d ms%n",
                    quantidadeClientes, tempoOrdenacao);
        } catch (Exception e){
            System.out.println("Erro ao carregar clientes: " + e.getMessage());
        }

        return clientes;
    }
}
