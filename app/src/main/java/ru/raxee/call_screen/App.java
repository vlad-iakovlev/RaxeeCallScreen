package ru.raxee.call_screen;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        context.setTheme(R.style.AppTheme);
    }

    public static Context getContext() {
        return context;
    }
}
