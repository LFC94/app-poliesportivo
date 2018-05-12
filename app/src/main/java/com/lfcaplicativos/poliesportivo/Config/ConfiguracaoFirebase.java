package com.lfcaplicativos.poliesportivo.Config;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;

public final class ConfiguracaoFirebase {

    private static FirebaseAuth firebaseAuth;
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
    public static void buscarConfiguracoes(final Preferencias preferencias){
        DatabaseReference referenciaConfiguracao = ConfiguracaoFirebase.getFirebaseDatabase().child(Chaves.CHAVE_CONFIGURACAO);

        referenciaConfiguracao.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        String chave = dados.getKey(), valor = dados.getValue().toString();
                        if(chave.equalsIgnoreCase(Chaves.CHAVE_ATU_CIDADE))
                            Chaves.atuServerCidade = valor;
                        else if(chave.equalsIgnoreCase(Chaves.CHAVE_ATU_ESTADO))
                            Chaves.atuServerEstado = valor;
                        else
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
