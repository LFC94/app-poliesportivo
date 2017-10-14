package com.lfcaplicativos.poliesportivo.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lfcaplicativos.poliesportivo.Config.ConfiguracaoFirebase;
import com.lfcaplicativos.poliesportivo.Objetos.Cidade;
import com.lfcaplicativos.poliesportivo.Objetos.Estado;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.ConexaoHTTP;
import com.lfcaplicativos.poliesportivo.Uteis.Permissao;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Usuario extends AppCompatActivity implements View.OnClickListener {
    private MaterialEditText edit_Usuario_Nome;
    private MaterialSpinner spinner_Usuario_Estado, spinner_Usuario_Cidade;
    private ImageView image_Usuario_Foto;
    private TextView text_Usuario_Conexao;

    private Bitmap bitmapFotoPerfil = null;
    private JSONObject jsonobject;
    private JSONArray jsonarray;
    private ProgressDialog mProgressDialog;
    private Timer timer;

    private ArrayList<String> estadolist;
    private ArrayList<Estado> estados;
    private ArrayList<String> cidadelist;
    private ArrayList<Cidade> cidades;

    private Preferencias preferencias;
    private DatabaseReference referenciaConfiguracao;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private boolean acesso_banco = true, prim_uf = true, prim_cid = true;
    private boolean loginNovo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);
        Bundle bundle = getIntent().getExtras();

        loginNovo = bundle.getBoolean("novo", false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        toolbar.setNavigationIcon(R.drawable.ic_navigate_before);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChamarTelaCalendario();
            }
        });

        preferencias = new Preferencias(this);
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        storage = FirebaseStorage.getInstance();

        edit_Usuario_Nome = (MaterialEditText) findViewById(R.id.edit_Usuario_Nome);
        spinner_Usuario_Estado = (MaterialSpinner) findViewById(R.id.spinner_Usuario_Estado);
        spinner_Usuario_Cidade = (MaterialSpinner) findViewById(R.id.spinner_Usuario_Cidade);
        image_Usuario_Foto = (ImageView) findViewById(R.id.image_Usuario_Foto);
        text_Usuario_Conexao = (TextView) findViewById(R.id.text_Usuario_Conexao);

        estados = new ArrayList<Estado>();
        estadolist = new ArrayList<String>();

        cidades = new ArrayList<Cidade>();
        cidadelist = new ArrayList<String>();

        spinner_Usuario_Estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {

                if (!acesso_banco)
                    return;

                if (position >= 0) {
                    CarregarCidade(estados.get(position).getSigla());
                } else {
                    spinner_Usuario_Estado.setError(R.string.notstate);
                    spinner_Usuario_Estado.requestFocus();
                    cidadelist.clear();
                    spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            cidadelist));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        BuscarDadosFirebase();

        ImagemPerfilUsuario(false);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_usuario_confirmar) {
            onClick(findViewById(R.id.item_usuario_confirmar));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.item_usuario_confirmar:

                if (edit_Usuario_Nome.getText().toString().trim().isEmpty()) {
                    edit_Usuario_Nome.setError(getString(R.string.notName));
                    edit_Usuario_Nome.requestFocus();
                    return;
                }

                preferencias.setNOME(edit_Usuario_Nome.getText().toString().trim());
                if (acesso_banco) {
                    if (spinner_Usuario_Cidade.getSelectedItemPosition() > 0) {
                        preferencias.setCIDADE(cidades.get(spinner_Usuario_Cidade.getSelectedItemPosition() - 1).getNome());
                    } else {
                        preferencias.setCIDADE("");
                    }

                    if (spinner_Usuario_Estado.getSelectedItemPosition() > 0) {
                        preferencias.setESTADO(estados.get(spinner_Usuario_Estado.getSelectedItemPosition() - 1).getNome());
                    } else {
                        preferencias.setESTADO("");
                    }
                }

                ImagemPerfilUsuario(true);

                DatabaseReference referenciaFire = ConfiguracaoFirebase.getFirebaseDatabase();
                referenciaFire.child(Chaves.CHAVE_USUARIO).child(preferencias.getSPreferencias(Chaves.CHAVE_ID)).setValue(preferencias.RetornaUsuarioPreferencias(false));

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(preferencias.getNOME()).build();

                GravarImagemFireBase(bitmapFotoPerfil);

                mUser = mAuth.getCurrentUser();
                mUser.updateProfile(profileUpdates);

                ChamarTelaCalendario();
                this.finish();
                break;
            case R.id.fab_Usuario_Foto:
                if (Permissao.ValidaPermicao(Usuario.this, Manifest.permission.CAMERA, 1)) {
                    AbrieCameraGaleria();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Chaves.CHAVE_RESULT_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {

                if (data.getData() != null) {
                    InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                    bitmapFotoPerfil = BitmapFactory.decodeStream(inputStream);
                } else {
                    Bundle extras = data.getExtras();
                    if (extras != null) {

                        bitmapFotoPerfil = (Bitmap) extras.get("data");
                    }
                }

                if (bitmapFotoPerfil != null)
                    image_Usuario_Foto.setImageBitmap(bitmapFotoPerfil);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void CarregarEstado() {
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
                                            spinner_Usuario_Estado.setSelection(i + 1);
                                    }
                                }
                            }

                        }
                    });
                } catch (Exception e) {
                    Log.e("ESTADO", "ERRO: " + e.getMessage());
                    runOnUiThread(new Runnable() {
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
            cidadelist.clear();
            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    cidadelist));
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

    private void ChamarTelaCalendario() {
        if (loginNovo) {
            Intent intent;
            intent = new Intent(Usuario.this, Calendario.class);
            startActivity(intent);
        } else {
            onBackPressed();
        }
        if (!acesso_banco) {
            timer.cancel();
        }
        this.finish();
    }

    private void AbrieCameraGaleria() {

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.selectPhoto));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, Chaves.CHAVE_RESULT_PHOTO);
    }

    private void ImagemPerfilUsuario(boolean Salvar) {

        if (Salvar) {
            if (bitmapFotoPerfil != null) {
                Bitmap realImage = bitmapFotoPerfil;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                preferencias.setFOTO_PERFIL(Base64.encodeToString(b, Base64.DEFAULT));
                image_Usuario_Foto.setImageBitmap(bitmapFotoPerfil);
            }
        } else {
            if (preferencias.getFOTO_PERFIL() != null && !preferencias.getFOTO_PERFIL().trim().isEmpty()) {
                byte[] b = Base64.decode(preferencias.getFOTO_PERFIL(), Base64.DEFAULT);
                bitmapFotoPerfil = BitmapFactory.decodeByteArray(b, 0, b.length);
                image_Usuario_Foto.setImageBitmap(bitmapFotoPerfil);
            }
        }

    }

    private void GravarImagemFireBase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        });
    }

    private void FalhaCarregarDados() {
        acesso_banco = false;

        if (preferencias.getNOME() != null) {
            edit_Usuario_Nome.setText(preferencias.getNOME());

            estadolist.clear();
            if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                estadolist.add(preferencias.getESTADO());
                spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        estadolist));

                spinner_Usuario_Estado.setSelection(1);
            } else {
                spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        estadolist));

                spinner_Usuario_Estado.setSelection(0);
            }
            cidadelist.clear();
            if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                cidadelist.clear();
                cidadelist.add(preferencias.getCIDADE());
                spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        cidadelist));

                spinner_Usuario_Cidade.setSelection(1);
            } else {
                spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        cidadelist));

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
                            if (ConexaoHTTP.verificaConexao(Usuario.this)) {
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
        String nomefoto_perfil = preferencias.getSPreferencias(Chaves.CHAVE_ID).replaceAll("[^a-zA-Z0-9]+", "").trim() + ".JPEG";

        storageRef = storage.getReference().child(Chaves.CHAVE_FOTO_PERFIL).child(nomefoto_perfil);

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

                    CarregarEstado();

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
