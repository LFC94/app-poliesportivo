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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lfcaplicativos.poliesportivo.Uteis.Permissao;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static com.lfcaplicativos.poliesportivo.Uteis.Validacao.validateDDDNumber;
import static com.lfcaplicativos.poliesportivo.Uteis.Validacao.validatePhoneNumber;

public class Login extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private static final String TAG = "PhoneAuthActivity";

    FirebaseAuth mAuth;
    FirebaseUser user;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ImageView imageLogo;
    private EditText editCodArea, editTelefone;
    View viewProgress, viewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewProgress = findViewById(R.id.Progress_Login);
        viewLayout = findViewById(R.id.Layout_Login_Scroll);
        imageLogo = (ImageView) findViewById(R.id.image_Login_Logo);
        editCodArea = (EditText) findViewById(R.id.edit_Login_CodArea);
        editTelefone = (EditText) findViewById(R.id.edit_Login_Telefone);

        SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("NN");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");

        MaskTextWatcher maskCodArea = new MaskTextWatcher(editCodArea, simpleMaskCodArea);
        MaskTextWatcher maskTelefone = new MaskTextWatcher(editTelefone, simpleMaskTelefone);

        editCodArea.addTextChangedListener(maskCodArea);
        editTelefone.addTextChangedListener(maskTelefone);

        editCodArea.setOnKeyListener(this);
        editTelefone.setOnKeyListener(this);

        showProgress(true, viewProgress, viewLayout);
        new DownloadImage(imageLogo, R.drawable.logo).execute("http://lfcsistemas.esy.es/poliesportivo/LFC1.png");

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId + " Token: " + token);
            }
        };
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

            switch (v.getId()) {
                case R.id.edit_Login_CodArea:
                    if (validateDDDNumber(editCodArea, getString(R.string.ddd_invalid)))
                        editTelefone.requestFocus();

                    break;
                case R.id.edit_Login_Telefone:
                    onClick(findViewById(R.id.button_Login_Login));
                    break;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Login_Login:
                if (validatePhoneNumber(editCodArea, getString(R.string.ddd_invalid)) && validatePhoneNumber(editTelefone, getString(R.string.phone_invalid))) {
                    String telefone = "+55" + editCodArea.getText().toString() + editTelefone.getText().toString().replace("-", "");
                    startPhoneNumberVerification(telefone);
                }

                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, final View mProgressView, final View mLoginFormView) {

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
            showProgress(false, viewProgress, viewLayout);
            bmImage.setImageBitmap(result);
            if (result == null) {
                bmImage.setImageResource(imagem);
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            user = task.getResult().getUser();

                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            }
                        }
                    }
                });
    }

    private void startPhoneNumberVerification(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


    }

}
