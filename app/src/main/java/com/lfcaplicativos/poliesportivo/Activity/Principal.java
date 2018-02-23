package com.lfcaplicativos.poliesportivo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lfcaplicativos.poliesportivo.Config.ConfiguracaoFirebase;
import com.lfcaplicativos.poliesportivo.Fragment.Fragment_Principal;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;

public class Principal extends AppCompatActivity  {


    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private int codigoNavegation = Chaves.CHAVE_NAVEGATIN_PRINCIPAL;
    private Preferencias preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            preferencias = new Preferencias(this);
            DatabaseReference referenciaConfiguracao = ConfiguracaoFirebase.getFirebaseDatabase().child(Chaves.CHAVE_CONFIGURACAO);

            referenciaConfiguracao.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        for (DataSnapshot dados : dataSnapshot.getChildren()) {
                            String chave = dados.getKey(), valor = dados.getValue().toString();
                            preferencias.setPreferencias(chave, valor);
                        }

                    } catch (Exception ignored) {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("ERRO", "DatabaseError:" + databaseError.getMessage());

                }
            });

            setContentView(R.layout.activity_principal);
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle(getTitle());
            setSupportActionBar(toolbar);

            navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            codigoNavegation = Chaves.CHAVE_NAVEGATIN_PRINCIPAL;
                            chamerFragment(Fragment_Principal.newInstance(Principal.this));
                            break;
                        case R.id.navigation_usuario:
                            Intent intent;
                            intent = new Intent(Principal.this, Usuario.class);
                            intent.putExtra("novo", false);
                            startActivity(intent);
                            break;
                        default:
                            return false;
                    }

                    return true;
                }
            });
            chamerFragment(Fragment_Principal.newInstance(this));

        } catch (Exception e) {
            Log.e("Erro", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        prepararMenu();

        return true;
    }




    private void chamerFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_Principal, fragment);
        transaction.commit();
        prepararMenu();
    }

    private void prepararMenu() {
        toolbar.getMenu().findItem(R.id.item_usuario_confirmar).setVisible(codigoNavegation == Chaves.CHAVE_NAVEGATIN_USUARIO);
        toolbar.refreshDrawableState();
    }


}
