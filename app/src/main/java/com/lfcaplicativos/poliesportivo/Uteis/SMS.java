package com.lfcaplicativos.poliesportivo.Uteis;

import android.telephony.SmsManager;

/**
 * Created by Lucas on 05/06/2017.
 * Classe utilizada para manipulacao de sms
 */

public class SMS {
    public static boolean envioSMS(String Telefone, String Mensagem) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Telefone, null, Mensagem, null, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
