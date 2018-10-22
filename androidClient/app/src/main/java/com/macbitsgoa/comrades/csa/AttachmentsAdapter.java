package com.macbitsgoa.comrades.csa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.macbitsgoa.comrades.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class AttachmentsAdapter extends RecyclerView.Adapter<AttachmentsAdapter.AttachmentsVH> {

    private ArrayList<String> fileNames,fileURLs;
    private Context context;

    public AttachmentsAdapter(ArrayList<String> fileNames, ArrayList<String> fileURLs, Context context) {
        this.fileNames = fileNames;
        this.fileURLs = fileURLs;
        this.context = context;
    }

    @Override
    public AttachmentsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AttachmentsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.vh_attachment,parent,false));
    }

    @Override
    public void onBindViewHolder(AttachmentsVH holder, int position) {
        holder.populateFile(fileNames.get(position),fileURLs.get(position));
    }

    @Override
    public int getItemCount() {
        return fileNames.size();
    }

    public class AttachmentsVH extends RecyclerView.ViewHolder
    {
        TextView AttachName;
        SimpleDraweeView AttachImage;

        public AttachmentsVH(View itemView) {
            super(itemView);
            AttachName = itemView.findViewById(R.id.AttachName);
            AttachImage = itemView.findViewById(R.id.AttachmentImg);
        }

        void populateFile(String filename,String fileURL)
        {
            AttachName.setText(filename);
            AttachImage.setImageURI(fileURL);

            // TODO Handle PDF files
        }
    }
}
