package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_RESUMO_PEDIDO;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityRevisaPedidoBinding;

public class ResumoPedidoActivity extends AppCompatActivity {
    private ActivityRevisaPedidoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRevisaPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_RESUMO_PEDIDO);
    }
}