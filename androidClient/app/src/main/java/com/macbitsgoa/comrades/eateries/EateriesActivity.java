package com.macbitsgoa.comrades.eateries;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.macbitsgoa.comrades.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class EateriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eateries);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
        }

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (myToolbar != null) {
            myToolbar.setNavigationOnClickListener(view -> onBackPressed());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        RecyclerView recyclerView = findViewById(R.id.RV_Eateries);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (position) {
                    case 1:
                    case 2:
                    case 5:
                    case 6:
                        return 1;
                    case 0:
                    case 3:
                    case 4:
                    case 7:
                        return 2;

                    //Span 1

                }
                throw new IllegalStateException("internal error");
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        EateriesAdapter adapter = new EateriesAdapter();
        recyclerView.setAdapter(adapter);

    }
}
