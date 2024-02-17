package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_NOVO_PEDIDO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityAdicionaNovoPedidoBinding;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.example.toyapedidos.ui.recyclerview.adapter.NovoPedidoAdapter;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdicionaNovoPedidoActivity extends AppCompatActivity {
    private ActivityAdicionaNovoPedidoBinding binding;
    private NovoPedidoAdapter novoPedidoAdapter;
    private List<ProdutoPedido> cardapioNovoPedido;
    private MaterialTextView txtSomaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdicionaNovoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_NOVO_PEDIDO);
        txtSomaTotal = binding.txtTotalPedido;
        atualizaCardapioNovoPedido();
    }

    private void atualizaCardapioNovoPedido() {
        List<ProdutoPedido> cardapioNovoPedido = pegaTodosProdutos();
        configuraRecyclerView(cardapioNovoPedido);
    }

    private List<ProdutoPedido> pegaTodosProdutos() {
        cardapioNovoPedido = new ArrayList<>();
        FirebaseDatabase meuBancoDados = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = meuBancoDados.getReference(CHAVE_LISTA_PRODUTO);
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn:snapshot.getChildren()){
                    ProdutoPedido produtoPedido = dn.getValue(ProdutoPedido.class);
                    if (produtoPedido != null){
                        produtoPedido.setQuantidade(0);
                        cardapioNovoPedido.add(produtoPedido);
                    }
                }
                atualizaTxtSomaTotal();
                novoPedidoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return cardapioNovoPedido;
    }

    private void atualizaTxtSomaTotal() {
        double somaTotal = 0;
        for (ProdutoPedido produtoPedido: cardapioNovoPedido){
            double valorItem = produtoPedido.getValor() * produtoPedido.getQuantidade();
            somaTotal += valorItem;
        }
        txtSomaTotal.setText(String.format("TOTAL R$ %s", somaTotal));
    }

    private void configuraRecyclerView(List<ProdutoPedido> cardapioNovoPedido) {
        RecyclerView meuRecycler = binding.recyclerViewNovoPedido;
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        configuraAdapter(cardapioNovoPedido, meuRecycler);
    }

    private void configuraAdapter(List<ProdutoPedido> cardapioNovoPedido, RecyclerView meuRecycler) {
        novoPedidoAdapter = new NovoPedidoAdapter(cardapioNovoPedido, getApplicationContext());
        meuRecycler.setAdapter(novoPedidoAdapter);
        novoPedidoAdapter.setOnItemClickListener((produtoPedido, posicao, botaoId) -> alteraQuantidadeProdutoPedido(produtoPedido, posicao, botaoId));
    }

    private void alteraQuantidadeProdutoPedido(ProdutoPedido produtoPedido, int posicao, int botaoId) {
        if (botaoId == R.id.itemBtnSomaQuantidade){
            Log.d("adicionaNovoPedido", "Clicou em adiciona: "+produtoPedido.getNome());
            int novaQuantidade = produtoPedido.getQuantidade() + 1;
            produtoPedido.setQuantidade(novaQuantidade);
            Log.d("adicionaNovoPedido", "Nova quantidade: "+produtoPedido.getQuantidade());
            novoPedidoAdapter.altera(produtoPedido, posicao);
            atualizaTxtSomaTotal();
        } else if (botaoId == R.id.itemBtnSubtraiQuantidade) {
            Log.d("adicionaNovoPedido", "Clicou em subtrai: "+produtoPedido.getNome());
            if (produtoPedido.getQuantidade() > 0){
                int novaQuantidade = produtoPedido.getQuantidade() - 1;
                produtoPedido.setQuantidade(novaQuantidade);
                Log.d("adicionaNovoPedido", "Nova quantidade: "+produtoPedido.getQuantidade());
                novoPedidoAdapter.altera(produtoPedido, posicao);
                atualizaTxtSomaTotal();
            }
        }
    }
}