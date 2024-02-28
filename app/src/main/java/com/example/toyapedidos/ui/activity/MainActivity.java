package com.example.toyapedidos.ui.activity;

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
    private static NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inicializaComponentes();
        setContentView(binding.getRoot());

        int itemNavegacao = R.id.navPedidos;
        drawer = binding.drawerLayoutMain;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
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
            vaiParaTelaInicialActivity();
        }
        if (fragmentoSelecionado != null){
            reposicionaFragmento(fragmentoSelecionado);
        }
        drawer.closeDrawer(GravityCompat.START);
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
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        mostraFragmentoSelecionado(item.getItemId());
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