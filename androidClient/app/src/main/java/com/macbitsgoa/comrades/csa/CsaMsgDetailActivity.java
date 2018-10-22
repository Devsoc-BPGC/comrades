package com.macbitsgoa.comrades.csa;

import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.macbitsgoa.comrades.MySimpleDraweeView;
import com.macbitsgoa.comrades.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Rushikesh Jogdand.
 */
public class CsaMsgDetailActivity extends AppCompatActivity {

    public static final String EXTRA_KEY_NEWS = "event";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        setContentView(R.layout.activity_csa_msg_detail);
        super.onCreate(savedInstanceState);
        findViewById(R.id.ib_back).setOnClickListener(view -> onBackPressed());

        CsaNews news = new Gson().fromJson(getIntent().getStringExtra(EXTRA_KEY_NEWS), CsaNews.class);

        final TextView titleTv = findViewById(R.id.Heading);
        final TextView contentTv = findViewById(R.id.content_csa_msg);
        final TextView nameTv = findViewById(R.id.sender_name);
        final TextView postTv = findViewById(R.id.sender_post);
        final TextView timestampTv = findViewById(R.id.time_stamp);
        final MySimpleDraweeView dpSdv = findViewById(R.id.profile_dp);
        final RecyclerView attachList = findViewById(R.id.AttachList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        attachList.setLayoutManager(layoutManager);

        titleTv.setText(news.title);
        nameTv.setText(news.name);
        postTv.setText(news.post);
        contentTv.setText(news.content);
        timestampTv.setText(news.timestamp);

        final AttachmentsAdapter attachmentsAdapter = new AttachmentsAdapter(news.attachment);
        attachList.setAdapter(attachmentsAdapter);
        dpSdv.setImageURI(news.profileImageUrl);
    }
}
