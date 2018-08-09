package com.macbitsgoa.comrades.eateries;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.macbitsgoa.comrades.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


public class EateriesDetailsAdapter extends PagerAdapter {

    private LayoutInflater mLayoutInflater;
    private ArrayList<String> list;


    public EateriesDetailsAdapter(Context context, ArrayList<String> list) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.layout_pager_eateries, container, false);
        PhotoView imageView = itemView.findViewById(R.id.imageView);
        Picasso.get().load(list.get(position)).into(imageView);
        Log.e("bhgghgj", list.get(position) + "");
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }


}