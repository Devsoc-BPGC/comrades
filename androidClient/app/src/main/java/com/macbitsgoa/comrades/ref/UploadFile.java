package com.macbitsgoa.comrades.ref;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.macbitsgoa.comrades.BuildConfig;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;
import static com.macbitsgoa.comrades.ref.MetaDataAndPermissions.AUTHORIZATION_FIELD_KEY;
import static com.macbitsgoa.comrades.ref.MetaDataAndPermissions.AUTHORIZATION_FIELD_VALUE_PREFIX;

/**
 * Code to upload file.
 * @author aayushSingla
 */

@SuppressWarnings("WeakerAccess")
public class UploadFile extends AsyncTask<Void, Void, String> {
    private static final String TAG = "MAC->" + UploadFile.class.getSimpleName();
    private final String path;
    private final String accessToken;
    private final String driveUploadUrl;

    UploadFile(final String path, final String accessToken, final String driveUploadUrl) {
        this.path = path;
        this.accessToken = accessToken;
        this.driveUploadUrl = driveUploadUrl;
    }

    @Override
    protected String doInBackground(final Void... voids) {
        return uploadFile();
    }

    private String uploadFile() {
        try {
            final File file = new File(path);
            final OkHttpClient okHttpClient = new OkHttpClient();

            final RequestBody requestBody = RequestBody.create(MediaType.parse(getMimeType(path)),
                    fileToBytes(file));

            final Request request = new Request.Builder()
                    .url(driveUploadUrl)
                    .addHeader("Content-Type", getMimeType(path))
                    .addHeader("Content-Length", String.valueOf(file.length()))
                    .addHeader(AUTHORIZATION_FIELD_KEY,
                            AUTHORIZATION_FIELD_VALUE_PREFIX + accessToken
                    )
                    .post(requestBody)
                    .build();
            final Response response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (final Exception e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    private static byte[] fileToBytes(final File file) {
        byte[] bytes = new byte[0];
        try (final FileInputStream inputStream = new FileInputStream(file)) {
            bytes = new byte[inputStream.available()];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(bytes);
        } catch (final IOException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return bytes;
    }

    private static String getMimeType(final String filePath) {
        String type = null;
        final String extension = getFileExtensionFromUrl(filePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    @Override
    protected void onPreExecute() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Uploading file");
        }
    }

    @Override
    protected void onPostExecute(final String result) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "File upload result is " + result);
        }
    }


}