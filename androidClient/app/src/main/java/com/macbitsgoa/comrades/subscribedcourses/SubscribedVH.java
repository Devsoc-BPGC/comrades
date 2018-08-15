package com.macbitsgoa.comrades.subscribedcourses;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.courselistfragment.CourseVm;
import com.macbitsgoa.comrades.courselistfragment.MyCourse;
import com.macbitsgoa.comrades.coursematerial.CourseActivity;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribedVH extends RecyclerView.ViewHolder {
    private final TextView nameTv;
    private final ImageButton unSubscribeButton;

    public SubscribedVH(final View itemView) {
        super(itemView);
        nameTv = itemView.findViewById(R.id.tv_name);
        unSubscribeButton = itemView.findViewById(R.id.button_unsubscribe);
    }

    public void populate(final MyCourse course) {
        nameTv.setText(course.name);
        itemView.setOnClickListener(view -> CourseActivity.launchCourse(itemView.getContext(),
                course._id, course.name));

        unSubscribeButton.setOnClickListener(view -> {
            // TODO: show snack bar with option to revert the un-subscription
            FirebaseMessaging.getInstance().unsubscribeFromTopic(BuildConfig.BUILD_TYPE + course._id);
            final CourseVm courseVm = ViewModelProviders.of((FragmentActivity) view.getContext()).get(CourseVm.class);
            course.setFollowing(false);
            courseVm.update(course);
        });
    }

}
