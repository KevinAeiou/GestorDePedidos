package com.example.toyapedidos.ui.pedidos;

import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_PEDIDOS;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.toyapedidos.databinding.FragmentoPedidosBinding;
import com.example.toyapedidos.ui.novoPedido.AdicionaNovoPedidoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentoPedidos extends Fragment {

    private FragmentoPedidosBinding binding;
    private FloatingActionButton botaoFlutuante;

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}