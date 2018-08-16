package com.macbitsgoa.comrades.search;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;

/**
 * @author aayush singla
 */

public class MaterialCoursesCursorAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    SearchView searchView;

    public MaterialCoursesCursorAdapter(Context context, Cursor cursor, SearchView searchView) {
        super(context, cursor, false);
        mLayoutInflater = LayoutInflater.from(context);
        this.searchView = searchView;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.layout_material_search, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow("fileName"));
        String addedBy = cursor.getString(cursor.getColumnIndexOrThrow("addedBy"));

        TextView addedByTv = view.findViewById(R.id.text_added_by);
        TextView nameTv = view.findViewById(R.id.tv_course_name);
        nameTv.setText(name);
        addedByTv.setText("Added by " + addedBy);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setQuery(name, true);
            }
        });

    }
}
