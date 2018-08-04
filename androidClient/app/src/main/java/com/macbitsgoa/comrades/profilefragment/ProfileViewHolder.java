package com.macbitsgoa.comrades.profilefragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.notification.NotificationSettings;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author aayush singla
 */

public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView tvProfile;

    public ProfileViewHolder(View view) {
        super(view);
        tvProfile = view.findViewById(R.id.tv_list);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        TextView tv = view.findViewById(R.id.tv_list);
        switch (tv.getText().toString()) {
            case "Notification Settings":
                Intent intent = new Intent(view.getContext(), NotificationSettings.class);
                view.getContext().startActivity(intent);
                break;
            default:
                break;
        }
    }
}