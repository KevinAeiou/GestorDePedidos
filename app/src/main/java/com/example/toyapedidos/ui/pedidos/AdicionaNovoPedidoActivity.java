package com.example.toyapedidos.ui.pedidos;

import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_CARDAPIO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_NOVO_PEDIDO;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.toyapedidos.R;

public class AdicionaNovoPedidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adiciona_novo_pedido);
        setTitle(CHAVE_TITULO_NOVO_PEDIDO);
    }
}