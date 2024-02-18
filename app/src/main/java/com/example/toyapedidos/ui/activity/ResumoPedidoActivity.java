package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_NOVO_PEDIDO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_RESUMO_PEDIDO;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityResumoPedidoBinding;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.example.toyapedidos.ui.recyclerview.adapter.NovoPedidoAdapter;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ResumoPedidoActivity extends AppCompatActivity {
    private ActivityResumoPedidoBinding binding;
    private ArrayList<ProdutoPedido> resumoPedido;
    private MaterialTextView txtSomaTotal;
    private NovoPedidoAdapter meuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResumoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_RESUMO_PEDIDO);
        recebeDadosIntent();
        txtSomaTotal = binding.txtTotalResumoPedido;
        atualizaTxtSomaTotal();
        configuraRecyclerView();
    }

    private void configuraRecyclerView() {
        RecyclerView meuRecycler = binding.recyclerViewResumoPedido;
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        configuraAdapter(meuRecycler);
    }

    private void configuraAdapter(RecyclerView meuRecycler) {
        meuAdapter = new NovoPedidoAdapter(resumoPedido, getApplicationContext());
        meuRecycler.setAdapter(meuAdapter);
        meuAdapter.setOnItemClickListener(((produtoPedido, posicao, botaoId) -> alteraQuantidadeProdutoPedido(produtoPedido,posicao, botaoId)));
    }

    private void alteraQuantidadeProdutoPedido(ProdutoPedido produtoPedido, int posicao, int botaoId) {
        if (botaoId == R.id.itemBtnSomaQuantidade){
            Log.d("adicionaNovoPedido", "Clicou em adiciona: "+produtoPedido.getNome());
            int novaQuantidade = produtoPedido.getQuantidade() + 1;
            produtoPedido.setQuantidade(novaQuantidade);
            Log.d("adicionaNovoPedido", "Nova quantidade: "+produtoPedido.getQuantidade());
            meuAdapter.altera(produtoPedido, posicao);
            atualizaTxtSomaTotal();
        } else if (botaoId == R.id.itemBtnSubtraiQuantidade) {
            Log.d("adicionaNovoPedido", "Clicou em subtrai: "+produtoPedido.getNome());
            if (produtoPedido.getQuantidade() > 0){
                int novaQuantidade = produtoPedido.getQuantidade() - 1;
                produtoPedido.setQuantidade(novaQuantidade);
                Log.d("adicionaNovoPedido", "Nova quantidade: "+produtoPedido.getQuantidade());
                meuAdapter.altera(produtoPedido, posicao);
                atualizaTxtSomaTotal();
            }
        }
    }

    private void atualizaTxtSomaTotal() {
        double somaTotal = 0;
        for (ProdutoPedido produtoPedido: resumoPedido){
            double valorItem = produtoPedido.getValor() * produtoPedido.getQuantidade();
            somaTotal += valorItem;
        }
        txtSomaTotal.setText(String.format("TOTAL R$ %s", somaTotal));
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOVO_PEDIDO)){
            resumoPedido = (ArrayList<ProdutoPedido>) dadosRecebidos.getSerializableExtra(CHAVE_NOVO_PEDIDO);
        }
    }
}