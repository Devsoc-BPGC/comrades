package com.macbitsgoa.comrades;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHC.BITS_EMAIL_SUFFIX;
import static com.macbitsgoa.comrades.CHC.TAG_PREFIX;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = TAG_PREFIX + MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 251;
    private static boolean wantsToAddCourse = false;
    final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .build();
    private CoordinatorLayout rootCl;
    private GoogleSignInClient gsiClient;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gsiClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.fab_add_course).setOnClickListener(view -> handleAddCourse());
        rootCl = findViewById(R.id.cl_main_activity);
        setSupportActionBar(findViewById(R.id.toolbar_main_act));
        final RecyclerView coursesRv = findViewById(R.id.rv_course_list);
        coursesRv.setLayoutManager(new LinearLayoutManager(this));
        coursesRv.setAdapter(new CourseAdapter());
    }

    private void handleAddCourse() {
        final boolean canAddCourse = GoogleSignIn.getLastSignedInAccount(this) != null;
        wantsToAddCourse = true;
        if (canAddCourse) {
            (new AddCourseFragment()).show(getSupportFragmentManager(), "AddCourseFragment");
            wantsToAddCourse = false;
        } else {
            Snackbar.make(rootCl, getString(R.string.login_to_add_course), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.login), view ->
                            launchDefaultSignIn())
                    .show();
        }
    }

    private void launchDefaultSignIn() {
        final Intent signInIntent = gsiClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_toolbar, menu);
        final boolean signedIn = GoogleSignIn.getLastSignedInAccount(this) != null;
        final MenuItem signOut = menu.findItem(R.id.action_sign_out);
        signOut.setVisible(signedIn);
        signOut.setOnMenuItemClickListener(menuItem -> {
            gsiClient.signOut().addOnCompleteListener(task -> invalidateOptionsMenu());
            return true;
        });
        return true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            validateGsiResult(data);
        }
    }

    private void validateGsiResult(final Intent data) {
        final Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            final GoogleSignInAccount account = task.getResult(ApiException.class);
            invalidateOptionsMenu();
            if (!account.getEmail().endsWith(BITS_EMAIL_SUFFIX)) {
                gsiClient.signOut()
                        .addOnCompleteListener(voidTask -> {
                            invalidateOptionsMenu();
                            Snackbar.make(rootCl, getString(R.string.bits_only_email_allowed), Snackbar.LENGTH_SHORT)
                                    .setAction(getString(R.string.login), view ->
                                            launchDefaultSignIn())
                                    .show();
                        });
            } else if (wantsToAddCourse) {
                wantsToAddCourse = false;
                handleAddCourse();
            }
        } catch (final ApiException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            Snackbar.make(rootCl, getString(R.string.sign_in_fails), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.retry), view -> launchDefaultSignIn())
                    .show();
        }
    }
}
