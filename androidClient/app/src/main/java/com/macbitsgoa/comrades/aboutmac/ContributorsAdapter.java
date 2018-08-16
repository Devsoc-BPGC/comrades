package com.macbitsgoa.comrades.aboutmac;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.macbitsgoa.comrades.CHCKt;
import com.macbitsgoa.comrades.FbListener;
import com.macbitsgoa.comrades.FirebaseKeysKt;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ContributorsAdapter extends RecyclerView.Adapter<ContributorsVh> {

    private ArrayList<String> contributors;

    ContributorsAdapter() {
        this.contributors = new ArrayList<>();
        DatabaseReference dbRef = CHCKt.getFirebaseRootRef().child(FirebaseKeysKt.CONTRIBUTORS);
        dbRef.keepSynced(true);
        dbRef.addValueEventListener(new FbListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contributors = new ArrayList<>();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                    contributors.add(childSnap.child(FirebaseKeysKt.CONTRIBUTORS_NAME).getValue(String.class));
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ContributorsVh onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContributorsVh(LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_contributors, parent, false));
    }

    @Override
    public void onBindViewHolder(ContributorsVh holder, int position) {
        holder.nameTextView.setText(contributors.get(position));
    }

    @Override
    public int getItemCount() {
        return contributors.size();
    }


}
