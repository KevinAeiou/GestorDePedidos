package com.example.toyapedidos.modelo;

import java.io.Serializable;

public class Produto implements Serializable {
    private String id;
    private String nome;
    private String descricao;
    private String categoria;
    private double valor;

    public Produto(){}
    public Produto(String id, String nome, String descricao, String categoria, double valor) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valor = valor;
    }
    public String getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getDescricao() {
        return descricao;
    }
    public double getValor() {
        return valor;
    }

    public String getCategoria() {
        return categoria;
    }
}
