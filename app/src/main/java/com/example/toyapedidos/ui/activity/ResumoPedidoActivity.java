package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_LISTA_PEDIDO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_NOVO_PEDIDO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_TITULO_RESUMO_PEDIDO;

import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityResumoPedidoBinding;
import com.example.toyapedidos.modelo.Pedido;
import com.example.toyapedidos.modelo.Produto;
import com.example.toyapedidos.modelo.ProdutoPedido;
import com.example.toyapedidos.ui.Utilitario;
import com.example.toyapedidos.ui.recyclerview.adapter.NovoPedidoAdapter;
import com.example.toyapedidos.ui.recyclerview.adapter.listener.OnItemClickListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ResumoPedidoActivity extends AppCompatActivity {
    private ActivityResumoPedidoBinding binding;
    private ArrayList<ProdutoPedido> resumoPedido;
    private MaterialTextView txtSomaTotal;
    private NovoPedidoAdapter meuAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResumoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(CHAVE_TITULO_RESUMO_PEDIDO);
        recebeDadosIntent();
        txtSomaTotal = binding.txtTotalResumoPedido;
        atualizaTxtSomaTotal();
        configuraRecyclerView();
        configuraBotaoConfirmaPedido();
    }

    private void configuraBotaoConfirmaPedido() {
        MaterialButton btnConfirmaPedido = binding.btnConfirmaResumoPedido;
        TextInputLayout txtNumeroMesa = binding.txtLayoutNumeroMesaResumoPedido;
        TextInputEditText edtNumeroMesa = binding.edtNumeroMesaResumoPedido;
        MaterialTextView txtTotalPedido = binding.txtTotalResumoPedido;
        String numeroMesa = Objects.requireNonNull(edtNumeroMesa.getText()).toString();
        btnConfirmaPedido.setOnClickListener(v ->{
            if (numeroMesa.isEmpty()){
                edtNumeroMesa.setText("0");
                txtNumeroMesa.setHelperText("Campo requerido!");
            } else {
                txtNumeroMesa.setHelperTextEnabled(false);
                String id = Utilitario.geraIdAleatorio();
                String total = txtTotalPedido.getText().toString();

                Date dataHora = new Date();
                TextInputEditText edtDescricao =  binding.edtObservacaoResumoPedido;
                String observacao = Objects.requireNonNull(edtDescricao.getText()).toString();
                if (observacao.isEmpty()){
                    observacao = "";
                }
                double valorDouble;
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
                try {
                    valorDouble = nf.parse(total).doubleValue();
                    Pedido novoPedido = new Pedido(id, resumoPedido, dataHora, observacao, valorDouble, Integer.parseInt(edtNumeroMesa.getText().toString()), 0);
                    cadastraNovoPedido(novoPedido);
                    vaiParaMainActivity();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void vaiParaMainActivity() {
        Intent iniciaVaiParaMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(iniciaVaiParaMainActivity);
        finish();
    }

    private void cadastraNovoPedido(Pedido novoPedido) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = database.getReference(CHAVE_LISTA_PEDIDO);
        minhaReferencia.child(novoPedido.getId()).setValue(novoPedido);
    }

    private void configuraRecyclerView() {
        RecyclerView meuRecycler = binding.recyclerViewResumoPedido;
        meuRecycler.setHasFixedSize(true);
        meuRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        configuraAdapter(meuRecycler);
    }

    private void configuraAdapter(RecyclerView meuRecycler) {
        meuAdapter = new NovoPedidoAdapter(resumoPedido, getApplicationContext());
        meuRecycler.setAdapter(meuAdapter);
        meuAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ProdutoPedido produtoPedido, int posicao, int botaoId) {
                alteraQuantidadeProdutoPedido(produtoPedido,posicao, botaoId);
            }

            @Override
            public void onItemClick(Produto produto, int posicao) {

            }
        });
    }

    private void alteraQuantidadeProdutoPedido(ProdutoPedido produtoPedido, int posicao, int botaoId) {
        if (botaoId == R.id.itemBtnIncrementaQuantidadeNovoPedido){
            Log.d("adicionaNovoPedido", "Clicou em adiciona: "+produtoPedido.getNome());
            int novaQuantidade = produtoPedido.getQuantidade() + 1;
            produtoPedido.setQuantidade(novaQuantidade);
            Log.d("adicionaNovoPedido", "Nova quantidade: "+produtoPedido.getQuantidade());
            meuAdapter.altera(produtoPedido, posicao);
            atualizaTxtSomaTotal();
        } else if (botaoId == R.id.itemBtnDecrementaQuantidadeNovoPedido) {
            Log.d("adicionaNovoPedido", "Clicou em subtrai: "+produtoPedido.getNome());
            if (produtoPedido.getQuantidade() > 0){
                int novaQuantidade = produtoPedido.getQuantidade() - 1;
                produtoPedido.setQuantidade(novaQuantidade);
                Log.d("adicionaNovoPedido", "Nova quantidade: "+produtoPedido.getQuantidade());
                meuAdapter.altera(produtoPedido, posicao);
                atualizaTxtSomaTotal();
            }
        }
    }

    private void atualizaTxtSomaTotal() {
        double somaTotal = 0;
        for (ProdutoPedido produtoPedido: resumoPedido){
            double valorItem = produtoPedido.getValor() * produtoPedido.getQuantidade();
            somaTotal += valorItem;
        }
        txtSomaTotal.setText(String.valueOf(somaTotal));
    }

    private void recebeDadosIntent() {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_NOVO_PEDIDO)){
            resumoPedido = (ArrayList<ProdutoPedido>) dadosRecebidos.getSerializableExtra(CHAVE_NOVO_PEDIDO);
        }
    }
}