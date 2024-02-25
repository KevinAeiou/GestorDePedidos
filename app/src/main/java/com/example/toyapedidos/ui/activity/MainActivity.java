package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_USUARIO;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializaComponentes();
        setContentView(binding.getRoot());

        int itemNavegacao = R.id.navPedidos;
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
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        mostraFragmentoSelecionado(item.getItemId());
        return true;
    }
}