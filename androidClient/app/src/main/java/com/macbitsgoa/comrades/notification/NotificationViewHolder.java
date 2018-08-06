package com.macbitsgoa.comrades.notification;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.CourseVm;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    private TextView nameTv;
    private SimpleDraweeView unSubscribeButton;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.tv_name);
        unSubscribeButton = itemView.findViewById(R.id.button_unsubscribe);
    }

    public void populate(MyCourse course) {
        nameTv.setText(course.getName());
        unSubscribeButton.setOnClickListener(view -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(BuildConfig.BUILD_TYPE + course.getId());
            CourseVm courseVm = ViewModelProviders.of((AppCompatActivity) ((ContextThemeWrapper) itemView.getContext()).getBaseContext()).get(CourseVm.class);
            course.setFollowing(false);
            courseVm.update(course);
        });
    }

}
