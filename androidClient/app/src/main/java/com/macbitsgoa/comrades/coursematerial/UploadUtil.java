package com.macbitsgoa.comrades.coursematerial;

import android.app.Notification;
import android.content.Context;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.crashlytics.android.Crashlytics;
import com.macbitsgoa.comrades.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.core.app.NotificationCompat;

import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;

/**
 * Helpful methods and constants for {@link Uploader} class.
 *
 * @author aayush singla
 */
public class UploadUtil {
    public static final String DRIVE_API_BASE_URL = "https://www.googleapis.com/drive/v3/files/";
    public static final String AUTHORIZATION_FIELD_KEY = "Authorization";
    public static final String AUTHORIZATION_FIELD_VALUE_PREFIX = "Bearer ";
    private static final String TAG = TAG_PREFIX + UploadUtil.class.getSimpleName();

    public static NotificationCompat.Builder sendNotification(Context context, int drawable, CharSequence message, CharSequence messageDetails, NotificationCompat.Action action) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "progress")
                .setSmallIcon(drawable)
                .setContentTitle(message)
                .setContentText(messageDetails)
                .setColor(R.color.colorAccent)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_HIGH);
        if (action != null) {
            mBuilder.addAction(action);
        }
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
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
        return null;
    }

    public static byte[] fileToBytes(final File file) {
        byte[] bytes;
        try {
            final FileInputStream inputStream = new FileInputStream(file);
            int available = inputStream.available();
            bytes = new byte[available];
            inputStream.read(bytes);
        } catch (final IOException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            return null;
        }
        return bytes;
    }


    public static String getMimeType(String filePath) throws UnsupportedEncodingException {
        String type = null;
        final String filePath1 = filePath.replaceAll(" ", "");
        final String extension = getFileExtensionFromUrl(URLEncoder.encode(filePath1, "UTF-8"));
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            Crashlytics.log(Log.DEBUG, TAG, "got null extension for file path "
                    + filePath + " The sanitized version was " + filePath1);
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
