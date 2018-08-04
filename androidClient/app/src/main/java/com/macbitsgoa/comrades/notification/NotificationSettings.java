package com.macbitsgoa.comrades.notification;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class NotificationSettings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private RecyclerView rv_subscribed_courses;
    public static String SETTINGS = "NotificationSetting";
    private ArrayList<SubscribedCourses> course = new ArrayList<>(0);
    private NotificationAdapter notificationAdapter;
    private LinearLayout viewNotificationsOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        viewNotificationsOff = findViewById(R.id.notifications_off);
        rv_subscribed_courses = findViewById(R.id.rv_notifications);
        Switch radioButton = findViewById(R.id.switch_notifications);
        radioButton.setOnCheckedChangeListener(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        radioButton.setChecked(prefs.getBoolean(SETTINGS, true));
        showViews(prefs.getBoolean(SETTINGS, true));
        rv_subscribed_courses.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(course);
        rv_subscribed_courses.setAdapter(notificationAdapter);

        NotificationVm notificationVm = ViewModelProviders.of(this).get(NotificationVm.class);
        notificationVm.getAll().observe(this, courses -> {
            course.clear();
            course.addAll(courses);
            notificationAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(SETTINGS, checked);
        edit.apply();
        showViews(checked);
    }

    private void showViews(Boolean checked) {
        if (checked) {
            rv_subscribed_courses.setVisibility(View.VISIBLE);
            viewNotificationsOff.setVisibility(View.GONE);
        } else {
            rv_subscribed_courses.setVisibility(View.GONE);
            viewNotificationsOff.setVisibility(View.VISIBLE);
        }
    }
}
