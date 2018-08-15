package com.macbitsgoa.comrades.courselistfragment;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;

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

    public CourseVh(View itemView) {
        super(itemView);
        codeChip = itemView.findViewById(R.id.chip_course_code);
        nameTv = itemView.findViewById(R.id.tv_course_name);
        subscribeButton = itemView.findViewById(R.id.notification_icon);
    }

    public void populate(MyCourse myCourse) {
        nameTv.setText(myCourse.getName());
        codeChip.setChipText(myCourse.getCode());
        itemView.setOnClickListener(view -> CourseActivity.launchCourse(itemView.getContext(),
                myCourse.getId(), myCourse.getName()));

        if (myCourse.getFollowing())
            subscribeButton.setImageResource(R.drawable.ic_notifications_active_black_24dp);
        else
            subscribeButton.setImageResource(R.drawable.ic_notifications_none_black_24dp);


        subscribeButton.setOnClickListener(view -> {
            final Animation animShake = AnimationUtils.loadAnimation(itemView.getContext(),
                    R.anim.subscribe);
            subscribeButton.startAnimation(animShake);

            CourseVm courseVm = ViewModelProviders.of((AppCompatActivity) itemView.getContext()).get(CourseVm.class);
            if (myCourse.getFollowing()) {
                myCourse.setFollowing(false);
                FirebaseMessaging.getInstance().unsubscribeFromTopic(BuildConfig.BUILD_TYPE + myCourse.getId());
                courseVm.update(myCourse);
                subscribeButton.setImageResource(R.drawable.ic_notifications_none_black_24dp);
            } else {
                myCourse.setFollowing(true);
                FirebaseMessaging.getInstance().subscribeToTopic(BuildConfig.BUILD_TYPE + myCourse.getId());
                courseVm.update(myCourse);
                subscribeButton.setImageResource(R.drawable.ic_notifications_active_black_24dp);
            }
        });

    }

}
