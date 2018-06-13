package com.macbitsgoa.comrades;

import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.coursematerial.CourseActivity;

import androidx.recyclerview.widget.RecyclerView;


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
            CourseActivity.show(rootView.getContext(), course.id, course.name);
        });
    }
}
