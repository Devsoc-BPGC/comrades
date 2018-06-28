package com.macbitsgoa.comrades.coursematerial;

import android.os.AsyncTask;
import android.util.Log;

import com.macbitsgoa.comrades.BuildConfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import static android.os.Environment.getExternalStorageDirectory;
import static com.macbitsgoa.comrades.CHC.TAG_PREFIX;
import static com.macbitsgoa.comrades.ComradesConstants.DOWNLOAD_DIRECTORY;

public class DownloadFile extends AsyncTask<Void, Integer, Integer> {

    private static final String TAG = TAG_PREFIX + DownloadFile.class.getSimpleName();
    private final String downloadUrl;
    private final String fName;
    private final String path;
    private final String extension;

    DownloadFile(final String downloadUrl, final String fName, final String extension) {

        path = String.format("%s/%s/%s/",
                getExternalStorageDirectory(),
                DOWNLOAD_DIRECTORY,
                CourseActivity.courseId);
        this.downloadUrl = downloadUrl;
        this.fName = fName;
        this.extension = extension;
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
            if (!file.exists()) {
                file.mkdirs();
            }

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file

            final OutputStream output = new FileOutputStream(path + fName + extension);
            byte data[] = new byte[1024];
            int fileLength = connection.getContentLength();
            long total = 0;
            while ((count = input.read(data)) != -1) {

                total += count;

                if (fileLength > 0) {
                    publishProgress((int) (total * 100 / fileLength));
                }

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
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(final Integer result) {
        super.onPostExecute(result);
        // Removes the progress bar
        //TODO: update the dl status in db
        //TODO: move this launch file code to appropriate place i.e. in onClick of the file item
        /*final File file = new File(path + fName + extension);
        final Intent generic = new Intent();
        final Uri uri =
                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, file);
        generic.setAction(Intent.ACTION_VIEW);
        generic.setDataAndType(uri, mimeType);
        generic.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);*/
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Download complete");
        }
    }

    @Override
    protected void onProgressUpdate(final Integer... values) {
        super.onProgressUpdate(values);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format(Locale.ENGLISH, "download progress = %d", values[0]));
        }
        //TODO: update the dl status in db
    }
}


