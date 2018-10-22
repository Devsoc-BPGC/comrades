package com.macbitsgoa.comrades.csa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.macbitsgoa.comrades.MySimpleDraweeView;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Rushikesh Jogdand.
 */
public class CsaMsgDetailActivity extends AppCompatActivity {

    TextView title,content,name,post,timestamp;
    MySimpleDraweeView simpleDraweeView;
    RecyclerView attachList;
    ArrayList<String> fileName,fileUrl;
    AttachmentsAdapter attachmentsAdapter;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.activity_csa_msg_detail);
        super.onCreate(savedInstanceState);

        title = findViewById(R.id.Heading);
        content = findViewById(R.id.content_csa_msg);
        name = findViewById(R.id.sender_name);
        post = findViewById(R.id.sender_post);
        timestamp = findViewById(R.id.time_stamp);
        simpleDraweeView = findViewById(R.id.profile_dp);
        attachList = findViewById(R.id.AttachList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        attachList.setLayoutManager(layoutManager);

        Intent openMsgDetails = getIntent();
        title.setText(openMsgDetails.getStringExtra("eventName"));
        name.setText(openMsgDetails.getStringExtra("senderName"));
        post.setText(openMsgDetails.getStringExtra("senderPost"));
        content.setText(openMsgDetails.getStringExtra("eventDetails"));
        timestamp.setText(openMsgDetails.getStringExtra("timeStamp"));

        fileName = new ArrayList<>();
        fileUrl = new ArrayList<>();

        fileName = openMsgDetails.getStringArrayListExtra("fileNames");
        fileUrl = openMsgDetails.getStringArrayListExtra("fileURLs");

        attachmentsAdapter = new AttachmentsAdapter(fileName,fileUrl,this);
        attachList.setAdapter(attachmentsAdapter);

        simpleDraweeView.setImageURI(openMsgDetails.getStringExtra("dpURL"));

    }
}
