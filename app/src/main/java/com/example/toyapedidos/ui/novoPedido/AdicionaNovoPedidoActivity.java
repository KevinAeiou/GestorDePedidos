package com.example.toyapedidos.ui.novoPedido;

import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_NOVO_PEDIDO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.example.toyapedidos.databinding.ActivityAdicionaNovoPedidoBinding;
import com.example.toyapedidos.modelo.Produto;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.example.toyapedidos.ui.novoPedido.recyclerView.NovoPedidoAdapter;
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
    private RecyclerView meuRecycler;
    private List<ProdutoPedido> cardapioNovoPedido;
    private DatabaseReference minhaReferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdicionaNovoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_NOVO_PEDIDO);
        //atualizaCardapioNovoPedido();
    }

    private void atualizaCardapioNovoPedido() {
        List<ProdutoPedido> cardapioNovoPedido = pegaTodosProdutos();
        configuraRecyclerView(cardapioNovoPedido);
    }

    private List<ProdutoPedido> pegaTodosProdutos() {
        cardapioNovoPedido = new ArrayList<>();
        FirebaseDatabase meuBancoDados = FirebaseDatabase.getInstance();
        minhaReferencia = meuBancoDados.getReference(CHAVE_LISTA_PRODUTO);
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn:snapshot.getChildren()){
                    Produto produto = dn.getValue(Produto.class);
                    ProdutoPedido produtoPedido = new ProdutoPedido(
                            produto.getId(),
                            produto.getNome(),
                            produto.getDescricao(),
                            produto.getCategoria(),
                            produto.getValor(),
                            0
                    );
                    if (produtoPedido != null){
                        cardapioNovoPedido.add(produtoPedido);
                    }
                }
                novoPedidoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return cardapioNovoPedido;
    }

    private void configuraRecyclerView(List<ProdutoPedido> cardapioNovoPedido) {
        meuRecycler = binding.recyclerViewNovoPedido;
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        configuraAdapter(cardapioNovoPedido, meuRecycler);
    }

    private void configuraAdapter(List<ProdutoPedido> cardapioNovoPedido, RecyclerView meuRecycler) {
        novoPedidoAdapter = new NovoPedidoAdapter(cardapioNovoPedido, getApplicationContext());
        meuRecycler.setAdapter(novoPedidoAdapter);
    }
}