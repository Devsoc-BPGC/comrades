package com.macbitsgoa.comrades.coursematerial;

import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class MaterialViewHolder extends RecyclerView.ViewHolder {
    public TextView tvFileName;
    public TextView tvOwnerName;


    public MaterialViewHolder(final View itemView) {
        super(itemView);
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        tvOwnerName = itemView.findViewById(R.id.tv_owner_name);
    }


}
