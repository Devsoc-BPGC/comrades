package com.macbitsgoa.comrades.coursematerial;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.macbitsgoa.comrades.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.core.app.NotificationCompat;

import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;

/**
 * Helpful methods and constants for {@link Uploader} class.
 * @author aayush singla
 */
public class UploadUtil {
    public static final String DRIVE_API_BASE_URL = "https://www.googleapis.com/drive/v3/files/";
    public static final String AUTHORIZATION_FIELD_KEY = "Authorization";
    public static final String AUTHORIZATION_FIELD_VALUE_PREFIX = "Bearer ";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_FILE_PATH = "filepath";
    private static final String KEY_FILE_NAME = "filename";
    private static final String TAG = TAG_PREFIX + UploadUtil.class.getSimpleName();

    public static Intent makeUploadIntent(final Context context, final String path, final String accessToken,
                                          final String fileName) {
        Intent intent = new Intent(context, UploadUtil.class);
        intent.putExtra(KEY_FILE_NAME, fileName);
        intent.putExtra(KEY_ACCESS_TOKEN, accessToken);
        intent.putExtra(KEY_FILE_PATH, path);
        return intent;
    }

    public static NotificationCompat.Builder sendNotification(Context context, int drawable, CharSequence message, CharSequence messageDetails) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, UploadUtil.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "progress")
                .setSmallIcon(drawable)
                .setContentTitle(message)
                .setContentText(messageDetails)
                .setColor(R.color.colorAccent)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);
        return mBuilder;
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static byte[] fileToBytes(final File file) {
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


    public static String getMimeType(String filePath) {
        String type = null;
        final String filePath1 = filePath.replaceAll(" ", "");
        final String extension = getFileExtensionFromUrl(filePath1);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getFileExtension(final String path) {
        final File file = new File(path);
        final String name = file.getName();
        final int i = name.lastIndexOf('.');
        final String ext = i > 0 ? name.substring(i + 1) : "";
        return "." + ext;
    }

}
