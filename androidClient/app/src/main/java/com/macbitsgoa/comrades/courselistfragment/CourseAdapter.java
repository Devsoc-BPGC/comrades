package com.macbitsgoa.comrades.courselistfragment;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Rushikesh Jogdand.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseVh> {

    private ArrayList<MyCourse> course;

    public CourseAdapter(ArrayList<MyCourse> course) {
        this.course = course;
    }

    @Override
    public CourseVh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseVh(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_course, parent, false));
    }

    @Override
    public void onBindViewHolder(CourseVh holder, int position) {
        holder.populate(course.get(position));
    }

    @Override
    public int getItemCount() {
        return course.size();
    }
}
