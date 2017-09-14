package com.lfcaplicativos.poliesportivo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lfcaplicativos.poliesportivo.Config.ConfiguracaoFirebase;
import com.lfcaplicativos.poliesportivo.Objetos.Cidade;
import com.lfcaplicativos.poliesportivo.Objetos.Estado;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.ConexaoHTTP;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Usuario extends AppCompatActivity implements View.OnClickListener {
    private EditText edit_Usuario_Nome;
    private Spinner spinner_Usuario_Estado;
    private Spinner spinner_Usuario_Cidade;
    private JSONObject jsonobject;
    private JSONArray jsonarray;
    private ProgressDialog mProgressDialog;

    private ArrayList<String> estadolist;
    private ArrayList<Estado> estados;
    private ArrayList<String> cidadelist;
    private ArrayList<Cidade> cidades;

    private Preferencias preferencias;
    private DatabaseReference referenciaConfiguraca;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private boolean prim_uf = true, prim_cid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        preferencias = new Preferencias(this);
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();

        edit_Usuario_Nome = (EditText) findViewById(R.id.edit_Usuario_Nome);
        spinner_Usuario_Estado = (Spinner) findViewById(R.id.spinner_Usuario_Estado);
        spinner_Usuario_Cidade = (Spinner) findViewById(R.id.spinner_Usuario_Cidade);

        estados = new ArrayList<Estado>();
        estadolist = new ArrayList<String>();

        cidades = new ArrayList<Cidade>();
        cidadelist = new ArrayList<String>();

        referenciaConfiguraca = ConfiguracaoFirebase.getFirebaseDatabase().child(Chaves.CHAVE_CONFIGURACAO);

        referenciaConfiguraca.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        String chave = dados.getKey(), valor = dados.getValue().toString();
                        preferencias.setPreferencias(chave, valor);
                    }
                    if (preferencias.getNOME() != null)
                        edit_Usuario_Nome.setText(preferencias.getNOME());

                    mProgressDialog = ProgressDialog.show(Usuario.this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.state) + "...", true);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String sJson = ConexaoHTTP.getJSONFromAPI(preferencias.getSPreferencias(Chaves.CHAVE_ULR_ESTADO));
                                jsonobject = new JSONObject(sJson);
                                jsonarray = jsonobject.getJSONArray("estado");
                                estadolist.clear();
                                estados.clear();
                                for (int i = 0; i < jsonarray.length(); i++) {
                                    jsonobject = jsonarray.getJSONObject(i);

                                    Estado estado = new Estado();
                                    estado.setIdPais(55);
                                    estado.setIdUF(jsonobject.optInt("id"));
                                    estado.setSigla(jsonobject.optString("uf"));
                                    estado.setNome(jsonobject.optString("nome"));
                                    estados.add(estado);

                                    estadolist.add(jsonobject.optString("uf").trim() + " - " + jsonobject.optString("nome"));
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
                                                android.R.layout.simple_spinner_dropdown_item,
                                                estadolist));
                                        if (prim_uf) {
                                            prim_uf = false;
                                            if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                                                for (int i = 0; i < estados.size(); i++) {
                                                    if (estados.get(i).getNome().equals(preferencias.getESTADO()))
                                                        spinner_Usuario_Estado.setSelection(i);
                                                }
                                            }
                                        }

                                    }
                                });
                            } catch (Exception e) {
                                Log.e("ESTADO", "ERRO: " + e.getMessage());
                            }
                            mProgressDialog.cancel();
                        }
                    }).start();
                } catch (Exception e) {
                    Log.e("ESTADO", "ERRO: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERRO", "DatabaseError:" + databaseError.getMessage());
            }
        });

        spinner_Usuario_Estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {
                // TODO Auto-generated method stub

                CarregarCidade(estados.get(position).getSigla());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }


    public void CarregarCidade(final String UFEstado) {

        if (UFEstado.trim().isEmpty()) {
            spinner_Usuario_Estado.requestFocus();
            return;
        }

        mProgressDialog = ProgressDialog.show(Usuario.this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.city) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = preferencias.getSPreferencias(Chaves.CHAVE_ULR_CIDADE);
                    if (preferencias.getSPreferencias(Chaves.CHAVE_ULR_CIDADE_PARAMETERS) != null && !preferencias.getSPreferencias(Chaves.CHAVE_ULR_CIDADE_PARAMETERS).trim().isEmpty()) {
                        url += "?" + preferencias.getSPreferencias(Chaves.CHAVE_ULR_CIDADE_PARAMETERS) + "='" + UFEstado + "'";
                    }

                    String sJson = ConexaoHTTP.getJSONFromAPI(url);
                    jsonobject = new JSONObject(sJson);
                    jsonarray = jsonobject.getJSONArray("cidade");
                    cidadelist.clear();
                    cidades.clear();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);

                        Cidade cidade = new Cidade();
                        cidade.setIdPais(55);
                        cidade.setIdUF(jsonobject.optInt("iduf"));
                        cidade.setIdCidade(jsonobject.optInt("id"));
                        cidade.setNome(jsonobject.optString("nome"));
                        cidades.add(cidade);

                        cidadelist.add(jsonobject.optString("nome"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    cidadelist));
                            if (!prim_uf && prim_cid) {
                                prim_cid = false;
                                if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                                    for (int i = 0; i < cidades.size(); i++) {
                                        if (cidades.get(i).getNome().equals(preferencias.getCIDADE()))
                                            spinner_Usuario_Cidade.setSelection(i);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_Usuario_Confirma) {
            if (edit_Usuario_Nome.getText().toString().trim().isEmpty()) {
                edit_Usuario_Nome.setError(getString(R.string.notName));
                edit_Usuario_Nome.requestFocus();
                return;
            }
            preferencias.setNOME(edit_Usuario_Nome.getText().toString().trim());
            preferencias.setCIDADE(cidades.get(spinner_Usuario_Cidade.getSelectedItemPosition()).getNome());
            preferencias.setESTADO(estados.get(spinner_Usuario_Estado.getSelectedItemPosition()).getNome());

            DatabaseReference referenciaFire = ConfiguracaoFirebase.getFirebaseDatabase();
            referenciaFire.child(Chaves.CHAVE_USUARIO).child(preferencias.getSPreferencias(Chaves.CHAVE_ID)).setValue(preferencias.RetornaUsuarioPreferencias(false));

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(preferencias.getNOME()).build();
            mUser = mAuth.getCurrentUser();
            mUser.updateProfile(profileUpdates);
            ChamarTelaCalendario();
        }
    }

    private void ChamarTelaCalendario() {
        Intent intent;
        intent = new Intent(Usuario.this, Calendario.class);
        startActivity(intent);
        this.finish();
    }
}
