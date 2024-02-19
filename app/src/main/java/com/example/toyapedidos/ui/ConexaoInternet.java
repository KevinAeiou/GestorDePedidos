package com.example.toyapedidos.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import java.util.ArrayList;

public class ConexaoInternet extends BroadcastReceiver {
    public enum TipoConexao {TIPO_NAO_CONECTADO,TIPO_WIFI, TIPO_MOBILE};

    private  TipoConexao tipoConexaoATUAL = TipoConexao.TIPO_NAO_CONECTADO; //cache
    private boolean inicializou = false;

    private TipoConexao getStatusConexao(Context context) {
        synchronized (tipoConexaoATUAL){
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            tipoConexaoATUAL = TipoConexao.TIPO_NAO_CONECTADO;

            NetworkInfo activeNetwork = cm==null ? null : cm.getActiveNetworkInfo();
            if (null != activeNetwork) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    tipoConexaoATUAL = TipoConexao.TIPO_WIFI;
                }
                if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                    tipoConexaoATUAL = TipoConexao.TIPO_MOBILE;
                }
            }

            return tipoConexaoATUAL;
        }
    }

    public TipoConexao getTipoConexaoAtual(Context context){
        if(!inicializou){
            inicializou = true;
            return getStatusConexao(context);
        }
        return tipoConexaoATUAL;
    }

    public interface IOnMudarEstadoConexao{
        void onMudar(TipoConexao tipoConexao);
    }
    private ArrayList<IOnMudarEstadoConexao> onMudarEstadoConexoesListeners = new ArrayList<>();

    public void addOnMudarEstadoConexao(IOnMudarEstadoConexao t){
        onMudarEstadoConexoesListeners.add(t);
    }
    public void removeOnMudarEstadoConexao(IOnMudarEstadoConexao t){
        onMudarEstadoConexoesListeners.remove(t);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TipoConexao tipo = getStatusConexao(context);

        for(IOnMudarEstadoConexao o : onMudarEstadoConexoesListeners){
            o.onMudar(tipo);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager!=null){
                for (Network net : connectivityManager.getAllNetworks()) {
                    NetworkInfo networkInfo = connectivityManager.getNetworkInfo(net);
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        connectivityManager.bindProcessToNetwork(net);
                        break;
                    }
                }
            }
        }
    }
}
