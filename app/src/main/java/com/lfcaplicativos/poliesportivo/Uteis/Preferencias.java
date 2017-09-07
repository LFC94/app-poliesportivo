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

    public void CadastraUsuarioPreferencias(String Telefone, String Id) {
        editor.putString(Chaves.CHAVE_TELEFONE, Telefone);
        editor.putString(Chaves.CHAVE_ID, Id);
        editor.commit();
    }

    public void setNOME(String Nome) {
        editor.putString(Chaves.CHAVE_NOME, Nome);
    }

    public String getNOME() {
        return sharedPreferences.getString(Chaves.CHAVE_NOME, null);
    }

    public String getTELEFONE() {
        return sharedPreferences.getString(Chaves.CHAVE_TELEFONE, null);
    }

    public String getID() {
        return sharedPreferences.getString(Chaves.CHAVE_ID, null);
    }

    public HashMap<String, String> RetornaUsuarioPreferencias() {
        HashMap<String, String> Retorno = new HashMap<>();

        Retorno.put(Chaves.CHAVE_NOME, sharedPreferences.getString(Chaves.CHAVE_NOME, null));
        Retorno.put(Chaves.CHAVE_TELEFONE, sharedPreferences.getString(Chaves.CHAVE_TELEFONE, null));
        Retorno.put(Chaves.CHAVE_ID, sharedPreferences.getString(Chaves.CHAVE_ID, null));

        return Retorno;
    }

}
