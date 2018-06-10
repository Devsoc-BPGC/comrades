package com.macbitsgoa.comrades.coursematerial;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHC.TAG_PREFIX;
import static com.macbitsgoa.comrades.FirebaseKeys.COURSE_MATERIAL;

public class CourseActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = TAG_PREFIX + CourseActivity.class.getSimpleName();
    public static final String EXTRA_COURSE_ID = "courseId";
    public static final String EXTRA_COURSE_NAME = "courseName";
    private final ArrayList<ItemCourseMaterial> materialArrayList = new ArrayList<>(0);
    private String courseId;
    private MaterialAdapter materialAdapter;
    FloatingActionButton btnAddMaterial;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initUi();

        databaseReference.child(COURSE_MATERIAL).child(courseId).addValueEventListener(this);
        btnAddMaterial.setOnClickListener(this);

    }

    private void initUi() {
        courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        btnAddMaterial = findViewById(R.id.fab_add_material);
        final String courseName = getIntent().getStringExtra(EXTRA_COURSE_NAME);
        final RecyclerView recyclerView = findViewById(R.id.rv_content_list);
        final Toolbar toolbar = findViewById(R.id.toolbar_course_activity);

        toolbar.inflateMenu(R.menu.course_activity_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setTitle(courseName);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        final LinearLayoutManager linearLayoutManager;
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        materialAdapter = new MaterialAdapter(materialArrayList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(materialAdapter);
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        materialArrayList.clear();
        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
            materialArrayList.add(snapshot.getValue(ItemCourseMaterial.class));
        }
        materialAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(final DatabaseError databaseError) {
        Log.e(TAG, databaseError.getCode() + databaseError.getMessage());
    }
}
