package com.macbitsgoa.comrades.homefragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.Collections;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


/**
 * @author aayush singla
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ValueEventListener {
    private ArrayList<ItemRecent> recent = new ArrayList<>();
    private RecentAdapter recentAdapter;
    private static final String TAG = TAG_PREFIX + HomeFragment.class.getSimpleName();

    public static Fragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recentAdapter = new RecentAdapter(recent);
        RecyclerView recentRv = view.findViewById(R.id.rv_recent);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recentRv.setLayoutManager(linearLayoutManager);
        recentRv.setAdapter(recentAdapter);
        FirebaseDatabase.getInstance().getReference("/recents/").orderByChild("timeStamp")
                .limitToLast(25).addValueEventListener(this);
        return view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        recent.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren())
            recent.add(snapshot.getValue(ItemRecent.class));
        Collections.reverse(recent);
        recentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getMessage());
    }
}
