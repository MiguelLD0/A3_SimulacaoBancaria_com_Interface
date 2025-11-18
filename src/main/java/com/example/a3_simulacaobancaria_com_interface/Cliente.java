package com.example.a3_simulacaobancaria_com_interface;

public class Cliente {
    private String nome;
    private String cpf;
    private int prioridade; // 0 igual a normal. 1 igual a (idoso; gestante ou PCD.)

    public Cliente(String nome, String cpf, int prioridade){
        this.nome = nome;
        this.cpf = cpf;
        this.prioridade = prioridade;
    }
    public String getNome(){ return nome; }
    public String getCpf() { return cpf; }
    public int getPrioridade() { return prioridade; }

    @Override
    public String toString(){
        return "Cliente: " + nome + "| CPF: " + cpf + "| Prioridade: " + prioridade;
    }
}

