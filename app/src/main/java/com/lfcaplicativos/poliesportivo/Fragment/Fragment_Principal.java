package com.lfcaplicativos.poliesportivo.Fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfcaplicativos.poliesportivo.Adapter.RecyclerPrincipal;
import com.lfcaplicativos.poliesportivo.Config.ConfiguracaoFirebase;
import com.lfcaplicativos.poliesportivo.Objetos.Ginasios;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.ConexaoHTTP;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Principal extends Fragment {
    private View viewPrincipal;
    private static Activity activityPrincipal;

    private MaterialEditText edit_Principal_Busca;
    private RecyclerView recycler_Principal_Ginasio;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Preferencias preferencias;
    private DatabaseReference referenciaConfiguracao;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private JSONObject jsonobject;
    private JSONArray jsonarray;
    private ProgressDialog mProgressDialog;


    public Fragment_Principal() {
        // Required empty public constructor
    }

    public static Fragment_Principal newInstance(Activity activity) {
        Fragment_Principal fragment = new Fragment_Principal();
        activityPrincipal = activity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewPrincipal = inflater.inflate(R.layout.fragment_principal, container, false);

        preferencias = new Preferencias(viewPrincipal.getContext());
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        storage = FirebaseStorage.getInstance();

        edit_Principal_Busca = (MaterialEditText) viewPrincipal.findViewById(R.id.edit_Principal_Busca);
        recycler_Principal_Ginasio = (RecyclerView) viewPrincipal.findViewById(R.id.recycler_principal_ginasio);


        recycler_Principal_Ginasio.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(activityPrincipal);
        recycler_Principal_Ginasio.setLayoutManager(mLayoutManager);
        if (Chaves.ginasio_principal == null) {
            CarregarGinasio();
        } else {
            mAdapter = new RecyclerPrincipal(Chaves.ginasio_principal);
            recycler_Principal_Ginasio.setAdapter(mAdapter);
        }
        return viewPrincipal;
    }


    private void CarregarGinasio() {

        mProgressDialog = ProgressDialog.show(activityPrincipal, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.gymnasium) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chaves.ginasio_principal = new ArrayList<Ginasios>();

                    String sJson = ConexaoHTTP.getJSONFromAPI(preferencias.getSPreferencias(Chaves.CHAVE_ULR_GINASIO));
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
                        ginasios.setModalidade(jsonobject.optString("modalidade"));
                        ginasios.setPiso(jsonobject.optString("piso"));
                        ginasios.setNomelogo(jsonobject.optString("nomelogo"));
                        ginasios.setCoberto(jsonobject.optBoolean("coberto", false));
                        ginasios.setEstacionamento(jsonobject.optBoolean("estacionamento", false));
                        ginasios.setNomelogo(jsonobject.optString("nomelogo"));

                        if (ginasios.getNomelogo() != null && !ginasios.getNomelogo().trim().isEmpty()) {
                            byte[] b = Base64.decode(ginasios.getNomelogo(), Base64.DEFAULT);
                            ginasios.setLogo(BitmapFactory.decodeByteArray(b, 0, b.length));
                        }
                    }
                    Thread.sleep(1000);
                    activityPrincipal.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter = new RecyclerPrincipal(Chaves.ginasio_principal);
                            recycler_Principal_Ginasio.setAdapter(mAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressDialog.cancel();
            }
        }).start();
    }

}
