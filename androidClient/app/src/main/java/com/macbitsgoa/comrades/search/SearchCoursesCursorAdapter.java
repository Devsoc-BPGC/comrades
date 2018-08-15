package com.macbitsgoa.comrades.search;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;

/**
 * @author aayush singla
 */
public class SearchCoursesCursorAdapter extends CursorAdapter {
    private LayoutInflater mLayoutInflater;

    public SearchCoursesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(R.layout.layout_course_search, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String code = cursor.getString(cursor.getColumnIndexOrThrow("code"));
        String addedBy = cursor.getString(cursor.getColumnIndexOrThrow("addedByName"));

        TextView codeTv = view.findViewById(R.id.text_course_code);
        TextView addedByTv = view.findViewById(R.id.text_added_by);
        TextView nameTv = view.findViewById(R.id.tv_course_name);
        nameTv.setText(name);
        codeTv.setText(code);
        addedByTv.setText("added by " + addedBy);

        view.setOnClickListener(view1 -> {
            CourseActivity.launchCourse(view1.getContext(), id, name);
        });

    }
}
