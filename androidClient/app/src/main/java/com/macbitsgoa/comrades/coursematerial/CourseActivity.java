package com.macbitsgoa.comrades.coursematerial;

import android.Manifest;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.persistance.Database;
import com.macbitsgoa.comrades.search.MaterialCoursesCursorAdapter;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static android.os.Environment.getExternalStorageDirectory;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.ComradesConstants.DOWNLOAD_DIRECTORY;

public class CourseActivity extends AppCompatActivity
        implements View.OnClickListener, ChildEventListener {

    public static final String ADD_FILE_FRAGMENT = "AddFileFragment";
    private static final String TAG = TAG_PREFIX + CourseActivity.class.getSimpleName();
    public static String databaseUrl;
    public static String courseId;
    public static String courseName;
    private final String dbUrl =
            "https://balmy-component-204213.firebaseio.com/" + BuildConfig.BUILD_TYPE + "/courseMaterial/";
    public static final String KEY_COURSE_ID = "courseId";
    public static final String KEY_COURSE_NAME = "courseName";
    private final FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    private ArrayList<CourseMaterial> materialArrayList = new ArrayList<>(0);
    private MaterialAdapter materialAdapter;
    private FloatingActionButton btnAddMaterial;
    private BroadcastReceiver broadcastReceiver;
    private MaterialVm materialVm;
    private SearchView searchView;

    public static void show(final Context context, final String courseId, final String courseName) {
        final Intent intent = new Intent(context, CourseActivity.class);
        intent.putExtra(KEY_COURSE_NAME, courseName);
        intent.putExtra(KEY_COURSE_ID, courseId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        if (savedInstanceState != null) {
            courseId = savedInstanceState.getString("courseId");
            courseName = savedInstanceState.getString("courseName");
        } else {
            if (getIntent().getStringExtra(KEY_COURSE_ID) != null)
                courseId = getIntent().getStringExtra(KEY_COURSE_ID);
            if (getIntent().getStringExtra(KEY_COURSE_NAME) != null)
                courseName = getIntent().getStringExtra(KEY_COURSE_NAME);
        }
        receiveDownloadMessage();
        initUi();

        materialVm = ViewModelProviders.of(this,
                new MaterialVmFactoryClass(this.getApplication(), courseId)).get(MaterialVm.class);
        materialVm.getMaterialList().observe(CourseActivity.this, courseMaterials -> {
            materialArrayList.clear();
            Log.e("TAG:1",materialArrayList.size()+"");
            materialArrayList.addAll(courseMaterials);
            materialAdapter.notifyDataSetChanged();
        });

        databaseInstance
                .getReferenceFromUrl(dbUrl)
                .child(courseId).addChildEventListener(this);
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
                                    materialArrayList.get(i).setDownloading(true);
                                    materialArrayList.get(i).setWaiting(false);
                                    materialVm.update(materialArrayList.get(i));
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
                                    break;
                                }
                            }
                            break;
                        //download successful
                        case 2:
                            for (int i = 0; i < materialArrayList.size(); i++) {
                                if (Objects.equals(materialArrayList.get(i).getId(), itemId)) {
                                    materialArrayList.get(i).setDownloading(false);
                                    materialArrayList.get(i).setWaiting(false);
                                    materialVm.update(materialArrayList.get(i));
                                    break;
                                }
                            }
                            break;

                        default:
                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "Downloading Failed");
                            }
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
        setSupportActionBar(toolbar);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.course_activity_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.e("TAG", "query:" + s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("TAG", "query:" + s);
                getMaterialFromDb(s);
                return false;
            }
        });
        return true;
    }

    private void getMaterialFromDb(String s) {
        String searchText = "%" + s + "%";
        Observable.just(searchText).observeOn(Schedulers.computation())
                .map(new Function<String, Cursor>() {
                    @Override
                    public Cursor apply(String searchStrt) {
                        return Database.getInstance(CourseActivity.this).getMaterialDao().searchMaterialCursor(courseId, searchStrt);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) {
                        handleResults(cursor);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        handleError(throwable);
                    }
                });

    }

    private void handleError(Throwable throwable) {
        Log.e("TAG", throwable.getMessage(), throwable);
        Toast.makeText(this, "Problem in Fetching Material",
                Toast.LENGTH_LONG).show();
    }

    private void handleResults(Cursor cursor) {
        searchView
                .setSuggestionsAdapter(new MaterialCoursesCursorAdapter(CourseActivity.this, cursor, searchView));
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
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        CourseMaterial courseMaterial = dataSnapshot.getValue(CourseMaterial.class);
        Log.e("TAG:2",courseMaterial.getId()+"");
        courseMaterial.setFilePath(String.format("%s/%s/%s/", getExternalStorageDirectory(),
                DOWNLOAD_DIRECTORY, courseId));
        courseMaterial.setCourseId(courseId);
        courseMaterial.setWaiting(false);
        courseMaterial.setDownloading(false);
        courseMaterial.setProgress(0);
        materialVm.insert(courseMaterial);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        CourseMaterial courseMaterial = dataSnapshot.getValue(CourseMaterial.class);
        for (int i = 0; i < materialArrayList.size(); i++) {
            if (Objects.equals(materialArrayList.get(i).getId(), courseMaterial.getId())) {
                courseMaterial.setFilePath(String.format("%s/%s/%s/", getExternalStorageDirectory(),
                        DOWNLOAD_DIRECTORY, courseId));
                courseMaterial.setCourseId(courseId);
                courseMaterial.setWaiting(materialArrayList.get(i).getWaiting());
                courseMaterial.setDownloading(materialArrayList.get(i).getDownloading());
                courseMaterial.setProgress(materialArrayList.get(i).getProgress());
                break;
            }
        }
        materialVm.update(courseMaterial);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        materialVm.delete(dataSnapshot.getValue(CourseMaterial.class));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //Empty Method
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("courseId", courseId);
        savedInstanceState.putString("courseName", courseName);
    }


}

