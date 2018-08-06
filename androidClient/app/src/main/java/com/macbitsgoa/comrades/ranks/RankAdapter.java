package com.macbitsgoa.comrades.ranks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class RankAdapter extends RecyclerView.Adapter<RankViewHolder> {
    private final ArrayList<UserObject> users;

    RankAdapter(final ArrayList<UserObject> users) {
        this.users = users;
    }

    @Override
    public RankViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.vh_rank
                , parent, false);
        return new RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RankViewHolder holder, final int position) {
        final UserObject obj = users.get(position);
        holder.populate(obj);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
