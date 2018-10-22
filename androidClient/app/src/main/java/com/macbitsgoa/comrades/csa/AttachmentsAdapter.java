package com.macbitsgoa.comrades.csa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.macbitsgoa.comrades.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.AttachmentsVH> {

    private List<Attachment> attachments;

    public AttachmentsAdapter(final List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public AttachmentsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttachmentsVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vh_attachment, parent, false));
    }

    @Override
    public void onBindViewHolder(AttachmentsVH holder, int position) {
        holder.populateFile(attachments.get(position));
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    public class AttachmentsVH extends RecyclerView.ViewHolder {
        private TextView filenameTv;
        private SimpleDraweeView fileThumbSdv;

        public AttachmentsVH(View itemView) {
            super(itemView);
            filenameTv = itemView.findViewById(R.id.tv_filename);
            fileThumbSdv = itemView.findViewById(R.id.sdv_file_thumb);
        }

        void populateFile(Attachment attachment) {
            filenameTv.setText(attachment.name);
            fileThumbSdv.setImageURI(attachment.url);
        }
    }
}
