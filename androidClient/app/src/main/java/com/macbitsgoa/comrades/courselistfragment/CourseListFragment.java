package com.macbitsgoa.comrades.courselistfragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


public class CourseListFragment extends Fragment implements ChildEventListener {

    private static final String ADD_COURSE_FRAGMENT = "addCourseFragment";
    private ArrayList<MyCourse> arrayList = new ArrayList<>();
    private CourseAdapter courseAdapter;
    private CoordinatorLayout rootCl;
    private final static String TAG = TAG_PREFIX + CourseListFragment.class.getSimpleName();
    private CourseVm courseVm;

    public static Fragment newInstance() {
        return new CourseListFragment();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        courseVm = ViewModelProviders.of(this).get(CourseVm.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        courseAdapter = new CourseAdapter(arrayList);
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        view.findViewById(R.id.fab_add_course).setOnClickListener(v -> handleAddCourse());
        rootCl = view.findViewById(R.id.cl_main_activity);
        final RecyclerView coursesRv = view.findViewById(R.id.rv_course_list);
        coursesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        coursesRv.setAdapter(courseAdapter);
        courseVm.getAll().observe(this, courses -> {
            arrayList.clear();
            arrayList.addAll(courses);
            courseAdapter.notifyDataSetChanged();
        });

        FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE)
                .child("/courses/").addChildEventListener(this);
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
            final FragmentManager fm = getActivity().getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            final DialogFragment addCourseFragment = new AddCourseFragment();
            addCourseFragment.show(ft, ADD_COURSE_FRAGMENT);


        } else if (signedIn) {
            Snackbar.make(rootCl, getString(R.string.storage_permission_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.allow), v ->
                            handleSignInAndStorage(getContext()))
                    .show();
        } else {
            Snackbar.make(rootCl, getString(R.string.login_to_add_course),
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

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        MyCourse myCourse = dataSnapshot.getValue(MyCourse.class);
        if (myCourse.getAddedByName() == null) {
            myCourse.setAddedByName("");
        }
        myCourse.setFollowing(false);
        courseVm.insert(myCourse);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        MyCourse myCourse = dataSnapshot.getValue(MyCourse.class);

        for (int i = 0; i < arrayList.size(); i++) {
            if (Objects.equals(arrayList.get(i).getId(), myCourse.getId())) {
                arrayList.remove(i);
                myCourse.setFollowing(arrayList.get(i).getFollowing());
                courseVm.update(myCourse);
                break;
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        MyCourse myCourse = dataSnapshot.getValue(MyCourse.class);
        courseVm.delete(myCourse);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.e(TAG, "Child Moved");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getMessage());
    }
}
