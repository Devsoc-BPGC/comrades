package com.macbitsgoa.comrades.coursematerial;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.macbitsgoa.comrades.BuildConfig;
import com.macbitsgoa.comrades.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import static android.os.Environment.getExternalStorageDirectory;
import static com.macbitsgoa.comrades.CHC.TAG_PREFIX;

public class DownloadFile extends AsyncTask<Void, Integer, Integer> {

    private static final String TAG = TAG_PREFIX + DownloadFile.class.getSimpleName();
    private final NotificationManagerCompat mNotifyManager;
    private final NotificationCompat.Builder builder;
    private final String downloadUrl;
    private final String fName;
    private final int id = 1;
    private final Context context;
    private final String path;
    private final String mimeType;

    DownloadFile(final Context context, final String downloadUrl, final String fName,
                 final String mimeType) {

        path = getExternalStorageDirectory() +
                context.getString(R.string.download_directory) + CourseActivity.courseId + "/";
        mNotifyManager = NotificationManagerCompat.from(context);
        this.downloadUrl = downloadUrl;
        this.fName = fName;
        this.context = context;
        this.mimeType = mimeType;

        builder = new NotificationCompat.Builder(context, "progress");
        builder.setContentTitle("Download in progress")
                .setContentText("Starting")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // Displays the progress bar for the first time.
        Toast.makeText(context, "Download Started", Toast.LENGTH_LONG).show();
        builder.setProgress(100, 0, false);
        mNotifyManager.notify(id, builder.build());
    }

    @Override
    protected void onProgressUpdate(final Integer... values) {
        // Update progress
        if (values[0] % 2 == 0) {
            builder.setProgress(100, values[0], false);
            builder.setContentText(values[0] + "%");
            mNotifyManager.notify(id, builder.build());
            super.onProgressUpdate(values);
        }
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected Integer doInBackground(final Void... params) {
        int count;
        try {

            final URL url = new URL(downloadUrl);
            final URLConnection connection = url.openConnection();
            connection.connect();

            File file = new File(path);
            if (!file.exists())
                file.mkdirs();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file

            final OutputStream output = new FileOutputStream(path + fName);
            byte data[] = new byte[1024];
            int fileLength = connection.getContentLength();
            long total = 0;
            while ((count = input.read(data)) != -1) {

                total += count;

                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));

                // writing data to file
                output.write(data, 0, count);

            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (final Exception e) {
            Log.e(TAG + ":Error: ", e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(final Integer result) {
        super.onPostExecute(result);
        // Removes the progress bar
        setIntentAction();

    }

    private void setIntentAction() {
        final File file = new File(path + fName);
        final Intent generic = new Intent();
        final Uri uri =
                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);

        generic.setAction(Intent.ACTION_VIEW);
        generic.setDataAndType(uri, mimeType);
        generic.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        final PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, generic, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentTitle(fName)
                .setContentText("Download Complete.")
                .setOngoing(false)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setProgress(0, 0, false);
        mNotifyManager.notify(id, builder.build());

        Toast.makeText(context, "Download Complete", Toast.LENGTH_LONG).show();

    }

}


