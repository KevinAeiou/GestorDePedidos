package com.example.toyapedidos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.toyapedidos.databinding.ActivityCadastraProdutoBinding;
import com.example.toyapedidos.databinding.ActivityCadastraUsuarioBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

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
            }
        });
    }

    private boolean verificaCampos() {
        boolean confirmacao = true;
            if (edtNomeUsuario.getText().toString().isEmpty()){
                txtNomeUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            } else {
                txtNomeUsuario.setHelperTextEnabled(false);
            }
            if (edtEmailUsuario.getText().toString().isEmpty()){
                txtEmailUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            } else {
                txtEmailUsuario.setHelperTextEnabled(false);
            }
            if (edtSenhaUsuario.getText().toString().isEmpty()){
                txtSenhaUsuario.setHelperText(getString(R.string.stringCampoNecessario));
            } else if (verificaSenhaRobusta()) {
                txtSenhaUsuario.setHelperTextEnabled(false);
            }
        return confirmacao;
    }

    private boolean verificaSenhaRobusta() {
        boolean confirmacao = false;
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
                            txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaCurta));
                        }
                    } else {
                        txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaNumerica));
                    }
                } else {
                    txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaMaiuscula));
                }
            }else {
                txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaMinuscula));
            }
        }else {
            txtSenhaUsuario.setHelperText(getString(R.string.stringSenhaEspecial));
        }

        return confirmacao;
    }
}