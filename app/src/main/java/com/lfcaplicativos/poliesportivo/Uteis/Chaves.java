package com.lfcaplicativos.poliesportivo.Uteis;

import com.lfcaplicativos.poliesportivo.Objetos.Cidade;
import com.lfcaplicativos.poliesportivo.Objetos.Estado;
import com.lfcaplicativos.poliesportivo.Objetos.Ginasios;
import com.lfcaplicativos.poliesportivo.Objetos.Horarios;

import java.util.ArrayList;

/**
 * Created by Lucas on 07/09/2017.
 * Classe de chaves de configuracao
 */

public class Chaves {

    public static final String CHAVE_USUARIO = "USUARIO";
    public static final String CHAVE_NOME = "NOME";
    public static final String CHAVE_ESTADO = "ESTADO";
    public static final String CHAVE_CIDADE = "CIDADE";
    public static final String CHAVE_ARRAY_ESTADO = "ARRAYESTADO";
    public static final String CHAVE_ARRAY_CIDADE = "ARRAYCIDADE";
    public static final String CHAVE_ATU_ESTADO = "ATUESTADO";
    public static final String CHAVE_ATU_CIDADE = "ATUCIDADE";
    public static final String CHAVE_TELEFONE = "TELEFONE";
    public static final String CHAVE_ID = "ID";
    public static final String CHAVE_FOTO_PERFIL = "FOTO_PERFIL";
    public static final String CHAVE_CONFIGURACAO = "CONFIGURACAO";
    public static final String CHAVE_URL_ESTADO = "URL_ESTADO";
    public static final String CHAVE_URL_CIDADE = "URL_CIDADE";
    public static final String CHAVE_URL_GINASIO = "URL_GINASIOS";
    public static final String CHAVE_URL_HORARIOS = "URL_HORARIOS";

    public static final String CHAVE_AUTENTC_PHONE = "AUTENTC_PHONE";
    public static final String CHAVE_AUTENTC_GOOGLE = "AUTENTC_GOOGLE";

    public static final int CHAVE_RESULT_PHOTO = 1;
    public static final int CHAVE_RESULT_LOCATION = 2;
    public static final int CHAVE_RESULT_GOOGLE = 3;
    public static int CHAVE_INDEX_PHONE = 0;
    public static int CHAVE_INDEX_GOOGLE = 0;

    public static String atuServerCidade;
    public static String atuServerEstado;
    public static ArrayList<String> estadolist_usuario;
    public static ArrayList<String> cidadelist_usuario;
    public static ArrayList<Estado> estados_usuario;
    public static ArrayList<Cidade> cidades_usuario;
    public static ArrayList<Ginasios> ginasio_principal;
    public static ArrayList<Horarios> horarios_ginasio;
}
