package com.macbitsgoa.comrades.profilefragment;

import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class ProfileViewHolder extends RecyclerView.ViewHolder {
    public TextView tvProfile;

    public ProfileViewHolder(View view) {
        super(view);
        tvProfile = view.findViewById(R.id.tv_list);
    }

}