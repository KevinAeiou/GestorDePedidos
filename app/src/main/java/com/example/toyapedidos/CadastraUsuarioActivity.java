package com.example.toyapedidos;

import static com.example.toyapedidos.ui.Constantes.CHAVE_CARGO_COLABORADOR;
import static com.example.toyapedidos.ui.Constantes.CHAVE_USUARIO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.toyapedidos.databinding.ActivityCadastraUsuarioBinding;
import com.example.toyapedidos.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastraUsuarioActivity extends AppCompatActivity {
    private ActivityCadastraUsuarioBinding binding;
    private TextInputLayout txtNomeUsuario, txtEmailUsuario, txtSenhaUsuario;
    private TextInputEditText edtNomeUsuario, edtEmailUsuario, edtSenhaUsuario;
    private MaterialTextView txtRecuperarSenha;
    private MaterialButton btnCadastrarUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastraUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        txtNomeUsuario = binding.txtNomeCadastraUsuario;
        txtEmailUsuario = binding.txtEmailCadastraUsuario;
        txtSenhaUsuario = binding.txtSenhaCadastraUsuario;
        edtNomeUsuario = binding.edtNomeCadastraUsuario;
        edtEmailUsuario = binding.edtEmailCadastraUsuario;
        edtSenhaUsuario = binding.edtSenhaCadastraUsuario;
        txtRecuperarSenha = binding.txtRecuperaSenha;
        btnCadastrarUsuario = binding.btnCadastrarUsuario;

        btnCadastrarUsuario.setOnClickListener(v ->{
            if (verificaCampos()){
                Snackbar.make(binding.getRoot(), "Todos os campos satisfeitos.", Snackbar.LENGTH_LONG).show();
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(edtEmailUsuario.getText().toString(), edtSenhaUsuario.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                salvaDadosUsuario();
                                vaiParaEntrarActivity();
                            } else {
                                String erro;
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthUserCollisionException exception){
                                    erro = "Email já cadastrado!";
                                } catch (Exception exception){
                                    erro = "Erro ao cadastrar usuário! Tente novamente!";
                                }
                                Snackbar.make(v, erro, Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void vaiParaEntrarActivity() {
        Intent iniciaVaiParaEntrarActivity = new Intent(getApplicationContext(), EntrarActivity.class);
        startActivity(iniciaVaiParaEntrarActivity);
        finish();
    }

    private void salvaDadosUsuario() {
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Usuario usuario = new Usuario(usuarioId, edtNomeUsuario.getText().toString(),CHAVE_CARGO_COLABORADOR);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhareferencia = database.getReference(CHAVE_USUARIO);
        minhareferencia.child(usuarioId).setValue(usuario);
    }

    private boolean verificaCampos() {
        return campoNomeValido() & campoEmailValido() & campoSenhaValida();
    }

    private boolean campoSenhaValida() {
        boolean confirmacao = false;
        if (edtSenhaUsuario.getText().toString().isEmpty()){
            txtSenhaUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else if (verificaSenhaRobusta()) {
            txtSenhaUsuario.setHelperTextEnabled(false);
            confirmacao = true;
        }
        return confirmacao;
    }

    private boolean campoEmailValido() {
        boolean confirmacao;
        String email = edtEmailUsuario.getText().toString();
        String emailPadrao = getString(R.string.stringEmailPadrao);
        if (email.isEmpty()){
            txtEmailUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            confirmacao = false;
        } else if (email.matches(emailPadrao)) {
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
        if (edtNomeUsuario.getText().toString().isEmpty()){
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
        String senha = edtSenhaUsuario.getText().toString();
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