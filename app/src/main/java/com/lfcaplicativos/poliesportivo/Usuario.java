package com.lfcaplicativos.poliesportivo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
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

    FirebaseAuth mAuth;
    FirebaseUser mUser;

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

        mProgressDialog = ProgressDialog.show(Usuario.this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.state) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sJson = ConexaoHTTP.getJSONFromAPI(Chaves.ULR_geonames + "3469034");
                    jsonobject = new JSONObject(sJson);
                    jsonarray = jsonobject.getJSONArray("geonames");
                    estadolist.clear();
                    estados.clear();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);

                        Estado estado = new Estado();
                        estado.setIdPais(3469034);
                        estado.setIdUF(jsonobject.optInt("geonameId"));
                        estado.setNome(jsonobject.optString("name"));
                        estados.add(estado);

                        estadolist.add(jsonobject.optString("name"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    estadolist));
                        }
                    });
                } catch (Exception e) {

                }
                mProgressDialog.cancel();
            }
        }).start();

        spinner_Usuario_Estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {
                // TODO Auto-generated method stub

                CarregarCidade(estados.get(position).getIdUF());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }


    public void CarregarCidade(final int idEstado) {
        mProgressDialog = ProgressDialog.show(Usuario.this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.city) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sJson = ConexaoHTTP.getJSONFromAPI(Chaves.ULR_geonames + String.valueOf(idEstado));
                    jsonobject = new JSONObject(sJson);
                    jsonarray = jsonobject.getJSONArray("geonames");
                    cidadelist.clear();
                    cidades.clear();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonobject = jsonarray.getJSONObject(i);

                        Cidade cidade = new Cidade();
                        cidade.setIdPais(3469034);
                        cidade.setIdUF(idEstado);
                        cidade.setIdUF(jsonobject.optInt("geonameId"));
                        cidade.setNome(jsonobject.optString("name"));
                        cidades.add(cidade);

                        cidadelist.add(jsonobject.optString("name"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    cidadelist));
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
            preferencias.setCIDADE(spinner_Usuario_Cidade.getSelectedItem().toString());
            preferencias.setESTADO(spinner_Usuario_Estado.getSelectedItem().toString());

            DatabaseReference referenciaFire = ConfiguracaoFirebase.getFirebaseDatabase();
            referenciaFire.child(Chaves.CHAVE_USUARIO).child(preferencias.RetornaUsuarioPreferencias().get(String.valueOf(Chaves.CHAVE_ID))).setValue(preferencias.RetornaUsuarioPreferencias());
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
