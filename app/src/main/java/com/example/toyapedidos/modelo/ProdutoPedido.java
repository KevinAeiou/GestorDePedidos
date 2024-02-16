package com.example.toyapedidos.modelo;

import java.io.Serializable;

public class ProdutoPedido extends Produto implements Serializable {
    private int quatidade;
    public ProdutoPedido(){}
    public ProdutoPedido(String id, String nome,String descricao, String categoria, double valor,int quatidade){
        super(id,nome,descricao,categoria,valor);
        this.quatidade = quatidade;
    }
    public int getQuatidade() {
        return quatidade;
    }
}
