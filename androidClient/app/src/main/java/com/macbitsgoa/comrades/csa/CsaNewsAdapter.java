package com.macbitsgoa.comrades.csa;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.macbitsgoa.comrades.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.csa.CsaMsgDetailActivity.EXTRA_KEY_NEWS;

public class CsaNewsAdapter extends RecyclerView.Adapter<CsaNewsAdapter.CsaNewsVH> {

    private List<CsaNews> NewsItems;

    public CsaNewsAdapter(List<CsaNews> newsItems) {
        NewsItems = newsItems;
    }

    @Override
    public CsaNewsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CsaNewsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_csanews, parent, false));
    }

    @Override
    public void onBindViewHolder(CsaNewsVH holder, int position) {
        holder.populateCsaNews(NewsItems.get(position));
    }

    @Override
    public int getItemCount() {
        return NewsItems.size();
    }

    public class CsaNewsVH extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private TextView nameTv;
        private TextView contentTv;
        private Button readMoreBtn;

        public CsaNewsVH(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.tv_title);
            nameTv = itemView.findViewById(R.id.tv_sender_name);
            contentTv = itemView.findViewById(R.id.tv_content);
            readMoreBtn = itemView.findViewById(R.id.btn_read_more);
        }

        void populateCsaNews(CsaNews csaNews) {
            titleTv.setText(csaNews.title);
            nameTv.setText(String.format("%s, %s | %s", csaNews.name, csaNews.post, csaNews.timestamp));
            contentTv.setText(csaNews.content);

            readMoreBtn.setOnClickListener(view -> {
                Intent openMsgDetails = new Intent(itemView.getContext(), CsaMsgDetailActivity.class);
                openMsgDetails.putExtra(EXTRA_KEY_NEWS, new Gson().toJson(csaNews));
                itemView.getContext().startActivity(openMsgDetails);
            });

        }
    }
}
