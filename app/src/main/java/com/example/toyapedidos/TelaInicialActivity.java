package com.example.toyapedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.toyapedidos.databinding.ActivityTelaInicialBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            //Snackbar.make(findViewById(R.id.layoutTelaInicial), "Vai para entra usuário.", Snackbar.LENGTH_LONG).show();
            Intent inicaVaiParaEntraUsuario = new Intent(getApplicationContext(), EntraUsuarioActivity.class);
            startActivity(inicaVaiParaEntraUsuario);
        });
        btnCadastrarUsuario.setOnClickListener(v -> {
            Log.d("telaInicial", "Botão cadastrar.");
            Intent iniciaVaiParaCadastraUsuario = new Intent(getApplicationContext(),CadastraUsuarioActivity.class);
            startActivity(iniciaVaiParaCadastraUsuario);
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