package com.lfcaplicativos.poliesportivo.Uteis;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by Lucas on 09/06/2017.
 */

public class Validacao {

    public static boolean validateDDDNumber(EditText editDDDNumber, String smErro) {
        String telefone = editDDDNumber.getText().toString();
        if (TextUtils.isEmpty(telefone)) {
            editDDDNumber.setError(smErro);
            editDDDNumber.requestFocus();
            return false;
        }
        return true;
    }

    public static boolean validatePhoneNumber(EditText editPhoneNumber, String smErro) {
        String telefone = editPhoneNumber.getText().toString();
        telefone.replace("-", "").trim();
        if (TextUtils.isEmpty(telefone)) {
            editPhoneNumber.setError(smErro);
            editPhoneNumber.requestFocus();
            return false;
        }
        return true;
    }
}
