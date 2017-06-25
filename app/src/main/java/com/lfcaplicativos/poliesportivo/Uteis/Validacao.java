package com.lfcaplicativos.poliesportivo.Uteis;

import android.text.TextUtils;
import android.widget.EditText;

import java.util.Arrays;

/**
 * Created by Lucas on 09/06/2017.
 */

public class Validacao {


    public static boolean validateDDDNumber(EditText editDDDNumber, String smErro) {
        String[] aSddds = {"11", "12", "13", "14", "15", "16", "17", "18", "19", "21", "22", "24",
                "27", "28", "31", "32", "33", "34", "35", "37", "38", "41", "42", "43", "44", "45",
                "46", "47", "48", "49", "51", "53", "54", "55", "61", "62", "63", "64", "65", "66",
                "67", "68", "69", "71", "73", "74", "75", "77", "79", "81", "82", "83", "84", "85",
                "86", "87", "88", "89", "91", "92", "93", "94", "95", "96", "97", "98", "99"};

        String sddd = editDDDNumber.getText().toString().trim();

        if ((TextUtils.isEmpty(sddd)) || (sddd.length() < 2) || (!Arrays.asList(aSddds).contains(sddd))) {
            editDDDNumber.setError(smErro);
            editDDDNumber.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validatePhoneNumber(EditText editPhoneNumber, String smErro) {
        String telefone = editPhoneNumber.getText().toString();
        telefone.replace("-", "").trim();
        if ((TextUtils.isEmpty(telefone)) || (telefone.length() < 8)) {
            editPhoneNumber.setError(smErro);
            editPhoneNumber.requestFocus();
            return false;
        }
        return true;
    }

    public static String formatacaoTelefone(String telefone){

        String telefoneformatado = telefone;
        telefoneformatado = telefoneformatado.replaceAll("[^0-9]", "");
        telefoneformatado = telefoneformatado.trim();
        telefoneformatado ="("+telefoneformatado.substring(2,4)+") "+telefoneformatado.substring(4,telefoneformatado.length()-4)+"-"+telefoneformatado.substring(telefoneformatado.length()-4,telefoneformatado.length());


        return telefoneformatado;
    }
}
