package com.example.toyapedidos.modelo;

import java.io.Serializable;

public class ProdutoPedido extends Produto implements Serializable {
    private int quantidade;
    public ProdutoPedido(){}
    public ProdutoPedido(String id, String nome,String descricao, String categoria, double valor,int quantidade){
        super(id,nome,descricao,categoria,valor);
        this.quantidade = quantidade;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade){
        this.quantidade = quantidade;
    };
}
