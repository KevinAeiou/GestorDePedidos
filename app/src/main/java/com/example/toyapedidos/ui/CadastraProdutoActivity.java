package com.example.toyapedidos.ui;

import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityCadastraProdutoBinding;
import com.example.toyapedidos.editText.CurrencyEditText;
import com.example.toyapedidos.modelo.Produto;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Locale;

public class CadastraProdutoActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference minhaReferencia;
    private ActivityCadastraProdutoBinding binding;
    private TextInputEditText inputEditNome, inputEditDescricao;
    private CurrencyEditText inputEditValor;
    private TextInputLayout inputTextNome, inputTextDescricao,inputTextValor;
    private String nome, descricao, valor;
    private final String[] menssagemErro={"Campo requerido!","Inválido!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastraProdutoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        inicializaComponentes();
        inputEditValor.setText("000");
    }

    private void inicializaComponentes() {
        inputEditNome = binding.inputEditTextNomeProduto;
        inputEditDescricao = binding.inputEditTextDescricaoProduto;
        inputEditValor = binding.inputEditTextValorProduto;
        inputTextNome = binding.inputLayoutTextNomeProduto;
        inputTextDescricao = binding.inputLayoutTextDescricaoProduto;
        inputTextValor = binding.inputLayoutTextValorProduto;
        database = FirebaseDatabase.getInstance();
        minhaReferencia = database.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_salva, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemMenuSalva){
            if (verificaCamposNovoProduto()){
                double valorDouble;
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
                try {
                    valorDouble = nf.parse(valor).doubleValue();
                    Produto produto = new Produto(
                            null,
                            nome,
                            descricao,
                            valorDouble
                    );
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verificaCamposNovoProduto() {
        nome = inputEditNome.getText().toString();
        descricao = inputEditDescricao.getText().toString();
        valor = inputEditValor.getText().toString();
        return verificaInputEditProduto(nome, inputTextNome, 0)&
                verificaInputEditProduto(descricao, inputTextDescricao, 0);
    }

    private boolean verificaInputEditProduto(String edtTexto, TextInputLayout inputLayout, int posicaoErro) {
        if (edtTexto.isEmpty()){
            inputLayout.setHelperTextEnabled(true);
            inputLayout.setHelperText(menssagemErro[posicaoErro]);
            inputLayout.setHelperTextColor(AppCompatResources.getColorStateList(getApplicationContext(),R.color.bordo));
            Log.d("cadastraProduto", "Negativo!");
            return false;
        }else {
            inputLayout.setHelperTextEnabled(false);
            Log.d("cadastraProduto", "Positivo!");
            return true;
        }
    }
}