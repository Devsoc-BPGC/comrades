package com.macbitsgoa.comrades.subscribedcourses;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.CourseVm;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;


public class SubscribedCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_courses);
        setSupportActionBar(findViewById(R.id.toolbar));

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        final ArrayList<MyCourse> myCourses = new ArrayList<>(0);
        final SubscribedAdapter subscribedAdapter = new SubscribedAdapter(myCourses);
        RecyclerView recyclerView = findViewById(R.id.rv_notifications);
        recyclerView.setAdapter(subscribedAdapter);
        LinearLayout linearLayout = findViewById(R.id.notSubscribed);
        final CourseVm courseVm = ViewModelProviders.of(this).get(CourseVm.class);
        courseVm.getFollowingList().observe(this, courses -> {
            myCourses.clear();
            if (courses.size() == 0) {
                linearLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                return;
            }
            myCourses.addAll(courses);
            subscribedAdapter.notifyDataSetChanged();
        });
    }


}
