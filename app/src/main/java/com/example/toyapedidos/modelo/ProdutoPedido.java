package com.example.toyapedidos.modelo;

import java.io.Serializable;

public class ProdutoPedido extends Produto implements Serializable {
    private int quantidade;
    private boolean check;
    public ProdutoPedido(){}
    public ProdutoPedido(String id, String nome, String descricao, String categoria, double valor, int quantidade, boolean check){
        super(id,nome,descricao,categoria,valor);
        this.quantidade = quantidade;
        this.check = check;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade){
        this.quantidade = quantidade;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
