package com.macbitsgoa.comrades.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseMaterial;
import com.macbitsgoa.comrades.coursematerial.MaterialAdapter;
import com.macbitsgoa.comrades.persistance.Database;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.coursematerial.CourseActivity.courseId;

/**
 * @author aayush singla
 */

public class MaterialSearchActivity extends AppCompatActivity {
    private ArrayList<CourseMaterial> arrayList;
    private RecyclerView recyclerView;
    private MaterialAdapter materialAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_material);
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
            materialAdapter = new MaterialAdapter(arrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(materialAdapter);
            showResults(courseId, "%" + query + "%");
        }
    }

    private void showResults(String courseId, String query) {
        Database.getInstance(this).getMaterialDao().searchMaterial(courseId, query).observe(MaterialSearchActivity.this, myMaterial -> {
            if (myMaterial.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                return;
            }
            arrayList.clear();
            arrayList.addAll(myMaterial);
            materialAdapter.notifyDataSetChanged();
        });

    }

}

