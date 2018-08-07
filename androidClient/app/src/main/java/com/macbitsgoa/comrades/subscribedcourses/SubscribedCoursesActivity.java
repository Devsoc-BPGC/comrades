package com.macbitsgoa.comrades.subscribedcourses;

import android.os.Bundle;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.CourseVm;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SubscribedCoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_courses);
        setSupportActionBar(findViewById(R.id.toolbar));

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        ArrayList<MyCourse> course = new ArrayList<>(0);
        RecyclerView rv_subscribed_courses = findViewById(R.id.rv_notifications);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        final SubscribedAdapter subscribedAdapter = new SubscribedAdapter(course);
        rv_subscribed_courses.setLayoutManager(linearLayoutManager);
        rv_subscribed_courses.setAdapter(subscribedAdapter);

        CourseVm courseVm = ViewModelProviders.of(this).get(CourseVm.class);
        courseVm.getFollowingList().observe(this, courses -> {
            course.clear();
            course.addAll(courses);
            subscribedAdapter.notifyDataSetChanged();
        });
    }


}
