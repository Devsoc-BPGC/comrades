package com.macbitsgoa.comrades;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static com.macbitsgoa.comrades.FirebaseKeys.COURSES;

/**
 * @author Rushikesh Jogdand.
 */
public class AddCourseFragment extends DialogFragment {
    private final DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child(COURSES);

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") final View rootView = inflater.inflate(R.layout.dialog_add_course, null);

        return new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton(getString(R.string.add_course), (dialogInterface, i) -> {
                    final String name = ((TextView) getDialog().findViewById(R.id.et_course_name)).getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(getActivity(), R.string.warn_empty_course_name, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final String id = courseRef.push().getKey();
                    final Course newCourse = new Course(name, id);
                    courseRef.child(newCourse.id).setValue(newCourse);
                })
                .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> { })
                .create();
    }
}
