package com.macbitsgoa.comrades.eateries;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macbitsgoa.comrades.ComradesConstants;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


/**
 * @author aayush singla
 */

public class EateriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<EateriesList> list = new ArrayList<>();

    public EateriesAdapter() {
        list.add(new EateriesList("RED CHILLIES", "", R.drawable.rc, ComradesConstants.RED_CHILLIES));
        list.add(new EateriesList("A MESS", "", R.drawable.cm, ComradesConstants.MESS_A));
        list.add(new EateriesList("C MESS", "", R.drawable.cm, ComradesConstants.MESS_C));
        list.add(new EateriesList("ICE AND SPICE", "", R.drawable.inc, ComradesConstants.ICE_SPICE));
        list.add(new EateriesList("FOOD KING", "", R.drawable.fk, ComradesConstants.FOODKING));
        list.add(new EateriesList("Night Canteen A", "", R.drawable.anc, ComradesConstants.NC_A));
        list.add(new EateriesList("Night Canteen C", "", R.drawable.cnc, ComradesConstants.NC_C));
        list.add(new EateriesList("Gaja Laxmi Snacks", "", R.drawable.gj, ComradesConstants.GAJA_LAXMI));
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.eateries_list, parent, false);
        viewHolder = new EateriesVh(view, list, parent.getContext());

        return viewHolder;

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        EateriesVh mv = (EateriesVh) holder;
        mv.Title.setText(list.get(position).getTitle());
        mv.desc.setText(list.get(position).getDescription());
        mv.image.setImageURI(Uri.parse("res:///" + list.get(position).getBackground()));
    }
}
