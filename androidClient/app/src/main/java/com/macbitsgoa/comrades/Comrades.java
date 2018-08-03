package com.macbitsgoa.comrades;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * @author aayush
 */

public class Comrades extends Application {

    private Boolean receiveNotification = true;


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }


    public Boolean getReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(Boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }
}
