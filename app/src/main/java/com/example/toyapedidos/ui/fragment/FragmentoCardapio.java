package com.example.toyapedidos.ui.fragment;

import static com.example.toyapedidos.ui.Constantes.CHAVE_CADASTRA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_MODIFICA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_REQUISICAO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_CARDAPIO;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.FragmentoCardapioBinding;
import com.example.toyapedidos.modelo.Produto;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.example.toyapedidos.ui.activity.CadastraProdutoActivity;
import com.example.toyapedidos.ui.recyclerview.adapter.CardapioAdapter;
import com.example.toyapedidos.ui.recyclerview.adapter.listener.OnItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentoCardapio extends Fragment {

    private FragmentoCardapioBinding binding;
    private FloatingActionButton botaoFlutuante;
    private CardapioAdapter cardapioAdapter;
    private RecyclerView meuRecycler;
    private List<Produto> cardapio;
    private DatabaseReference minhaReferencia;
    private SwipeRefreshLayout refreshLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(CHAVE_TITULO_CARDAPIO);

        binding = FragmentoCardapioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
        atualizaCardapio();
        configuraRefreshLayout();
        configuraDeslizeItem();
        botaoFlutuante.setOnClickListener(v -> vaiParaCadastraProdutoActivity(new Produto(), CHAVE_CADASTRA_PRODUTO));

    }

    private void configuraDeslizeItem() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int itemPosicao = viewHolder.getAdapterPosition();
                if (cardapioAdapter != null){
                    Produto produtoRemovido = cardapio.get(itemPosicao);
                    cardapioAdapter.remove(itemPosicao);
                    Snackbar snackbarDesfazer = Snackbar.make(requireView(), produtoRemovido.getNome(), Snackbar.LENGTH_LONG);
                    snackbarDesfazer.setAction(getString(R.string.stringDesfazer), v -> cardapioAdapter.adiciona(itemPosicao, produtoRemovido));
                    snackbarDesfazer.show();
                    snackbarDesfazer.addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event != DISMISS_EVENT_ACTION){
                                removeProdutoCardapio(produtoRemovido);
                            }
                        }
                    });

                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(meuRecycler);
    }

    private void removeProdutoCardapio(Produto produtoRemovido) {
        minhaReferencia.child(produtoRemovido.getId()).removeValue();
    }

    private void configuraRefreshLayout() {
        refreshLayout.setOnRefreshListener(this::atualizaCardapio);
    }

    private void atualizaCardapio() {
        List<Produto> cardapio = pegaTodosProdutos();
        configuraRecyclerView(cardapio);
    }

    private void configuraRecyclerView(List<Produto> cardapio) {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(cardapio, meuRecycler);
    }

    private void configuraAdapter(List<Produto> cardapio, RecyclerView meuRecycler) {
        cardapioAdapter = new CardapioAdapter(cardapio, getContext());
        meuRecycler.setAdapter(cardapioAdapter);
        cardapioAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ProdutoPedido produtoPedido, int posicao, int botaoId) {

            }

            @Override
            public void onItemClick(Produto produto, int posicao) {
                vaiParaCadastraProdutoActivity(produto, CHAVE_MODIFICA_PRODUTO);
            }
        });
    }

    private List<Produto> pegaTodosProdutos() {
        cardapio = new ArrayList<>();
        minhaReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cardapio.clear();
                for (DataSnapshot dn:snapshot.getChildren()){
                    Produto produto = dn.getValue(Produto.class);
                    if (produto != null){
                        cardapio.add(produto);
                    }
                }
                cardapioAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(requireView(), "Erro ao carregar cardápio: "+ error, Snackbar.LENGTH_LONG).show();
            }
        });
        return cardapio;
    }

    private void inicializaComponentes() {
        botaoFlutuante = binding.floatingActionButton;
        meuRecycler = binding.recyclerViewFragmentoCardapio;
        refreshLayout = binding.refreshLayoutFragmentoCardapio;
        FirebaseDatabase meuBancoDados = FirebaseDatabase.getInstance();
        minhaReferencia = meuBancoDados.getReference(CHAVE_LISTA_PRODUTO);
    }

    private void vaiParaCadastraProdutoActivity(Produto produto, int chaveRequisicao) {
        Intent iniciaVaiParaCadastraProdutoActivity = new Intent(getActivity(), CadastraProdutoActivity.class);
        iniciaVaiParaCadastraProdutoActivity.putExtra(CHAVE_REQUISICAO,chaveRequisicao);
        iniciaVaiParaCadastraProdutoActivity.putExtra(CHAVE_PRODUTO,produto);
        startActivity(iniciaVaiParaCadastraProdutoActivity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}