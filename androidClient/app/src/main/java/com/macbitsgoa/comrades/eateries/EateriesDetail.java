package com.macbitsgoa.comrades.eateries;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class EateriesDetail extends AppCompatActivity {

    private EateriesDetailsAdapter m_Eateries_DetailsAdapter;
    private ArrayList<String> arrayList;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private TextView current;
    private TextView count;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eateries_detail);
        //finding Views
        ViewPager mViewPager = findViewById(R.id.ViewPager);
        spinner = findViewById(R.id.progressBar1);
        count = findViewById(R.id.count);
        current = findViewById(R.id.current);
        spinner.setVisibility(View.VISIBLE);
        String eatery = getIntent().getStringExtra("EATERY");
        DatabaseReference ref = database.getReference().child(BuildConfig.BUILD_TYPE)
                .child("eateries").child(eatery);

        arrayList = new ArrayList<>();
        m_Eateries_DetailsAdapter = new EateriesDetailsAdapter(this, arrayList);
        mViewPager.setAdapter(m_Eateries_DetailsAdapter);
        mViewPager.setOffscreenPageLimit(5);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                arrayList.clear();
                count.setText(String.valueOf(snapshot.getChildrenCount()));
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String abc = postSnapshot.getValue(String.class);
                    arrayList.add(abc);
                }
                m_Eateries_DetailsAdapter.notifyDataSetChanged();
                spinner.setVisibility(View.GONE);
                current.setText("1");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current.setText(String.valueOf(position + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}



