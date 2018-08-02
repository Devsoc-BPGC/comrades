package com.macbitsgoa.comrades.homefragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


/**
 * @author aayush singla
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ChildEventListener {
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
                .limitToLast(25).addChildEventListener(this);
        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        recent.add(0, dataSnapshot.getValue(ItemRecent.class));
        recentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        ItemRecent itemRecent = dataSnapshot.getValue(ItemRecent.class);
        for (int i = 0; i < recent.size(); i++) {
            if (Objects.equals(recent.get(i).getFileId(), itemRecent.getFileId())) {
                recent.remove(i);
                recent.add(i, itemRecent);
                recentAdapter.notifyDataSetChanged();
                Log.e(TAG, "Child Updated");
                break;
            }
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        ItemRecent itemRecent = dataSnapshot.getValue(ItemRecent.class);
        for (int i = 0; i < recent.size(); i++) {
            if (Objects.equals(recent.get(i).getFileId(), itemRecent.getFileId())) {
                recent.remove(i);
                recentAdapter.notifyDataSetChanged();
                Log.e(TAG, "Child Updated");
                break;
            }
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.e(TAG, "Child Moved");

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.e(TAG, databaseError.getMessage());
    }
}
