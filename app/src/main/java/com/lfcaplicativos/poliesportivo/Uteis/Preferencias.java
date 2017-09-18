package com.lfcaplicativos.poliesportivo.Uteis;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Lucas on 05/06/2017.
 * Classe para manipulacao de Preferences
 */

public class Preferencias {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final String Arquivo = "PoliesportivoPreferences";


    public Preferencias(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Arquivo, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setPreferencias(String Chave, String Valor) {
        editor.putString(Chave, Valor);
        editor.commit();
    }

    public void setPreferencias(String Chave, int Valor) {
        editor.putInt(Chave, Valor);
        editor.commit();
    }

    public void setPreferencias(String Chave, float Valor) {
        editor.putFloat(Chave, Valor);
        editor.commit();
    }

    public void setPreferencias(String Chave, boolean Valor) {
        editor.putBoolean(Chave, Valor);
        editor.commit();
    }


    public String getSPreferencias(String Chave) {
        return sharedPreferences.getString(Chave, null);
    }

    public int getIPreferencias(String Chave) {
        return sharedPreferences.getInt(Chave, 0);
    }

    public float getFPreferencias(String Chave) {
        return sharedPreferences.getFloat(Chave, 0);
    }

    public boolean getBPreferencias(String Chave) {
        return sharedPreferences.getBoolean(Chave, false);
    }


    /************** Usuario *************/
    public void CadastraUsuarioPreferencias(String Nome, String Telefone, String Id, String Cidade, String Estado) {
        editor.putString(Chaves.CHAVE_TELEFONE, Telefone);
        editor.putString(Chaves.CHAVE_ID, Id);
        editor.putString(Chaves.CHAVE_NOME, Nome);
        editor.putString(Chaves.CHAVE_CIDADE, Cidade);
        editor.putString(Chaves.CHAVE_ESTADO, Estado);
        editor.commit();
    }

    public void setNOME(String Nome) {
        editor.putString(Chaves.CHAVE_NOME, Nome);
        editor.commit();
    }

    public void setESTADO(String Estado) {
        editor.putString(Chaves.CHAVE_ESTADO, Estado);
        editor.commit();
    }

    public void setCIDADE(String Cidade) {
        editor.putString(Chaves.CHAVE_CIDADE, Cidade);
        editor.commit();
    }

    public String getNOME() {
        return sharedPreferences.getString(Chaves.CHAVE_NOME, null);
    }

    public String getESTADO() {
        return sharedPreferences.getString(Chaves.CHAVE_ESTADO, null);
    }

    public String getCIDADE() {
        return sharedPreferences.getString(Chaves.CHAVE_CIDADE, null);
    }

    public String getTELEFONE() {
        return sharedPreferences.getString(Chaves.CHAVE_TELEFONE, null);
    }

    public String getID() {
        return sharedPreferences.getString(Chaves.CHAVE_ID, null);
    }

    public HashMap<String, String> RetornaUsuarioPreferencias(boolean notnull) {
        HashMap<String, String> Retorno = new HashMap<>();

        if (notnull) {
            if (getNOME() != null)
                Retorno.put(Chaves.CHAVE_NOME, sharedPreferences.getString(Chaves.CHAVE_NOME, null));
            if (getTELEFONE() != null)
                Retorno.put(Chaves.CHAVE_TELEFONE, sharedPreferences.getString(Chaves.CHAVE_TELEFONE, null));
            if (getID() != null)
                Retorno.put(Chaves.CHAVE_ID, sharedPreferences.getString(Chaves.CHAVE_ID, null));
            if (getESTADO() != null)
                Retorno.put(Chaves.CHAVE_ESTADO, sharedPreferences.getString(Chaves.CHAVE_ESTADO, null));
            if (getCIDADE() != null)
                Retorno.put(Chaves.CHAVE_CIDADE, sharedPreferences.getString(Chaves.CHAVE_CIDADE, null));
        } else {
            Retorno.put(Chaves.CHAVE_NOME, sharedPreferences.getString(Chaves.CHAVE_NOME, null));
            Retorno.put(Chaves.CHAVE_TELEFONE, sharedPreferences.getString(Chaves.CHAVE_TELEFONE, null));
            Retorno.put(Chaves.CHAVE_ID, sharedPreferences.getString(Chaves.CHAVE_ID, null));
            Retorno.put(Chaves.CHAVE_ESTADO, sharedPreferences.getString(Chaves.CHAVE_ESTADO, null));
            Retorno.put(Chaves.CHAVE_CIDADE, sharedPreferences.getString(Chaves.CHAVE_CIDADE, null));
        }

        return Retorno;
    }

    public void setFOTO_PERFIL(String Foto_Perfil) {
        editor.putString(Chaves.CHAVE_FOTO_PERFIL, Foto_Perfil);
        editor.commit();
    }

    public String getFOTO_PERFIL() {
        return sharedPreferences.getString(Chaves.CHAVE_FOTO_PERFIL, null);
    }


    /************** CONFIGURACAO *************/
    public void ConfiguracaoPreferencias(String UrlEstado, String UrlEstadoPar, String UrlCidade, String UrlCidadePar) {
        editor.putString(Chaves.CHAVE_ULR_CIDADE, UrlCidade);
        editor.putString(Chaves.CHAVE_ULR_CIDADE_PARAMETERS, UrlCidadePar);
        editor.putString(Chaves.CHAVE_ULR_ESTADO, UrlEstado);
        editor.putString(Chaves.CHAVE_ULR_ESTADO_PARAMETERS, UrlEstadoPar);
        editor.commit();
    }


}
