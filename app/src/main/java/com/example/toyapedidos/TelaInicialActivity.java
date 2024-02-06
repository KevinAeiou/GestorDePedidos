package com.example.toyapedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.toyapedidos.databinding.ActivityTelaInicialBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

public class TelaInicialActivity extends AppCompatActivity {
    private ActivityTelaInicialBinding binding;
    private MaterialButton btnEntrarUsuario, btnCadastrarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnEntrarUsuario = binding.btnEntrar;
        btnCadastrarUsuario = binding.btnCadastrar;

        btnEntrarUsuario.setOnClickListener(v -> {
            Log.d("telaInicial", "Botão entrar.");
            Snackbar.make(findViewById(R.id.layoutTelaInicial), "Vai para entra usuário.", Snackbar.LENGTH_LONG).show();
        });
        btnCadastrarUsuario.setOnClickListener(v -> {
            Log.d("telaInicial", "Botão cadastrar.");
            Intent iniciaVaiParaCadastraUsuario = new Intent(getApplicationContext(),CadastraUsuarioActivity.class);
            startActivity(iniciaVaiParaCadastraUsuario);
            finish();
        });
    }
}