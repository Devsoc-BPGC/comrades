package com.macbitsgoa.comrades.csa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Rushikesh Jogdand.
 */
public class CsaFragment extends Fragment {

    private RecyclerView csaNewsList;
    private DatabaseReference databaseReference;
    private ArrayList<CsaNews> csaNews;

    private CsaNewsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_csa, container, false);
        csaNewsList = view.findViewById(R.id.csanewslist);
        csaNewsList.setLayoutManager(new LinearLayoutManager(getContext()));
        csaNews = new ArrayList<>();
        adapter = new CsaNewsAdapter(csaNews,getContext());

        loadCsaNews();

        return view;
    }

    private void loadCsaNews()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("debug").child("adminFeed");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    System.out.println("CHILD LOADED FROM FB>>>>>>>>>>"+child);
                    CsaNews newsItem = new CsaNews();

                    newsItem.setEventName(child.child("title").getValue(String.class));
                    newsItem.setEventDescLong(child.child("content").getValue(String.class));
                    newsItem.setSenderName(child.child("name").getValue(String.class));
                    newsItem.setSenderPost(child.child("post").getValue(String.class));
                    newsItem.setTimeStamp(child.child("timestamp").getValue(String.class));
                    newsItem.setDpURL(child.child("profileImageURL").getValue(String.class));

                    if(newsItem.getEventDescLong().length()>200)
                    {
                        newsItem.setEventDescShort(newsItem.getEventDescLong().substring(0,200)+"...");
                    }
                    else
                    {
                        newsItem.setEventDescShort(newsItem.getEventDescLong());
                    }

                    ArrayList<String> filenames = new ArrayList<>();
                    ArrayList<String> fileurls = new ArrayList<>();

                    for(DataSnapshot attachment : child.child("attachment").getChildren())
                    {
                        filenames.add(attachment.child("name").getValue(String.class));
                        fileurls.add(attachment.child("url").getValue(String.class));
                    }

                    newsItem.setFileNames(filenames);
                    newsItem.setFileURLs(fileurls);

                    System.out.println("NEWSITEM>>>>>>>>>>"+newsItem);

                    csaNews.add(newsItem);
                }

                csaNewsList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("ERROR>>>>>>>>>>>"+databaseError.getMessage());
            }
        });
    }
}
