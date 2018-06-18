package com.lfcaplicativos.poliesportivo.Config;


import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.lfcaplicativos.poliesportivo.BuildConfig;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;

public final class ConfiguracaoFirebase {

    private static FirebaseAuth firebaseAuth;
    private static FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static DatabaseReference databaseReference;


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

    public static FirebaseRemoteConfig getFirebaseRemoteConfig() {
        if (mFirebaseRemoteConfig == null) {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setDeveloperModeEnabled(BuildConfig.DEBUG)
                    .build();
            mFirebaseRemoteConfig.setConfigSettings(configSettings);
        }
        return mFirebaseRemoteConfig;
    }

    public static void buscarConfiguracoes(final Preferencias preferencias){
        getFirebaseRemoteConfig();
        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        }
                        Chaves.atuServerCidade = mFirebaseRemoteConfig.getString(Chaves.CHAVE_ATU_CIDADE);
                        if(Chaves.atuServerCidade == null || Chaves.atuServerCidade.trim().isEmpty())
                            Chaves.atuServerCidade = preferencias.getSPreferencias(Chaves.CHAVE_ATU_CIDADE);

                        Chaves.atuServerEstado = mFirebaseRemoteConfig.getString(Chaves.CHAVE_ATU_ESTADO);
                        if(Chaves.atuServerEstado == null || Chaves.atuServerEstado.trim().isEmpty())
                            Chaves.atuServerEstado = preferencias.getSPreferencias(Chaves.CHAVE_ATU_ESTADO);

                        preferencias.setPreferencias(Chaves.CHAVE_URL_ESTADO,mFirebaseRemoteConfig.getString(Chaves.CHAVE_URL_ESTADO));
                        preferencias.setPreferencias(Chaves.CHAVE_URL_CIDADE,mFirebaseRemoteConfig.getString(Chaves.CHAVE_URL_CIDADE));

                    }
                });


        DatabaseReference referenciaConfiguracao = ConfiguracaoFirebase.getFirebaseDatabase().child(Chaves.CHAVE_CONFIGURACAO);

        referenciaConfiguracao.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        String chave = dados.getKey(), valor = dados.getValue().toString();
                        preferencias.setPreferencias(chave, valor);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
