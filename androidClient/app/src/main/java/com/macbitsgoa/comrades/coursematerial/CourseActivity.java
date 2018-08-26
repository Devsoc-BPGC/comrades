package com.macbitsgoa.comrades.coursematerial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import io.reactivex.schedulers.Schedulers;

import static android.os.Environment.getExternalStorageDirectory;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.ComradesConstants.DOWNLOAD_DIRECTORY;

public class CourseActivity extends AppCompatActivity
        implements View.OnClickListener, ChildEventListener {

    public static final String ADD_FILE_FRAGMENT = "AddFileFragment";
    public static final String KEY_COURSE_ID = "courseId";
    public static final String KEY_COURSE_NAME = "courseName";
    private static final String TAG = TAG_PREFIX + CourseActivity.class.getSimpleName();
    public static String databaseUrl;
    public static String courseId;
    public static String courseName;
    private final String dbUrl =
            "https://balmy-component-204213.firebaseio.com/" + BuildConfig.BUILD_TYPE + "/courseMaterial/";
    private final FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    private final ArrayList<CourseMaterial> materialArrayList = new ArrayList<>(0);
    private MaterialAdapter materialAdapter;
    private FloatingActionButton btnAddMaterial;
    private BroadcastReceiver broadcastReceiver;
    private MaterialVm materialVm;
    private SearchView searchView;
    private ProgressBar progressBar;
    private int currentSortOrder = 0;

    public static void launchCourse(final Context context, final String courseId, final String courseName) {
        final Intent intent = new Intent(context, CourseActivity.class);
        intent.putExtra(KEY_COURSE_NAME, courseName);
        intent.putExtra(KEY_COURSE_ID, courseId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        if (savedInstanceState != null) {
            courseId = savedInstanceState.getString("courseId");
            courseName = savedInstanceState.getString("courseName");
        } else {
            if (getIntent().getStringExtra(KEY_COURSE_ID) != null) {
                courseId = getIntent().getStringExtra(KEY_COURSE_ID);
            }
            if (getIntent().getStringExtra(KEY_COURSE_NAME) != null) {
                courseName = getIntent().getStringExtra(KEY_COURSE_NAME);
            }
        }
        initBroadCastReceiver();
        initUi();

        materialVm = ViewModelProviders.of(this).get(MaterialVm.class);
        materialVm.getMaterialListByName(courseId).observe(CourseActivity.this, courseMaterials -> {
            materialArrayList.clear();
            materialArrayList.addAll(courseMaterials);
            materialAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        });
        materialVm.getMaterialCount(courseId).observe(this,
                count -> ((TextView) findViewById(R.id.tv_file_count)).setText(count + " files"));

        databaseInstance
                .getReferenceFromUrl(dbUrl)
                .child(courseId).addChildEventListener(this);
        btnAddMaterial.setOnClickListener(this);
    }

    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                final Bundle resultData = intent.getExtras();
                if (resultData != null) {
                    final String itemId = resultData.getString("id");
                    if (itemId == null) {
                        Log.e(TAG, "itemId was found null. Returning", new Throwable("Trace").fillInStackTrace());
                        return;
                    }
                    final int resultCode = resultData.getInt("resultCode");
                    final int index = CourseMaterial.findIndex(materialArrayList, itemId);
                    if (index < 0) {
                        Log.e(TAG, "material was null.", new Throwable("Trace").fillInStackTrace());
                        return;
                    }
                    switch (resultCode) {

                        // download starting
                        case 0: {
                            materialArrayList.get(index).isDownloading = true;
                            materialArrayList.get(index).isWaiting = false;
                            materialVm.update(materialArrayList.get(index));
                        }
                        break;

                        //updating progress
                        case 1: {
                            materialArrayList.get(index).progress = resultData.getInt("progress");
                            materialAdapter.notifyItemChanged(index);
                        }
                        break;

                        // download successful
                        case 2: {
                            materialArrayList.get(index).isDownloading = false;
                            materialArrayList.get(index).isWaiting = false;
                            materialVm.update(materialArrayList.get(index));
                        }
                        break;

                        default: {
                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "Downloading Failed", new Throwable("Trace").fillInStackTrace());
                            }
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
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        final RecyclerView recyclerView = findViewById(R.id.rv_content_list);
        findViewById(R.id.ib_sort).setOnClickListener(v -> handleSort());
        final Toolbar toolbar = findViewById(R.id.toolbar_course_activity);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.course_activity_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        final androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(courseName);
        }
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        final LinearLayoutManager linearLayoutManager;
        linearLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        materialAdapter = new MaterialAdapter(materialArrayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(materialAdapter);
    }

    private void handleSort() {
        final CharSequence[] sortOrders = new CharSequence[]{
                "File Name",
                "File Size",
                "File Type"
        };
        new AlertDialog.Builder(this)
                .setTitle("Sort By")
                .setSingleChoiceItems(sortOrders, currentSortOrder, (dialog, which) -> {
                    if (which == 0) {
                        currentSortOrder = 0;
                        materialVm.getMaterialListByName(courseId).observe(this, courses -> {
                            materialArrayList.clear();
                            materialArrayList.addAll(courses);
                            materialAdapter.notifyDataSetChanged();
                        });
                    } else if (which == 1) {
                        currentSortOrder = 1;
                        materialVm.getMaterialListBySize(courseId).observe(this, materials -> {
                            materialArrayList.clear();
                            materialArrayList.addAll(materials);
                            materialAdapter.notifyDataSetChanged();
                        });
                    } else {
                        currentSortOrder = 2;
                        materialVm.getMaterialListByFileType(courseId).observe(this, materials -> {
                            materialArrayList.clear();
                            materialArrayList.addAll(materials);
                            materialAdapter.notifyDataSetChanged();
                        });
                    }
                    dialog.dismiss();
                }).show();
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("courseId", courseId);
        savedInstanceState.putString("courseName", courseName);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.course_activity_toolbar, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menuItem.getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@NonNull final String s) {
                Log.e(TAG, "query:" + s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(@NonNull final String s) {
                Log.e(TAG, "query:" + s);
                getMaterialFromDb(s);
                return false;
            }
        });
        return true;
    }

    @SuppressLint("CheckResult")
    private void getMaterialFromDb(final String s) {
        final String searchText = "%" + s + "%";
        Observable.just(searchText).observeOn(Schedulers.computation())
                .map(searchStart -> Database.getInstance(CourseActivity.this).getMaterialDao().searchMaterialCursor(courseId, searchStart)).observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults, this::handleError);
    }

    private void handleError(final Throwable throwable) {
        Log.e("TAG", throwable.getMessage(), throwable);
        Toast.makeText(this, "Problem in Fetching Material",
                Toast.LENGTH_LONG).show();
    }

    private void handleResults(final Cursor cursor) {
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
    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, final String s) {
        final CourseMaterial courseMaterial = dataSnapshot.getValue(CourseMaterial.class);
        if (courseMaterial == null) {
            Log.e(TAG, "null course material", new Throwable("Trace").fillInStackTrace());
            return;
        }
        courseMaterial.filePath = String.format("%s/%s/%s/", getExternalStorageDirectory(),
                DOWNLOAD_DIRECTORY, courseId);
        courseMaterial.courseId = courseId;
        courseMaterial.isWaiting = false;
        courseMaterial.isDownloading = false;
        courseMaterial.progress = 0;
        materialVm.insert(courseMaterial);
    }

    @Override
    public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, final String s) {
        final CourseMaterial courseMaterial = dataSnapshot.getValue(CourseMaterial.class);
        if (courseMaterial == null) {
            Log.e(TAG, "null course material", new Throwable("Trace").fillInStackTrace());
            return;
        }
        final int index = CourseMaterial.findIndex(materialArrayList, courseMaterial._id);
        if (index < 0) {
            Log.e(TAG, "material was null.", new Throwable("Trace").fillInStackTrace());
            return;
        }
        courseMaterial.filePath = String.format("%s/%s/%s/", getExternalStorageDirectory(),
                DOWNLOAD_DIRECTORY, courseId);
        courseMaterial.setCourseId(courseId);
        courseMaterial.isWaiting = materialArrayList.get(index).isWaiting;
        courseMaterial.isDownloading = materialArrayList.get(index).isDownloading;
        courseMaterial.progress = materialArrayList.get(index).progress;
        materialVm.update(courseMaterial);
    }

    @Override
    public void onChildRemoved(@NonNull final DataSnapshot dataSnapshot) {
        materialVm.delete(dataSnapshot.getValue(CourseMaterial.class));
    }

    @Override
    public void onChildMoved(@NonNull final DataSnapshot dataSnapshot, final String s) {
        //Empty Method
    }

    @Override
    public void onCancelled(@NonNull final DatabaseError databaseError) {
        Log.e(TAG, databaseError.getCode() + databaseError.getMessage(), databaseError.toException());
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register broadcast receiver
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(DownloadService.ACTION));
    }

}

