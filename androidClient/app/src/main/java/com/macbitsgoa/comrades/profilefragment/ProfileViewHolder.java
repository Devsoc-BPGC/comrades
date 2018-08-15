package com.macbitsgoa.comrades.profilefragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;
import com.macbitsgoa.comrades.eateries.EateriesActivity;
import com.macbitsgoa.comrades.subscribedcourses.SubscribedCoursesActivity;

import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.macbitsgoa.comrades.HomeActivity.SETTINGS;

/**
 * @author aayush singla
 */

public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private SharedPreferences prefs;
    private TextView tvProfile;
    private SimpleDraweeView rightArrow;
    private Switch radioButton;

    public ProfileViewHolder(View view) {
        super(view);
        tvProfile = view.findViewById(R.id.tv_list);
        radioButton = itemView.findViewById(R.id.switch_notifications);
        rightArrow = itemView.findViewById(R.id.right_arrow);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (tvProfile.getText().toString()) {
            case "My Courses":
                Intent intent = new Intent(view.getContext(), SubscribedCoursesActivity.class);
                view.getContext().startActivity(intent);
                break;
            case "Menus":
                Intent intentEatery = new Intent(view.getContext(), EateriesActivity.class);
                view.getContext().startActivity(intentEatery);
                break;
            case "Imp. Documents":
                CourseActivity.launchCourse(view.getContext(), "-LJUx7EJ78rkt41kHNFs", "Imp. Docs");
                break;
            case "My Uploads":
                Toast.makeText(view.getContext(), "Coming Soon", Toast.LENGTH_LONG).show();
                break;
            case "Terms And Conditions":
                View v = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.layout_terms_conditions, null);
                PopupWindow popupWindow = new PopupWindow(v, MATCH_PARENT, MATCH_PARENT, true);
                popupWindow.setAnimationStyle(R.style.animation);
                popupWindow.showAtLocation(view.getRootView(),
                        Gravity.CENTER, 0, 0);
                break;
            case "Privacy":
                View v1 = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.layout_privacy, null);
                PopupWindow popupWindowPrivacy = new PopupWindow(v1, MATCH_PARENT, MATCH_PARENT, true);
                popupWindowPrivacy.setAnimationStyle(R.style.animation);
                popupWindowPrivacy.showAtLocation(view.getRootView(),
                        Gravity.CENTER, 0, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(SETTINGS, b);
        edit.apply();
    }

    public void populate(String s) {
        tvProfile.setText(s);
        if (Objects.equals(s, "Notifications")) {
            radioButton.setVisibility(View.VISIBLE);
            rightArrow.setVisibility(View.GONE);
            prefs = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            radioButton.setChecked(prefs.getBoolean(SETTINGS, true));
            radioButton.setTextOn("On");
            radioButton.setTextOff("Off");
            radioButton.setOnCheckedChangeListener(this);
        }
    }
}
