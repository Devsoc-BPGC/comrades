package com.macbitsgoa.comrades.aboutmac;

import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

import androidx.recyclerview.widget.RecyclerView;

public class ContributorsVh extends RecyclerView.ViewHolder {

    public TextView nameTextView;


    public ContributorsVh(View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.item_format_contributors_name_tv);
    }
}
