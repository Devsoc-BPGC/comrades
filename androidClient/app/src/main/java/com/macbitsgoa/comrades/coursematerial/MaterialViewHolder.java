package com.macbitsgoa.comrades.coursematerial;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import androidx.recyclerview.widget.RecyclerView;


/**
 * @author aayush singla
 */

public class MaterialViewHolder extends RecyclerView.ViewHolder {
    public TextView tvFileName;
    public TextView tvOwnerName;
    public View rootView;


    public MaterialViewHolder(final View itemView) {
        super(itemView);
        rootView = itemView;
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        tvOwnerName = itemView.findViewById(R.id.tv_owner_name);
    }

    public void populate(final String fileUrl, final String fName, final String mimeType) {

        rootView.setOnClickListener(view -> {
            final Context context = rootView.getContext();
            if (GoogleSignIn.getLastSignedInAccount(context) == null) {
                //  && context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
                final Intent intent = new Intent(context, GetGoogleSignInActivity.class);
                context.startActivity(intent);
                return;
            }

            final DownloadFile downloadFile = new DownloadFile(context, fileUrl, fName, mimeType);
            downloadFile.execute();

        });

    }


}
