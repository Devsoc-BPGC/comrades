package com.macbitsgoa.comrades.courselistfragment;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.notification.SubscribedCourses;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Rushikesh Jogdand.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseVh> {

    private ArrayList<ItemCourse> courses;
    private ArrayList<SubscribedCourses> subscribedCourses;

    public CourseAdapter(ArrayList<ItemCourse> courses, ArrayList<SubscribedCourses> subscribedCourses) {
        this.courses = courses;
        this.subscribedCourses = subscribedCourses;
    }

    @Override
    public CourseVh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CourseVh(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_course, parent, false));
    }

    @Override
    public void onBindViewHolder(CourseVh holder, int position) {
        holder.populate(courses.get(position), subscribedCourses);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
