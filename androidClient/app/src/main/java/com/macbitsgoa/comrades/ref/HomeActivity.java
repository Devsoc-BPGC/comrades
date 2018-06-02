package com.macbitsgoa.comrades.ref;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener, StorageChooser.OnSelectListener {
    public static final String FILE_ID_KEY = "fileId";
    public static final String WEB_CONTENT_LINK = "webContentLink";
    private static final String TAG = "MAC->" + HomeActivity.class.getSimpleName();
    private Button upload;
    private Button download;
    private StorageChooser chooser;
    private String accessToken;
    private ValueEventListener dbListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        chooser = new StorageChooser.Builder()
                .withActivity(HomeActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build();

        upload.setOnClickListener(this);
        accessToken = getIntent().getStringExtra(SignInActivity.ACCESS_TOKEN_KEY);
        dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final HashMap<String, String> filesData = new HashMap<>(0);

                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String fileId = snapshot.child(FILE_ID_KEY).getValue(String.class);
                    final String link = snapshot.child(WEB_CONTENT_LINK).getValue(String.class);
                    filesData.put(fileId, link);
                }

                final Intent intent = new Intent(HomeActivity.this, DownloadActivity.class);
                intent.putExtra(DownloadActivity.FILES_MAP_KEY, filesData);
                startActivity(intent);
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(HomeActivity.class.getSimpleName(), databaseError.getMessage(),
                        databaseError.toException());
            }
        };
        chooser.setOnSelectListener(this);

        download.setOnClickListener(this);
    }

    private void initViews() {
        upload = findViewById(R.id.btn_upload);
        download = findViewById(R.id.btn_download);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btn_upload: {
                chooser.show();
                return;
            }
            case R.id.btn_download: {
                FirebaseDatabase.getInstance().getReference()
                        .addListenerForSingleValueEvent(dbListener);
                return;
            }
            default: {
                break;
            }
        }
    }

    @Override
    public void onSelect(final String path) {
        final UploadFile uploadFile = new UploadFile(path, accessToken,
                getString(R.string.drive_upload_url));
        final String response;
        try {
            response = uploadFile.execute().get();
            final JSONObject jsonObject = new JSONObject(response);
            final String fileId = (String) jsonObject.get("id");
            final MetaDataAndPermissions permissions = new MetaDataAndPermissions(
                    fileId, accessToken, getString(R.string.drive_api_base_url));
            permissions.execute();
        } catch (final InterruptedException | ExecutionException | JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
    }
}





