package com.lfcaplicativos.poliesportivo.Activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfcaplicativos.poliesportivo.Adapter.RecyclerPrincipal;
import com.lfcaplicativos.poliesportivo.Config.ConfiguracaoFirebase;
import com.lfcaplicativos.poliesportivo.Objetos.Ginasios;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.ConexaoHTTP;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;
import com.lfcaplicativos.poliesportivo.Uteis.Validacao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Principal extends AppCompatActivity {
    private RecyclerView recycler_Principal_Ginasio;
    private RecyclerView.Adapter mAdapter;
    private Preferencias preferencias;
    private DatabaseReference referenciaConfiguracao;
    private FirebaseUser mUser;
    private StorageReference storageRef;
    private SearchView menu_search_Principal;
    private Toolbar toolbar;

    private JSONObject jsonobject;
    private JSONArray jsonarray;
    private ProgressDialog mProgressDialog;

    private boolean novo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = new Bundle();
        preferencias = new Preferencias(this);
        novo =  (preferencias.getNOME() == null || preferencias.getNOME().trim().isEmpty());
        ConfiguracaoFirebase.buscarConfiguracoes(preferencias);

        setContentView(R.layout.activity_principal);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        FirebaseAuth mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        recycler_Principal_Ginasio = findViewById(R.id.recycler_principal_ginasio);

        recycler_Principal_Ginasio.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recycler_Principal_Ginasio.setLayoutManager(mLayoutManager);

        if(novo) {
            chamaUsuario(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Chaves.ginasio_principal == null) {
            carregarGinasio();
        } else {
            mAdapter = new RecyclerPrincipal(Chaves.ginasio_principal);
            recycler_Principal_Ginasio.setAdapter(mAdapter);
            ((RecyclerPrincipal) mAdapter).setOnItemClickListener(new RecyclerPrincipal
                    .MyClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    chamaGinasio(position);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        final MenuItem searchItem = menu.findItem(R.id.item_principal_search);
        menu_search_Principal = (SearchView) searchItem.getActionView();

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                Validacao.itemsVisibility(menu, searchItem, false);
                toolbar.refreshDrawableState();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                Validacao.itemsVisibility(menu, searchItem, true);
                toolbar.refreshDrawableState();
                return true;
            }
        });

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        menu_search_Principal.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

        menu_search_Principal.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                return true;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_principal_usuario) {
            chamaUsuario(false);
        }

        return super.onOptionsItemSelected(item);
    }

    private void carregarGinasio() {

        mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.gymnasium) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chaves.ginasio_principal = new ArrayList<Ginasios>();

                    String sJson = ConexaoHTTP.getJSONFromAPI(preferencias.getSPreferencias(Chaves.CHAVE_URL_GINASIO));
                    jsonobject = new JSONObject(sJson);
                    jsonarray = jsonobject.getJSONArray("ginasio");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);

                        final Ginasios ginasios = new Ginasios();
                        Chaves.ginasio_principal.add(ginasios);
                        ginasios.setCodigo(jsonobject.optInt("id"));
                        ginasios.setNome(jsonobject.optString("nome"));
                        ginasios.setFantasia(jsonobject.optString("fantasia"));
                        ginasios.setEndereco(jsonobject.optString("endereco"));
                        ginasios.setNumero(jsonobject.optString("numero"));
                        ginasios.setBairro(jsonobject.optString("bairro"));
                        ginasios.setCidade(jsonobject.optString("cidade"));
                        ginasios.setEstado(jsonobject.optString("estado"));
                        ginasios.setLatitude(jsonobject.optDouble("lat"));
                        ginasios.setLongitude(jsonobject.optDouble("lng"));
                        ginasios.setModalidade(jsonobject.optString("modalidade"));
                        ginasios.setPiso(jsonobject.optString("piso"));
                        ginasios.setNomeLogo(jsonobject.optString("nomelogo"));
                        ginasios.setCoberto(jsonobject.optBoolean("coberto", false));
                        ginasios.setEstacionamento(jsonobject.optBoolean("estacionamento", false));
                        ginasios.setNomeLogo(jsonobject.optString("nomelogo"));

                        if (ginasios.getNomeLogo() != null && !ginasios.getNomeLogo().trim().isEmpty()) {
                            byte[] b = Base64.decode(ginasios.getNomeLogo(), Base64.DEFAULT);
                            ginasios.setLogo(BitmapFactory.decodeByteArray(b, 0, b.length));
                        }
                    }
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter = new RecyclerPrincipal(Chaves.ginasio_principal);
                            recycler_Principal_Ginasio.setAdapter(mAdapter);
                            ((RecyclerPrincipal) mAdapter).setOnItemClickListener(new RecyclerPrincipal
                                    .MyClickListener() {
                                @Override
                                public void onItemClick(int position, View v) {
                                    chamaGinasio(position);
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressDialog.cancel();
            }
        }).start();
    }

    private void chamaGinasio(int position) {
        Intent intent;
        intent = new Intent(Principal.this, Ginasio.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    private void chamaUsuario(boolean userNovo){
        Intent intent;
        intent = new Intent(Principal.this, Usuario.class);
        intent.putExtra("novo", userNovo);
        startActivity(intent);
    }


}
