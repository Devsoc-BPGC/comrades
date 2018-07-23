package com.macbitsgoa.comrades.coursematerial;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class DownloadService extends IntentService {

    private static final String KEY_FILE_PATH = "filepath";
    private static final String KEY_DOWNLOAD_URL = "downloadUrl";
    private static final String KEY_FILE_NAME = "filename";
    private static final String KEY_FILE_EXTENSION = "extension";
    private static final String TAG = TAG_PREFIX + DownloadService.class.getSimpleName();
    private static final String KEY_ITEM_ID = "itemId";
    private static final String KEY_ITEM_POSITION = "itemPosition";
    private static final String KEY_FILE_SIZE = "fileSize";
    public static final String ACTION = "com.macbitsgoa.comrades.action.ACTION_TAG";
    private static final String RESULT_CODE = "resultCode";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            String id = intent.getStringExtra(KEY_ITEM_ID);
            String downloadUrl = intent.getStringExtra(KEY_DOWNLOAD_URL);
            String path = intent.getStringExtra(KEY_FILE_PATH);
            String fileName = intent.getStringExtra(KEY_FILE_NAME);
            String extension = intent.getStringExtra(KEY_FILE_EXTENSION);
            Long fileLength = intent.getLongExtra(KEY_FILE_SIZE, 5454544);
            int position = intent.getIntExtra(KEY_ITEM_POSITION, 0);

            Bundle startBundle = new Bundle();
            startBundle.putString("id", id);
            startBundle.putInt("position", position);
            startBundle.putInt(RESULT_CODE, 0);
            Intent startIntent = new Intent(ACTION);
            startIntent.putExtras(startBundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(startIntent);

            int count;
            try {

                final URL url = new URL(downloadUrl);
                final URLConnection connection = url.openConnection();
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.connect();

                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                final OutputStream output = new FileOutputStream(path + fileName + extension);
                byte[] data = new byte[1024];
                long total = 0;
                int progress;
                while ((count = input.read(data)) != -1) {
                    Log.e("count", count + "");
                    total += count;

                    progress = (int) (total * 100 / fileLength);
                    if (fileLength > 0) {
                        Bundle progressBundle = new Bundle();
                        progressBundle.putString("id", id);
                        progressBundle.putInt("position", position);
                        progressBundle.putInt("progress", progress);
                        progressBundle.putInt(RESULT_CODE, 1);
                        Intent messageIntent = new Intent(ACTION);
                        messageIntent.putExtras(progressBundle);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

                        // writing data to file
                        output.write(data, 0, count);

                    }
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();

            } catch (final Exception e) {
                Log.e(TAG + ":Error: ", e.getMessage());
            }

            Bundle finalBundle = new Bundle();
            finalBundle.putString("id", id);
            finalBundle.putInt("position", position);
            finalBundle.putInt(RESULT_CODE, 2);
            Intent finalIntent = new Intent(ACTION);
            finalIntent.putExtras(startBundle);
            LocalBroadcastManager.getInstance(this).sendBroadcast(finalIntent);

        }


    }

    /**
     * used to construct an intent to start the Download service.
     *
     * @param context     context reference
     * @param downloadUrl link of the file to download
     * @param fileName    name of the file
     * @param extension   extension of the file
     * @param filePath    path of where to save the file
     * @param itemId      id of the file in firebase database
     * @param fileSize    size of the file to download
     * @return return an Intent with all data.
     */
    public static Intent makeDownloadIntent(final Context context, final String downloadUrl,
                                            final String fileName, final String extension,
                                            final String filePath,
                                            String itemId, Long fileSize) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(KEY_ITEM_ID, itemId);
        intent.putExtra(KEY_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(KEY_FILE_NAME, fileName);
        intent.putExtra(KEY_FILE_SIZE, fileSize);
        intent.putExtra(KEY_FILE_EXTENSION, extension);
        intent.putExtra(KEY_FILE_PATH, filePath);
        return intent;
    }

}
