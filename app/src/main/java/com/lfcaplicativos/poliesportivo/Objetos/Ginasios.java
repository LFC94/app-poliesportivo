package com.lfcaplicativos.poliesportivo.Objetos;

import android.graphics.Bitmap;

/**
 * Created by Lucas on 09/12/2017.
 */

public class Ginasios {
    private int codigo;
    private double latitude, longitude;
    private String nome, fantasia, endereco, numero, bairro, cidade, estado, modalidade, nomelogo, piso;
    private Bitmap logo;
    private Boolean estacionamento, coberto;


    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getNomelogo() {
        return nomelogo;
    }

    public void setNomelogo(String nomelogo) {
        this.nomelogo = nomelogo;
    }

    public String getPiso() {
        return piso;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    public Boolean getEstacionamento() {
        return estacionamento;
    }

    public void setEstacionamento(Boolean estacionamento) {
        this.estacionamento = estacionamento;
    }

    public Boolean getCoberto() {
        return coberto;
    }

    public void setCoberto(Boolean coberto) {
        this.coberto = coberto;
    }
}
