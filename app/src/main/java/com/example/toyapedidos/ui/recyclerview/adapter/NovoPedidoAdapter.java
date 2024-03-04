package com.example.toyapedidos.ui.recyclerview.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.example.toyapedidos.ui.recyclerview.adapter.listener.OnItemClickListener;

import java.util.List;

public class NovoPedidoAdapter extends RecyclerView.Adapter<NovoPedidoAdapter.NovoPedidoViewHolder> {
    private List<ProdutoPedido> novoPedido;
    private OnItemClickListener onItemClickListener;
    private final Context context;

    public NovoPedidoAdapter(List<ProdutoPedido> novoPedido, Context context) {
        this.novoPedido = novoPedido;
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public void setListaFiltrada(List<ProdutoPedido> listaFiltrada){
        this.novoPedido = listaFiltrada;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public NovoPedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_novo_pedido, parent, false);
        return new NovoPedidoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull NovoPedidoAdapter.NovoPedidoViewHolder holder, int position) {
        ProdutoPedido produtoPedido = novoPedido.get(position);
        holder.vincula(produtoPedido);
    }

    @Override
    public int getItemCount() {
        if (novoPedido == null) return 0;
        return novoPedido.size();
    }
    public void altera(ProdutoPedido produtoPedido, int posicao){
        novoPedido.set(posicao, produtoPedido);
        notifyDataSetChanged();
    }
    public void limpaLista() {
        novoPedido.clear();
        notifyDataSetChanged();
    }
    public class NovoPedidoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeProdutoPedido;
        private final TextView descricaoProdutoPedido;
        private final TextView valorProdutoPedido;
        private final TextView quantidadeProdutoPedido;
        private ProdutoPedido produtoPedido;

        public NovoPedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeProdutoPedido = itemView.findViewById(R.id.itemNomeNovoPedido);
            descricaoProdutoPedido = itemView.findViewById(R.id.itemDescricaoNovoPedido);
            valorProdutoPedido = itemView.findViewById(R.id.itemValorNovoPedido);
            quantidadeProdutoPedido = itemView.findViewById(R.id.itemQuantidadeNovoPedido);
            ImageButton botaoAdiciona = itemView.findViewById(R.id.itemBtnIncrementaQuantidadeNovoPedido);
            ImageButton botaoSubtrai = itemView.findViewById(R.id.itemBtnDecrementaQuantidadeNovoPedido);
            botaoAdiciona.setOnClickListener(v -> onItemClickListener.onItemClick(produtoPedido, getAdapterPosition(), R.id.itemBtnIncrementaQuantidadeNovoPedido));
            botaoSubtrai.setOnClickListener(v -> onItemClickListener.onItemClick(produtoPedido, getAdapterPosition(), R.id.itemBtnDecrementaQuantidadeNovoPedido));
        }
        public void vincula(ProdutoPedido produtoPedido){
            this.produtoPedido = produtoPedido;
            preencheCampos(produtoPedido);
        }

        private void preencheCampos(ProdutoPedido produtoPedido) {
            nomeProdutoPedido.setText(produtoPedido.getNome());
            descricaoProdutoPedido.setText(produtoPedido.getDescricao());
            valorProdutoPedido.setText("R$ "+produtoPedido.getValor());
            quantidadeProdutoPedido.setText(String.valueOf(produtoPedido.getQuantidade()));
        }
    }
}
