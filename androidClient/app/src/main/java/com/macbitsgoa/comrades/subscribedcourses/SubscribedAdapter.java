package com.macbitsgoa.comrades.subscribedcourses;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribedAdapter extends RecyclerView.Adapter<SubscribedVH> {

    private final ArrayList<MyCourse> course;

    public SubscribedAdapter(final ArrayList<MyCourse> course) {
        this.course = course;
    }

    @NonNull
    @Override
    public SubscribedVH onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new SubscribedVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_notification_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SubscribedVH holder, final int position) {
        holder.populate(course.get(position));
    }

    @Override
    public int getItemCount() {
        return course.size();
    }
}
