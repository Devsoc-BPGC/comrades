package com.macbitsgoa.comrades.ref;

import android.os.AsyncTask;
import android.util.Log;

import com.macbitsgoa.comrades.BuildConfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.annotation.Nullable;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Downloads the file.
 * @author aayush singla
 */
@SuppressWarnings("WeakerAccess")
public class DownloadFile extends AsyncTask<String, String, String> {
    private static final String DOWNLOAD_DIRECTORY = "/StudentCompanion/";
    private static final String TAG = "MAC->" + DownloadFile.class.getSimpleName();
    private final String filename;

    @SuppressWarnings("WeakerAccess")
    public DownloadFile(final String filename) {
        this.filename = filename;
    }

    @Nullable
    @Override
    protected String doInBackground(final String... fileUrl) {
        int count;
        final URL url;
        final File file = new File(getExternalStorageDirectory() + DOWNLOAD_DIRECTORY);
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();
        try {
            url = new URL(fileUrl[0]);
        } catch (final MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            return null;
        }
        try {
            final URLConnection connection;
            final InputStream input;
            final OutputStream output;
            final int lengthOfFile;
            final byte[] data;
            output = new FileOutputStream(getExternalStorageDirectory()
                    + DOWNLOAD_DIRECTORY + filename);
            connection = url.openConnection();
            connection.connect();
            input = new BufferedInputStream(url.openStream(), 8192);

            lengthOfFile = connection.getContentLength();

            data = new byte[1024];

            long total = 0;
            for (count = input.read(data); count != -1; count = input.read(data)) {
                total += count;
                publishProgress(String.valueOf((int) ((total * 100) / lengthOfFile)));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (final IOException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            return null;
        }
        return fileUrl[0];
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Download starting");
        }
    }

    @Override
    protected void onPostExecute(final String fileUrl) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "File with url " + fileUrl + " downloaded");
        }
    }

    protected void onProgressUpdate(final String... progress) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, progress[0] + "%");
        }
    }

}

