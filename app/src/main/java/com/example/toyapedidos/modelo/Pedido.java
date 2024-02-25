package com.example.toyapedidos.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Pedido implements Serializable, Comparable<Pedido>{
    private String id;
    private ArrayList<ProdutoPedido> produtos;
    private Date dataHora;
    private String observacao;
    private double valor;
    private int numeroMesa;
    private int estado;
    private boolean isExpandable;
    public Pedido(){}
    public Pedido(String id, ArrayList<ProdutoPedido> produtos, Date dataHora, String descricao, double valor, int numeroMesa, int estado) {
        this.id = id;
        this.produtos = produtos;
        this.dataHora = dataHora;
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

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public int compareTo(Pedido pedido) {
        if (getDataHora() == null || pedido.getDataHora() == null)
            return 0;
        return getDataHora().compareTo(pedido.getDataHora());
    }
}
