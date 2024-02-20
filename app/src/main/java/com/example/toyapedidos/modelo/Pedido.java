package com.example.toyapedidos.modelo;

import java.io.Serializable;
import java.util.ArrayList;

public class Pedido implements Serializable {
    private String id;
    private ArrayList<ProdutoPedido> produtos;
    private String data;
    private String hora;
    private String observacao;
    private double valor;
    private int numeroMesa;
    private int estado;
    private boolean isExpandable;
    public Pedido(){}
    public Pedido(String id, ArrayList<ProdutoPedido> produtos, String data, String hora, String descricao, double valor, int numeroMesa, int estado) {
        this.id = id;
        this.produtos = produtos;
        this.data = data;
        this.hora = hora;
        this.observacao = descricao;
        this.valor = valor;
        this.numeroMesa = numeroMesa;
        this.estado = estado;
        isExpandable = false;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public ArrayList<ProdutoPedido> getProdutos() {
        return produtos;
    }

    public String getObservacao() {
        return observacao;
    }

    public String getId() {
        return id;
    }

    public double getValor() {
        return valor;
    }

    public int getEstado() {
        return estado;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }
}
