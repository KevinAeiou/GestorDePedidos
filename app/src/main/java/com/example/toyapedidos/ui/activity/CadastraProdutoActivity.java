package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_CADASTRA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_EMPRESAS;
import static com.example.toyapedidos.ui.Constantes.CHAVE_ID_EMPRESA;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Locale;
import java.util.Objects;

public class CadastraProdutoActivity extends AppCompatActivity {
    private FirebaseDatabase meuBancoDados;
    private DatabaseReference minhaReferencia;
    private ActivityCadastraProdutoBinding binding;
    private TextInputEditText inputEditNome, inputEditDescricao;
    private CurrencyEditText inputEditValor;
    private TextInputLayout inputTextNome;
    private TextInputLayout inputTextDescricao;
    private MaterialAutoCompleteTextView autoCompleteCategorias;
    private String nome, descricao, categoria, valor, empresaId;
    private String[] categorias;
    private Produto produtoRecebido, produtoModificado;
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
            empresaId = (String) dadosRecebidos.getSerializableExtra(CHAVE_ID_EMPRESA);
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
        inputEditValor.setText(produtoRecebido.getValor()+"0");
        autoCompleteCategorias.setText(produtoRecebido.getCategoria());
    }

    private void inicializaComponentes() {
        inputEditNome = binding.inputEditTextNomeProduto;
        inputEditDescricao = binding.inputEditTextDescricaoProduto;
        inputEditValor = binding.inputEditTextValorProduto;
        autoCompleteCategorias = binding.inputEditTextCategoriaProduto;
        inputTextNome = binding.inputLayoutTextNomeProduto;
        inputTextDescricao = binding.inputLayoutTextDescricaoProduto;
        meuBancoDados = FirebaseDatabase.getInstance();
        minhaReferencia = meuBancoDados.getReference();
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
                        finish();
                    } else if (codigoRequisicao == CHAVE_MODIFICA_PRODUTO) {
                        modificaProduto();
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void modificaProduto() {
        if (produtoRecebidoEhModificado()) {
            new MaterialAlertDialogBuilder(CadastraProdutoActivity.this)
                    .setTitle("Modificar produto?")
                    .setNegativeButton("Não", (dialog, which) ->{
                        finish();
                    })
                    .setPositiveButton("Sim", (dialog, which) -> {
                        minhaReferencia = meuBancoDados.getReference(CHAVE_EMPRESAS).child(empresaId).child(CHAVE_LISTA_PRODUTO);
                        minhaReferencia.child(produtoModificado.getId()).setValue(produtoModificado);
                        finish();
                    })
                    .show();
            Log.d("cadastraProduto", "Produto modificado");
        } else {
            finish();
        }
    }

    private boolean produtoRecebidoEhModificado() {
        produtoModificado = produtoRecebido;
        boolean confirmacao = false;
        double valorRecebidoFormatado = (produtoRecebido.getValor());
        double valorDouble;
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        try {
            valorDouble = nf.parse(valor).doubleValue();
            if (!produtoRecebido.getNome().equals(nome)){
                Log.d("cadastraProduto", "Nome mofificado");
                produtoModificado.setNome(nome);
                confirmacao = true;
            } else if (!produtoRecebido.getDescricao().equals(descricao)) {
                Log.d("cadastraProduto", "Descrição mofificado");
                produtoModificado.setDescricao(descricao);
                confirmacao = true;
            } else if (!produtoRecebido.getCategoria().equals(categoria)) {
                Log.d("cadastraProduto", "Categoria mofificado");
                produtoModificado.setCategoria(categoria);
                confirmacao = true;
            } else if (valorRecebidoFormatado != valorDouble) {
                Log.d("cadastraProduto", "Valor mofificado");
                Log.d("cadastraProduto", "Valor recebido: "+valorRecebidoFormatado);
                Log.d("cadastraProduto", "Valor mofificado: "+valorDouble);
                produtoModificado.setValor(valorDouble);
                confirmacao = true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return confirmacao;
    }

    private void cadastraNovoProduto(double valorDouble) {
        minhaReferencia = meuBancoDados.getReference(CHAVE_EMPRESAS).child(empresaId).child(CHAVE_LISTA_PRODUTO);
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
        nome = Objects.requireNonNull(inputEditNome.getText()).toString();
        descricao = Objects.requireNonNull(inputEditDescricao.getText()).toString();
        categoria = autoCompleteCategorias.getText().toString();
        valor = Objects.requireNonNull(inputEditValor.getText()).toString();
        Log.d("cadastraProduto", "Valor recuperado da view: "+valor);
        return verificaInputEditProduto(nome, inputTextNome)&
                verificaInputEditProduto(descricao, inputTextDescricao);
    }

    private boolean verificaInputEditProduto(String edtTexto, TextInputLayout inputLayout) {
        if (edtTexto.isEmpty()){
            inputLayout.setHelperTextEnabled(true);
            inputLayout.setHelperText(menssagemErro[0]);
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