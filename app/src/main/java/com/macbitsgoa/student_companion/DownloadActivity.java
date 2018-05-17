package com.macbitsgoa.student_companion;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.button_container)
    LinearLayout scrollView;
    HashMap<String,String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        hashMap= (HashMap<String, String>) getIntent().getSerializableExtra("hashmap");
        Object[] array=hashMap.keySet().toArray();

        for(int i=0;i<array.length;i++){
            Button button=new Button(this);
            button.setText("File"+i+":"+(String)array[i]);
            button.setTag((String)array[i]);
            button.setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 200);
            params.setMargins(0, 8, 0, 0);
            button.setLayoutParams(params);
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            scrollView.addView(button);

        }

    }

    @Override
    public void onClick(View v) {
       String url= hashMap.get(v.getTag());
        DownloadFile downloadFile=new DownloadFile(this,"aayush");
        downloadFile.execute(url);
    }
}
