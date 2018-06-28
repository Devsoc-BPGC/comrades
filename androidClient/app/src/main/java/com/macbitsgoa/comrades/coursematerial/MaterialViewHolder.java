package com.macbitsgoa.comrades.coursematerial;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.snackbar.Snackbar;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import androidx.recyclerview.widget.RecyclerView;


/**
 * @author aayush singla
 */
public class MaterialViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvFileName;
    private final TextView tvOwnerName;
    private final View rootView;


    public MaterialViewHolder(final View itemView) {
        super(itemView);
        rootView = itemView;
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        tvOwnerName = itemView.findViewById(R.id.tv_owner_name);
    }

    public void populate(ItemCourseMaterial object) {
        tvOwnerName.setText("added by " + object.getAddedBy());
        tvFileName.setText(object.getFileName());

        rootView.setOnClickListener(view -> {
            final Context context = rootView.getContext();
            final boolean signedIn = GoogleSignIn.getLastSignedInAccount(context) != null;
            boolean storagePermission = true;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                storagePermission =
                        context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED;
            }

            if (signedIn && storagePermission) {
                final DownloadFile downloadFile =
                        new DownloadFile(object.getLink(), object.getFileName(), object.getExtension());
                downloadFile.execute();
            } else if (signedIn) {
                Snackbar.make(rootView, context.getString(R.string.storage_permission_needed), Snackbar.LENGTH_LONG)
                        .setAction(context.getString(R.string.allow), v ->
                                handleSignInAndStorage(context))
                        .show();
            } else {
                Snackbar.make(rootView, context.getString(R.string.login_to_download_file), Snackbar.LENGTH_LONG)
                        .setAction(context.getString(R.string.login), v ->
                                handleSignInAndStorage(context))
                        .show();
            }

        });

    }

    private static void handleSignInAndStorage(final Context context) {
        final Intent intent = new Intent(context, GetGoogleSignInActivity.class);
        context.startActivity(intent);
    }


}
