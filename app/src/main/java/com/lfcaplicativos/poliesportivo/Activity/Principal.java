package com.lfcaplicativos.poliesportivo.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lfcaplicativos.poliesportivo.Fragment.Fragment_Principal;
import com.lfcaplicativos.poliesportivo.Fragment.Fragment_Usuario;
import com.lfcaplicativos.poliesportivo.R;
import com.lfcaplicativos.poliesportivo.Uteis.Chaves;
import com.lfcaplicativos.poliesportivo.Uteis.Permissao;

import java.io.InputStream;

public class Principal extends AppCompatActivity implements View.OnClickListener {
    public static Bitmap bitmapFotoPerfil = null;

    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private int codigoNavegation = Chaves.CHAVE_NAVEGATIN_PRINCIPAL;
    private boolean novo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Bundle args = new Bundle();
            if (args != null) {
                args.putBoolean("novo", novo);
            }

            setContentView(R.layout.activity_principal);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(getTitle());
            setSupportActionBar(toolbar);

            navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if (novo) {
                        return false;
                    }
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            codigoNavegation = Chaves.CHAVE_NAVEGATIN_PRINCIPAL;
                            chamerFragment(Fragment_Principal.newInstance(Principal.this));
                            break;
                        case R.id.navigation_usuario:
                            codigoNavegation = Chaves.CHAVE_NAVEGATIN_USUARIO;
                            chamerFragment(Fragment_Usuario.newInstance(Principal.this));
                            break;
                        default:
                            return false;
                    }

                    return true;
                }
            });

            if (novo) {
                chamerFragment(Fragment_Usuario.newInstance(this));
            } else {
                chamerFragment(Fragment_Principal.newInstance(this));
            }
        } catch (Exception e) {
            Log.e("Erro", e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
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
            if (Fragment_Usuario.edit_Usuario_Nome.getText().toString().trim().isEmpty()) {
                Fragment_Usuario.edit_Usuario_Nome.setError(getString(R.string.notName));
                Fragment_Usuario.edit_Usuario_Nome.requestFocus();
                return true;
            }
            novo = false;
            Fragment_Usuario.salvar = true;
            codigoNavegation = Chaves.CHAVE_NAVEGATIN_USUARIO;
            navigation.setSelectedItemId(R.id.navigation_home);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        prepararMenu();

        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab_Usuario_Foto:
                if (Permissao.ValidaPermicao(Principal.this, Manifest.permission.CAMERA, 1)) {
                    AbrieCameraGaleria();
                }
                break;
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
                    Fragment_Usuario.image_Usuario_Foto.setImageBitmap(bitmapFotoPerfil);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
