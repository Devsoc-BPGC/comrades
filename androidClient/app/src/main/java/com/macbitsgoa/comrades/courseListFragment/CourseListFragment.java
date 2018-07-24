package com.macbitsgoa.comrades.courseListFragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.snackbar.Snackbar;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.courseListFragment.AddCourseActivityKt.launchCourseChooser;

public class CourseListFragment extends Fragment {

    private final CourseAdapter courseAdapter = new CourseAdapter();
    private CoordinatorLayout rootCl;

    public static Fragment newInstance() {
        return new CourseListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        view.findViewById(R.id.fab_add_course).setOnClickListener(v -> handleAddCourse());
        rootCl = view.findViewById(R.id.cl_main_activity);
        final RecyclerView coursesRv = view.findViewById(R.id.rv_course_list);
        coursesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        coursesRv.setAdapter(courseAdapter);
        CourseListVm viewModel = ViewModelProviders.of(this).get(CourseListVm.class);
        viewModel.getCourseList().observe(this, courseAdapter::setCourses);
        return view;
    }


    private void handleAddCourse() {
        final boolean signedIn = GoogleSignIn.getLastSignedInAccount(getContext()) != null;
        boolean storagePermission = true;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            storagePermission =
                    getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED;
        }
        if (signedIn && storagePermission) {
            launchCourseChooser(this);

        } else if (signedIn) {
            Snackbar.make(rootCl, getString(R.string.storage_permission_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.allow), v ->
                            handleSignInAndStorage(getContext()))
                    .show();
        } else {
            Snackbar.make(rootCl, getString(R.string.login_to_download_file),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.login), v ->
                            handleSignInAndStorage(getContext()))
                    .show();
        }
    }

    private static void handleSignInAndStorage(final Context context) {
        final Intent intent = new Intent(context, GetGoogleSignInActivity.class);
        context.startActivity(intent);
    }

}
