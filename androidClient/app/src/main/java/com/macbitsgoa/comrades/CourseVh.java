package com.macbitsgoa.comrades;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.coursematerial.CourseActivity;

import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.coursematerial.CourseActivity.EXTRA_COURSE_ID;
import static com.macbitsgoa.comrades.coursematerial.CourseActivity.EXTRA_COURSE_NAME;

/**
 * @author Rushikesh Jogdand.
 */
public class CourseVh extends RecyclerView.ViewHolder {
    private final TextView nameTv;
    private final View rootView;

    public CourseVh(final View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.tv_course_name);
        rootView = itemView;
    }

    public void populate(final Course course) {
        nameTv.setText(course.name);
        rootView.setOnClickListener(view -> {
            final Intent courseIntent = new Intent(view.getContext(), CourseActivity.class);
            courseIntent.putExtra(EXTRA_COURSE_ID, course.id);
            courseIntent.putExtra(EXTRA_COURSE_NAME, course.name);
            view.getContext().startActivity(courseIntent);
        });
    }
}
