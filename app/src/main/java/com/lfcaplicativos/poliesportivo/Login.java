package com.lfcaplicativos.poliesportivo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.lfcaplicativos.poliesportivo.Uteis.Permissao;

import java.io.InputStream;
public class Login extends AppCompatActivity {

    private EditText editCodArea, editName, editTelefone;
    private ImageView imageLogo;
    private View mProgressView, mLoginFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        imageLogo = (ImageView) findViewById(R.id.image_Login_Logo);
        mLoginFormView = findViewById(R.id.Layout_Login_Scroll);
        mProgressView = findViewById(R.id.Progress_Login);

        editCodArea = (EditText) findViewById(R.id.edit_Login_CodArea);
        editName = (EditText) findViewById(R.id.edit_Login_Nome);
        editTelefone = (EditText) findViewById(R.id.edit_Login_Telefone);

        SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("(NN)");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");

        MaskTextWatcher maskCodArea = new MaskTextWatcher(editCodArea, simpleMaskCodArea);
        MaskTextWatcher maskTelefone = new MaskTextWatcher(editTelefone, simpleMaskTelefone);

        editCodArea.addTextChangedListener(maskCodArea);
        editTelefone.addTextChangedListener(maskTelefone);

        showProgress(true);
        new DownloadImage(imageLogo, R.drawable.logo).execute("http://lfcsistemas.esy.es/poliesportivo/LFC.png");

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        int imagem;

        public DownloadImage(ImageView bmImage, int imagem) {
            this.bmImage = bmImage;
            this.imagem = imagem;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            showProgress(false);
            bmImage.setImageBitmap(result);
            if (result == null) {
                imageLogo.setImageResource(imagem);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < grantResults.length; i++) {
            int resultado = grantResults[i];
            if (resultado == PackageManager.PERMISSION_DENIED) {
                Permissao.alertaValidacaoPemissao(this, permissions[i]);
            }
        }
    }

}
