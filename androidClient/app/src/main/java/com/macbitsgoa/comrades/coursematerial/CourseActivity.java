package com.macbitsgoa.comrades.coursematerial;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;

public class CourseActivity extends AppCompatActivity
        implements View.OnClickListener, ValueEventListener {

    public static final String ADD_FILE_FRAGMENT = "AddFileFragment";
    private static final String TAG = TAG_PREFIX + CourseActivity.class.getSimpleName();
    public static String databaseUrl;
    public static String courseId;
    public static String courseName;
    private final String dbUrl =
            "https://balmy-component-204213.firebaseio.com/courseMaterial/";
    private final FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    private ArrayList<ItemCourseMaterial> materialArrayList = new ArrayList<>(0);
    private MaterialAdapter materialAdapter;
    private FloatingActionButton btnAddMaterial;
    private BroadcastReceiver broadcastReceiver;

    public static void show(final Context context, final String courseId, final String courseName) {
        final Intent intent = new Intent(context, CourseActivity.class);
        CourseActivity.courseId = courseId;
        CourseActivity.courseName = courseName;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        receiveDownloadMessage();
        initUi();

        databaseInstance
                .getReferenceFromUrl(dbUrl)
                .child(courseId).addValueEventListener(this);
        btnAddMaterial.setOnClickListener(this);
    }

    private void receiveDownloadMessage() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle resultData = intent.getExtras();
                if (resultData != null) {
                    String itemId = resultData.getString("id");
                    int resultCode = resultData.getInt("resultCode");
                    switch (resultCode) {
                        // download starting
                        case 0:
                            for (int i = 0; i < materialArrayList.size(); i++) {
                                if (Objects.equals(materialArrayList.get(i).getId(), itemId)) {
                                    materialArrayList.get(i).setDownloadStatus("Downloading");
                                    materialAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                            break;

                        //updating progress
                        case 1:
                            int progress = resultData.getInt("progress");
                            for (int i = 0; i < materialArrayList.size(); i++) {
                                if (Objects.equals(materialArrayList.get(i).getId(), itemId)) {
                                    materialArrayList.get(i).setProgress(progress);
                                    materialAdapter.notifyItemChanged(i);
                                    Log.e(TAG, "progress updated");
                                    break;
                                }
                            }
                            break;

                        //download successful
                        case 2:
                            for (int i = 0; i < materialArrayList.size(); i++) {
                                if (Objects.equals(materialArrayList.get(i).getId(), itemId)) {
                                    materialArrayList.get(i).setDownloadStatus("click to open");
                                    materialAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                            break;
                        default:
                            if (BuildConfig.DEBUG)
                                Log.e(TAG, "Downloading Failed");
                            break;
                    }
                }
            }
        };

    }

    private void initUi() {
        databaseUrl = dbUrl + courseId;
        btnAddMaterial = findViewById(R.id.fab_add_material);
        RecyclerView recyclerView = findViewById(R.id.rv_content_list);
        final Toolbar toolbar = findViewById(R.id.toolbar_course_activity);

        toolbar.inflateMenu(R.menu.course_activity_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setTitle(courseName);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        final LinearLayoutManager linearLayoutManager;
        linearLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        materialAdapter = new MaterialAdapter(materialArrayList);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(materialAdapter);
    }

    @Override
    public void onClick(final View view) {
        final boolean signedIn = GoogleSignIn.getLastSignedInAccount(this) != null;
        boolean storagePermission = true;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            storagePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }

        if (signedIn && storagePermission) {
            showFragment();
        } else if (signedIn) {
            Snackbar.make(btnAddMaterial,
                    getString(R.string.storage_permission_needed), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.allow), v ->
                            handleSignInAndStorage())
                    .show();
        } else {
            Snackbar.make(btnAddMaterial,
                    getString(R.string.login_to_add_file), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.login), v ->
                            handleSignInAndStorage())
                    .show();
        }
    }

    private void showFragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        final DialogFragment uploadFileFragment = new UploadFileFragment();
        uploadFileFragment.show(ft, ADD_FILE_FRAGMENT);
        ft.addToBackStack(ADD_FILE_FRAGMENT);
    }

    private void handleSignInAndStorage() {
        final Intent signInIntent =
                new Intent(CourseActivity.this, GetGoogleSignInActivity.class);
        startActivity(signInIntent);
    }

    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        materialArrayList.clear();
        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            materialArrayList.add(snapshot.getValue(ItemCourseMaterial.class));
        }
        materialAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(final DatabaseError databaseError) {
        Log.e(TAG, databaseError.getCode() + databaseError.getMessage());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onResume");
        }
        // Register broadcast receiver
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(DownloadService.ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "onPause");
        }
        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

}

