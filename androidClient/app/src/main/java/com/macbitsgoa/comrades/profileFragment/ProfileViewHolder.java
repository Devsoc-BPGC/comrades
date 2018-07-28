package com.macbitsgoa.comrades.profileFragment;

import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvProfile;

    public ProfileViewHolder(View view) {
        super(view);
        tvProfile = view.findViewById(R.id.tv_list);
        view.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

    }
}