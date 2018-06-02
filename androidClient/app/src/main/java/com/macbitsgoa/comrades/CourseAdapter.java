package com.macbitsgoa.comrades;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHC.TAG_PREFIX;
import static com.macbitsgoa.comrades.FirebaseKeys.COURSES;

/**
 * @author Rushikesh Jogdand.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseVh> {
    private final List<Course> courses = new ArrayList<>(0);
    private static final String TAG = TAG_PREFIX + CourseAdapter.class.getSimpleName();

    public CourseAdapter() {
        FirebaseDatabase.getInstance().getReference().child(COURSES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                courses.clear();
                for (final DataSnapshot c : dataSnapshot.getChildren()) {
                    courses.add(c.getValue(Course.class));
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    @Override
    public CourseVh onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new CourseVh(LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_course, parent, false));
    }

    @Override
    public void onBindViewHolder(final CourseVh holder, final int position) {
        holder.populate(courses.get(position));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
