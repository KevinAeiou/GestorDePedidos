package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_ID_EMPRESA;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityEntraUsuarioBinding;
import com.example.toyapedidos.ui.ConexaoInternet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class EntraUsuarioActivity extends AppCompatActivity {
    private ActivityEntraUsuarioBinding binding;
    private TextInputLayout txtEmailUsuario, txtSenhaUsuario;
    private TextInputEditText edtEmailUsuario, edtSenhaUsuario;
    private MaterialButton btnEntraUsuario;
    private ConexaoInternet conexaoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntraUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        conexaoInternet = new ConexaoInternet();
        registerReceiver(conexaoInternet,intentFilter);

        txtEmailUsuario = binding.txtEmailEntraUsuario;
        txtSenhaUsuario = binding.txtSenhaEntraUsuario;
        edtEmailUsuario = binding.edtEmailEntraUsuario;
        edtSenhaUsuario = binding.edtSenhaEntraUsuario;
        btnEntraUsuario = binding.btnEntraUsuario;

        btnEntraUsuario.setOnClickListener(v -> {
            if (verificaCampos()){
                btnEntraUsuario.setEnabled(false);
                ConexaoInternet.TipoConexao tipoConexao = conexaoInternet.getTipoConexaoAtual(getApplicationContext());
                if (conexaoExiste(tipoConexao)) {
                    autenticaUsuario();
                }
            }
        });
        conexaoInternet.addOnMudarEstadoConexao(tipoConexao -> {
            if (conexaoExiste(tipoConexao)) {
                btnEntraUsuario.setEnabled(true);
            }
        });
    }

    private boolean conexaoExiste(ConexaoInternet.TipoConexao tipoConexao) {
        if (tipoConexao == ConexaoInternet.TipoConexao.TIPO_MOBILE || tipoConexao == ConexaoInternet.TipoConexao.TIPO_WIFI){
            return true;
        } else if (tipoConexao == ConexaoInternet.TipoConexao.TIPO_NAO_CONECTADO) {
            Snackbar.make(binding.constraintLayoutEntraUsuario,"Sem conexão!", Snackbar.LENGTH_LONG).show();
        }
        return false;
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
                            btnEntraUsuario.setEnabled(true);
                        }
                    }
                });
    }

    private void vaiParaMainActivity() {
        Intent iniciaVaiParaMainActivity = new Intent(
                getApplicationContext(), MainActivity.class);
        iniciaVaiParaMainActivity.putExtra(CHAVE_ID_EMPRESA, "");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(conexaoInternet);
        binding = null;
    }
}