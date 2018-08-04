package com.macbitsgoa.comrades.notification;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.R;

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

    public void populate(SubscribedCourses subscribedCourse) {
        nameTv.setText(subscribedCourse.getName());
        unSubscribeButton.setOnClickListener(view -> {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(BuildConfig.BUILD_TYPE + subscribedCourse.getId());
            NotificationVm notificationVm = ViewModelProviders.of((AppCompatActivity) ((ContextThemeWrapper) itemView.getContext()).getBaseContext()).get(NotificationVm.class);
            notificationVm.delete(subscribedCourse);
        });
    }

}
