package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_CARGO_COLABORADOR;
import static com.example.toyapedidos.ui.Constantes.CHAVE_EMPRESAS;
import static com.example.toyapedidos.ui.Constantes.CHAVE_ID_EMPRESA;
import static com.example.toyapedidos.ui.Constantes.CHAVE_USUARIO;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityCadastraUsuarioBinding;
import com.example.toyapedidos.modelo.Empresa;
import com.example.toyapedidos.modelo.Usuario;
import com.example.toyapedidos.ui.ConexaoInternet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class CadastraUsuarioActivity extends AppCompatActivity {
    private ActivityCadastraUsuarioBinding binding;
    private MaterialTextView txtNomeEmpresa;
    private TextInputLayout txtNomeUsuario, txtEmailUsuario, txtSenhaUsuario;
    private TextInputEditText edtNomeUsuario, edtEmailUsuario, edtSenhaUsuario;
    private String stringNome, stringEmail, stringSenha;
    private MaterialButton btnCadastrarUsuario;
    private ConexaoInternet conexaoInternet;
    private Empresa empresaAtual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastraUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recebeDados();
        txtNomeEmpresa = binding.txtNomeEmpresa;
        txtNomeUsuario = binding.txtInputNomeNovoUsuario;
        txtEmailUsuario = binding.txtInputEmailNovoUsuario;
        txtSenhaUsuario = binding.txtInputSenhaNovoUsuario;
        edtNomeUsuario = binding.edtInputNomeNovoUsuario;
        edtEmailUsuario = binding.edtInputEmailNovoUsuario;
        edtSenhaUsuario = binding.edtInputSenhaNovoUsuario;
        btnCadastrarUsuario = binding.btnCadastrarNovoUsuario;
        
        txtNomeEmpresa.setText(empresaAtual.getNome());
        
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        conexaoInternet = new ConexaoInternet();
        registerReceiver(conexaoInternet,intentFilter);

        btnCadastrarUsuario.setOnClickListener(v ->{
            stringNome = Objects.requireNonNull(edtNomeUsuario.getText()).toString();
            stringEmail = Objects.requireNonNull(edtEmailUsuario.getText()).toString();
            stringSenha = Objects.requireNonNull(edtSenhaUsuario.getText()).toString();
            if (verificaCampos()){
                btnCadastrarUsuario.setEnabled(false);
                ConexaoInternet.TipoConexao tipoConexao = conexaoInternet.getTipoConexaoAtual(getApplicationContext());
                if (conexaoExiste(tipoConexao)){
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(stringEmail, stringSenha)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    salvaDadosUsuario();
                                    voltaParaMainActivity();
                                } else {
                                    String erro;
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthUserCollisionException exception){
                                        erro = "Email já cadastrado!";
                                    } catch (Exception exception){
                                        erro = "Erro ao cadastrar usuário! Tente novamente!";
                                    }
                                    btnCadastrarUsuario.setEnabled(true);
                                    Snackbar.make(binding.constraintLayoutCadastraUsuario, erro, Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
        conexaoInternet.addOnMudarEstadoConexao(tipoConexao -> {
            if (conexaoExiste(tipoConexao)) {
                btnCadastrarUsuario.setEnabled(true);
            } else {
                btnCadastrarUsuario.setEnabled(false);
            }
        });
    }

    private void recebeDados() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_ID_EMPRESA)){
            empresaAtual = (Empresa) dadosRecebidos.getSerializableExtra(CHAVE_ID_EMPRESA);
        }
    }

    private boolean conexaoExiste(ConexaoInternet.TipoConexao tipoConexao) {
        if (tipoConexao == ConexaoInternet.TipoConexao.TIPO_MOBILE || tipoConexao == ConexaoInternet.TipoConexao.TIPO_WIFI){
            return true;
        } else if (tipoConexao == ConexaoInternet.TipoConexao.TIPO_NAO_CONECTADO) {
            Snackbar.make(binding.constraintLayoutCadastraUsuario,"Sem conexão!", Snackbar.LENGTH_LONG).show();
        }
        return false;
    }

    private void voltaParaMainActivity() {
        finish();
    }

    private void salvaDadosUsuario() {
        String usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Usuario usuario = new Usuario(usuarioId, stringNome, CHAVE_CARGO_COLABORADOR);
        DatabaseReference minhareferencia = database.getReference(CHAVE_EMPRESAS);
        minhareferencia.child(empresaAtual.getId()).child(CHAVE_USUARIO).child(usuarioId).setValue(usuario);
    }

    private boolean verificaCampos() {
        return campoNomeValido() & campoEmailValido() & campoSenhaValida();
    }

    private boolean campoSenhaValida() {
        boolean confirmacao = false;
        if (stringSenha.isEmpty()){
            txtSenhaUsuario.setHelperText(getString(R.string.stringCampoNecessario));
        } else if (verificaSenhaRobusta()) {
            txtSenhaUsuario.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }

    private boolean campoEmailValido() {
        boolean confirmacao;
        String emailPadrao = getString(R.string.stringEmailPadrao);
        if (stringEmail.isEmpty()){
            txtEmailUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else if (stringEmail.matches(emailPadrao)) {
            txtEmailUsuario.setHelperTextEnabled(false);
            confirmacao = true;
        } else {
            txtEmailUsuario.setHelperText(getString(R.string.stringEmailInvalido));
            confirmacao = false;
        }
        return confirmacao;
    }

    private boolean campoNomeValido() {
        boolean confirmacao;
        if (stringNome.isEmpty()){
            txtNomeUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else {
            txtNomeUsuario.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }

    private boolean verificaSenhaRobusta() {
        boolean confirmacao;
        String senha = Objects.requireNonNull(edtSenhaUsuario.getText()).toString();
        int tamanhoSenha = senha.length();
        String letraMinuscula = getString(R.string.stringCasoLetraMinuscula);
        String letraMaiuscula = getString(R.string.stringCasoLetraMaiuscula);
        String numerico = getString(R.string.stringCasoNumerico);
        String especial = getString(R.string.stringCasoEspecial);

        if (senha.matches(especial)){
            if (senha.matches(letraMinuscula)){
                if (senha.matches(letraMaiuscula)){
                    if (senha.matches(numerico)){
                        if (tamanhoSenha >= 8){
                            confirmacao = true;
                        } else {
                            confirmacao = false;
                            txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaCurta));
                        }
                    } else {
                        confirmacao = false;
                        txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaNumerica));
                    }
                } else {
                    confirmacao = false;
                    txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaMaiuscula));
                }
            }else {
                confirmacao = false;
                txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaMinuscula));
            }
        }else {
            confirmacao = false;
            txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaEspecial));
        }

        return confirmacao;
    }
}