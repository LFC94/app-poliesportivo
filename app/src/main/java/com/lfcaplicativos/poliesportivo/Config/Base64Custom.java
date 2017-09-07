package com.lfcaplicativos.poliesportivo.Config;

import android.util.Base64;

/**
 * Created by Lucas on 07/09/2017.
 */

public class Base64Custom {

    public static String CodificarBase64(String Texto) {
        return Base64.encodeToString(Texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n\\r)", "").replaceAll("\\n", "").trim();
    }

    public static String DecodificarBase64(String Texto) {
        return new String(Base64.decode(Texto, Base64.DEFAULT));
    }
}
