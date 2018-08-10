package com.macbitsgoa.comrades.eateries;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.macbitsgoa.comrades.ComradesConstants;
import com.macbitsgoa.comrades.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class EateriesVh extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView Title;
    public TextView desc;
    public SimpleDraweeView image;
    private List<EateriesList> list;
    private Context context;

    public EateriesVh(View itemView, List<EateriesList> list, Context context) {
        super(itemView);
        this.context = context;
        this.list = list;
        Title = itemView.findViewById(R.id.item_format_categories_title);
        desc = itemView.findViewById(R.id.item_format_categories_description);
        image = itemView.findViewById(R.id.item_format_categories_background);

        image.setOnClickListener(this);
    }

    public void onClick(View view) {

        if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.NC_A)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.NC_A);
            context.startActivity(intent);

        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.NC_C)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.NC_C);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.MESS_A)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.MESS_A);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.MESS_C)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.MESS_C);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.MONGINIES)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.MONGINIES);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.ICE_SPICE)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.ICE_SPICE);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.FOODKING)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.FOODKING);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.RED_CHILLIES)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.RED_CHILLIES);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.IC)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.IC);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.GAJA_LAXMI)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.GAJA_LAXMI);
            context.startActivity(intent);
        } else if (list.get(getAdapterPosition()).getId().equals(ComradesConstants.DOMINOES)) {
            Intent intent = new Intent(context, EateriesDetail.class);
            intent.putExtra("EATERY", ComradesConstants.DOMINOES);
            context.startActivity(intent);
        } else {
            Toast toast = Toast.makeText(context, "INVALID", Toast.LENGTH_SHORT);
            toast.show();
        }


    }


}




