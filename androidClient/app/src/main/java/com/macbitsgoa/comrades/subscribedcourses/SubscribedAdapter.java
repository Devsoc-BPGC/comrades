package com.macbitsgoa.comrades.subscribedcourses;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class SubscribedAdapter extends RecyclerView.Adapter<SubscribedViewHolder> {
    private ArrayList<MyCourse> course;

    public SubscribedAdapter(ArrayList<MyCourse> course) {
        this.course = course;
    }

    @Override
    public SubscribedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscribedViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_notification_rv, null));
    }

    @Override
    public void onBindViewHolder(SubscribedViewHolder holder, int position) {
        holder.populate(course.get(position));
    }

    @Override
    public int getItemCount() {
        return course.size();
    }
}
