package com.macbitsgoa.comrades.coursematerial;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.snackbar.Snackbar;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;

import java.io.File;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @author aayush singla
 */
public class MaterialViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvFileName;
    private final TextView tvOwnerName;
    private final View rootView;
    private final DonutProgress donutProgress;
    private final TextView tvDownloadStatus;


    public MaterialViewHolder(final View itemView) {
        super(itemView);
        rootView = itemView;
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        tvOwnerName = itemView.findViewById(R.id.tv_owner_name);
        donutProgress = itemView.findViewById(R.id.donut_progress);
        tvDownloadStatus = itemView.findViewById(R.id.status);
    }

    /**
     * updates the view in recycler with the data and sets onClick listener to it.
     *
     * @param data: object of class @{@link ItemCourseMaterial}
     */
    public void populate(ItemCourseMaterial data) {
        tvOwnerName.setText("added by " + data.getAddedBy());
        tvFileName.setText(data.getFileName());

        if (data.getFileAvailable()) {
            donutProgress.setDonut_progress("100");
            tvDownloadStatus.setText("click to open");
        } else {
            donutProgress.setDonut_progress(String.valueOf(data.getProgress()));
            tvDownloadStatus.setText(data.getDownloadStatus());
        }

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
                if (data.getFileAvailable()) {
                    openFile(data);
                } else {
                    tvDownloadStatus.setText(R.string.download_status_waiting);
                    final Intent downloadIntent =
                            DownloadService.makeIntent(itemView.getContext(), data.getLink(),
                                    data.getFileName(), data.getExtension(), data.getFilePath(),
                                    data.getId(), data.getFileSize());
                    itemView.getContext().startService(downloadIntent);
                }
            } else if (signedIn) {
                Snackbar.make(rootView, context.getString(R.string.storage_permission_needed),
                        Snackbar.LENGTH_LONG)
                        .setAction(context.getString(R.string.allow), v ->
                                handleSignInAndStorage(context))
                        .show();
            } else {
                Snackbar.make(rootView, context.getString(R.string.login_to_download_file),
                        Snackbar.LENGTH_LONG)
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

    private void openFile(ItemCourseMaterial obj) {
        final File file = new File(obj.getFilePath() + obj.getFileName() + obj.getExtension());
        final Intent generic = new Intent();
        final Uri uri =
                FileProvider.getUriForFile(itemView.getContext(), BuildConfig.APPLICATION_ID, file);
        generic.setAction(Intent.ACTION_VIEW);
        generic.setDataAndType(uri, obj.getMimeType());
        generic.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        itemView.getContext().startActivity(generic);
    }



}
