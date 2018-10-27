package com.macbitsgoa.comrades;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;
import com.macbitsgoa.comrades.csa.CsaNews;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.macbitsgoa.comrades.FirebaseKeysKt.FCM_KEY_TYPE;
import static com.macbitsgoa.comrades.FirebaseKeysKt.FCM_TYPE_CSA_NOTIFS;
import static com.macbitsgoa.comrades.FirebaseKeysKt.FCM_TYPE_MATERIAL_ADDED;
import static com.macbitsgoa.comrades.NotificationChannelMetaData.CSA_UPDATES;
import static com.macbitsgoa.comrades.coursematerial.CourseActivity.KEY_COURSE_ID;
import static com.macbitsgoa.comrades.coursematerial.CourseActivity.KEY_COURSE_NAME;

/**
 * Listens to firebase messages.
 *
 * @author Rushikesh Jogdand.
 * @author Omkar Kanade.
 */
public class FcmService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage message) {
        switch (message.getData().get(FCM_KEY_TYPE)) {
            case FCM_TYPE_MATERIAL_ADDED: {
                handleMaterialAdded(message.getData());
            }
            break;
            case FCM_TYPE_CSA_NOTIFS: {
                final CsaNews news = new Gson().fromJson(new JSONObject(message.getData()).toString(), CsaNews.class);

                Notification.Builder builder = buildNotification(news);
                ((NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(new Random().nextInt(), builder.build());
            }
            break;
        }
    }

    private void handleMaterialAdded(Map<String, String> data) {
        if (!TextUtils.equals(FirebaseAuth.getInstance().getUid(), data.get("addedById"))) {
            final Intent openCourseIntent = new Intent(this, CourseActivity.class);
            openCourseIntent.putExtra(KEY_COURSE_ID, data.get("courseId"));
            openCourseIntent.putExtra(KEY_COURSE_NAME, data.get("courseName"));
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openCourseIntent, 0);
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NotificationChannelMetaData.COURSE_UPDATES.getId())
                    .setSmallIcon(R.drawable.ic_file)
                    .setContentTitle(data.get("courseName"))
                    .setContentText(data.get("msg"))
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.ic_open_in_new_black_24dp, "Open Course",
                            pendingIntent);
            if (Build.VERSION.SDK_INT >= 23) {
                mBuilder.setColor(getColor(R.color.colorPrimary));
            } else {
                mBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            Notification notification = mBuilder.build();
            notificationManager.notify(0, notification);
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
