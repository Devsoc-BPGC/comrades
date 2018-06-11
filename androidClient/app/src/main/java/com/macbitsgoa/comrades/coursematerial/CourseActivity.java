package com.macbitsgoa.comrades.coursematerial;

import android.Manifest;
import android.content.Intent;
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
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHC.TAG_PREFIX;

public class CourseActivity extends AppCompatActivity
        implements View.OnClickListener, ValueEventListener {

    public static String databaseUrl;
    public static final String ADD_FILE_FRAGMENT = "AddFileFragment";
    private final FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
    private static final String TAG = TAG_PREFIX + CourseActivity.class.getSimpleName();
    public static final String EXTRA_COURSE_ID = "courseId";
    public static final String EXTRA_COURSE_NAME = "courseName";
    private final ArrayList<ItemCourseMaterial> materialArrayList = new ArrayList<>(0);
    public static String courseId;
    private MaterialAdapter materialAdapter;
    private FloatingActionButton btnAddMaterial;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initUi();

        databaseInstance
                .getReferenceFromUrl(getResources().getString(R.string.DATABASE_URL_MATERIAL))
                .child(courseId).addValueEventListener(this);
        btnAddMaterial.setOnClickListener(this);

    }

    private void initUi() {
        courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        databaseUrl = getString(R.string.DATABASE_URL_MATERIAL) + courseId;
        btnAddMaterial = findViewById(R.id.fab_add_material);
        final String courseName = getIntent().getStringExtra(EXTRA_COURSE_NAME);
        final RecyclerView recyclerView = findViewById(R.id.rv_content_list);
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

    private void handleSignInAndStorage() {
        final Intent signInIntent =
                new Intent(CourseActivity.this, GetGoogleSignInActivity.class);
        startActivity(signInIntent);
    }

}

