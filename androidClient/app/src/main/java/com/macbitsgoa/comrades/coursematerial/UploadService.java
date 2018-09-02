package com.macbitsgoa.comrades.coursematerial;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.R;
import com.macbitsgoa.comrades.persistance.Database;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;
import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;

/**
 * @author aayush singla
 */

public class UploadService extends IntentService {

    public static final String DRIVE_API_BASE_URL = "https://www.googleapis.com/drive/v3/files/";
    public static final String AUTHORIZATION_FIELD_KEY = "Authorization";
    public static final String AUTHORIZATION_FIELD_VALUE_PREFIX = "Bearer ";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_FILE_PATH = "filepath";
    private static final String KEY_FILE_NAME = "filename";
    private static final String TAG = TAG_PREFIX + UploadService.class.getSimpleName();
    private String fName;
    private String fileId;
    private String path;
    private String accessToken;
    private Long fileSize;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private String hashId;
    public static int NOTIFICATION_ID;
    private Boolean shouldContinue;

    public UploadService() {
        super("UploadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            path = intent.getStringExtra(KEY_FILE_PATH);
            accessToken = intent.getStringExtra(KEY_ACCESS_TOKEN);
            fName = intent.getStringExtra(KEY_FILE_NAME);
        }
        NOTIFICATION_ID = (new Random()).nextInt(100);
        sendNotification(R.drawable.ic_launcher_foreground, "Uploading " + fName, "Please wait...");
        mBuilder.setProgress(0, 0, true);
        mBuilder.setOngoing(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        long time = System.currentTimeMillis();
        hashId = calculateMD5(new File(path));
        Log.e("TAG", System.currentTimeMillis() - time + " " + hashId);

        checkHashIdFirebase(hashId);
        if (shouldContinue) {
            final String response = uploadFile();

            try {
                final JSONObject jsonObject = new JSONObject(response);
                fileId = (String) jsonObject.get("id");
                getPermissions();
                final JSONObject metaData = getMetadata();
                if (metaData == null) {
                    Log.e(TAG, "Received null metadata, returning");
                    return;
                }


                pushToFirebase(metaData);


            } catch (final JSONException e) {
                Log.e(TAG, e.getMessage(), e.fillInStackTrace());
                sendNotification(R.drawable.ic_launcher_foreground, "Comrades",
                        "File Could not be uploaded.Please try again later.");
                mBuilder.setProgress(0, 0, false);
                mBuilder.setOngoing(false);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
            sendNotification(R.drawable.ic_cloud_done_black_24dp, fName + " uploaded",
                    "Thanks for Contributing");
            mBuilder.setProgress(0, 0, false);
            mBuilder.setOngoing(false);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

    }

    private void checkHashIdFirebase(String hashId) {
        CourseMaterial material = Database.getInstance(this).getMaterialDao().checkHashId(hashId);
        if (material == null) {
            shouldContinue = true;
        } else {
            shouldContinue = false;
            sendNotification(R.drawable.ic_cloud_done_black_24dp, fName + " couldn't be uploaded",
                    "A similar file already exists in this course with name " + material.getFileName());
            mBuilder.setProgress(0, 0, false);
            mBuilder.setOngoing(false);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    private void sendNotification(int drawable, String msg, String msg_detail) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, UploadService.class), 0);

        mBuilder = new NotificationCompat.Builder(this, "progress")
                .setSmallIcon(drawable)
                .setContentTitle(msg)
                .setContentText(msg_detail)
                .setColor(R.color.colorAccent)
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setContentIntent(contentIntent);

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

    public static Intent makeUploadIntent(final Context context, final String path, final String accessToken,
                                          final String fileName) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra(KEY_FILE_NAME, fileName);
        intent.putExtra(KEY_ACCESS_TOKEN, accessToken);
        intent.putExtra(KEY_FILE_PATH, path);
        return intent;
    }


    private String uploadFile() {
        try {
            File file = new File(path);
            fileSize = file.length();
            Log.e(TAG, fileSize + "");
            final OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setRetryOnConnectionFailure(true);
            okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
            okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
            final RequestBody requestBody = RequestBody.create(MediaType.parse(getMimeType(path)),
                    fileToBytes(file));

            final String driveUploadUrl =
                    "https://www.googleapis.com/upload/drive/v3/files?uploadType=media";
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

    private void getPermissions() throws JSONException {

        final JSONObject jsonPermission = new JSONObject()
                .put("role", "reader")
                .put("type", "anyone")
                .put("allowFileDiscovery", "true");

        final OkHttpClient client = new OkHttpClient();
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),
                jsonPermission.toString());

        final Request permission = new Request.Builder()
                .addHeader(AUTHORIZATION_FIELD_KEY,
                        AUTHORIZATION_FIELD_VALUE_PREFIX + accessToken)
                .url(DRIVE_API_BASE_URL + fileId + "/permissions")
                .post(requestBody)
                .build();

        try {
            client.newCall(permission).execute();
        } catch (final IOException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }

    }

    @Nullable
    private JSONObject getMetadata() {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(DRIVE_API_BASE_URL + fileId + "?access_token=" + accessToken + "&fields=*")
                .get()
                .build();
        try {
            final Response response = okHttpClient.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (final IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }

        return null;
    }

    private void pushToFirebase(final JSONObject jsonObject) throws JSONException {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl(CourseActivity.databaseUrl);
        final JSONObject ownerObject = (JSONObject) jsonObject.getJSONArray("owners").get(0);
        final String owner = (String) ownerObject.get("displayName");
        final Boolean hasThumbnaill = (Boolean) jsonObject.get("hasThumbnail");
        String thumbnailLink;
        if (hasThumbnaill)
            thumbnailLink = (String) jsonObject.get("thumbnailLink");
        else
            thumbnailLink = (String) jsonObject.get("iconLink");
        String iconLink = (String) jsonObject.get("iconLink");
        iconLink = iconLink.replace("16", "128");
        final CourseMaterial itemCourseMaterial = new CourseMaterial();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        itemCourseMaterial.setAddedById(user.getUid());
        itemCourseMaterial.setAddedBy(owner);
        itemCourseMaterial.setFileName(fName);
        itemCourseMaterial.setExtension(getFileExtension(path));
        itemCourseMaterial.setId(fileId);
        itemCourseMaterial.setThumbnailLink(thumbnailLink);
        itemCourseMaterial.setIconLink(iconLink);
        itemCourseMaterial.setFileSize(fileSize);
        itemCourseMaterial.setFilePath(null);
        itemCourseMaterial.setDownloading(null);
        itemCourseMaterial.setWaiting(null);
        itemCourseMaterial.setProgress(0);
        itemCourseMaterial.setHashId(hashId);
        itemCourseMaterial.setWebViewLink(jsonObject.get("webViewLink").toString());
        itemCourseMaterial.setLink(jsonObject.get("webContentLink").toString());
        itemCourseMaterial.setMimeType(jsonObject.get("mimeType").toString());
        dbRef.child(hashId).setValue(itemCourseMaterial);
    }

    public static String getFileExtension(final String path) {
        final File file = new File(path);
        final String name = file.getName();
        final int i = name.lastIndexOf('.');
        final String ext = i > 0 ? name.substring(i + 1) : "";
        return "." + ext;
    }

}


