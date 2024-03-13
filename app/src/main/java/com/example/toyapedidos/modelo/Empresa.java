package com.example.toyapedidos.modelo;

public class Empresa {
    private String id;
    private String nome;

    public Empresa(){}
    public Empresa(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public String getId() {
        return id;
    }
}
