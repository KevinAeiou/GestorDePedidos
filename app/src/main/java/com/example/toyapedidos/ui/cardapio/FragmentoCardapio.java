package com.example.toyapedidos.ui.cardapio;

import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_CARDAPIO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_PEDIDOS;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.toyapedidos.databinding.FragmentoCardapioBinding;
import com.example.toyapedidos.ui.CadastraProdutoActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentoCardapio extends Fragment {

    private FragmentoCardapioBinding binding;
    private FloatingActionButton botaoFlutuante;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(CHAVE_TITULO_CARDAPIO);
        CardapioViewModel galleryViewModel =
                new ViewModelProvider(this).get(CardapioViewModel.class);

        binding = FragmentoCardapioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        botaoFlutuante = binding.floatingActionButton;
        botaoFlutuante.setOnClickListener(v -> vaiParaCadastraProdutoActivity());

        final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void vaiParaCadastraProdutoActivity() {
        Intent iniciaVaiParaCadastraProdutoActivity = new Intent(getActivity(), CadastraProdutoActivity.class);
        startActivity(iniciaVaiParaCadastraProdutoActivity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}