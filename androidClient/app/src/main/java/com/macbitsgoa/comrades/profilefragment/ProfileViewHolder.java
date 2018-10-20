package com.macbitsgoa.comrades.profilefragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;
import com.macbitsgoa.comrades.eateries.EateriesActivity;
import com.macbitsgoa.comrades.subscribedcourses.SubscribedCoursesActivity;

import androidx.recyclerview.widget.RecyclerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * @author aayush singla
 */

public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView linkTv;

    public ProfileViewHolder(View view) {
        super(view);
        linkTv = view.findViewById(R.id.tv_link);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (linkTv.getText().toString()) {
            case "My Courses": {
                Intent intent = new Intent(view.getContext(), SubscribedCoursesActivity.class);
                view.getContext().startActivity(intent);
            }
            break;
            case "Menus": {
                Intent intentEatery = new Intent(view.getContext(), EateriesActivity.class);
                view.getContext().startActivity(intentEatery);
            }
            break;
            case "Imp. Documents": {
                CourseActivity.launchCourse(view.getContext(), "-LJUx7EJ78rkt41kHNFs", "Imp. Docs");
            }
            break;
            case "My Uploads": {
                Toast.makeText(view.getContext(), "Coming Soon", Toast.LENGTH_LONG).show();
            }
            break;
            case "Terms And Conditions": {
                View v = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.layout_terms_conditions, null);
                PopupWindow popupWindow = new PopupWindow(v, MATCH_PARENT, MATCH_PARENT, true);
                popupWindow.setAnimationStyle(R.style.animation);
                popupWindow.showAtLocation(view.getRootView(),
                        Gravity.CENTER, 0, 0);
            }
            break;
            case "Privacy Policy": {
                View v1 = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.layout_privacy, null);
                PopupWindow popupWindowPrivacy = new PopupWindow(v1, MATCH_PARENT, MATCH_PARENT, true);
                popupWindowPrivacy.setAnimationStyle(R.style.animation);
                popupWindowPrivacy.showAtLocation(view.getRootView(),
                        Gravity.CENTER, 0, 0);
            }
            break;
            case "Notifications": {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, itemView.getContext().getPackageName());
                    itemView.getContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + itemView.getContext().getPackageName()));
                    itemView.getContext().startActivity(intent);
                }
            }
            break;
            default:
                break;
        }
    }

    public void populate(String s) {
        linkTv.setText(s);
    }
}
