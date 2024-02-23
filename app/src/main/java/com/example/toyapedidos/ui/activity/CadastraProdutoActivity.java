package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_CADASTRA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_MODIFICA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_REQUISICAO;

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
    private String[] categorias;
    private Produto produtoRecebido;
    private int codigoRequisicao;
    private final String[] menssagemErro={"Campo requerido!","Inválido!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastraProdutoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        inicializaComponentes();
        ArrayAdapter<String> categoriaAdapter = new ArrayAdapter<>(this,
                R.layout.item_dropdown, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recebeDados();
        autoCompleteCategorias.setAdapter(categoriaAdapter);
    }

    private void recebeDados() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_PRODUTO)){
            codigoRequisicao = (int) dadosRecebidos.getSerializableExtra(CHAVE_REQUISICAO);
            if (codigoRequisicao == CHAVE_CADASTRA_PRODUTO) {
                Log.d("cadastraProduto", "Novo produto.");
                autoCompleteCategorias.setText(categorias[0]);
                inputEditValor.setText("000");
            } else if (codigoRequisicao ==CHAVE_MODIFICA_PRODUTO) {
                produtoRecebido = (Produto) dadosRecebidos.getSerializableExtra(CHAVE_PRODUTO);
                Log.d("cadastraProduto", "Altera produto.");
                preencheCampos(produtoRecebido);
            }
        }
    }
    private void preencheCampos(Produto produtoRecebido) {
        inputEditNome.setText(produtoRecebido.getNome());
        inputEditDescricao.setText(produtoRecebido.getDescricao());
        Log.d("cadastraProduto", "Valor: "+produtoRecebido.getValor()+"0");
        inputEditValor.setText(String.valueOf(produtoRecebido.getValor())+0);
        autoCompleteCategorias.setText(produtoRecebido.getCategoria());
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
        categorias = getResources().getStringArray(R.array.categorias);
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
                    if (codigoRequisicao == CHAVE_CADASTRA_PRODUTO){
                    valorDouble = nf.parse(valor).doubleValue();
                        cadastraNovoProduto(valorDouble);
                    } else if (codigoRequisicao == CHAVE_MODIFICA_PRODUTO) {
                        modificaProduto();
                    }
                    finish();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void modificaProduto() {
        if (produtoRecebidoEhModificado()){
            Log.d("cadastraProduto", "Produto modificado");
        } else {
            Log.d("cadastraProduto", "Produto não modificado");
        }
    }

    private boolean produtoRecebidoEhModificado() {
        boolean confirmacao = false;
        String valorRecebidoFormatado = (produtoRecebido.getValor()+"0");
        String valorFormatado = valor.replaceAll(",",".");
        if (!produtoRecebido.getNome().equals(nome)){
            Log.d("cadastraProduto", "Nome mofificado");
            confirmacao = true;
        } else if (!produtoRecebido.getDescricao().equals(descricao)) {
            Log.d("cadastraProduto", "Descrição mofificado");
            confirmacao = true;
        } else if (!produtoRecebido.getCategoria().equals(categoria)) {
            Log.d("cadastraProduto", "Categoria mofificado");
            confirmacao = true;
        } else if (!valorRecebidoFormatado.equals(valorFormatado)) {
            Log.d("cadastraProduto", "Valor mofificado");
            Log.d("cadastraProduto", "Valor recebido: "+valorRecebidoFormatado);
            Log.d("cadastraProduto", "Valor mofificado: "+valorFormatado);
            confirmacao = true;
        }
        return confirmacao;
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