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
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.coursematerial.CourseMaterial;
import com.macbitsgoa.comrades.homefragment.ItemRecent;
import com.macbitsgoa.comrades.persistance.Database;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;

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
        Comrades.context = getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Boolean coursesPresent = preferences.getBoolean("Courses Present", false);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        ArrayList<CourseMaterial> arrayList = new ArrayList<>(0);

        FirebaseDatabase.getInstance().getReference().child("release").child("courseMaterial").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot sp : snapshot.getChildren()) {
                        CourseMaterial courseMaterial = sp.getValue(CourseMaterial.class);
                        arrayList.add(courseMaterial);
                    }
                }
                Log.e("vhgvjhbnkm", arrayList.size() + "");
                (new Asyc()).execute(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!coursesPresent) {
            FirebaseDatabase.getInstance().getReference(BuildConfig.BUILD_TYPE).child("courses")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FetchAllCourses fetchAllCourses = new FetchAllCourses();
                            fetchAllCourses.execute(dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });

        }

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
                myCourse.setFollowing(false);
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

    private class Asyc extends AsyncTask<ArrayList<CourseMaterial>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<CourseMaterial>... arrayLists) {
            ArrayList<CourseMaterial> arrayList = arrayLists[0];
            Log.e("vhgvjhbnkm", arrayList.size() + "");
            FirebaseDatabase.getInstance().getReference().child("release").child("recents").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemRecent itemRecent = snapshot.getValue(ItemRecent.class);
                        Log.e("vhgvjhbnkm", itemRecent.getCourseId() + " courseId");
                        if (Objects.equals(Objects.requireNonNull(itemRecent).getType(), "recent_material")) {
                            Log.e("vhgvjhbnkm", itemRecent.getFileId() + " fileId");
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (Objects.equals(arrayList.get(i).getId(), itemRecent.getFileId())) {
                                    FirebaseDatabase.getInstance().getReference().child("release").child("courseMaterial").child(itemRecent.getCourseId()).child(arrayList.get(i).getHashId()).child("timeStamp").setValue(itemRecent.getTimeStamp());
                                    Log.e("vhgvjhbnkm", arrayList.get(i).getHashId() + " hashId");
                                    break;
                                }
                            }
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return null;
        }
    }
}
