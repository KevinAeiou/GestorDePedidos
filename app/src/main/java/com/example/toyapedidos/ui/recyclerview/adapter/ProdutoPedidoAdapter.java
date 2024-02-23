package com.example.toyapedidos.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.List;

public class ProdutoPedidoAdapter extends RecyclerView.Adapter<ProdutoPedidoAdapter.ProdutoPedidoViewHolder> {
    private List<ProdutoPedido> produtosPedido;
    private Context context;

    public ProdutoPedidoAdapter(List<ProdutoPedido> produtoPedidos, Context context) {
        this.produtosPedido = produtoPedidos;
        this.context = context;
    }

    @NonNull
    @Override
    public ProdutoPedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_produto_pedido, parent, false);
        return new ProdutoPedidoViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoPedidoAdapter.ProdutoPedidoViewHolder holder, int position) {
        ProdutoPedido produtoPedido = produtosPedido.get(position);
        holder.vincula(produtoPedido);
    }

    @Override
    public int getItemCount() {
        return produtosPedido.size();
    }

    public class ProdutoPedidoViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCheckBox checkBoxProdutoPedido;
        private final TextView txtQuantidadeProdudoPedido;
        private final TextView txtNomeProdutoPedido;
        private ProdutoPedido produtoPedido;
        public ProdutoPedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxProdutoPedido = itemView.findViewById(R.id.checkBoxItemProdutoPedido);
            txtQuantidadeProdudoPedido = itemView.findViewById(R.id.txtQuantidadeItemProdutoPedido);
            txtNomeProdutoPedido = itemView.findViewById(R.id.txtNomeItemProdutoPedido);
        }
        public void vincula(ProdutoPedido produtoPedido){
            this.produtoPedido = produtoPedido;
            preencheCampos(produtoPedido);
        }

        private void preencheCampos(ProdutoPedido produtoPedido) {
            checkBoxProdutoPedido.setChecked(produtoPedido.isCheck());
            txtQuantidadeProdudoPedido.setText(produtoPedido.getQuantidade()+" x");
            txtNomeProdutoPedido.setText(produtoPedido.getNome());
        }
    }
}
