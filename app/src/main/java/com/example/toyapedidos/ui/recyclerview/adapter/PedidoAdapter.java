package com.example.toyapedidos.ui.recyclerview.adapter;

import static com.example.toyapedidos.R.drawable.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
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
    private Context context;

    public PedidoAdapter(List<Pedido> pedidos, Context context) {
        this.pedidos = pedidos;
        this.context = context;
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
        NovoPedidoAdapter novoPedidoAdapter = new NovoPedidoAdapter(produtosPedido, context);
        holder.recyclerExpasivelItemPedido.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerExpasivelItemPedido.setHasFixedSize(true);
        holder.recyclerExpasivelItemPedido.setAdapter(novoPedidoAdapter);


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

    public class PedidoViewHolder extends RecyclerView.ViewHolder{
        private final LinearLayout linearLayoutItemPedido;
        private final ConstraintLayout layoutExpansivelItemPedido;
        private final TextView idPedido;
        private final TextView numeroMesaPedido;
        private final TextView observacaoPedido;
        //private final TextView valorPedido;
        //private final ArrayList<ProdutoPedido> listaProdutoPedido;
        private final RecyclerView recyclerExpasivelItemPedido;
        private final ImageView iconeSeta;
        private Pedido pedido;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutItemPedido = itemView.findViewById(R.id.linearLayoutItemPedido);
            layoutExpansivelItemPedido = itemView.findViewById(R.id.layoutExpansivelItemPedido);
            idPedido = itemView.findViewById(R.id.itemIdPedido);
            numeroMesaPedido = itemView.findViewById(R.id.itemNumeroMesaPedido);
            observacaoPedido = itemView.findViewById(R.id.txtObservacaoItemPedido);
            recyclerExpasivelItemPedido = itemView.findViewById(R.id.recyclerViewItemPedido);
            iconeSeta = itemView.findViewById(R.id.itemBtnExpandeConteudo);
        }

        public void vincula(Pedido pedido) {
            this.pedido = pedido;
            preencheCampos(pedido);
        }

        private void preencheCampos(Pedido pedido) {
            idPedido.setText(pedido.getId());
            observacaoPedido.setText(pedido.getObservacao());
            numeroMesaPedido.setText(String.valueOf(pedido.getNumeroMesa()));
        }
    }
}
