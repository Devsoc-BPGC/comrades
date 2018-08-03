package com.macbitsgoa.comrades.courseListfragment;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;
import com.macbitsgoa.comrades.notification.NotificationVm;
import com.macbitsgoa.comrades.notification.SubscribedCourses;

import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush
 */

public class CourseVh extends RecyclerView.ViewHolder {
    private TextView nameTv;
    private Chip codeChip;
    private ImageButton subscribeButton;
    private boolean subscribed = false;

    public CourseVh(View itemView) {
        super(itemView);
        codeChip = itemView.findViewById(R.id.chip_course_code);
        nameTv = itemView.findViewById(R.id.tv_course_name);
        subscribeButton = itemView.findViewById(R.id.notification_icon);
    }

    void populate(ItemCourse course, List<SubscribedCourses> subscribedCourses) {

        for (int i = 0; i < subscribedCourses.size(); i++) {
            if (Objects.equals(course.getId(), subscribedCourses.get(i).getId())) {
                subscribed = true;
                break;
            }
        }

        nameTv.setText(course.getName());
        codeChip.setChipText(course.getCode());
        itemView.setOnClickListener(view -> CourseActivity.show(itemView.getContext(),
                course.getId(), course.getName()));

        if (subscribed)
            subscribeButton.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        else
            subscribeButton.setImageResource(R.drawable.ic_notifications_none_black_24dp);


        subscribeButton.setOnClickListener(view -> {
            final Animation animShake = AnimationUtils.loadAnimation(itemView.getContext(),
                    R.anim.subscribe);
            subscribeButton.startAnimation(animShake);
            SubscribedCourses subscribedCourse = new SubscribedCourses();
            subscribedCourse.setName(course.getName());
            subscribedCourse.setId(course.getId());
            subscribedCourse.setAddedById(course.getAddedById());
            subscribedCourse.setCode(course.getCode());
            Log.e("context.courseVH", itemView.getContext().toString());
            NotificationVm notificationVm = ViewModelProviders.of((AppCompatActivity) itemView.getContext()).get(NotificationVm.class);
            if (subscribed) {
                subscribed = false;
                notificationVm.delete(subscribedCourse);
                subscribeButton.setImageResource(R.drawable.ic_notifications_none_black_24dp);
            } else {
                subscribed = true;
                notificationVm.insert(subscribedCourse);
                subscribeButton.setImageResource(R.drawable.ic_notifications_active_black_24dp);
            }
        });

    }

}
