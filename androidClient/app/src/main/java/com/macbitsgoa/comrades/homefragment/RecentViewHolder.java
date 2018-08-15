package com.macbitsgoa.comrades.homefragment;

import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.MySimpleDraweeView;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.HomeActivity.navigation;

/**
 * @author aayush singla
 */

public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView tvUpdate;
    private TextView tvDateTime;
    private ItemRecent obj;
    private MySimpleDraweeView imageCreator;

    public RecentViewHolder(View view) {
        super(view);
        tvDateTime = view.findViewById(R.id.timeStamp);
        tvUpdate = view.findViewById(R.id.tv_update);
        imageCreator = view.findViewById(R.id.image_creator);
        view.setOnClickListener(this);
    }

    public void populate(ItemRecent obj) {
        this.obj = obj;
        imageCreator.setParam(obj.getAddedById());
        tvUpdate.setText(obj.getMessage());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        tvDateTime.setText(sfd.format(new Date(obj.getTimeStamp())));
        imageCreator.setImageURI(obj.getAddedByPhoto());
    }

    @Override
    public void onClick(View view) {
        if (Objects.equals(obj.getType(), "recent_material"))
            CourseActivity.launchCourse(view.getContext(), obj.getCourseId(), obj.getCourseName());
        else if (Objects.equals(obj.getType(), "recent_course"))
            navigation.setSelectedItemId(R.id.navigation_courses);
    }
}
