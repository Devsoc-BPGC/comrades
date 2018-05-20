package com.macbitsgoa.companions;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.Serializable;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String FILES_MAP_KEY = "filesData";
    private LinearLayout scrollView;
    private HashMap<String, String> filesData;
    private static final String TAG = "MAC->" + DownloadActivity.class.getSimpleName();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initViews();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Serializable filesDataSer = getIntent().getSerializableExtra(FILES_MAP_KEY);
        if (filesDataSer instanceof HashMap) {
            //noinspection unchecked
            filesData = (HashMap<String, String>) filesDataSer;
        } else {
            Log.e(TAG, "filesDataSet is not instance of hashMap");
        }
        int i = 1;
        final int btnHeight = 200;
        for (final HashMap.Entry<String, String> fileData : filesData.entrySet()) {
            final Button button = new Button(this);
            button.setText("File " + i + ":" + fileData.getKey());
            button.setTag(fileData.getValue());
            button.setOnClickListener(this);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, btnHeight);
            params.setMargins(0, 8, 0, 0);
            button.setLayoutParams(params);
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            scrollView.addView(button);
            i++;
        }
    }

    private void initViews() {
        scrollView = findViewById(R.id.button_container);
    }

    @Override
    public void onClick(final View v) {
        final String url = (String) v.getTag();
        final DownloadFile downloadFile = new DownloadFile("Untitled");
        downloadFile.execute(url);
    }
}
