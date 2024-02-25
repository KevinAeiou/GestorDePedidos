package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_NOVO_PEDIDO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_NOVO_PEDIDO;
import static com.example.toyapedidos.ui.Utilitario.removeAcentos;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityNovoPedidoBinding;
import com.example.toyapedidos.modelo.Produto;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.example.toyapedidos.ui.ConexaoInternet;
import com.example.toyapedidos.ui.recyclerview.adapter.NovoPedidoAdapter;
import com.example.toyapedidos.ui.recyclerview.adapter.listener.OnItemClickListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NovoPedidoActivity extends AppCompatActivity {
    private ActivityNovoPedidoBinding binding;
    private NovoPedidoAdapter novoPedidoAdapter;
    private List<ProdutoPedido> cardapioNovoPedido;
    private MaterialTextView txtSomaTotal;
    private MaterialButton btnResumoPedido;
    private ConexaoInternet conexaoInternet;
    private ProgressBar progressoCircular;
    private ChipGroup chipGrupo;
    private HorizontalScrollView scrollView;
    private List<Integer> listaCategoriasSelecionadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNovoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_NOVO_PEDIDO);
        inicializaComponentes();
        atualizaCardapioNovoPedido();
        configuraBotaoResumoPedido();

        chipGrupo.setOnCheckedStateChangeListener((group, checkedIds) -> configuraListaFiltrada(checkedIds));

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        conexaoInternet = new ConexaoInternet();
        registerReceiver(conexaoInternet,intentFilter);
    }

    private void configuraChipGrupo() {
        scrollView.setVisibility(View.VISIBLE);
    }

    private void inicializaComponentes() {
        txtSomaTotal = binding.txtTotalPedido;
        progressoCircular = binding.progressoCircularNovoPedido;
        chipGrupo = binding.chipGroupCategoriasNovoPedido;
        scrollView = binding.scrollViewGrupoChip;
        listaCategoriasSelecionadas = new ArrayList<>();
    }

    private void configuraBotaoResumoPedido() {
        btnResumoPedido = binding.btnResumoPedido;
        btnResumoPedido.setOnClickListener(v ->{
            ArrayList<ProdutoPedido> novoPedido = new ArrayList<>();
            for (ProdutoPedido itemPedido: cardapioNovoPedido){
                if (itemPedido.getQuantidade() > 0){
                    novoPedido.add(itemPedido);
                }
            }
            if (novoPedido.isEmpty()){
                Snackbar.make(binding.getRoot(), "Nem um item selecionado!", Snackbar.LENGTH_LONG).show();
            }else {
                vaiParaResumoPedidoActivity(novoPedido);
            }
        });
    }

    private void vaiParaResumoPedidoActivity(ArrayList<ProdutoPedido> novoPedido) {
        Intent iniciaVaiParaResumoPedido = new Intent(getApplicationContext(), ResumoPedidoActivity.class);
        iniciaVaiParaResumoPedido.putExtra(CHAVE_NOVO_PEDIDO, novoPedido);
        startActivity(iniciaVaiParaResumoPedido);
        finish();
    }

    private void atualizaCardapioNovoPedido() {
        pegaTodosProdutos();
        List<ProdutoPedido> cardapioNovoPedido = new ArrayList<>();
        configuraRecyclerView(cardapioNovoPedido);
    }

    private void pegaTodosProdutos() {
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

                configuraChipGrupo();
                atualizaTxtSomaTotal();
                progressoCircular.setVisibility(View.GONE);
                configuraListaFiltrada(listaCategoriasSelecionadas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(binding.getRoot(), "Erro ao recuperar cardápio: "+error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void configuraListaFiltrada(List<Integer> listaIdCategoriasSelecionadas) {
        ArrayList<ProdutoPedido> listaFiltrada = new ArrayList<>();
        if (listaIdCategoriasSelecionadas.isEmpty()){
            novoPedidoAdapter.setListaFiltrada(cardapioNovoPedido);
        } else {
            List<String> categoriasSelecionadas = new ArrayList<>();
            for (int idCategoriaSelecionada : listaIdCategoriasSelecionadas){

                if (idCategoriaSelecionada == R.id.chipCategoriaBebidas){
                    if (!categoriasSelecionadas.contains(getString(R.string.stringBebidas))){
                        categoriasSelecionadas.add(getString(R.string.stringBebidas));
                    }
                } else if (idCategoriaSelecionada == R.id.chipCategoriaDiversos) {
                    if (!categoriasSelecionadas.contains(getString(R.string.stringDiversos))){
                        categoriasSelecionadas.add(getString(R.string.stringDiversos));
                    }
                } else if (idCategoriaSelecionada == R.id.chipCategoriaPizzas) {
                    if (!categoriasSelecionadas.contains(getString(R.string.stringPizzas))){
                        categoriasSelecionadas.add(getString(R.string.stringPizzas));
                    }
                } else if (idCategoriaSelecionada == R.id.chipCategoriaSanduiches) {
                    if (!categoriasSelecionadas.contains(getString(R.string.stringSanduiches))){
                        categoriasSelecionadas.add(getString(R.string.stringSanduiches));
                    }
                } else if (idCategoriaSelecionada == R.id.chipCategoriaTacasMilkshakes) {
                    if (!categoriasSelecionadas.contains(getString(R.string.stringTacas))){
                        categoriasSelecionadas.add(getString(R.string.stringTacas));
                    }
                }
            }
            for (String categoriaSelecionada : categoriasSelecionadas){
                for (ProdutoPedido produtoPedido : cardapioNovoPedido) {
                    String pedido1 = removeAcentos(produtoPedido.getCategoria()).replaceAll("-","").replaceAll(" ","").toLowerCase();
                    String pedido2 = removeAcentos(categoriaSelecionada).replaceAll("-","").replaceAll(" ","").toLowerCase();
                    Log.d("novoPedido", "Categorias: "+pedido1+" x "+pedido2);
                    if (pedido1.contains(pedido2)) {
                        listaFiltrada.add(produtoPedido);
                    }
                }
            }
            novoPedidoAdapter.setListaFiltrada(listaFiltrada);
        }
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
        novoPedidoAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ProdutoPedido produtoPedido, int posicao, int botaoId) {
                alteraQuantidadeProdutoPedido(produtoPedido, posicao, botaoId);
            }

            @Override
            public void onItemClick(Produto produto, int posicao) {

            }
        });
    }

    private void alteraQuantidadeProdutoPedido(ProdutoPedido produtoPedido, int posicao, int botaoId) {
        if (botaoId == R.id.itemBtnIncrementaQuantidadeNovoPedido){
            Log.d("adicionaNovoPedido", "Clicou em adiciona: "+produtoPedido.getNome());
            int novaQuantidade = produtoPedido.getQuantidade() + 1;
            produtoPedido.setQuantidade(novaQuantidade);
            Log.d("adicionaNovoPedido", "Nova quantidade: "+produtoPedido.getQuantidade());
            novoPedidoAdapter.altera(produtoPedido, posicao);
            atualizaTxtSomaTotal();
        } else if (botaoId == R.id.itemBtnDecrementaQuantidadeNovoPedido) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(conexaoInternet);
    }
}