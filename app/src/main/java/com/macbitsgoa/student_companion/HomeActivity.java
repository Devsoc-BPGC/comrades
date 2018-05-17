package com.macbitsgoa.student_companion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.codekidlabs.storagechooser.StorageChooser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.btn_upload)
    Button upload;
    @BindView(R.id.btn_download)
    Button download;
    StorageChooser chooser;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        chooser = new StorageChooser.Builder()
                .withActivity(HomeActivity.this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                .setType(StorageChooser.FILE_PICKER)
                .build();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooser.show();
            }
        });

        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                Log.e("SELECTED_PATH", path);

                UploadFile uploadFile=new UploadFile(path,getIntent().getStringExtra("accessToken"),HomeActivity.this);
                String response_string = null;
                try {
                    response_string = uploadFile.execute().get();
                    JSONObject jsonObject = new JSONObject(response_string);
                    String fileId = (String) jsonObject.get("id");
                    MetaDataAndPermissions metaDataAndPermissions = new MetaDataAndPermissions(HomeActivity.this, fileId, getIntent().getStringExtra("accessToken"));
                    metaDataAndPermissions.execute();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String,String> hashMap=new HashMap<>();

                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            String fileId=snapshot.child("fileId").getValue(String.class);
                            String link=snapshot.child("webContentLink").getValue(String.class);
                            hashMap.put(fileId,link);
                        }

                        Intent intent=new Intent(HomeActivity.this,DownloadActivity.class);
                        intent.putExtra("hashmap",hashMap);
                        startActivity(intent);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }


}





