package com.macbitsgoa.comrades.csa;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.macbitsgoa.comrades.R;

import org.json.JSONObject;

import java.util.Random;

import static com.macbitsgoa.comrades.FirebaseKeysKt.FCM_KEY_TYPE;
import static com.macbitsgoa.comrades.FirebaseKeysKt.FCM_TYPE_CSA_NOTIFS;
import static com.macbitsgoa.comrades.NotificationChannelMetaData.CSA_UPDATES;

/**
 * @author Omkar Kanade.
 */
public class CsaFcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        if (FCM_TYPE_CSA_NOTIFS.equals(remoteMessage.getData().get(FCM_KEY_TYPE))) {

            final CsaNews news = new Gson().fromJson(new JSONObject(remoteMessage.getData()).toString(), CsaNews.class);

            Notification.Builder builder = buildNotification(news);
            ((NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(new Random().nextInt(), builder.build());
        }
    }

    public Notification.Builder buildNotification(CsaNews news) {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(getApplicationContext(), CSA_UPDATES.getId());
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        return builder
                .setContentTitle(news.name + ", " + news.post)
                .setContentText(news.title)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp);
    }
}
