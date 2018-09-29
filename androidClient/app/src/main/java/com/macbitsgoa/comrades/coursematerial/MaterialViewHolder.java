package com.macbitsgoa.comrades.coursematerial;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.snackbar.Snackbar;
import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.GetGoogleSignInActivity;
import com.macbitsgoa.comrades.R;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;

import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import static com.macbitsgoa.comrades.coursematerial.Downloader.download;


/**
 * @author aayush singla
 */
public class MaterialViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvFileName;
    private final TextView tvOwnerName;
    private final CircularProgressBar donutProgress;
    private final TextView tvDownloadStatus;
    private final SimpleDraweeView iconDraweeView;
    /**
     * Invariant: This observer will always be null unless observing for actual
     * download progress.
     */
    private Observer<Integer> progressObserver = null;
    /**
     * This object will be null for inactive downloads.
     */
    private LiveData<Integer> progress = null;

    public MaterialViewHolder(final View itemView) {
        super(itemView);
        tvFileName = itemView.findViewById(R.id.tv_file_name);
        iconDraweeView = itemView.findViewById(R.id.icon);
        tvOwnerName = itemView.findViewById(R.id.tv_owner_name);
        donutProgress = itemView.findViewById(R.id.donut_progress);
        tvDownloadStatus = itemView.findViewById(R.id.status);
    }

    /**
     * updates the view in recycler with the data and sets onClick listener to it.
     *
     * @param material object of class @{@link CourseMaterial}
     */
    public void populate(CourseMaterial material) {
        tvOwnerName.setText("Added by " + material.addedBy);
        tvFileName.setText(material.fileName);

        switch (material.downloadStatus) {
            case CLICK_TO_OPEN:
                donutProgress.setProgress(100);
                tvDownloadStatus.setText("Click to Open");
                break;

            case WAIT_DOWNLOADING:
                progressObserver = i -> {
                    Log.e("mac", "i = " + i);
                    donutProgress.setProgress(i);
                };
                progress = new MaterialRepository(itemView.getContext()).getDownloadProgress(material._id);
                progress.observeForever(progressObserver);
                tvDownloadStatus.setText("Downloading");
                break;

            case CLICK_TO_DOWNLOAD:
                donutProgress.setProgress(0);
                tvDownloadStatus.setText("Click to Download");
                break;
        }

        itemView.setOnClickListener(v -> onClick(material));
        if (material.iconLink != null) {
            iconDraweeView.setImageURI(material.iconLink);
        }
    }

    public void onClick(CourseMaterial data) {
        final Context context = itemView.getContext();
        final boolean signedIn = GoogleSignIn.getLastSignedInAccount(context) != null;
        boolean storagePermission = true;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            storagePermission =
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED;
        }

        if (signedIn && storagePermission) {
            switch (data.downloadStatus) {
                case CLICK_TO_DOWNLOAD:
                    download(data);
                    break;
                case WAIT_DOWNLOADING:
                    break;
                case CLICK_TO_OPEN:
                    openFile(data);
                    break;
            }
        } else if (signedIn) {
            Snackbar.make(itemView, context.getString(R.string.storage_permission_needed),
                    Snackbar.LENGTH_LONG)
                    .setAction(context.getString(R.string.allow), v ->
                            handleSignInAndStorage(context))
                    .show();
        } else {
            Snackbar.make(itemView, context.getString(R.string.login_to_download_file),
                    Snackbar.LENGTH_LONG)
                    .setAction(context.getString(R.string.login), v ->
                            handleSignInAndStorage(context))
                    .show();
        }
    }

    private static void handleSignInAndStorage(final Context context) {
        final Intent intent = new Intent(context, GetGoogleSignInActivity.class);
        context.startActivity(intent);
    }

    private void openFile(CourseMaterial obj) {
        final File file = new File(obj.getFilePath() + obj.getFileName() + obj.getExtension());
        final Intent generic = new Intent();
        final Uri uri =
                FileProvider.getUriForFile(itemView.getContext(), BuildConfig.APPLICATION_ID, file);
        generic.setAction(Intent.ACTION_VIEW);
        generic.setDataAndType(uri, obj.getMimeType());
        generic.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        itemView.getContext().startActivity(generic);
    }

    public void cleanUp() {
        if (progress != null) {
            progress.removeObserver(progressObserver);
            progressObserver = null;
            progress = null;
        }
    }
}
