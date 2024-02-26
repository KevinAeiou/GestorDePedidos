package com.example.toyapedidos.ui.fragment;

import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PEDIDO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_PEDIDOS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.FragmentoPedidosBinding;
import com.example.toyapedidos.modelo.Pedido;
import com.example.toyapedidos.ui.activity.NovoPedidoActivity;
import com.example.toyapedidos.ui.recyclerview.adapter.PedidoAdapter;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentoPedidos extends Fragment {

    private FragmentoPedidosBinding binding;
    private FloatingActionButton botaoFlutuante;
    private PedidoAdapter pedidoAdapter;
    private RecyclerView meuRecycler;
    private List<Pedido> pedidos, pedidosFiltrado;
    private ChipGroup chipGrupo;
    private DatabaseReference minhaReferencia;
    private int estadoSelecionado;
    private ProgressBar progresso;
    private DrawerLayout drawerLayoutMain;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(CHAVE_TITULO_PEDIDOS);
        binding = FragmentoPedidosBinding.inflate(inflater, container, false);
        Log.d("fragmentoPedidos", "onCreateView criado.");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("fragmentoPedidos", "onViewCreate criado.");
        inicializaComponentes();
        configuraBotaoFlutuante();
        atualizaPedidos();
        configuraDeslizeItem();
        configuraChipFiltraEstados();
    }

    private void configuraChipFiltraEstados() {
        chipGrupo.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int idChipSelecionado = checkedIds.get(0);
            if (idChipSelecionado == R.id.chipEstadoParaFazerPedidos){
                estadoSelecionado = 0;
            } else if(idChipSelecionado == R.id.chipEstadoFazendoPedidos){
                estadoSelecionado = 1;
            } else if(idChipSelecionado == R.id.chipEstadoProntoPedidos){
                estadoSelecionado = 2;
            } else if(idChipSelecionado == R.id.chipEstadoEntreguePedidos){
                estadoSelecionado = 3;
            }
            Log.d(CHAVE_TITULO_PEDIDOS, "Estado selecionado listener"+estadoSelecionado);
            configuraChipEstados(estadoSelecionado);
        });
    }

    private void configuraDeslizeItem() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int posicaoDeslize = viewHolder.getAdapterPosition();
                if (pedidosFiltrado.get(posicaoDeslize).getEstado()==3){
                    Snackbar.make(getActivity().findViewById(R.id.drawerLayoutMain),"Estado não pode ser alterado", Snackbar.LENGTH_LONG).show();
                    pedidoAdapter.notifyDataSetChanged();
                }else {
                    alteraEstadoPedido(posicaoDeslize);
                    if (pedidoAdapter != null){
                        pedidoAdapter.remove(posicaoDeslize);
                    }
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(meuRecycler);
    }

    private void alteraEstadoPedido(int posicaoDeslize) {
        Pedido pedidoSelecionado = pedidosFiltrado.get(posicaoDeslize);
        int novoEstado = pedidoSelecionado.getEstado() + 1;
        String idPedido = pedidoSelecionado.getId();
        minhaReferencia.child(idPedido).child("estado").setValue(novoEstado);
    }

    private void configuraChipEstados(int estadoSelecionado) {
        if (chipGrupo.getVisibility() == View.GONE){
            chipGrupo.setVisibility(View.VISIBLE);
        }
        pedidosFiltrado = filtraListaChip(estadoSelecionado);
        if (pedidosFiltrado.isEmpty()) {
            Snackbar.make(getActivity().findViewById(R.id.drawerLayoutMain), "Nem um pedido encontrado!", Snackbar.LENGTH_LONG).show();
            pedidoAdapter.limpaLista();
        } else {
            for (int x=0;x<pedidosFiltrado.size();x++){
                Log.d("pedidoLista", "Data hora não ordenado: "+pedidosFiltrado.get(x).getNumeroMesa()+pedidosFiltrado.get(x).getDataHora());
            }
            pedidosFiltrado.sort((o1, o2) -> {
                if (o1.getDataHora() == null  || o2.getDataHora() == null)
                    return 0;
                return o1.getDataHora().compareTo(o2.getDataHora());
            });
            for (int x=0;x<pedidosFiltrado.size();x++){
                Log.d("pedidoLista", "Data hora ordenado: "+pedidosFiltrado.get(x).getNumeroMesa()+pedidosFiltrado.get(x).getDataHora());
            }
            pedidoAdapter.setListaFiltrada(pedidosFiltrado);
        }
    }

    private List<Pedido> filtraListaChip(int estadoSelecionado) {
        List<Pedido> listaFiltradaPedidos = new ArrayList<>();
        Log.d("fragmentoPedidos", "Estado selecionado: "+estadoSelecionado);
        for (Pedido pedido : pedidos) {
            if (pedido.getEstado() == estadoSelecionado) {
                Log.d("fragmentoPedidos", pedido.getId());
                listaFiltradaPedidos.add(pedido);
            }
        }
        return listaFiltradaPedidos;
    }

    private void atualizaPedidos() {
        pegaTodosPedidos();
        List<Pedido> pedidosFiltrado = new ArrayList<>();
        configuraRecyclerView(pedidosFiltrado);
    }

    private void configuraRecyclerView(List<Pedido> pedidosFiltrado) {
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        configuraAdapter(pedidosFiltrado, meuRecycler);
    }

    private void configuraAdapter(List<Pedido> pedidos, RecyclerView meuRecycler) {
        pedidoAdapter = new PedidoAdapter(pedidos, getContext());
        meuRecycler.setAdapter(pedidoAdapter);
    }

    private void pegaTodosPedidos() {
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
                Log.d(CHAVE_TITULO_PEDIDOS, "Carregou lista de pedidos no servidor.");
                configuraChipEstados(estadoSelecionado);
                progresso.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(getActivity().findViewById(R.id.drawerLayoutMain), "Erro ao carregar pedidos!"+ error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void configuraBotaoFlutuante() {
        botaoFlutuante.setOnClickListener(v -> vaiParaAdicionaNovoPedido());
    }

    private void vaiParaAdicionaNovoPedido() {
        Intent iniciaVaiParaCadastraNovoPedido = new Intent(getActivity(), NovoPedidoActivity.class);
        startActivity(iniciaVaiParaCadastraNovoPedido);
    }

    private void inicializaComponentes() {
        progresso = binding.progressoCircularPedidos;
        botaoFlutuante = binding.botaoFlutuanteNovoPedido;
        meuRecycler = binding.recyclerViewFragmentoPedido;
        chipGrupo = binding.chipGroupEstadosPedidos;
        FirebaseDatabase meuBancoDados = FirebaseDatabase.getInstance();
        minhaReferencia = meuBancoDados.getReference(CHAVE_LISTA_PEDIDO);
        int idChipSelecionado = chipGrupo.getCheckedChipId();
        if (idChipSelecionado == R.id.chipEstadoParaFazerPedidos){
            estadoSelecionado = 0;
        } else if(idChipSelecionado == R.id.chipEstadoFazendoPedidos){
            estadoSelecionado = 1;
        } else if(idChipSelecionado == R.id.chipEstadoProntoPedidos){
            estadoSelecionado = 2;
        } else if(idChipSelecionado == R.id.chipEstadoProntoPedidos){
            estadoSelecionado = 2;
        } else if(idChipSelecionado == R.id.chipEstadoEntreguePedidos){
            estadoSelecionado = 3;
        }
        Log.d(CHAVE_TITULO_PEDIDOS, "Estado inicial selecionado: "+estadoSelecionado);
     }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}