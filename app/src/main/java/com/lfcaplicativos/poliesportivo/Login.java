package com.lfcaplicativos.poliesportivo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
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
import com.lfcaplicativos.poliesportivo.Uteis.Preferencias;
import com.lfcaplicativos.poliesportivo.application.ConfiguracaoFirebase;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static com.lfcaplicativos.poliesportivo.Uteis.Validacao.formatacaoTelefone;
import static com.lfcaplicativos.poliesportivo.Uteis.Validacao.validateDDDNumber;
import static com.lfcaplicativos.poliesportivo.Uteis.Validacao.validatePhoneNumber;

public class Login extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    private static final String TAG = "PhoneAuthActivity";

    private boolean isUpdating, isReenviarCodigo;
    private String sVerificaId, sTelefoneVerificacao, sCodigoVerificacao;

    CountDownTimer countTimerReenvia;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    Preferencias preferencias;
    private ImageView imageLogo;
    private EditText editCodArea, editTelefone, editCodeVerifica;
    private TextView textMsg_Verifica_Fone, textReenvioCodigo;
    View viewProgress, viewLayout;
    Dialog dialogTelaVerificacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferencias = new Preferencias(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String Uid = user.getUid();

            Firebase firebase = ConfiguracaoFirebase.getFirebase();

            ChamarTelaCalendario();

        }
        viewProgress = findViewById(R.id.Progress_Login);
        viewLayout = findViewById(R.id.Layout_Login_Scroll);
        imageLogo = (ImageView) findViewById(R.id.image_Login_Logo);
        editCodArea = (EditText) findViewById(R.id.edit_Login_CodArea);
        editTelefone = (EditText) findViewById(R.id.edit_Login_Telefone);

        findViewById(R.id.button_Login_Login).setFocusable(false);
        // SimpleMaskFormatter simpleMaskCodArea = new SimpleMaskFormatter("NN");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");

        // MaskTextWatcher maskCodArea = new MaskTextWatcher(editCodArea, simpleMaskCodArea);
        MaskTextWatcher maskTelefone = new MaskTextWatcher(editTelefone, simpleMaskTelefone);
        editTelefone.addTextChangedListener(maskTelefone);
        editCodArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String current = s.toString();
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }
                String number = current.replaceAll("[^0-9]*", "");
                isUpdating = true;
                editCodArea.setText(number);
                editCodArea.setSelection(number.length());
                if (number.length() >= 2) {
                    if (validateDDDNumber(editCodArea, getString(R.string.ddd_invalid))) {
                        editCodArea.clearFocus();
                        editTelefone.requestFocus();
                    }
                }
            }
        });

        editCodArea.setOnKeyListener(this);
        editTelefone.setOnKeyListener(this);

        showProgress(true, viewProgress, viewLayout);
        new DownloadImage(imageLogo, R.drawable.logo).execute("http://lfcsistemas.esy.es/poliesportivo/LFC.png");

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
                sVerificaId = verificationId;
                mResendToken = token;
            }
        };


        if (preferencias.getCODEVERIFICACAO() != null && !preferencias.getCODEVERIFICACAO().trim().isEmpty()) {

            verifyPhoneNumberWithCode(preferencias.getIDVERIFICACAO(), preferencias.getCODEVERIFICACAO());
        }


    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
            if(keyCode == KeyEvent.KEYCODE_ENTER) {
                switch (v.getId()) {
                    case R.id.edit_Login_CodArea:
                        if (validateDDDNumber(editCodArea, getString(R.string.ddd_invalid)))
                            editTelefone.requestFocus();
                        break;
                    case R.id.edit_Login_Telefone:
                        onClick(findViewById(R.id.button_Login_Login));
                        break;
                }
            }else if(keyCode == 67){
                switch (v.getId()) {
                    case R.id.edit_Login_CodArea:
                        //
                        break;
                    case R.id.edit_Login_Telefone:
                        if (editTelefone.length()<=1){
                            editCodArea.requestFocus();
                        }
                        break;
                }

            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_Login_Login:
                if (validateDDDNumber(editCodArea, getString(R.string.ddd_invalid)) && validatePhoneNumber(editTelefone, getString(R.string.phone_invalid))) {
                    String sMensagem;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.msg_verificaremos_numero));
                    sMensagem = editCodArea.getText().toString() + " " + editTelefone.getText().toString();
                    int bFim = sMensagem.length();
                    sMensagem += "\n\n" + getString(R.string.msg_number_correto_edit);

                    SpannableString spanString = new SpannableString(sMensagem);
                    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, bFim, 0);
                    builder.setMessage(spanString);

                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sTelefoneVerificacao = "+55" + editCodArea.getText().toString() + editTelefone.getText().toString().replace("-", "");
                            startPhoneNumberVerification(sTelefoneVerificacao);
                            chamarTelaVerificacao();
                        }
                    });
                    builder.setNegativeButton(R.string.edit, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            editCodArea.requestFocus();
                        }
                    });
                    builder.create().show();
                }

                break;
            case R.id.button_Verificacao_Verifica:
                verificaCode();
                break;
            case R.id.text_Validacao_NumErrado:
                isReenviarCodigo = false;
                countTimerReenvia.cancel();
                dialogTelaVerificacao.cancel();
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            startPhoneNumberVerification(preferencias.getTELEFONE());
        }
        // [END_EXCLUDE]
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, final View mProgressView,
                              final View mLoginFormView) {

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < grantResults.length; i++) {
            int resultado = grantResults[i];
            if (resultado == PackageManager.PERMISSION_DENIED) {
                Permissao.alertaValidacaoPemissao(this, permissions[i]);
            }
        }
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            isReenviarCodigo = false;
                            countTimerReenvia.cancel();
                            preferencias.CadastraUsuarioPreferencias(null, sTelefoneVerificacao, sVerificaId, sCodigoVerificacao);
                            user = task.getResult().getUser();
                            ChamarTelaCalendario();
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

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void chamarTelaVerificacao(){
//        setContentView(R.layout.activity_validacao_telefone);

        dialogTelaVerificacao = new Dialog(this, R.style.FullTela);
        dialogTelaVerificacao.setContentView(R.layout.activity_validacao_telefone);

        String sTitulo = getString(R.string.verify) + " ";
        int iIni = sTitulo.length();
        sTitulo +=formatacaoTelefone(sTelefoneVerificacao);
        SpannableString spanString = new SpannableString(sTitulo);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), iIni, sTitulo.length(), 0);
        dialogTelaVerificacao.setTitle(spanString);

        textMsg_Verifica_Fone = (TextView) dialogTelaVerificacao.findViewById(R.id.text_Validacao_Msg_aguardando_SMS);

        String sMensagem = getString(R.string.msg_verificaremos_numero)+" ";
        iIni = sMensagem.length();
        sMensagem += formatacaoTelefone(sTelefoneVerificacao);
        SpannableString spanString2 = new SpannableString(sMensagem);
        spanString2.setSpan(new StyleSpan(Typeface.BOLD), iIni, sMensagem.length(), 0);
        textMsg_Verifica_Fone.setText(spanString2);

        isReenviarCodigo = true;
        textReenvioCodigo = (TextView) dialogTelaVerificacao.findViewById(R.id.text_Validacao_Cronometro);

        countTimerReenvia = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long l) {
                int segundos = (int) ( l / 1000 ) % 60;      // se não precisar de segundos, basta remover esta linha.
                int minutos  = (int) ( l / 60000 ) % 60;     // 60000   = 60 * 1000
                textReenvioCodigo.setText(String.format( "%02d:%02d", minutos,segundos ));


            }

            @Override
            public void onFinish() {
                if(isReenviarCodigo) {
                    resendVerificationCode(sTelefoneVerificacao, mResendToken);
                    chamarTelaVerificacao();
                }
            }
        }.start();


        editCodeVerifica = (EditText) dialogTelaVerificacao.findViewById(R.id.edit_Validacao_CodeVerif);

        editCodeVerifica.clearFocus();

        editCodeVerifica.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String code = editable.toString().trim();
                String number = code.replaceAll("[^0-9]*", "");
                isUpdating = true;
                editCodeVerifica.setText(number);
                editCodeVerifica.setSelection(number.length());
                if (number.length() == 6){
                    verificaCode();
                }
            }
        });

        dialogTelaVerificacao.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                isReenviarCodigo = false;
                countTimerReenvia.cancel();
                dialogTelaVerificacao.cancel();
            }
        });

        dialogTelaVerificacao.show();
    }

    public void verificaCode(){
       String code = editCodeVerifica.getText().toString().trim().replaceAll("[^0-9]*", "");;

        if (TextUtils.isEmpty(code)){
            editCodeVerifica.setError(getString(R.string.invalid_code));
            editCodeVerifica.requestFocus();
            return;
        }
        sCodigoVerificacao = code;
        verifyPhoneNumberWithCode(sVerificaId, sCodigoVerificacao);

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }


    private void ChamarTelaCalendario() {
        Intent intent = new Intent(Login.this, Calendario.class);
        startActivity(intent);
    }


}
