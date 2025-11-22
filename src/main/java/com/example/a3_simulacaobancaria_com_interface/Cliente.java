package com.example.a3_simulacaobancaria_com_interface;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cliente {
    private String nome;
    private String cpf;
    private int prioridade;
    private LocalDateTime agendamento;
    private LocalDate data;
    private String hora;
    private LocalDateTime dataHoraAdicao;
    public Cliente(String nome, String cpf, int prioridade, LocalDate data, String hora){
        this.nome = nome;
        this.cpf = cpf;
        this.prioridade = prioridade;
        this.data = data;
        this.hora = hora;
        this.agendamento = null; // sem agendamento
        this.dataHoraAdicao = LocalDateTime.now();
    }

    public Cliente(String nome, String cpf, int prioridade, LocalDateTime agendamento){
        this.nome = nome;
        this.cpf = cpf;
        this.prioridade = prioridade;
        this.agendamento = agendamento;
    }
    public LocalDateTime getDataHoraAdicao() {
        return dataHoraAdicao;
    }

    public void setDataHoraAdicao(LocalDateTime dataHoraAdicao) {
        this.dataHoraAdicao = dataHoraAdicao;
    }
    public String getNome(){ return nome; }
    public String getCpf() { return cpf; }
    public int getPrioridade() { return prioridade; }
    public LocalDateTime getAgendamento() { return agendamento; }

    public void setAgendamento(LocalDateTime agendamento) {
        this.agendamento = agendamento;
    }
    public LocalDate getData() { return data; }
    public String getHora() { return hora; }
    @Override
    public String toString() {
        return "Cliente: " + nome +
                " | CPF: " + cpf +
                " | Prioridade: " + prioridade +
                " | Data: " + data +
                " | Hora: " + hora;
    }
}