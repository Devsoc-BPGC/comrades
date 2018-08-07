package com.macbitsgoa.comrades.search;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

/**
 * @author aayush singla
 */
public class SearchCoursesCursorAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private SearchView searchView;

    public SearchCoursesCursorAdapter(Context context, Cursor cursor, SearchView sv) {
        super(context, cursor, false);
        mContext = context;
        searchView = sv;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.layout_course_search, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String code = cursor.getString(cursor.getColumnIndexOrThrow("code"));
        String addedBy = cursor.getString(cursor.getColumnIndexOrThrow("addedByName"));

        TextView codeTv = view.findViewById(R.id.text_course_code);
        TextView addedByTv = view.findViewById(R.id.text_added_by);
        TextView nameTv = view.findViewById(R.id.tv_course_name);
        nameTv.setText(name);
        addedByTv.setText(code);
        codeTv.setText("added by " + addedBy);

        view.setOnClickListener(view1 -> {

        });

    }
}