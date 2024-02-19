package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_CADASTRA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_MODIFICA_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_PRODUTO;
import static com.example.toyapedidos.ui.Constantes.CHAVE_USUARIO;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityMainBinding;
import com.example.toyapedidos.modelo.Usuario;
import com.example.toyapedidos.ui.fragment.FragmentoCardapio;
import com.example.toyapedidos.ui.fragment.FragmentoPedidos;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializaComponentes();
        setContentView(binding.getRoot());

        int itemNavegacao = R.id.navPedidos;
        itemNavegacao = recebeDadosIntent(itemNavegacao);
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navegacaoView;
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> drawer.openDrawer(GravityCompat.START));
        navigationView.bringChildToFront(getCurrentFocus());
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, R.string.stringAbreNavegacao, R.string.stringFechaMenuNavegacao);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        mostraFragmentoSelecionado(itemNavegacao);
        navigationView.setCheckedItem(itemNavegacao);
        View cabecalhoView = navigationView.getHeaderView(0);
        TextView txtNavNome = cabecalhoView.findViewById(R.id.txtNavCabecalhoNome);
        TextView txtNavCargo = cabecalhoView.findViewById(R.id.txtNavCabecalhoCargo);
        FirebaseDatabase meusDados = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = meusDados.getReference(CHAVE_USUARIO);
        String usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        minhaReferencia.child(usuarioId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Usuario usuarioAtual = snapshot.getValue(Usuario.class);
                if (usuarioAtual != null) {
                    txtNavNome.setText(usuarioAtual.getNome());
                    txtNavCargo.setText(usuarioAtual.getCargo());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "Erro: "+error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private int recebeDadosIntent(int itemNavegacao) {
        Intent dadosRecebidos = getIntent();
        if (dadosRecebidos.hasExtra(CHAVE_PRODUTO)){
            int chaveProduto = (int) dadosRecebidos.getSerializableExtra(CHAVE_PRODUTO);
            if (chaveProduto == CHAVE_CADASTRA_PRODUTO){
                itemNavegacao = R.id.navCardapio;
            } else if (chaveProduto == CHAVE_MODIFICA_PRODUTO) {

            }
        }
        return itemNavegacao;
    }

    private void mostraFragmentoSelecionado(int itemNavegacao) {
        Fragment fragmentoSelecionado = null;
        if (itemNavegacao == R.id.navPedidos){
            fragmentoSelecionado = new FragmentoPedidos();
            TAG = "navPedidos";
        } else if (itemNavegacao == R.id.navCardapio){
            fragmentoSelecionado = new FragmentoCardapio();
            TAG = "navCardapio";
        }else if (itemNavegacao == R.id.navSair){
            FirebaseAuth.getInstance().signOut();
            vaiParaEntraUsuarioActivity();
        }
        if (fragmentoSelecionado != null){
            reposicionaFragmento(fragmentoSelecionado);
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private void vaiParaEntraUsuarioActivity() {
        Intent iniciaVaiParaEntraUsuarioActivity = new Intent(getApplicationContext(), EntraUsuarioActivity.class);
        startActivity(iniciaVaiParaEntraUsuarioActivity);
        finish();
    }

    private void reposicionaFragmento(Fragment fragmentoSelecionado) {
        FragmentManager gerenciadorDeFragmento = getSupportFragmentManager();
        FragmentTransaction transicaoDeFragmento = gerenciadorDeFragmento.beginTransaction();
        transicaoDeFragmento.replace(R.id.frameLayout, fragmentoSelecionado);
        transicaoDeFragmento.addToBackStack(TAG);
        transicaoDeFragmento.commit();
    }

    private void inicializaComponentes() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result->{
            Log.d("mainActivity","onActivityResult");
            if (result.getResultCode()==1){
                Intent intent=result.getData();
                if (intent!=null){

                }
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        mostraFragmentoSelecionado(item.getItemId());
        return true;
    }
    public boolean estaConectado(){
        ConnectivityManager gerenciadorDeConexao = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo informacaoDeConexao = gerenciadorDeConexao.getActiveNetworkInfo();
        if (informacaoDeConexao == null)
            return false;
        if (informacaoDeConexao.getType() == ConnectivityManager.TYPE_WIFI)
            Snackbar.make(binding.getRoot(), "Conexão wifi", Snackbar.LENGTH_LONG).show();
        if (informacaoDeConexao.getType() == ConnectivityManager.TYPE_MOBILE)
            Snackbar.make(binding.getRoot(), "Conexão mobile", Snackbar.LENGTH_LONG).show();
        return informacaoDeConexao.isConnectedOrConnecting();
    }
}