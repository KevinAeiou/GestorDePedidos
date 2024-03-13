package com.example.toyapedidos.ui.activity;

import static com.example.toyapedidos.ui.Constantes.CHAVE_CARGO_ADMINISTRADOR;
import static com.example.toyapedidos.ui.Constantes.CHAVE_EMPRESAS;
import static com.example.toyapedidos.ui.Constantes.CHAVE_ID_EMPRESA;
import static com.example.toyapedidos.ui.Constantes.CHAVE_USUARIO;
import static com.example.toyapedidos.ui.Constantes.ID_CANAL;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.toyapedidos.R;
import com.example.toyapedidos.databinding.ActivityMainBinding;
import com.example.toyapedidos.modelo.Empresa;
import com.example.toyapedidos.modelo.Produto;
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

import java.io.Serializable;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private String TAG;
    private Empresa empresaAtual;
    private NavigationView navigationView;
    private int itemNavegacao;
    private static NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializaComponentes();
        setContentView(binding.getRoot());

        Menu menu = navigationView.getMenu();
        // Esconde item menu Novo usuario
        menu.findItem(R.id.navNovoUsuario).setVisible(false);
        menu.findItem(R.id.navCardapio).setVisible(false);
        menu.findItem(R.id.navPedidos).setVisible(false);

        configuraToolbar();
        navigationView.bringChildToFront(getCurrentFocus());
        configuraToogle();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(itemNavegacao);

        configuraCabecalhoNagivationDrawer(menu, navigationView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void configuraToogle() {
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawer, R.string.stringAbreNavegacao, R.string.stringFechaMenuNavegacao);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
    }

    private void configuraToolbar() {
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> drawer.openDrawer(GravityCompat.START));
    }

    private void configuraCabecalhoNagivationDrawer(Menu menu, NavigationView navigationView) {
        View cabecalhoView = navigationView.getHeaderView(0);
        TextView txtNavNome = cabecalhoView.findViewById(R.id.txtNavCabecalhoNome);
        TextView txtNavCargo = cabecalhoView.findViewById(R.id.txtNavCabecalhoCargo);
        TextView txtNavEmpresa = cabecalhoView.findViewById(R.id.txtNavCabecalhoEmpresa);
        FirebaseDatabase meusDados = FirebaseDatabase.getInstance();
        DatabaseReference minhaReferencia = meusDados.getReference(CHAVE_EMPRESAS);
        minhaReferencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dn:snapshot.getChildren()){
                    Empresa empresa = dn.getValue(Empresa.class);
                    if (empresa != null){
                        Log.d("mainActivity", "ID empresa: "+empresa.getId());
                        Log.d("mainActivity", "Nome empresa: "+empresa.getNome());
                        minhaReferencia.child(empresa.getId()).child(CHAVE_USUARIO).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dn:snapshot.getChildren()){
                                    Usuario usuario = dn.getValue(Usuario.class);
                                    if (usuario != null && usuario.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                                        empresaAtual = empresa;
                                        txtNavNome.setText(usuario.getNome());
                                        txtNavCargo.setText(usuario.getCargo());
                                        txtNavEmpresa.setText(empresa.getNome());
                                        menu.findItem(R.id.navPedidos).setVisible(true);
                                        menu.findItem(R.id.navCardapio).setVisible(true);
                                        if (usuario.getCargo().equals(CHAVE_CARGO_ADMINISTRADOR)){
                                            menu.findItem(R.id.navNovoUsuario).setVisible(true);
                                        }
                                        mostraFragmentoSelecionado();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void mostraFragmentoSelecionado() {
        Fragment fragmentoSelecionado = null;
        Bundle argumento = new Bundle();
        argumento.putString(CHAVE_ID_EMPRESA, empresaAtual.getId());
        if (itemNavegacao == R.id.navPedidos){
            fragmentoSelecionado = new FragmentoPedidos();
            fragmentoSelecionado.setArguments(argumento);
            TAG = "navPedidos";
        } else if (itemNavegacao == R.id.navCardapio){
            fragmentoSelecionado = new FragmentoCardapio();
            fragmentoSelecionado.setArguments(argumento);
            TAG = "navCardapio";
        } else if (itemNavegacao == R.id.navNovoUsuario){
            vaiParaCadastraNovoUsuario();
        } else if (itemNavegacao == R.id.navSair){
            FirebaseAuth.getInstance().signOut();
            vaiParaTelaInicialActivity();
        }
        if (fragmentoSelecionado != null){
            reposicionaFragmento(fragmentoSelecionado);
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    private void vaiParaCadastraNovoUsuario() {
        Intent iniciaVaiParaCadastraUsuarioActivity = new Intent(getApplicationContext(), CadastraUsuarioActivity.class);
        iniciaVaiParaCadastraUsuarioActivity.putExtra(CHAVE_ID_EMPRESA, (Serializable) empresaAtual);
        startActivity(iniciaVaiParaCadastraUsuarioActivity);
    }

    private void vaiParaTelaInicialActivity() {
        Intent iniciaVaiParaTelaInicialActivity = new Intent(getApplicationContext(), TelaInicialActivity.class);
        startActivity(iniciaVaiParaTelaInicialActivity);
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
        drawer = binding.drawerLayoutMain;
        navigationView = binding.navegacaoView;
        itemNavegacao = R.id.navPedidos;
        empresaAtual = null;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        itemNavegacao = item.getItemId();
        mostraFragmentoSelecionado();
        return true;
    }
    public void criaCanalDeNotificacao(NotificationCompat.Builder construtor) {
        Intent iniciaAplicacao = new Intent(this, MainActivity.class);
        iniciaAplicacao.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, iniciaAplicacao, PendingIntent.FLAG_IMMUTABLE);

        construtor.setSmallIcon(R.drawable.ic_menu)
                .setContentTitle("Titulo teste")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canalNotificacao = notificationManager.getNotificationChannel(ID_CANAL);
            if (canalNotificacao == null) {
                CharSequence name = "NomeDoCanal";
                String description = "DescriçãoDoCanal";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                canalNotificacao = new NotificationChannel(ID_CANAL, name, importance);
                canalNotificacao.enableVibration(true);
                canalNotificacao.setDescription(description);
                canalNotificacao.enableLights(true);
                canalNotificacao.setLightColor(Color.RED);
                notificationManager.createNotificationChannel(canalNotificacao);
            }
        }
        notificationManager.notify(0, construtor.build());
    }
}