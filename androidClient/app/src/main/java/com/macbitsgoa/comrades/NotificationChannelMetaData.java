package com.macbitsgoa.comrades;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

/**
 * @author Rushikesh Jogdand.
 */
public enum NotificationChannelMetaData {
    COURSE_UPDATES("comrades.course.material.addition", "Course Material Updates", "Updates of new files added to courses you follow"),
    UPLOAD_PROGRESS("comrades.uploads.progress", "Uploads Progress", "progress of ongoing uploads"),
    CSA_UPDATES("comrades.csa", "CSA Announcements", "messages from CSA and administrators");
    String id;
    String name;
    String description;

    NotificationChannelMetaData(final String id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }


    /**
     * For Oreo and next devices, we need to register the channel with system.
     * This method is called from {@link Comrades} to do that.
     * @param application {@link Comrades}
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannel(Application application) {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(description);
        NotificationManager manager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);
    }
}
