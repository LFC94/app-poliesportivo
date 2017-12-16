package com.lfcaplicativos.poliesportivo.Uteis;

import com.lfcaplicativos.poliesportivo.Objetos.Cidade;
import com.lfcaplicativos.poliesportivo.Objetos.Estado;
import com.lfcaplicativos.poliesportivo.Objetos.Ginasios;

import java.util.ArrayList;

/**
 * Created by Lucas on 07/09/2017.
 */

public class Chaves {

    public static final String ULR_FIREBASE = "https://poliesportivo-37275.firebaseio.com/";

    public static final String CHAVE_USUARIO = "USUARIO";
    public static final String CHAVE_NOME = "NOME";
    public static final String CHAVE_ESTADO = "ESTADO";
    public static final String CHAVE_CIDADE = "CIDADE";
    public static final String CHAVE_TELEFONE = "TELEFONE";
    public static final String CHAVE_ID = "ID";
    public static final String CHAVE_FOTO_PERFIL = "FOTO_PERFIL";
    public static final String CHAVE_CONFIGURACAO = "CONFIGURACAO";
    public static final String CHAVE_ULR_ESTADO = "URL_ESTADO";
    public static final String CHAVE_ULR_CIDADE = "URL_CIDADE";
    public static final String CHAVE_ULR_GINASIO = "URL_GINASIOS";


    public static final int CHAVE_RESULT_PHOTO = 1;

    public static final int CHAVE_NAVEGATIN_USUARIO = 3;
    public static final int CHAVE_NAVEGATIN_PRINCIPAL = 0;


    public static ArrayList<String> estadolist_usuario;
    public static ArrayList<String> cidadelist_usuario;
    public static ArrayList<Estado> estados_usuario;
    public static ArrayList<Cidade> cidades_usuario;
    public static ArrayList<Ginasios> ginasio_principal;
}
