package com.example.a3_simulacaobancaria_com_interface;

import java.io.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Relatorio {
    private static Relatorio instance;
    private List<String> registros;
    private DateTimeFormatter formatter;
    private static final String ARQUIVO_RELATORIO = "relatorio.dat";

    private Relatorio() {
        this.registros = new ArrayList<>();
        this.formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        carregarRelatorio();
        registrar("=== SISTEMA INICIADO ===");
        registrar("Sistema iniciado em: " + LocalDateTime.now().format(formatter));
    }

    public static Relatorio getInstance() {
        if (instance == null) {
            instance = new Relatorio();
        }
        return instance;
    }

    public void registrar(String mensagem) {
        String registro = "[" + LocalDateTime.now().format(formatter) + "] " + mensagem;
        registros.add(registro);
        System.out.println("RELATÓRIO: " + registro);
        salvarRegistro(registro);
    }

    // Carrega o relatório do arquivo ao iniciar o sistema
    private void carregarRelatorio() {
        File arquivo = new File(ARQUIVO_RELATORIO);
        if (arquivo.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_RELATORIO))) {
                String linha;
                while ((linha = br.readLine()) != null) {
                    if (!linha.trim().isEmpty() && !linha.contains("=== FIM DO RELATÓRIO ===")) {
                        registros.add(linha);
                    }
                }
                System.out.println("Relatório anterior carregado: " + registros.size() + " registros");
            } catch (IOException e) {
                System.err.println("Erro ao carregar relatório: " + e.getMessage());
            }
        } else {
            System.out.println("Arquivo de relatório não encontrado. Criando novo.");
        }
    }

    // Salva cada registro individualmente no arquivo
    private void salvarRegistro(String registro) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_RELATORIO, true))) {
            // Não salva a linha "=== FIM DO RELATÓRIO ===" no arquivo
            if (!registro.contains("=== FIM DO RELATÓRIO ===")) {
                bw.write(registro);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar registro: " + e.getMessage());
        }
    }

    public void registrarRemocao(Cliente cliente, String motivo) {
        System.out.println("=== REGISTRANDO REMOÇÃO NO RELATÓRIO ===");
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("CPF: " + cliente.getCpf());
        System.out.println("Motivo: " + motivo);

        String mensagem = String.format("REMOÇÃO - Cliente: %s | CPF: %s | Prioridade: %d | Motivo: %s",
                cliente.getNome(), cliente.getCpf(), cliente.getPrioridade(), motivo);
        registrar(mensagem);

        // Registrar tempo na fila imediatamente após a remoção
        if (cliente.getDataHoraAdicao() != null) {
            registrarTempoFila(cliente, cliente.getDataHoraAdicao());
        } else {
            System.err.println("AVISO: dataHoraAdicao é null para o cliente: " + cliente.getNome());
            registrar("AVISO - Não foi possível calcular tempo na fila para: " + cliente.getNome() + " (data de adição não disponível)");
        }
    }

    public void registrarTempoFila(Cliente cliente, LocalDateTime dataAdicao) {
        try {
            System.out.println("=== REGISTRANDO TEMPO NA FILA ===");
            System.out.println("Cliente: " + cliente.getNome());
            System.out.println("Data de adição: " + dataAdicao);

            Duration tempoNaFila = Duration.between(dataAdicao, LocalDateTime.now());
            long horas = tempoNaFila.toHours();
            long minutos = tempoNaFila.toMinutesPart();
            long segundos = tempoNaFila.toSecondsPart();

            String tempoFormatado = String.format("%02d:%02d:%02d", horas, minutos, segundos);

            String mensagem = String.format("TEMPO NA FILA - Cliente: %s | CPF: %s | Tempo total: %s",
                    cliente.getNome(), cliente.getCpf(), tempoFormatado);
            registrar(mensagem);

            System.out.println("Tempo registrado: " + tempoFormatado);

        } catch (Exception e) {
            System.err.println("Erro ao registrar tempo na fila: " + e.getMessage());
            e.printStackTrace();
            registrar("ERRO - Não foi possível calcular tempo na fila para: " + cliente.getNome());
        }
    }

    public void registrarOrdenacao(long tempoMs, int quantidadeClientes) {
        String mensagem = String.format("ORDENAÇÃO - %d clientes ordenados em %d milissegundos",
                quantidadeClientes, tempoMs);
        registrar(mensagem);
    }

    public void registrarAdicaoCliente(Cliente cliente) {
        String mensagem = String.format("ADIÇÃO - Cliente: %s | CPF: %s | Prioridade: %d | Data: %s | Hora: %s",
                cliente.getNome(), cliente.getCpf(), cliente.getPrioridade(),
                cliente.getData().toString(), cliente.getHora());
        registrar(mensagem);
    }

    public List<String> getRegistros() {
        return new ArrayList<>(registros);
    }

    public String gerarRelatorioCompleto() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO COMPLETO DO SISTEMA ===\n");
        sb.append("Gerado em: ").append(LocalDateTime.now().format(formatter)).append("\n");
        sb.append("Total de registros: ").append(registros.size()).append("\n");
        sb.append("=====================================\n\n");

        for (String registro : registros) {
            sb.append(registro).append("\n");
        }

        // NOTA: "=== FIM DO RELATÓRIO ===" NÃO é salvo no arquivo, só aparece na visualização
        sb.append("\n=== FIM DO RELATÓRIO ===\n");
        return sb.toString();
    }

    // Método para limpar o relatório
    public void limparRelatorio() {
        registros.clear();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARQUIVO_RELATORIO))) {
            bw.write(""); // Limpa o arquivo
            // Não registrar a limpeza imediatamente para evitar loop
        } catch (IOException e) {
            System.err.println("Erro ao limpar relatório: " + e.getMessage());
        }
        // Agora registrar após limpar
        registrar("=== RELATÓRIO LIMPO ===");
        registrar("Relatório limpo em: " + LocalDateTime.now().format(formatter));
    }

    // Método para buscar registros específicos
    public List<String> buscarRegistrosPorCliente(String nomeCliente) {
        List<String> resultados = new ArrayList<>();
        for (String registro : registros) {
            if (registro.toLowerCase().contains(nomeCliente.toLowerCase())) {
                resultados.add(registro);
            }
        }
        return resultados;
    }


    public void registrarInicioAtendimento(Cliente cliente) {
        String mensagem = String.format("ATENDIMENTO INICIADO - Cliente: %s | CPF: %s | Prioridade: %d",
                cliente.getNome(), cliente.getCpf(), cliente.getPrioridade());
        registrar(mensagem);
    }

    public void registrarFimAtendimento(Cliente cliente, java.time.Duration tempoAtendimento) {
        try {
            long horas = tempoAtendimento.toHours();
            long minutos = tempoAtendimento.toMinutes() % 60;
            long segundos = tempoAtendimento.getSeconds() % 60;

            String tempoFormatado = String.format("%02d:%02d:%02d", horas, minutos, segundos);

            String mensagem = String.format("ATENDIMENTO FINALIZADO - Cliente: %s | CPF: %s | Tempo de atendimento: %s",
                    cliente.getNome(), cliente.getCpf(), tempoFormatado);
            registrar(mensagem);

        } catch (Exception e) {
            System.err.println("Erro ao registrar fim de atendimento: " + e.getMessage());
            registrar("ERRO - Não foi possível registrar tempo de atendimento para: " + cliente.getNome());
        }
    }

    public void registrarCancelamentoAtendimento(Cliente cliente, java.time.Duration tempoAtendimento) {
        try {
            long horas = tempoAtendimento.toHours();
            long minutos = tempoAtendimento.toMinutes() % 60;
            long segundos = tempoAtendimento.getSeconds() % 60;

            String tempoFormatado = String.format("%02d:%02d:%02d", horas, minutos, segundos);

            String mensagem = String.format("ATENDIMENTO CANCELADO - Cliente: %s | CPF: %s | Tempo decorrido: %s",
                    cliente.getNome(), cliente.getCpf(), tempoFormatado);
            registrar(mensagem);

        } catch (Exception e) {
            System.err.println("Erro ao registrar cancelamento de atendimento: " + e.getMessage());
            registrar("ERRO - Não foi possível registrar cancelamento para: " + cliente.getNome());
        }
    }
}