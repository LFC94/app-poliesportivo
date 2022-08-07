package com.lfcaplicativos.poliesportivo.Uteis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;

import com.lfcaplicativos.poliesportivo.R;

/**
 * Created by Lucas on 05/06/2017.
 */

@SuppressWarnings("ALL")
public class Permissao {
    public static boolean validaPermicao(Activity activity, String permissao) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), permissao) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void chamarPermicao(Activity activity, String[] permissao, int RequestCod) {
        ActivityCompat.requestPermissions(activity, permissao, RequestCod);
    }

    public static void alertaValidacaoPemissao(final Activity activity, String permissao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.permissao_negada));
        builder.setMessage(activity.getString(R.string.message_permission_denied) + permissao);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setNeutralButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
