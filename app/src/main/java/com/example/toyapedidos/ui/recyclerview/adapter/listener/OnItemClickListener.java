package com.example.toyapedidos.ui.recyclerview.adapter.listener;

import com.example.toyapedidos.modelo.Produto;
import com.example.toyapedidos.modelo.ProdutoPedido;

public interface OnItemClickListener {
    void onItemClick(ProdutoPedido produtoPedido, int posicao, int botaoId);
    void onItemClick(Produto produto, int posicao);
}
