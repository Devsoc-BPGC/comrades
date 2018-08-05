package com.macbitsgoa.comrades.ranks;

import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.MySimpleDraweeView;
import com.macbitsgoa.comrades.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class RankViewHolder extends RecyclerView.ViewHolder {
    private TextView tvScore;
    private TextView tvUploads;
    private TextView tvName;
    private TextView tvRank;
    private MySimpleDraweeView imageCreator;

    public RankViewHolder(View view) {
        super(view);
        tvUploads = view.findViewById(R.id.tv_uploads);
        tvRank = view.findViewById(R.id.tv_rank);
        tvName = view.findViewById(R.id.tv_name);
        tvScore = view.findViewById(R.id.tv_score);
        imageCreator = view.findViewById(R.id.user_image);
    }

    public void populate(UserObject obj) {
        imageCreator.setParam(obj.getId());
        tvScore.setText(String.valueOf(obj.getScore()));
        tvUploads.setText(String.valueOf(obj.getUploads()));
        tvRank.setText(String.valueOf(obj.getRank()));
        tvName.setText(obj.getName());
        imageCreator.setImageURI(obj.getPhotoUrl());
    }

}
