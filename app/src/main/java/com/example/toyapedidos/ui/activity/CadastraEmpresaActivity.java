package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_EMPRESAS;
import static com.example.toyapedidos.ui.Constantes.CHAVE_USUARIO;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityCadastraEmpresaBinding;
import com.example.toyapedidos.modelo.Empresa;
import com.example.toyapedidos.modelo.Usuario;
import com.example.toyapedidos.ui.ConexaoInternet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class CadastraEmpresaActivity extends AppCompatActivity {
    private ActivityCadastraEmpresaBinding binding;
    private TextInputLayout txtNome, txtAdministrador, txtEmail, txtSenha;
    private TextInputEditText edtNome, edtAdministrador, edtEmail, edtSenha;
    private String stringNome, stringAdminstrador, stringEmail, stringSenha;
    private MaterialButton btnCadastrar;
    private ConexaoInternet conexaoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastraEmpresaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        txtNome = binding.txtInputNomeEmpresa;
        txtAdministrador = binding.txtInputAdminstradorEmpresa;
        txtEmail = binding.txtInputEmailAdmistradorEmpresa;
        txtSenha = binding.txtInputSenhaEmpresa;
        edtNome = binding.edtInputNomeEmpresa;
        edtAdministrador = binding.edtInputAdminstradorEmpresa;
        edtEmail = binding.edtInputEmailAdminstradorEmpresa;
        edtSenha = binding.edtInputSenhaEmpresa;
        btnCadastrar = binding.btnCadastrarNovaEmpresa;

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        conexaoInternet = new ConexaoInternet();
        registerReceiver(conexaoInternet,intentFilter);

        btnCadastrar.setOnClickListener(v -> {
            btnCadastrar.setEnabled(false);
            stringNome = Objects.requireNonNull(edtNome.getText()).toString();
            stringAdminstrador = Objects.requireNonNull(edtAdministrador.getText()).toString();
            stringEmail = Objects.requireNonNull(edtEmail.getText()).toString();
            stringSenha = Objects.requireNonNull(edtSenha.getText()).toString();
            if (verificaCampos()) {
                ConexaoInternet.TipoConexao tipoConexao = conexaoInternet.getTipoConexaoAtual(getApplicationContext());
                if (conexaoExiste(tipoConexao)) {
                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(stringEmail, stringSenha)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    salvaDadosEmpresa();
                                    vaiParaTelaInicial();
                                } else {
                                    String erro;
                                    try {
                                        throw Objects.requireNonNull(task.getException());
                                    } catch (FirebaseAuthUserCollisionException exception){
                                        erro = "Email já cadastrado!";
                                    } catch (Exception exception){
                                        erro = "Erro ao cadastrar usuário! Tente novamente!";
                                    }
                                    btnCadastrar.setEnabled(true);
                                    Snackbar.make(binding.constraintLayoutCadastraEmpresa, erro, Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            } else {
                btnCadastrar.setEnabled(true);
            }
        });

        conexaoInternet.addOnMudarEstadoConexao(tipoConexao -> {
            if (conexaoExiste(tipoConexao)){
                btnCadastrar.setEnabled(true);
            } else {
                btnCadastrar.setEnabled(false);
            }
        });
    }

    private void salvaDadosEmpresa() {
        String empresaId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Empresa empresa = new Empresa(empresaId, stringNome);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_EMPRESAS);
        minhareferencia.child(empresaId).setValue(empresa).addOnCompleteListener(task -> {
            DatabaseReference minhaReferencia = database.getReference(CHAVE_EMPRESAS);
            Usuario usuario = new Usuario(empresaId, stringAdminstrador, "Administrador");
            minhaReferencia.child(empresaId).child(CHAVE_USUARIO).child(empresaId).setValue(usuario);
        });


    }

    private boolean verificaCampos() {
        return campoNomeValido() & campoAdmistradorValido() & campoEmailValido() & campoSenhaValida();
    }

    private boolean campoSenhaValida() {
        boolean confirmacao = false;
        if (stringSenha.isEmpty()){
            txtSenha.setHelperText(getString(R.string.stringCampoNecessario));
        } else if (verificaSenhaRobusta()) {
            txtSenha.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }

    private boolean campoEmailValido() {
        boolean confirmacao;
        String emailPadrao = getString(R.string.stringEmailPadrao);
        if (stringEmail.isEmpty()){
            txtEmail.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else if (stringEmail.matches(emailPadrao)) {
            txtEmail.setHelperTextEnabled(false);
            confirmacao = true;
        } else {
            txtEmail.setHelperText(getString(R.string.stringEmailInvalido));
            confirmacao = false;
        }
        return confirmacao;
    }

    private boolean campoAdmistradorValido() {
        boolean confirmacao;
        if (stringAdminstrador.isEmpty()){
            txtAdministrador.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else {
            txtAdministrador.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }

    private boolean campoNomeValido() {
        boolean confirmacao;
        if (stringNome.isEmpty()){
            txtNome.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else {
            txtNome.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }

    private boolean conexaoExiste(ConexaoInternet.TipoConexao tipoConexao) {
        if (tipoConexao == ConexaoInternet.TipoConexao.TIPO_MOBILE || tipoConexao == ConexaoInternet.TipoConexao.TIPO_WIFI){
            return true;
        } else if (tipoConexao == ConexaoInternet.TipoConexao.TIPO_NAO_CONECTADO) {
            Snackbar.make(binding.constraintLayoutCadastraEmpresa,"Sem conexão!", Snackbar.LENGTH_LONG).show();
        }
        return false;
    }
    private boolean verificaSenhaRobusta() {
        boolean confirmacao;
        int tamanhoSenha = stringSenha.length();
        String letraMinuscula = getString(R.string.stringCasoLetraMinuscula);
        String letraMaiuscula = getString(R.string.stringCasoLetraMaiuscula);
        String numerico = getString(R.string.stringCasoNumerico);
        String especial = getString(R.string.stringCasoEspecial);

        if (stringSenha.matches(especial)){
            if (stringSenha.matches(letraMinuscula)){
                if (stringSenha.matches(letraMaiuscula)){
                    if (stringSenha.matches(numerico)){
                        if (tamanhoSenha >= 8){
                            confirmacao = true;
                        } else {
                            confirmacao = false;
                            txtSenha.setHelperText(getString(R.string.stringSenhaCurta));
                        }
                    } else {
                        confirmacao = false;
                        txtSenha.setHelperText(getString(R.string.stringSenhaNumerica));
                    }
                } else {
                    confirmacao = false;
                    txtSenha.setHelperText(getString(R.string.stringSenhaMaiuscula));
                }
            }else {
                confirmacao = false;
                txtSenha.setHelperText(getString(R.string.stringSenhaMinuscula));
            }
        }else {
            confirmacao = false;
            txtSenha.setHelperText(getString(R.string.stringSenhaEspecial));
        }
        return confirmacao;
    }
    private void vaiParaTelaInicial() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(conexaoInternet);
    }
}