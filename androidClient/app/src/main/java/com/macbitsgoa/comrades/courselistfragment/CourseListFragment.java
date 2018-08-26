package com.macbitsgoa.comrades.courselistfragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.HomeActivity.snack;


public class CourseListFragment extends Fragment
        implements ChildEventListener {

    private static final String ADD_COURSE_FRAGMENT = "addCourseFragment";
    private final static String TAG = TAG_PREFIX + CourseListFragment.class.getSimpleName();
    private final ArrayList<MyCourse> arrayList = new ArrayList<>(0);
    private CourseAdapter courseAdapter;
    private CourseVm courseVm;
    private int currentSortOrder = 0;

    public static Fragment newInstance() {
        return new CourseListFragment();
    }

    public static void handleAddCourse(Context context) {
        final boolean signedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        boolean storagePermission = true;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            storagePermission =
                    (ContextCompat.checkSelfPermission(Objects.requireNonNull(context), READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED);
        }

        if (signedIn && storagePermission) {
            final FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
            final FragmentTransaction ft = fm.beginTransaction();
            final DialogFragment addCourseFragment = new AddCourseFragment();
            addCourseFragment.show(ft, ADD_COURSE_FRAGMENT);

        } else if (signedIn) {
            Snackbar.make(snack, context.getString(R.string.storage_permission_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(context.getString(R.string.allow), v ->
                            handleSignInAndStorage(context))
                    .show();
        } else {
            Snackbar.make(snack, context.getString(R.string.login_to_add_course),
                    Snackbar.LENGTH_LONG)
                    .setAction(context.getString(R.string.login), v ->
                            handleSignInAndStorage(context))
                    .show();
        }
    }

    private static void handleSignInAndStorage(final Context context) {
        final Intent intent = new Intent(context, GetGoogleSignInActivity.class);
        context.startActivity(intent);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        courseVm = ViewModelProviders.of(this).get(CourseVm.class);
        final View view = inflater.inflate(R.layout.fragment_course_list, container, false);
        RecyclerView coursesRv = view.findViewById(R.id.rv_course_list);
        courseAdapter = new CourseAdapter(arrayList);
        coursesRv.setAdapter(courseAdapter);
        coursesRv.setLayoutManager(new LinearLayoutManager(getContext()));
        courseVm.getAllCoursesByName().observe(this, courses -> {
            arrayList.clear();
            arrayList.addAll(courses);
            courseAdapter.notifyDataSetChanged();
        });
        courseVm.courseCount.observe(this,
                count -> ((TextView) view.findViewById(R.id.tv_file_count)).setText(count + " Courses"));
        view.findViewById(R.id.ib_sort).setOnClickListener(v -> handleSort());
        FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE)
                .child("/courses/").addChildEventListener(this);
        return view;
    }

    public void handleSort() {
        final CharSequence[] sortOrders = new CharSequence[]{
                "Course Name",
                "Course Code",
        };
        new AlertDialog.Builder(getActivity())
                .setTitle("Sort By")
                .setSingleChoiceItems(sortOrders, currentSortOrder, (dialog, which) -> {
                    if (which == 0) {
                        currentSortOrder = 0;
                        courseVm.getAllCoursesByName().observe(this, courses -> {
                            arrayList.clear();
                            arrayList.addAll(courses);
                            courseAdapter.notifyDataSetChanged();
                        });
                    } else {
                        currentSortOrder = 1;
                        courseVm.getAllCoursesByCode().observe(this, courses -> {
                            arrayList.clear();
                            arrayList.addAll(courses);
                            courseAdapter.notifyDataSetChanged();
                        });
                    }
                    dialog.dismiss();
                }).show();
    }

    @Override
    public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, final String s) {
        final MyCourse myCourse = dataSnapshot.getValue(MyCourse.class);
        assert myCourse != null;
        if (myCourse.addedByName == null) {
            myCourse.setAddedByName("");
        }
        myCourse.setFollowing(false);
        courseVm.insert(myCourse);
    }

    @Override
    public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, final String s) {
        final MyCourse myCourse = dataSnapshot.getValue(MyCourse.class);
        assert myCourse != null;
        for (int i = 0; i < arrayList.size(); i++) {
            if (Objects.equals(arrayList.get(i)._id, myCourse._id)) {
                arrayList.remove(i);
                myCourse.isFollowing = arrayList.get(i).isFollowing;
                courseVm.update(myCourse);
                break;
            }
        }
    }

    @Override
    public void onChildRemoved(@NonNull final DataSnapshot dataSnapshot) {
        final MyCourse myCourse = dataSnapshot.getValue(MyCourse.class);
        courseVm.delete(myCourse);
    }

    @Override
    public void onChildMoved(@NonNull final DataSnapshot dataSnapshot, final String s) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Child Moved " + dataSnapshot.getValue());
        }
    }

    @Override
    public void onCancelled(final @NonNull DatabaseError databaseError) {
        Log.e(TAG, databaseError.getMessage(), databaseError.toException());
    }

}
