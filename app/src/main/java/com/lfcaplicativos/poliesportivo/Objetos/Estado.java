package com.lfcaplicativos.poliesportivo.Objetos;

/**
 * Created by Lucas on 07/09/2017.
 */

@SuppressWarnings("ALL")
public class Estado {
    private int idUF;
    private int idPais;
    private String Sigla;
    private String Nome;

    public int getIdUF() {
        return idUF;
    }

    public void setIdUF(int idUF) {
        this.idUF = idUF;
    }

    public int getIdPais() {
        return idPais;
    }

    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    public String getSigla() {
        return Sigla;
    }

    public void setSigla(String sigla) {
        Sigla = sigla;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }
}
