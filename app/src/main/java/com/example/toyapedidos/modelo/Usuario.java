package com.example.toyapedidos.modelo;

public class Usuario {
    private String id;
    private String nome;
    private String cargo;
    public Usuario(){}

    public Usuario(String id, String nome, String cargo) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }
}
