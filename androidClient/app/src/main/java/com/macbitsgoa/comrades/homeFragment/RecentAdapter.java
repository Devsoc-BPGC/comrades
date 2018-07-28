package com.macbitsgoa.comrades.homeFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class RecentAdapter extends RecyclerView.Adapter<RecentViewHolder> {
    private final ArrayList<ItemRecent> recents;

    RecentAdapter(final ArrayList<ItemRecent> recents) {
        this.recents = recents;
    }

    @Override
    public RecentViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_recents, parent, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecentViewHolder holder, final int position) {
        final ItemRecent obj = recents.get(position);
        holder.populate(obj);
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }
}
