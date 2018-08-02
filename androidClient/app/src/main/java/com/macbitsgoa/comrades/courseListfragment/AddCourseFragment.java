package com.macbitsgoa.comrades.courseListfragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.R;

import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


/**
 * @author aayush singla and Rushikesh Jogdand
 */

public class AddCourseFragment extends DialogFragment implements TextWatcher {

    private TextInputEditText nameEt;
    private TextInputEditText streamIdEt;
    private TextInputEditText courseNumberEt;
    private TextInputLayout nameTil;
    private TextInputLayout streamIdTil;
    private TextInputLayout courseNumberTil;
    private Dialog.OnClickListener positiveClickListener;


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_add_course, null);
        nameEt = view.findViewById(R.id.et_course_name);
        streamIdEt = view.findViewById(R.id.et_stream_code);
        courseNumberEt = view.findViewById(R.id.et_course_number);
        nameTil = view.findViewById(R.id.til_course_name);
        streamIdTil = view.findViewById(R.id.til_stream_code);
        courseNumberTil = view.findViewById(R.id.til_course_number);

        nameEt.addTextChangedListener(this);
        courseNumberEt.addTextChangedListener(this);
        streamIdEt.addTextChangedListener(this);
        setPositiveClick();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton("Add Course", positiveClickListener)
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                });

        return builder.create();
    }


    private void setPositiveClick() {
        positiveClickListener = (dialogInterface, i) -> {
            boolean allFieldsSet = true;
            String courseName = nameEt.getText().toString();
            String streamId = streamIdEt.getText().toString();
            String courseNumber = courseNumberEt.getText().toString();

            if (Objects.equals(courseName, "")) {
                nameTil.setError("a course must have a name");
                allFieldsSet = false;
            }
            if (Objects.equals(streamId, "")) {
                streamIdTil.setError("a stream is a must");
                allFieldsSet = false;
            }
            if (Objects.equals(courseNumber, "")) {
                courseNumberTil.setError("a course must have a number");
                allFieldsSet = false;
            }
            if (allFieldsSet) {
                DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("/courses/").push();
                String key = dbr.getKey();
                ItemCourse itemCourse = new ItemCourse();
                itemCourse.setId(key);
                itemCourse.setAddedById(FirebaseAuth.getInstance().getCurrentUser().getUid());
                itemCourse.setName(courseName.toUpperCase());
                itemCourse.setCode((streamId + courseNumber).toUpperCase());
                dbr.setValue(itemCourse);
            }
        };
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (Objects.equals(editable.toString(), "")) {
            if (editable == nameEt.getEditableText()) {
                nameTil.setError("A course must have a name");
            } else if (editable == courseNumberEt.getEditableText()) {
                courseNumberTil.setError("A course number is must");
            } else if (editable == streamIdEt.getEditableText()) {
                streamIdTil.setError("A stream is must");
            }
        }
    }

}

