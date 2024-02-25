package com.example.toyapedidos.ui.recyclerview.adapter;

import static com.example.toyapedidos.R.drawable.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.modelo.Pedido;
import com.example.toyapedidos.modelo.ProdutoPedido;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {
    private List<Pedido> pedidos;
    private List<ProdutoPedido> produtosPedido;
    private final Context context;

    public PedidoAdapter(List<Pedido> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
    }
    public void setListaFiltrada(List<Pedido> listaFiltrada){
        this.pedidos = listaFiltrada;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoAdapter.PedidoViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        holder.vincula(pedido);
        boolean isExpandable = pedido.isExpandable();
        holder.layoutExpansivelItemPedido.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
        if (isExpandable){
            holder.iconeSeta.setImageResource(ic_cima);
        }else {
            holder.iconeSeta.setImageResource(ic_baixo);
        }
        ProdutoPedidoAdapter produtoPedidoAdapter = new ProdutoPedidoAdapter(produtosPedido, context);
        holder.recyclerExpasivelItemPedido.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerExpasivelItemPedido.setHasFixedSize(true);
        holder.recyclerExpasivelItemPedido.setAdapter(produtoPedidoAdapter);

        holder.linearLayoutItemPedido.setOnClickListener(view -> {
            pedido.setExpandable(!pedido.isExpandable());
            produtosPedido = pedido.getProdutos();
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }
    public void remove(int posicao){
        if (posicao<0 || posicao>=pedidos.size()){
            return;
        }
        pedidos.remove(posicao);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao,pedidos.size());
        notifyDataSetChanged();
    }
    public void limpaLista(){
        pedidos.clear();
        notifyDataSetChanged();
    }
    public static class PedidoViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout linearLayoutItemPedido;
        private final ConstraintLayout layoutExpansivelItemPedido;
        private final TextView numeroMesaPedido;
        private final TextView observacaoPedido;
        private final RecyclerView recyclerExpasivelItemPedido;
        private final ImageView iconeSeta;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutItemPedido = itemView.findViewById(R.id.linearLayoutItemPedido);
            layoutExpansivelItemPedido = itemView.findViewById(R.id.layoutExpansivelItemPedido);
            numeroMesaPedido = itemView.findViewById(R.id.itemDescricaoNovoPedido);
            observacaoPedido = itemView.findViewById(R.id.txtObservacaoItemPedido);
            recyclerExpasivelItemPedido = itemView.findViewById(R.id.recyclerViewItemPedido);
            iconeSeta = itemView.findViewById(R.id.itemBtnIncrementaQuantidadeNovoPedido);
        }

        public void vincula(Pedido pedido) {
            preencheCampos(pedido);
        }

        private void preencheCampos(Pedido pedido) {
            if (pedido.getObservacao().isEmpty()){
                observacaoPedido.setVisibility(View.GONE);
            } else {
                observacaoPedido.setVisibility(View.VISIBLE);
                observacaoPedido.setText(pedido.getObservacao());
            }
            numeroMesaPedido.setText("Mesa "+pedido.getNumeroMesa());
        }
    }
}
