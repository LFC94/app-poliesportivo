package com.lfcaplicativos.poliesportivo.Config;


import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;

public final class ConfiguracaoFirebase {

    private static Firebase firebase;
    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference databaseReference;


    public static Firebase getFirebase() {

        if (firebase == null) {
            firebase = new Firebase(Chaves.ULR_FIREBASE);
        }

        return firebase;
    }

    public static DatabaseReference getFirebaseDatabase() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

}