package com.example.toyapedidos.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.modelo.Produto;
import com.example.toyapedidos.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class CardapioAdapter extends RecyclerView.Adapter<CardapioAdapter.ProdutoViewHolder> {
    private final List<Produto> produtos;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public CardapioAdapter(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

    }

    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_produto, parent, false);
        return new ProdutoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull CardapioAdapter.ProdutoViewHolder holder, int position) {
        Produto produto = produtos.get(position);
        holder.vincula(produto);
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }
    public void remove(int posicao){
        if (posicao < 0 || posicao >= produtos.size()){
            return;
        }
        produtos.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, produtos.size());
        notifyDataSetChanged();
    }
    public void adiciona(int posicao, Produto produto){
        if (posicao < 0 || posicao >= produtos.size()){
            return;
        }
        produtos.add(posicao,produto);
        notifyItemInserted(posicao);
        notifyItemRangeChanged(posicao, produtos.size());
        notifyDataSetChanged();
    }
    public class ProdutoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeProduto;
        private final TextView descricaoProduto;
        private final TextView valorProduto;
        private Produto produto;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeProduto = itemView.findViewById(R.id.itemNomeProduto);
            descricaoProduto = itemView.findViewById(R.id.itemDescricaoNovoPedido);
            valorProduto = itemView.findViewById(R.id.itemValorProduto);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(produto, getAdapterPosition()));
        }

        public void vincula(Produto produto) {
            this.produto = produto;
            preencheCampos(produto);
        }

        private void preencheCampos(Produto produto) {
            nomeProduto.setText(produto.getNome());
            descricaoProduto.setText(produto.getDescricao());
            valorProduto.setText("R$ "+produto.getValor());
        }
    }
}
