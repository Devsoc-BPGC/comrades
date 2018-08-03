package com.macbitsgoa.comrades.notification;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder {
    private TextView nameTv;
    private Button unSubscribeButton;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.tv_name);
        unSubscribeButton = itemView.findViewById(R.id.button_unsubscribe);
    }

    public void populate(SubscribedCourses subscribedCourse) {
        nameTv.setText(subscribedCourse.getName());
        unSubscribeButton.setOnClickListener(view -> {
            Log.e("context.notiVH", ((ContextThemeWrapper) itemView.getContext()).getBaseContext().toString());
            NotificationVm notificationVm = ViewModelProviders.of((AppCompatActivity) ((ContextThemeWrapper) itemView.getContext()).getBaseContext()).get(NotificationVm.class);
            notificationVm.delete(subscribedCourse);
        });
    }

}
