package com.lfcaplicativos.poliesportivo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.lfcaplicativos.poliesportivo.R;

public class Principal extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_principal);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getTitle());
            setSupportActionBar(toolbar);

            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.navigation_home:

                        case R.id.navigation_dashboard:
                        case R.id.navigation_usuario:
                            ChamaUsuario();
                            break;
                    }
                    return true;
                }
            });
        }catch (Exception e){
            Log.e("Erro",e.getMessage());
        }
    }

    private void ChamaUsuario() {
        Intent intent;
        intent = new Intent(Principal.this, Usuario.class);
        startActivity(intent);
        this.finish();

    }

}
