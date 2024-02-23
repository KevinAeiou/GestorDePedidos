package com.example.toyapedidos.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.modelo.Pedido;
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

    public class NovoPedidoViewHolder extends RecyclerView.ViewHolder {
        private final TextView nomeProdutoPedido;
        private final TextView descricaoProdutoPedido;
        private final TextView valorProdutoPedido;
        private final TextView quantidadeProdutoPedido;
        private ProdutoPedido produtoPedido;
        private ImageButton botaoAdiciona;
        private ImageButton botaoSubtrai;

        public NovoPedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeProdutoPedido = itemView.findViewById(R.id.itemIdPedido);
            descricaoProdutoPedido = itemView.findViewById(R.id.itemNumeroMesaPedido);
            valorProdutoPedido = itemView.findViewById(R.id.itemValorNovoPedido);
            quantidadeProdutoPedido = itemView.findViewById(R.id.itemQuantidadeNovoPedido);
            botaoAdiciona = itemView.findViewById(R.id.itemBtnExpandeConteudo);
            botaoSubtrai = itemView.findViewById(R.id.itemBtnSubtraiQuantidade);
            botaoAdiciona.setOnClickListener(v -> onItemClickListener.onItemClick(produtoPedido, getAdapterPosition(), R.id.itemBtnExpandeConteudo));
            botaoSubtrai.setOnClickListener(v -> onItemClickListener.onItemClick(produtoPedido, getAdapterPosition(), R.id.itemBtnSubtraiQuantidade));
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
