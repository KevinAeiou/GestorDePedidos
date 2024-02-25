package com.example.toyapedidos.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityEntraUsuarioBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class EntraUsuarioActivity extends AppCompatActivity {
    private TextInputLayout txtEmailUsuario, txtSenhaUsuario;
    private TextInputEditText edtEmailUsuario, edtSenhaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.toyapedidos.databinding.ActivityEntraUsuarioBinding binding = ActivityEntraUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtEmailUsuario = binding.txtEmailEntraUsuario;
        txtSenhaUsuario = binding.txtSenhaEntraUsuario;
        edtEmailUsuario = binding.edtEmailEntraUsuario;
        edtSenhaUsuario = binding.edtSenhaEntraUsuario;
        MaterialButton btnEntraUsuario = binding.btnEntraUsuario;

        btnEntraUsuario.setOnClickListener(v -> {
            if (verificaCampos()){
                autenticaUsuario();
            }
        });
    }

    private void autenticaUsuario() {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(Objects.requireNonNull(edtEmailUsuario.getText()).toString(), Objects.requireNonNull(edtSenhaUsuario.getText()).toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        vaiParaMainActivity();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception exception){
                            txtEmailUsuario.setHelperText("Email ou senha inválidos!");
                            txtSenhaUsuario.setHelperText("Email ou senha inválidos!");
                        }
                    }
                });
    }

    private void vaiParaMainActivity() {
        Intent iniciaVaiParaMainActivity = new Intent(
                getApplicationContext(), MainActivity.class);
        startActivity(iniciaVaiParaMainActivity);
        finish();
    }

    private boolean verificaCampos() {
        return campoEmailValido() & campoSenhaValida();
    }

    private boolean campoEmailValido() {
        boolean confirmacao;
        if (Objects.requireNonNull(edtEmailUsuario.getText()).toString().isEmpty()){
            txtEmailUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else {
            txtEmailUsuario.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }

    private boolean campoSenhaValida() {
        boolean confirmacao;
        if (Objects.requireNonNull(edtSenhaUsuario.getText()).toString().isEmpty()){
            txtSenhaUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else {
            txtSenhaUsuario.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }
}