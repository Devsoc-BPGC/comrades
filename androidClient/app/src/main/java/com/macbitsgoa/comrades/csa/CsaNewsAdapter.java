package com.macbitsgoa.comrades.csa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CsaNewsAdapter extends RecyclerView.Adapter<CsaNewsAdapter.CsaNewsVH> {

    private ArrayList<CsaNews> NewsItems;
    private Context context;

    public CsaNewsAdapter(ArrayList<CsaNews> newsItems, Context context) {
        NewsItems = newsItems;
        this.context = context;
    }

    @Override
    public CsaNewsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CsaNewsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_csanews,parent,false));
    }

    @Override
    public void onBindViewHolder(CsaNewsVH holder, int position) {
        holder.populateCsaNews(NewsItems.get(position));
    }

    @Override
    public int getItemCount() {
        return NewsItems.size();
    }

    public class CsaNewsVH extends RecyclerView.ViewHolder
    {
        TextView eventName,senderName,senderPost,eventDetails,timeStamp;

        Button readMore;

        public CsaNewsVH(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.EventTitle);
            senderName = itemView.findViewById(R.id.SenderName);
            senderPost = itemView.findViewById(R.id.SenderPost);
            eventDetails = itemView.findViewById(R.id.EventDetails);
            timeStamp = itemView.findViewById(R.id.NewsTimeStamp);
            readMore = itemView.findViewById(R.id.ReadMore);
        }

        void populateCsaNews(CsaNews csaNews)
        {
            eventName.setText(csaNews.getEventName());
            senderName.setText(csaNews.getSenderName());
            senderPost.setText(csaNews.getSenderPost());
            eventDetails.setText(csaNews.getEventDescShort());
            timeStamp.setText(csaNews.getTimeStamp());

            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openMsgDetails = new Intent(context,CsaMsgDetailActivity.class);
                    openMsgDetails.putExtra("eventName",csaNews.getEventName());
                    openMsgDetails.putExtra("senderName",csaNews.getSenderName());
                    openMsgDetails.putExtra("senderPost",csaNews.getSenderPost());
                    openMsgDetails.putExtra("timeStamp",csaNews.getTimeStamp());
                    openMsgDetails.putExtra("dpURL",csaNews.getDpURL());
                    openMsgDetails.putExtra("eventDetails",csaNews.getEventDescLong());
                    openMsgDetails.putStringArrayListExtra("fileNames",csaNews.getFileNames());
                    openMsgDetails.putStringArrayListExtra("fileURLs",csaNews.getFileURLs());
                    context.startActivity(openMsgDetails);
                }
            });

        }
    }
}
