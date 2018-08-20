package com.macbitsgoa.comrades.profilefragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.ranks.RankActivity;
import com.macbitsgoa.comrades.ranks.UserObject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView tvUserName;
    private TextView tvUserAuthority;
    private TextView tvScore;
    private TextView tvUploads;
    private TextView tvRank;
    private SimpleDraweeView tvUserImage;
    private static final String TAG = TAG_PREFIX + ProfileFragment.class.getSimpleName();


    public static Fragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserAuthority = view.findViewById(R.id.tv_user_authority);
        tvScore = view.findViewById(R.id.tv_score);
        tvRank = view.findViewById(R.id.tv_rank);
        tvUploads = view.findViewById(R.id.tv_uploads);
        tvUserImage = view.findViewById(R.id.userImage);
        LinearLayout rankList = view.findViewById(R.id.rankList);
        rankList.setOnClickListener(view1 -> startActivity(new Intent(getContext(), RankActivity.class)));
        RecyclerView recyclerView = view.findViewById(R.id.rv_profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new ProfileAdapter());
    }

    private void initiateView(UserObject obj) {
        tvUserName.setText(obj.getName());
        tvUserAuthority.setText(obj.getAuthority());
        tvScore.setText(String.valueOf(obj.getScore()));
        tvUploads.setText(String.valueOf(obj.getUploads()));
        tvRank.setText(String.valueOf(obj.getRank()));
        tvUserImage.setImageURI(obj.getPhotoUrl());
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            tvUserAuthority.setOnClickListener(null);
            FirebaseDatabase.getInstance().getReference().child(BuildConfig.BUILD_TYPE)
                    .child("/users/").child(firebaseUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserObject obj = dataSnapshot.getValue(UserObject.class);
                            initiateView(obj);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, databaseError.getMessage());
                        }
                    });
        } else {
            tvUserImage.setImageResource(R.drawable.ic_profile_black);
            tvUserAuthority.setOnClickListener(view1 -> {
                final Intent intent = new Intent(getContext(), GetGoogleSignInActivity.class);
                startActivity(intent);
            });
        }
    }
}
