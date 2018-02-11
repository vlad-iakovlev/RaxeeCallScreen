package ru.raxee.call_screen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

class Call {
    @SuppressLint("StaticFieldLeak")
    private static Call instance = null;

    private final Context context;

    private Call() {
        context = App.getContext();
    }

    static Call getInstance() {
        if (instance == null) {
            instance = new Call();
        }

        return instance;
    }

    void answer() {
        try {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            assert telecomManager != null;

            telecomManager.getClass().getMethod("acceptRingingCall").invoke(telecomManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void dismiss() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            assert telephonyManager != null;

            telephonyManager.getClass().getMethod("endCall").invoke(telephonyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
