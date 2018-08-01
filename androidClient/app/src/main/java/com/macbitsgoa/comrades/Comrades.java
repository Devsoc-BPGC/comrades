package com.macbitsgoa.comrades;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * @author aayush
 */

public class Comrades extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
