package com.macbitsgoa.comrades;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.persistance.Database;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import androidx.annotation.NonNull;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.HomeActivity.SETTINGS;


/**
 * @author aayush
 */

public class Comrades extends Application {

    private final String TAG = TAG_PREFIX + Comrades.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Stream.of(NotificationChannelMetaData.values()).forEach(c -> c.createChannel(this));
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean coursesPresent = preferences.getBoolean("Courses Present", false);
        keepVersionUpdated();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (!coursesPresent) {
            FirebaseDatabase.getInstance().getReference(BuildConfig.BUILD_TYPE).child("courses")
                    .addListenerForSingleValueEvent(new FbListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                            FetchAllCourses fetchAllCourses = new FetchAllCourses(Database.getInstance(getApplicationContext()), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
                            fetchAllCourses.execute(dataSnapshot);
                        }
                    });

        }

        FirebaseMessaging.getInstance().subscribeToTopic("CsaNotifications")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.e("error msg","Error in subscription");
                        }

                    }
                });

    }

    private void keepVersionUpdated() {
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap<>();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_NOTIFY, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.macbitsgoa.comrades");

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "remote config is fetched.");
                        firebaseRemoteConfig.activateFetched();
                    }
                });
    }

    private static class FetchAllCourses extends AsyncTask<DataSnapshot, Void, Void> {

        private Database database;
        private SharedPreferences sharedPreferences;

        private FetchAllCourses(final Database database, final SharedPreferences sharedPreferences) {
            this.database = database;
            this.sharedPreferences = sharedPreferences;
        }

        @Override
        protected Void doInBackground(DataSnapshot... params) {
            DataSnapshot dataSnapshot = params[0];
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                MyCourse myCourse = snapshot.getValue(MyCourse.class);
                Objects.requireNonNull(myCourse).setFollowing(false);
                database.getCourseDao().insert(myCourse);
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putBoolean(SETTINGS, true);
            edit.putBoolean("Courses Present", Boolean.TRUE);
            edit.apply();
            return null;
        }
    }
}
