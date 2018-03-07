package com.lfcaplicativos.poliesportivo.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.lfcaplicativos.poliesportivo.Config.ConfiguracaoFirebase;
import com.lfcaplicativos.poliesportivo.Objetos.Cidade;
import com.lfcaplicativos.poliesportivo.Objetos.Estado;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.ConexaoHTTP;
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Usuario extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public MaterialEditText edit_Usuario_Nome;
    public ImageView image_Usuario_Foto;
    public Bitmap bitmapFotoPerfil = null;
    private MaterialSpinner spinner_Usuario_Estado, spinner_Usuario_Cidade;
    private SignInButton button_Usuario_SingInGoogle;
    private Button button_Usuario_DisconnectGoogle;

    private JSONObject jsonobject;
    private JSONArray jsonarray;
    private ProgressDialog mProgressDialog;
    private Timer timer;

    private Preferencias preferencias;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private boolean acesso_banco = true, prim_uf = true, prim_cid = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());
        setSupportActionBar(toolbar);

        preferencias = new Preferencias(this);
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        mUser = mAuth.getCurrentUser();

        edit_Usuario_Nome = findViewById(R.id.edit_Usuario_Nome);
        spinner_Usuario_Estado = findViewById(R.id.spinner_Usuario_Estado);
        spinner_Usuario_Cidade = findViewById(R.id.spinner_Usuario_Cidade);
        image_Usuario_Foto = findViewById(R.id.image_Usuario_Foto);

        button_Usuario_SingInGoogle = findViewById(R.id.button_Usuario_SingInGoogle);

        button_Usuario_DisconnectGoogle = findViewById(R.id.button_Usuario_DisconnectGoogle);
        button_Usuario_SingInGoogle.setOnClickListener(this);

        if (preferencias.getBPreferencias(Chaves.CHAVE_AUTENTC_GOOGLE)) {
            button_Usuario_SingInGoogle.setVisibility(View.INVISIBLE);
        } else {
            button_Usuario_DisconnectGoogle.setVisibility(View.INVISIBLE);
        }

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
                    carregarCidade(Chaves.estados_usuario.get(position).getSigla());
                } else {
                    spinner_Usuario_Estado.setError(R.string.notstate);
                    spinner_Usuario_Estado.requestFocus();
                    if (Chaves.cidadelist_usuario == null)
                        return;

                    Chaves.cidadelist_usuario.clear();
                    spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            Chaves.cidadelist_usuario));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        buscarDadosFirebase();

        imagemPerfilUsuario(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onBackPressed() {
        if (!gravarUsuario())
            return;

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_usuario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.item_usuario_confirmar:
                if (gravarUsuario())
                    finish();
                break;
            case R.id.item_usuario_importeGoogle:
                impotarDadosGoogle();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        try {
            menu.findItem(R.id.item_usuario_importeGoogle).setVisible(preferencias.getBPreferencias(Chaves.CHAVE_AUTENTC_GOOGLE));

        } catch (Exception e) {
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab_Usuario_Foto:
                abrieCameraGaleria();
                break;
            case R.id.button_Usuario_SingInGoogle:
                signIn();
                break;
            case R.id.button_Usuario_DisconnectGoogle:
                mAuth.signOut();
                // Google revoke access
                Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {

                            }
                        });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Chaves.CHAVE_RESULT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
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
                break;
            case Chaves.CHAVE_RESULT_GOOGLE:
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void abrieCameraGaleria() {

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.selectPhoto));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        startActivityForResult(chooserIntent, Chaves.CHAVE_RESULT_PHOTO);
    }

    private void carregarEstado() {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.state) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chaves.estados_usuario = new ArrayList<Estado>();
                    Chaves.estadolist_usuario = new ArrayList<String>();
                    String sJson = ConexaoHTTP.getJSONFromAPI(preferencias.getSPreferencias(Chaves.CHAVE_URL_ESTADO));
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            falhaCarregarDados();
                        }
                    });
                }
                mProgressDialog.cancel();
            }
        }).start();
    }

    private void carregarCidade(final String UFEstado) {

        if (UFEstado.trim().isEmpty()) {
            spinner_Usuario_Estado.setError(R.string.notstate);
            spinner_Usuario_Estado.requestFocus();
            Chaves.cidadelist_usuario.clear();
            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    Chaves.cidadelist_usuario));
            return;
        }

        mProgressDialog = ProgressDialog.show(this, getString(R.string.loading), getString(R.string.loading) + " " + getString(R.string.city) + "...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Chaves.cidades_usuario = new ArrayList<Cidade>();
                    Chaves.cidadelist_usuario = new ArrayList<String>();
                    String url = preferencias.getSPreferencias(Chaves.CHAVE_URL_CIDADE);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
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
                } catch (Exception ignored) {

                }
                mProgressDialog.cancel();
            }
        }).start();

    }

    private void imagemPerfilUsuario(boolean Salvar) {

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

    private void falhaCarregarDados() {
        acesso_banco = false;

        if (preferencias.getNOME() != null) {
            edit_Usuario_Nome.setText(preferencias.getNOME());

            Chaves.estadolist_usuario.clear();
            if (preferencias.getESTADO() != null && !preferencias.getESTADO().trim().isEmpty()) {
                Chaves.estadolist_usuario.add(preferencias.getESTADO());
                spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.estadolist_usuario));

                spinner_Usuario_Estado.setSelection(1);
            } else {
                spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.estadolist_usuario));

                spinner_Usuario_Estado.setSelection(0);
            }
            Chaves.cidadelist_usuario.clear();
            if (preferencias.getCIDADE() != null && !preferencias.getCIDADE().trim().isEmpty()) {
                Chaves.cidadelist_usuario.clear();
                Chaves.cidadelist_usuario.add(preferencias.getCIDADE());
                spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.cidadelist_usuario));

                spinner_Usuario_Cidade.setSelection(1);
            } else {
                spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        Chaves.cidadelist_usuario));

                spinner_Usuario_Cidade.setSelection(0);
            }
        }
        spinner_Usuario_Estado.setEnabled(acesso_banco);
        spinner_Usuario_Cidade.setEnabled(acesso_banco);

        try {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    new Thread(new Runnable() {
                        public void run() {
                            if (ConexaoHTTP.verificaConexao(Usuario.this)) {
                                buscarDadosFirebase();
                                timer.cancel();
                            }
                        }
                    }).start();
                }
            }, 0, 1000);
        } catch (Exception ignored) {

        }

    }

    private void buscarDadosFirebase() {
        acesso_banco = true;
        spinner_Usuario_Estado.setEnabled(acesso_banco);
        spinner_Usuario_Cidade.setEnabled(acesso_banco);

        DatabaseReference referenciaConfiguracao = ConfiguracaoFirebase.getFirebaseDatabase().child(Chaves.CHAVE_CONFIGURACAO);

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
                        carregarEstado();
                    } else {
                        spinner_Usuario_Estado.setAdapter(new ArrayAdapter<>(Usuario.this,
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
                            spinner_Usuario_Cidade.setAdapter(new ArrayAdapter<>(Usuario.this,
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
                    falhaCarregarDados();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ERRO", "DatabaseError:" + databaseError.getMessage());
                falhaCarregarDados();
            }
        });

    }

    private boolean gravarUsuario() {
        if (edit_Usuario_Nome.getText().toString().trim().isEmpty()) {
            edit_Usuario_Nome.setError(getString(R.string.notName));
            edit_Usuario_Nome.requestFocus();
            return false;
        }
        if (spinner_Usuario_Estado.getSelectedItemPosition() <= 0) {
            spinner_Usuario_Estado.setError(R.string.notstate);
            spinner_Usuario_Estado.requestFocus();
            return false;
        }

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

        imagemPerfilUsuario(true);

        DatabaseReference referenciaFire = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFire.child(Chaves.CHAVE_USUARIO).child(preferencias.getSPreferencias(Chaves.CHAVE_ID)).setValue(preferencias.retornaUsuarioPreferencias(false));

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(preferencias.getNOME()).build();


        FirebaseUser mUser = mAuth.getCurrentUser();
        mUser.updateProfile(profileUpdates);
        return true;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Chaves.CHAVE_RESULT_GOOGLE);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    mUser = mAuth.getCurrentUser();
                    impotarDadosGoogle();

                } else {

                }

            }
        });
    }

    private void impotarDadosGoogle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Usuario.this);
        builder.setTitle(getString(R.string.google));
        builder.setMessage(getString(R.string.message_dataGoogle));
        builder.setIcon(R.drawable.googleg_standard_color_18);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mUser.getProviderData().get(Chaves.CHAVE_INDEX_GOOGLE).getDisplayName().trim().isEmpty())
                    edit_Usuario_Nome.setText(mUser.getProviderData().get(Chaves.CHAVE_INDEX_GOOGLE).getDisplayName());
                if (mUser.getProviderData().get(Chaves.CHAVE_INDEX_GOOGLE).getPhotoUrl() != null) {
                    Picasso.with(getApplicationContext()).load(mUser.getProviderData().get(Chaves.CHAVE_INDEX_GOOGLE).getPhotoUrl().toString()).into(image_Usuario_Foto);
                    bitmapFotoPerfil = image_Usuario_Foto.getDrawingCache();
                }

            }
        });
        builder.setNegativeButton(R.string.no, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
