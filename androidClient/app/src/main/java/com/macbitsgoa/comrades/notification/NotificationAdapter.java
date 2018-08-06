package com.macbitsgoa.comrades.notification;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
    private ArrayList<MyCourse> course;

    public NotificationAdapter(ArrayList<MyCourse> course) {
        this.course = course;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_notification_rv, null));
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        holder.populate(course.get(position));
    }

    @Override
    public int getItemCount() {
        return course.size();
    }
}
