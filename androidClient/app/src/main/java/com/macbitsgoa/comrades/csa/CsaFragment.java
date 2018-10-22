package com.macbitsgoa.comrades.csa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.FbListener;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Rushikesh Jogdand.
 */
public class CsaFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_csa, container, false);
        RecyclerView csaNewsList = view.findViewById(R.id.csanewslist);
        csaNewsList.setLayoutManager(new LinearLayoutManager(getContext()));

        List<CsaNews> csaNews = new ArrayList<>();
        CsaNewsAdapter adapter = new CsaNewsAdapter(csaNews);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("debug").child("adminFeed");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new FbListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    CsaNews newsItem = child.getValue(CsaNews.class);
                    csaNews.add(newsItem);
                }
                csaNewsList.setAdapter(adapter);
            }
        });
        return view;
    }
}
