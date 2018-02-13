package com.lfcaplicativos.poliesportivo.Fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lfcaplicativos.poliesportivo.Activity.Principal;
import com.lfcaplicativos.poliesportivo.Config.ConfiguracaoFirebase;
import com.lfcaplicativos.poliesportivo.Objetos.Cidade;
import com.lfcaplicativos.poliesportivo.Objetos.Estado;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.ConexaoHTTP;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Usuario extends Fragment {

    private View viewUsuario;
    private static Activity activityUsuario;
    public static MaterialEditText edit_Usuario_Nome;
    private MaterialSpinner spinner_Usuario_Estado, spinner_Usuario_Cidade;
    public static ImageView image_Usuario_Foto;
    private TextView text_Usuario_Conexao;

    private JSONObject jsonobject;
    private JSONArray jsonarray;
    private ProgressDialog mProgressDialog;
    private Timer timer;


    private Preferencias preferencias;
    private DatabaseReference referenciaConfiguracao;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private boolean acesso_banco = true, prim_uf = true, prim_cid = true;
    public static boolean salvar = false;

    public Fragment_Usuario() {
        // Required empty public constructor
    }

    public static Fragment_Usuario newInstance(Activity activity) {
        Fragment_Usuario fragment = new Fragment_Usuario();
        activityUsuario = activity;
        return fragment;
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (salvar) {
            salvar = false;

            preferencias.setNOME(edit_Usuario_Nome.getText().toString().trim());
            if (acesso_banco) {
                if (spinner_Usuario_Cidade.getSelectedItemPosition() > 0) {
                    preferencias.setCIDADE(Chaves.cidades_usuario.get(spinner_Usuario_Cidade.getSelectedItemPosition() - 1).getNome());
                } else {
                    preferencias.setCIDADE("");
                }

                if (spinner_Usuario_Estado.getSelectedItemPosition() > 0) {
                    preferencias.setESTADO(Chaves.estados_usuario.get(spinner_Usuario_Estado.getSelectedItemPosition() - 1).getNome());
                } else {
                    preferencias.setESTADO("");
                }
            }

            ImagemPerfilUsuario(true);

            DatabaseReference referenciaFire = ConfiguracaoFirebase.getFirebaseDatabase();
            referenciaFire.child(Chaves.CHAVE_USUARIO).child(preferencias.getSPreferencias(Chaves.CHAVE_ID)).setValue(preferencias.retornaUsuarioPreferencias(false));

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(preferencias.getNOME()).build();


            mUser = mAuth.getCurrentUser();
            mUser.updateProfile(profileUpdates);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewUsuario = inflater.inflate(R.layout.fragment_usuario, container, false);

        preferencias = new Preferencias(viewUsuario.getContext());
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        storage = FirebaseStorage.getInstance();

        edit_Usuario_Nome = (MaterialEditText) viewUsuario.findViewById(R.id.edit_Usuario_Nome);
        spinner_Usuario_Estado = (MaterialSpinner) viewUsuario.findViewById(R.id.spinner_Usuario_Estado);
        spinner_Usuario_Cidade = (MaterialSpinner) viewUsuario.findViewById(R.id.spinner_Usuario_Cidade);
        image_Usuario_Foto = (ImageView) viewUsuario.findViewById(R.id.image_Usuario_Foto);
        text_Usuario_Conexao = (TextView) viewUsuario.findViewById(R.id.text_Usuario_Conexao);


        spinner_Usuario_Estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {

                if (!acesso_banco)
                    return;

                if (position >= 0) {
                    if (Chaves.cidadelist_usuario != null && !prim_cid) {
                        return;
                    }
                    CarregarCidade(Chaves.estados_usuario.get(position).getSigla());
                } else {
                    spinner_Usuario_Estado.setError(R.string.notstate);
                    spinner_Usuario_Estado.requestFocus();
                    Chaves.cidadelist_usuario.clear();
                    spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(activityUsuario,
                            android.R.layout.simple_spinner_dropdown_item,
                            Chaves.cidadelist_usuario));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        BuscarDadosFirebase();

        ImagemPerfilUsuario(false);


        return viewUsuario;
    }

    private void CarregarEstado() {
        mProgressDialog = ProgressDialog.show(activityUsuario, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.state) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chaves.estados_usuario = new ArrayList<Estado>();
                    Chaves.estadolist_usuario = new ArrayList<String>();
                    String sJson = ConexaoHTTP.getJSONFromAPI(preferencias.getSPreferencias(Chaves.CHAVE_ULR_ESTADO));
                    jsonobject = new JSONObject(sJson);
                    jsonarray = jsonobject.getJSONArray("estado");
                    Chaves.estadolist_usuario.clear();
                    Chaves.estados_usuario.clear();
                    Estado estado = new Estado();
                    estado.setIdPais(55);
                    estado.setIdUF(0);
                    estado.setSigla("");
                    estado.setNome("");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);

                        estado = new Estado();
                        estado.setIdPais(55);
                        estado.setIdUF(jsonobject.optInt("id"));
                        estado.setSigla(jsonobject.optString("uf"));
                        estado.setNome(jsonobject.optString("nome"));
                        Chaves.estados_usuario.add(estado);

                        Chaves.estadolist_usuario.add(jsonobject.optString("uf").trim() + " - " + jsonobject.optString("nome"));
                    }
                    activityUsuario.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(activityUsuario,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    Chaves.estadolist_usuario));
                            if (prim_uf) {
                                prim_uf = false;
                                if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                                    for (int i = 0; i < Chaves.estados_usuario.size(); i++) {
                                        if (Chaves.estados_usuario.get(i).getNome().equals(preferencias.getESTADO()))
                                            spinner_Usuario_Estado.setSelection(i + 1);
                                    }
                                }
                            }

                        }
                    });
                } catch (Exception e) {
                    Log.e("ESTADO", "ERRO: " + e.getMessage());
                    activityUsuario.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FalhaCarregarDados();
                        }
                    });
                }
                mProgressDialog.cancel();
            }
        }).start();
    }

    private void CarregarCidade(final String UFEstado) {

        if (UFEstado.trim().isEmpty()) {
            spinner_Usuario_Estado.setError(R.string.notstate);
            spinner_Usuario_Estado.requestFocus();
            Chaves.cidadelist_usuario.clear();
            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(activityUsuario,
                    android.R.layout.simple_spinner_dropdown_item,
                    Chaves.cidadelist_usuario));
            return;
        }

        mProgressDialog = ProgressDialog.show(activityUsuario, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.city) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chaves.cidades_usuario = new ArrayList<Cidade>();
                    Chaves.cidadelist_usuario = new ArrayList<String>();
                    String url = preferencias.getSPreferencias(Chaves.CHAVE_ULR_CIDADE);
                    url += "?UF='" + UFEstado + "'";


                    String sJson = ConexaoHTTP.getJSONFromAPI(url);
                    jsonobject = new JSONObject(sJson);
                    jsonarray = jsonobject.getJSONArray("cidade");
                    Chaves.cidadelist_usuario.clear();
                    Chaves.cidades_usuario.clear();

                    Cidade cidade = new Cidade();
                    cidade.setIdPais(0);
                    cidade.setIdUF(0);
                    cidade.setIdCidade(0);
                    cidade.setNome("");

                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);

                        cidade = new Cidade();
                        cidade.setIdPais(55);
                        cidade.setIdUF(jsonobject.optInt("iduf"));
                        cidade.setIdCidade(jsonobject.optInt("id"));
                        cidade.setNome(jsonobject.optString("nome"));
                        Chaves.cidades_usuario.add(cidade);

                        Chaves.cidadelist_usuario.add(jsonobject.optString("nome"));
                    }
                    activityUsuario.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(activityUsuario,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    Chaves.cidadelist_usuario));
                            if (!prim_uf && prim_cid) {
                                prim_cid = false;
                                if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                                    for (int i = 0; i < Chaves.cidades_usuario.size(); i++) {
                                        if (Chaves.cidades_usuario.get(i).getNome().equals(preferencias.getCIDADE()))
                                            spinner_Usuario_Cidade.setSelection(i + 1);
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {

                }
                mProgressDialog.cancel();
            }
        }).start();

    }

    private void ImagemPerfilUsuario(boolean Salvar) {

        if (Salvar) {
            if (Principal.bitmapFotoPerfil != null) {
                Bitmap realImage = Principal.bitmapFotoPerfil;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                preferencias.setFOTO_PERFIL(Base64.encodeToString(b, Base64.DEFAULT));
                image_Usuario_Foto.setImageBitmap(Principal.bitmapFotoPerfil);
            }
        } else {
            if (preferencias.getFOTO_PERFIL() != null && !preferencias.getFOTO_PERFIL().trim().isEmpty()) {
                byte[] b = Base64.decode(preferencias.getFOTO_PERFIL(), Base64.DEFAULT);
                Principal.bitmapFotoPerfil = BitmapFactory.decodeByteArray(b, 0, b.length);
                image_Usuario_Foto.setImageBitmap(Principal.bitmapFotoPerfil);
            }
        }

    }

    private void FalhaCarregarDados() {
        acesso_banco = false;

        if (preferencias.getNOME() != null) {
            edit_Usuario_Nome.setText(preferencias.getNOME());

            Chaves.estadolist_usuario.clear();
            if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                Chaves.estadolist_usuario.add(preferencias.getESTADO());
                spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(activityUsuario,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.estadolist_usuario));

                spinner_Usuario_Estado.setSelection(1);
            } else {
                spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(activityUsuario,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.estadolist_usuario));

                spinner_Usuario_Estado.setSelection(0);
            }
            Chaves.cidadelist_usuario.clear();
            if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                Chaves.cidadelist_usuario.clear();
                Chaves.cidadelist_usuario.add(preferencias.getCIDADE());
                spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(activityUsuario,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.cidadelist_usuario));

                spinner_Usuario_Cidade.setSelection(1);
            } else {
                spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(activityUsuario,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.cidadelist_usuario));

                spinner_Usuario_Cidade.setSelection(0);
            }
        }
        spinner_Usuario_Estado.setEnabled(acesso_banco);
        spinner_Usuario_Cidade.setEnabled(acesso_banco);
        text_Usuario_Conexao.setVisibility(acesso_banco ? View.INVISIBLE : View.VISIBLE);

        try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    new Thread(new Runnable() {
                        public void run() {
                            if (ConexaoHTTP.verificaConexao(activityUsuario)) {
                                BuscarDadosFirebase();
                                timer.cancel();
                            }
                        }
                    }).start();
                }
            }, 0, 1000);
        } catch (Exception e) {

        }

    }

    private void BuscarDadosFirebase() {
        acesso_banco = true;
        spinner_Usuario_Estado.setEnabled(acesso_banco);
        spinner_Usuario_Cidade.setEnabled(acesso_banco);
        text_Usuario_Conexao.setVisibility(acesso_banco ? View.INVISIBLE : View.VISIBLE);

        referenciaConfiguracao = ConfiguracaoFirebase.getFirebaseDatabase().child(Chaves.CHAVE_CONFIGURACAO);

        referenciaConfiguracao.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        String chave = dados.getKey(), valor = dados.getValue().toString();
                        preferencias.setPreferencias(chave, valor);
                    }
                    if (preferencias.getNOME() != null)
                        edit_Usuario_Nome.setText(preferencias.getNOME());

                    if (Chaves.estadolist_usuario == null) {
                        CarregarEstado();
                    } else {
                        spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(activityUsuario,
                                android.R.layout.simple_spinner_dropdown_item,
                                Chaves.estadolist_usuario));
                        prim_uf = false;
                        if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                            for (int i = 0; i < Chaves.estados_usuario.size(); i++) {
                                if (Chaves.estados_usuario.get(i).getNome().equals(preferencias.getESTADO()))
                                    spinner_Usuario_Estado.setSelection(i + 1);
                            }
                        }

                        if (Chaves.cidadelist_usuario != null) {
                            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(activityUsuario,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    Chaves.cidadelist_usuario));
                            prim_cid = false;
                            if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                                for (int i = 0; i < Chaves.cidades_usuario.size(); i++) {
                                    if (Chaves.cidades_usuario.get(i).getNome().equals(preferencias.getCIDADE()))
                                        spinner_Usuario_Cidade.setSelection(i + 1);
                                }
                            }

                        }


                    }

                } catch (Exception e) {
                    Log.e("ESTADO", "ERRO: " + e.getMessage());
                    FalhaCarregarDados();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERRO", "DatabaseError:" + databaseError.getMessage());
                FalhaCarregarDados();
            }
        });

    }


}
