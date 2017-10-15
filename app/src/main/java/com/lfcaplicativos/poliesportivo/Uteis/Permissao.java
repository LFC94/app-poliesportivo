package com.lfcaplicativos.poliesportivo.Uteis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.lfcaplicativos.poliesportivo.R;

/**
 * Created by Lucas on 05/06/2017.
 */

public class Permissao {
    public static boolean ValidaPermicao(Activity activity, String permissao, int RequestCod) {
        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), permissao) != PackageManager.PERMISSION_GRANTED) {
                String[] aPermissao = new String[]{permissao};
                ActivityCompat.requestPermissions(activity, aPermissao, RequestCod);
            }

        }
        return true;
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
