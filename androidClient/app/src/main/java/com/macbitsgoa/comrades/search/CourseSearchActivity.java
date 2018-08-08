package com.macbitsgoa.comrades.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.CourseAdapter;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.persistance.Database;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class CourseSearchActivity extends AppCompatActivity {
    private ArrayList<MyCourse> arrayList;
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // Get the intent, verify the action and get the query
        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            arrayList = new ArrayList<>(0);
            recyclerView = findViewById(R.id.recyclerView);
            courseAdapter = new CourseAdapter(arrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(courseAdapter);
            showResults("%" + query + "%");
        }
    }

    private void showResults(String query) {
        Database.getInstance(this).getCourseDao().getSearchResult(query).observe(CourseSearchActivity.this, myCourses -> {
            if (myCourses.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                return;
            }
            arrayList.clear();
            arrayList.addAll(myCourses);
            courseAdapter.notifyDataSetChanged();
        });

    }

}
