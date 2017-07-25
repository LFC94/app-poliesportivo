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
    private final String CHAVE_NOME = "NOME";
    private final String CHAVE_TELEFONE = "TELEFONE";
    private final String CHAVE_IDVERIFICACAO = "IDVERIFICACAO";
    private final String CHAVE_CODEVERIFICACAO = "CODEVERIFICACAO";

    public Preferencias(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Arquivo, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void CadastraUsuarioPreferencias(String Nome, String Telefone, String verificationId, String codeverication) {
        editor.putString(CHAVE_NOME, Nome);
        editor.putString(CHAVE_TELEFONE, Telefone);
        editor.putString(CHAVE_IDVERIFICACAO, verificationId);
        editor.putString(CHAVE_CODEVERIFICACAO, codeverication);
        editor.commit();
    }


    public String getNOME() {

        return sharedPreferences.getString(CHAVE_NOME, null);

    }

    public String getTELEFONE() {

        return sharedPreferences.getString(CHAVE_TELEFONE, null);
    }

    public String getIDVERIFICACAO() {

        return sharedPreferences.getString(CHAVE_IDVERIFICACAO, null);
    }

    public String getCODEVERIFICACAO() {

        return sharedPreferences.getString(CHAVE_CODEVERIFICACAO, null);
    }

    public HashMap<String, String> RetornaUsuarioPreferencias() {
        HashMap<String, String> Retorno = new HashMap<>();

        Retorno.put(CHAVE_NOME, sharedPreferences.getString(CHAVE_NOME, null));
        Retorno.put(CHAVE_TELEFONE, sharedPreferences.getString(CHAVE_TELEFONE, null));
        Retorno.put(CHAVE_IDVERIFICACAO, sharedPreferences.getString(CHAVE_IDVERIFICACAO, null));

        return Retorno;
    }

}
