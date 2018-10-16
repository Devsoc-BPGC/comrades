package com.macbitsgoa.comrades;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.coursematerial.UploadUtil;
import com.macbitsgoa.comrades.persistance.Database;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.HomeActivity.SETTINGS;


/**
 * @author aayush
 */

public class Comrades extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private final String TAG = TAG_PREFIX + Comrades.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        UploadUtil.createNotificationChannel(this);
        FcmReceiverService.Companion.createNotificationChannel(this);
        Comrades.context = getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean coursesPresent = preferences.getBoolean("Courses Present", false);
        keepVersionUpdated();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (!coursesPresent) {
            FirebaseDatabase.getInstance().getReference(BuildConfig.BUILD_TYPE).child("courses")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                            FetchAllCourses fetchAllCourses = new FetchAllCourses();
                            fetchAllCourses.execute(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(@NotNull DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });

        }

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

    public static Context getAppContext() {
        return context;
    }


    private static class FetchAllCourses extends AsyncTask<DataSnapshot, Void, Void> {

        @Override
        protected Void doInBackground(DataSnapshot... params) {
            DataSnapshot dataSnapshot = params[0];
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                MyCourse myCourse = snapshot.getValue(MyCourse.class);
                Objects.requireNonNull(myCourse).setFollowing(false);
                Database.getInstance(getAppContext()).getCourseDao().insert(myCourse);
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getAppContext());
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(SETTINGS, true);
            edit.putBoolean("Courses Present", Boolean.TRUE);
            edit.apply();
            return null;
        }
    }
}
