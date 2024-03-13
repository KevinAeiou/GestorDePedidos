package com.example.toyapedidos.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.toyapedidos.databinding.ActivityTelaInicialBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TelaInicialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.toyapedidos.databinding.ActivityTelaInicialBinding binding = ActivityTelaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MaterialButton btnEntrarUsuario = binding.btnEntrar;
        MaterialButton btnCadastrarEmpresa = binding.btnCadastrarEmpresa;

        btnEntrarUsuario.setOnClickListener(v -> {
            Log.d("telaInicial", "Botão entrar.");
            //Snackbar.make(findViewById(R.id.layoutTelaInicial), "Vai para entra usuário.", Snackbar.LENGTH_LONG).show();
            Intent inicaVaiParaEntraUsuario = new Intent(getApplicationContext(), EntraUsuarioActivity.class);
            startActivity(inicaVaiParaEntraUsuario);
        });
        btnCadastrarEmpresa.setOnClickListener(v -> {
            Log.d("telaInicial", "Botão cadastrar.");
            Intent iniciaVaiParaCadastrarEmpresa = new Intent(getApplicationContext(), CadastraEmpresaActivity.class);
            startActivity(iniciaVaiParaCadastrarEmpresa);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioAtual != null){
            vaiParaMainActivity();
        }
    }

    private void vaiParaMainActivity() {
        Intent iniciaVaiParaMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iniciaVaiParaMainActivity);
        finish();
    }
}