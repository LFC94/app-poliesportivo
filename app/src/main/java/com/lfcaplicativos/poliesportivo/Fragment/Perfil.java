package com.lfcaplicativos.poliesportivo.Fragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.lfcaplicativos.poliesportivo.Activity.Calendario;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Perfil extends Fragment implements View.OnClickListener {
    private View view;
    private Activity activity;

    private MaterialEditText edit_Perfil_Nome;
    private MaterialSpinner spinner_Perfil_Estado, spinner_Perfil_Cidade;
    private ImageView image_Perfil_Foto;
    private TextView text_Perfil_Conexao;

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

    public Perfil() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
        activity = getActivity();
        setHasOptionsMenu(true);

        loginNovo = savedInstanceState.getBoolean("novo", false);
        preferencias = new Preferencias(this.activity);
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        storage = FirebaseStorage.getInstance();

        edit_Perfil_Nome = (MaterialEditText) view.findViewById(R.id.edit_Perfil_Nome);
        spinner_Perfil_Estado = (MaterialSpinner) view.findViewById(R.id.spinner_Perfil_Estado);
        spinner_Perfil_Cidade = (MaterialSpinner) view.findViewById(R.id.spinner_Perfil_Cidade);
        image_Perfil_Foto = (ImageView) view.findViewById(R.id.image_Perfil_Foto);
        text_Perfil_Conexao = (TextView) view.findViewById(R.id.text_Perfil_Conexao);

        estados = new ArrayList<Estado>();
        estadolist = new ArrayList<String>();

        cidades = new ArrayList<Cidade>();
        cidadelist = new ArrayList<String>();

        spinner_Perfil_Estado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {

                if (!acesso_banco)
                    return;

                if (position >= 0) {
                    CarregarCidade(estados.get(position).getSigla());
                } else {
                    spinner_Perfil_Estado.setError(R.string.notstate);
                    spinner_Perfil_Estado.requestFocus();
                    cidadelist.clear();
                    spinner_Perfil_Cidade.setAdapter(new ArrayAdapter<>(activity,
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


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_perfil, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_perfil_confirmar) {
            onClick(view.findViewById(R.id.item_perfil_confirmar));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.item_perfil_confirmar:

                if (edit_Perfil_Nome.getText().toString().trim().isEmpty()) {
                    edit_Perfil_Nome.setError(getString(R.string.notName));
                    edit_Perfil_Nome.requestFocus();
                    return;
                }

                preferencias.setNOME(edit_Perfil_Nome.getText().toString().trim());
                if (acesso_banco) {
                    if (spinner_Perfil_Cidade.getSelectedItemPosition() > 0) {
                        preferencias.setCIDADE(cidades.get(spinner_Perfil_Cidade.getSelectedItemPosition() - 1).getNome());
                    } else {
                        preferencias.setCIDADE("");
                    }

                    if (spinner_Perfil_Estado.getSelectedItemPosition() > 0) {
                        preferencias.setESTADO(estados.get(spinner_Perfil_Estado.getSelectedItemPosition() - 1).getNome());
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

                break;
            case R.id.fab_Perfil_Foto:
                if (Permissao.ValidaPermicao(activity, Manifest.permission.CAMERA, 1)) {
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
                    InputStream inputStream = activity.getApplicationContext().getContentResolver().openInputStream(data.getData());
                    bitmapFotoPerfil = BitmapFactory.decodeStream(inputStream);
                } else {
                    Bundle extras = data.getExtras();
                    if (extras != null) {

                        bitmapFotoPerfil = (Bitmap) extras.get("data");
                    }
                }

                if (bitmapFotoPerfil != null)
                    image_Perfil_Foto.setImageBitmap(bitmapFotoPerfil);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void CarregarEstado() {
        mProgressDialog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.state) + "...", true);
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Perfil_Estado.setAdapter(new ArrayAdapter<>(activity,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    estadolist));
                            if (prim_uf) {
                                prim_uf = false;
                                if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                                    for (int i = 0; i < estados.size(); i++) {
                                        if (estados.get(i).getNome().equals(preferencias.getESTADO()))
                                            spinner_Perfil_Estado.setSelection(i + 1);
                                    }
                                }
                            }

                        }
                    });
                } catch (Exception e) {
                    Log.e("ESTADO", "ERRO: " + e.getMessage());
                    activity.runOnUiThread(new Runnable() {
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
            spinner_Perfil_Estado.setError(R.string.notstate);
            spinner_Perfil_Estado.requestFocus();
            cidadelist.clear();
            spinner_Perfil_Cidade.setAdapter(new ArrayAdapter<>(activity,
                    android.R.layout.simple_spinner_dropdown_item,
                    cidadelist));
            return;
        }

        mProgressDialog = ProgressDialog.show(activity, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.city) + "...", true);
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Perfil_Cidade.setAdapter(new ArrayAdapter<>(activity,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    cidadelist));
                            if (!prim_uf && prim_cid) {
                                prim_cid = false;
                                if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                                    for (int i = 0; i < cidades.size(); i++) {
                                        if (cidades.get(i).getNome().equals(preferencias.getCIDADE()))
                                            spinner_Perfil_Cidade.setSelection(i + 1);
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
            intent = new Intent(activity, Calendario.class);
            startActivity(intent);
        } else {
            activity.onBackPressed();
        }
        if (!acesso_banco) {
            timer.cancel();
        }
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
                image_Perfil_Foto.setImageBitmap(bitmapFotoPerfil);
            }
        } else {
            if (preferencias.getFOTO_PERFIL() != null && !preferencias.getFOTO_PERFIL().trim().isEmpty()) {
                byte[] b = Base64.decode(preferencias.getFOTO_PERFIL(), Base64.DEFAULT);
                bitmapFotoPerfil = BitmapFactory.decodeByteArray(b, 0, b.length);
                image_Perfil_Foto.setImageBitmap(bitmapFotoPerfil);
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
            edit_Perfil_Nome.setText(preferencias.getNOME());

            estadolist.clear();
            if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                estadolist.add(preferencias.getESTADO());
                spinner_Perfil_Estado.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        estadolist));

                spinner_Perfil_Estado.setSelection(1);
            } else {
                spinner_Perfil_Estado.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        estadolist));

                spinner_Perfil_Estado.setSelection(0);
            }
            cidadelist.clear();
            if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                cidadelist.clear();
                cidadelist.add(preferencias.getCIDADE());
                spinner_Perfil_Cidade.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        cidadelist));

                spinner_Perfil_Cidade.setSelection(1);
            } else {
                spinner_Perfil_Cidade.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_spinner_dropdown_item,
                        cidadelist));

                spinner_Perfil_Cidade.setSelection(0);
            }
        }
        spinner_Perfil_Estado.setEnabled(acesso_banco);
        spinner_Perfil_Cidade.setEnabled(acesso_banco);
        text_Perfil_Conexao.setVisibility(acesso_banco ? View.INVISIBLE : View.VISIBLE);

        try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    new Thread(new Runnable() {
                        public void run() {
                            if (ConexaoHTTP.verificaConexao(activity)) {
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
        spinner_Perfil_Estado.setEnabled(acesso_banco);
        spinner_Perfil_Cidade.setEnabled(acesso_banco);
        text_Perfil_Conexao.setVisibility(acesso_banco ? View.INVISIBLE : View.VISIBLE);
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
                        edit_Perfil_Nome.setText(preferencias.getNOME());

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
