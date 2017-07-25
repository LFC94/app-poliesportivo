package com.lfcaplicativos.poliesportivo.application;


import com.firebase.client.Firebase;

public final class ConfiguracaoFirebase {

    private static Firebase firebase;
    private static final String ULR_FIREBASE = "https://poliesportivo-37275.firebaseio.com/";

    public static Firebase getFirebase() {

        if (firebase == null) {
            firebase = new Firebase(ULR_FIREBASE);
        }

        return firebase;
    }

}
