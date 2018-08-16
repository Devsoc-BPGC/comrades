package com.macbitsgoa.comrades.ranks;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class RankActivity extends AppCompatActivity implements ValueEventListener {
    private ArrayList<UserObject> arrayList = new ArrayList<>(0);
    private RankAdapter rankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        Toolbar toolbar = findViewById(R.id.toolbar_ranks);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        RecyclerView rvRanks = findViewById(R.id.rv_rank);
        rankAdapter = new RankAdapter(arrayList);
        rvRanks.setLayoutManager(new LinearLayoutManager(this));
        rvRanks.setAdapter(rankAdapter);

        FirebaseDatabase.getInstance().getReference()
                .child(BuildConfig.BUILD_TYPE)
                .child("users")
                .orderByChild("rank")
                .addValueEventListener(this);

    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        arrayList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren())
            arrayList.add(snapshot.getValue(UserObject.class));
        rankAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
