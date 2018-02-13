package com.lfcaplicativos.poliesportivo.Objetos;

/**
 * Created by Lucas on 13/02/2018.
 */

public class Horarios {

    private int codigo, stratus;
    private String horaInicial, horaFinal, mensagem, textoStatus;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getStratus() {
        return stratus;
    }

    public void setStratus(int stratus) {
        this.stratus = stratus;
    }

    public String getHoraInicial() {
        return horaInicial;
    }

    public void setHoraInicial(String horaInicial) {
        this.horaInicial = horaInicial;
    }

    public String getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(String horaFinal) {
        this.horaFinal = horaFinal;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getTextoStatus() {
        return textoStatus;
    }

    public void setTextoStatus(String textoStatus) {
        this.textoStatus = textoStatus;
    }
}
