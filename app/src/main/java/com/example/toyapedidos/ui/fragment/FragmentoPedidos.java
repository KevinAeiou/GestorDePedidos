package com.example.toyapedidos.ui.fragment;

import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PEDIDO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_PEDIDOS;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.databinding.FragmentoPedidosBinding;
import com.example.toyapedidos.modelo.Pedido;
import com.example.toyapedidos.ui.activity.AdicionaNovoPedidoActivity;
import com.example.toyapedidos.ui.recyclerview.adapter.PedidoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentoPedidos extends Fragment {

    private FragmentoPedidosBinding binding;
    private FloatingActionButton botaoFlutuante;
    private PedidoAdapter pedidoAdapter;
    private RecyclerView meuRecycler;
    private List<Pedido> pedidos;
    private DatabaseReference minhaReferencia;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(CHAVE_TITULO_PEDIDOS);
        binding = FragmentoPedidosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializaComponentes();
        configuraBotaoFlutuante();
        atualizaPedidos();
    }

    private void atualizaPedidos() {
        List<Pedido> pedidos = pegaTodosPedidos();
        configuraRecyclerView(pedidos);
    }

    private void configuraRecyclerView(List<Pedido> pedidos) {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(pedidos, meuRecycler);
    }

    private void configuraAdapter(List<Pedido> pedidos, RecyclerView meuRecycler) {
        pedidoAdapter = new PedidoAdapter(pedidos, getContext());
        meuRecycler.setAdapter(pedidoAdapter);
    }

    private List<Pedido> pegaTodosPedidos() {
        pedidos = new ArrayList<>();
        minhaReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pedidos.clear();
                for (DataSnapshot dn: snapshot.getChildren()){
                    Pedido pedido = dn.getValue(Pedido.class);
                    if (pedido != null){
                        pedidos.add(pedido);
                    }
                }
                pedidoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(binding.getRoot(), "Erro ao carregar pedidos!"+ error, Snackbar.LENGTH_LONG).show();
            }
        });
        return pedidos;
    }

    private void configuraBotaoFlutuante() {
        botaoFlutuante.setOnClickListener(v -> vaiParaAdicionaNovoPedido());
    }

    private void vaiParaAdicionaNovoPedido() {
        Intent iniciaVaiParaCadastraNovoPedido = new Intent(getActivity(), AdicionaNovoPedidoActivity.class);
        startActivity(iniciaVaiParaCadastraNovoPedido);
    }

    private void inicializaComponentes() {
        botaoFlutuante = binding.botaoFlutuanteNovoPedido;
        meuRecycler = binding.recyclerViewFragmentoPedido;
        FirebaseDatabase meuBancoDados = FirebaseDatabase.getInstance();
        minhaReferencia = meuBancoDados.getReference(CHAVE_LISTA_PEDIDO);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}