package com.example.a3_simulacaobancaria_com_interface;

import java.io.*;
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
                        .append(c.getPrioridade()).append("\n");
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
                clientes.add(new Cliente(partes[0], partes[1], Integer.parseInt(partes[2])));
            }
        } catch (Exception e){
            System.out.println("Erro ao carregar clientes: " + e.getMessage());
        }
        return clientes;
    }
}

