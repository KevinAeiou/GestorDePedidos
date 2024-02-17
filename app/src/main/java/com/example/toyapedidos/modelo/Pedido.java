package com.example.toyapedidos.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Pedido implements Serializable {
    private ArrayList<ProdutoPedido> produtos;
    private LocalDateTime dataHora;
    private int numeroMesa;
    public Pedido(){}
    public Pedido(ArrayList<ProdutoPedido> produtos, LocalDateTime dataHora, int numeroMesa) {
        this.produtos = produtos;
        this.dataHora = dataHora;
        this.numeroMesa = numeroMesa;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public ArrayList<ProdutoPedido> getProdutos() {
        return produtos;
    }
}
