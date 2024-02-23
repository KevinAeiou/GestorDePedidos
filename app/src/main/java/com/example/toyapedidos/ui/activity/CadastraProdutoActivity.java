package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_PRODUTO;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityCadastraProdutoBinding;
import com.example.toyapedidos.editText.CurrencyEditText;
import com.example.toyapedidos.modelo.Produto;
import com.example.toyapedidos.ui.Utilitario;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
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
    private TextInputLayout inputTextNome, inputTextDescricao,inputTextValor, inputTextCategoria;
    private MaterialAutoCompleteTextView autoCompleteCategorias;
    private String nome, descricao, categoria,valor;
    private final String[] menssagemErro={"Campo requerido!","Inválido!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastraProdutoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        inicializaComponentes();
        recebeDados();
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this,
                R.layout.item_dropdown, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteCategorias.setText(categorias[0]);
        autoCompleteCategorias.setAdapter(categoriaAdapter);
        inputEditValor.setText("000");
    }

    private void recebeDados() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_PRODUTO)){
            Produto produtoRecebido = (Produto) dadosRecebidos.getSerializableExtra(CHAVE_PRODUTO);
            if (!produtoRecebido.getId().isEmpty()){
                preencheCampos(produtoRecebido);
            }
        }
    }
    private void preencheCampos(Produto produtoRecebido) {
        inputEditNome.setText(produtoRecebido.getNome());
        inputEditDescricao.setText(produtoRecebido.getDescricao());
        inputEditValor.setText(String.valueOf(produtoRecebido.getValor()));
    }

    private void inicializaComponentes() {
        inputEditNome = binding.inputEditTextNomeProduto;
        inputEditDescricao = binding.inputEditTextDescricaoProduto;
        inputEditValor = binding.inputEditTextValorProduto;
        autoCompleteCategorias = binding.inputEditTextCategoriaProduto;
        inputTextNome = binding.inputLayoutTextNomeProduto;
        inputTextDescricao = binding.inputLayoutTextDescricaoProduto;
        inputTextValor = binding.inputLayoutTextValorProduto;
        inputTextCategoria = binding.inputLayoutTextCategoriaProduto;
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
                    cadastraNovoProduto(valorDouble);
                    finish();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private void cadastraNovoProduto(double valorDouble) {
        minhaReferencia = database.getReference(CHAVE_LISTA_PRODUTO);
        String novoId = Utilitario.geraIdAleatorio();
        Produto produto = new Produto(
                novoId,
                nome,
                descricao,
                categoria,
                valorDouble
        );
        minhaReferencia.child(novoId).setValue(produto);
    }
    private boolean verificaCamposNovoProduto() {
        nome = inputEditNome.getText().toString();
        descricao = inputEditDescricao.getText().toString();
        categoria = autoCompleteCategorias.getText().toString();
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