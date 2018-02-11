package ru.raxee.call_screen;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction())) {
            RingingWindow ringingWindow = RingingWindow.getInstance();

            try {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                    processRinging(context, intent);
                }
                if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                    ringingWindow.hide();
                }
                if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                    ringingWindow.hide();
                }
            } catch (Exception e) {
                ringingWindow.hide();
                e.printStackTrace();
            }
        }
    }

    private void processRinging(final Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert powerManager != null;

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        assert keyguardManager != null;

        if (!powerManager.isInteractive() || keyguardManager.inKeyguardRestrictedInputMode()) {
            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Contact contact = new Contact(phoneNumber);

            RingingWindow ringingWindow = RingingWindow.getInstance();
            ringingWindow.setData(contact);
            ringingWindow.show();
        }
    }
}
