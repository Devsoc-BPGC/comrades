package com.macbitsgoa.comrades;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;

/**
 * @author aayush singla
 */

public class MySimpleDraweeView extends SimpleDraweeView implements View.OnClickListener {

    private String uuid;
    private static final String TAG = TAG_PREFIX + MySimpleDraweeView.class.getSimpleName();

    public MySimpleDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setParam(String uuid) {
        this.uuid = uuid;
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.view_profile, null);
        TextView tv_name = v.findViewById(R.id.tv_user_name);
        TextView tv_authority = v.findViewById(R.id.tv_user_authority);
        TextView tv_uploads = v.findViewById(R.id.tv_uploads);
        TextView tv_score = v.findViewById(R.id.tv_score);
        TextView tv_rank = v.findViewById(R.id.tv_rank);
        SimpleDraweeView draweeView = v.findViewById(R.id.userImage);

        v.setBackgroundColor(getResources().getColor(R.color.transparent_black));
        tv_authority.setTextColor(getResources().getColor(R.color.colorAccentContrast));
        tv_uploads.setTextColor(getResources().getColor(R.color.colorAccentContrast));
        tv_score.setTextColor(getResources().getColor(R.color.colorAccentContrast));
        tv_name.setTextColor(getResources().getColor(R.color.colorAccentContrast));
        tv_rank.setTextColor(getResources().getColor(R.color.colorAccentContrast));


        PopupWindow popupWindow = new PopupWindow(v, MATCH_PARENT, MATCH_PARENT, true);
        popupWindow.setAnimationStyle(R.style.animation);
        popupWindow.showAtLocation(getRootView(),
                Gravity.CENTER, 0, 0);
        if (uuid != null) {
            tv_authority.setOnClickListener(null);
            FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE)
                    .child("/users/" + uuid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            tv_name.setText(dataSnapshot.child("name").getValue(String.class));
                            tv_authority.setText(dataSnapshot.child("authority").getValue(String.class));
                            tv_uploads.setText(dataSnapshot.child("uploads").getValue(Long.class) + "");
                            tv_score.setText(dataSnapshot.child("score").getValue(Long.class) + "");
                            tv_rank.setText(dataSnapshot.child("rank").getValue(Long.class) + "");
                            draweeView.setImageURI(dataSnapshot.child("photoUrl").getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            tv_authority.setText(R.string.warning_internet);
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });
        } else {
            tv_authority.setOnClickListener(view1 -> {
                final Intent intent = new Intent(getContext(), GetGoogleSignInActivity.class);
                view1.getContext().startActivity(intent);
            });
            draweeView.setImageResource(R.drawable.ic_profile_white);
        }
    }
}
