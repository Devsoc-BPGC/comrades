package com.macbitsgoa.comrades.coursematerial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Aayush Singla
 */

public class MaterialAdapter extends RecyclerView.Adapter<MaterialViewHolder> {
    private final ArrayList<CourseMaterial> materialArrayList;

    public MaterialAdapter(final ArrayList<CourseMaterial> materialArrayList) {
        this.materialArrayList = materialArrayList;
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_material, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MaterialViewHolder holder, final int position) {
        holder.populate(materialArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return materialArrayList.size();
    }
}
