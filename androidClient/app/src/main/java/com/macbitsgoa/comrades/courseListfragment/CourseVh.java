package com.macbitsgoa.comrades.courseListfragment;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush
 */

public class CourseVh extends RecyclerView.ViewHolder {
    private TextView nameTv;
    private Chip codeChip;
    private ImageButton subscribeButton;
    private boolean subscribed = true;

    public CourseVh(View itemView) {
        super(itemView);
        codeChip = itemView.findViewById(R.id.chip_course_code);
        nameTv = itemView.findViewById(R.id.tv_course_name);
        subscribeButton = itemView.findViewById(R.id.notification_icon);
    }

    void populate(ItemCourse course) {
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

            if (subscribed) {
                subscribed = false;
                subscribeButton.setImageResource(R.drawable.ic_notifications_active_black_24dp);
            } else {
                subscribed = true;
                subscribeButton.setImageResource(R.drawable.ic_notifications_none_black_24dp);
            }
        });

    }

}
