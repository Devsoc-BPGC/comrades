package com.macbitsgoa.comrades.coursematerial;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
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
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.CHCKt.getCourseMaterialRef;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.AUTHORIZATION_FIELD_KEY;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.AUTHORIZATION_FIELD_VALUE_PREFIX;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.DRIVE_API_BASE_URL;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.calculateMD5;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.fileToBytes;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.getFileExtension;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.getMimeType;
import static com.macbitsgoa.comrades.coursematerial.UploadUtil.sendNotification;

/**
 * {@link Worker} for uploading files.
 * Use {@link #upload(String, String, String, String)} to easily upload the file.
 *
 * @author Aayush Singla.
 * @author Rushikesh Jogdand.
 */
public class Uploader extends Worker {
    public static final String KEY_PATH = "path";
    public static final String KEY_ACCESS_TOKEN = "accessToken";
    public static final String KEY_FILE_NAME = "fileName";
    public static final String KEY_COURSE_ID = "courseId";
    private static final String TAG = TAG_PREFIX + Uploader.class.getSimpleName();
    private String path;
    private String accessToken;
    private String fileName;
    private String courseId;
    private String fileId;
    private long fileSize;
    private String fileHash;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private int notificationId;

    /**
     * Handy method to access this class.
     *
     * @param filePath    of the file to be uploaded.
     * @param accessToken of user's google drive.
     * @param fileName    by which this file will be saved in db.
     * @param courseId    to which this file belongs.
     */
    public static void upload(final String filePath, final String accessToken, final String fileName, final String courseId) {
        Data uploaderData = new Data.Builder()
                .putString(KEY_PATH, filePath)
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_FILE_NAME, fileName)
                .putString(KEY_COURSE_ID, courseId)
                .build();
        Constraints uploadWorkConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest uploadRequest = new OneTimeWorkRequest.Builder(Uploader.class)
                .setConstraints(uploadWorkConstraints)
                .setInputData(uploaderData)
                .build();
        WorkManager.getInstance()
                .enqueue(uploadRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data input = getInputData();
        path = input.getString(KEY_PATH);
        accessToken = input.getString(KEY_ACCESS_TOKEN);
        fileName = input.getString(KEY_FILE_NAME);
        courseId = input.getString(KEY_COURSE_ID);

        notificationId = (new SecureRandom()).nextInt(100);
        notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notifyInit();
        fileHash = calculateMD5(new File(path));
        CourseMaterial duplicate = findDuplicate();
        if (duplicate != null) {
            notifyDuplicate(duplicate.getFileName());
            return Result.FAILURE;
        }

        String response = uploadFile();
        if (response == null) {
            notifyFailure();
            return Result.FAILURE;
        }
        final JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            fileId = (String) jsonObject.get("id");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            notifyFailure();
            return Result.FAILURE;
        }
        if (!makePublic(accessToken, fileId)) {
            notifyFailure();
            return Result.FAILURE;
        }
        JSONObject metadata = obtainMetadata(fileId, accessToken);
        if (metadata == null) {
            notifyFailure();
            return Result.FAILURE;
        }
        try {
            writeToDb(metadata);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            notifyFailure();
            return Result.FAILURE;
        }
        notifySuccess();
        return Result.SUCCESS;
    }

    /**
     * Get metadata for the file.
     *
     * @param fileId      of query file.
     * @param accessToken of owner.
     * @return {@link JSONObject} result.
     */
    public static JSONObject obtainMetadata(String fileId, String accessToken) {
        final OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(DRIVE_API_BASE_URL + fileId + "?access_token=" + accessToken + "&fields=*")
                .get()
                .build();
        Response response;
        JSONObject metadata;
        try {
            response = okHttpClient.newCall(request).execute();
            metadata = new JSONObject(response.body().string());
        } catch (final IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            return null;
        }
        return metadata;
    }

    private String uploadFile() {
        try {
            File file = new File(path);
            fileSize = file.length();
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

    /**
     * Make the file with fileId public.
     *
     * @param accessToken of owner.
     * @param fileId      of corresponding file.
     * @return whether making the file succeeded.
     */
    public static boolean makePublic(String accessToken, String fileId) {
        final JSONObject jsonPermission;
        try {
            jsonPermission = new JSONObject()
                    .put("role", "reader")
                    .put("type", "anyone")
                    .put("allowFileDiscovery", "true");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }

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
            return false;
        }
        return true;
    }

    private void writeToDb(JSONObject jsonObject) throws JSONException {
        DatabaseReference materialNode = getCourseMaterialRef().child(courseId);
        final JSONObject ownerObject = (JSONObject) jsonObject.getJSONArray("owners").get(0);
        final String owner = (String) ownerObject.get("displayName");
        final Boolean hasThumbnail = (Boolean) jsonObject.get("hasThumbnail");
        String thumbnailLink;
        thumbnailLink = hasThumbnail ? (String) jsonObject.get("thumbnailLink") : (String) jsonObject.get("iconLink");
        String iconLink = (String) jsonObject.get("iconLink");
        iconLink = iconLink.replace("16", "128");
        final CourseMaterial itemCourseMaterial = new CourseMaterial();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        itemCourseMaterial.setAddedById(user.getUid());
        itemCourseMaterial.setAddedBy(owner);
        itemCourseMaterial.setFileName(fileName);
        itemCourseMaterial.setExtension(getFileExtension(path));
        itemCourseMaterial.setId(fileId);
        itemCourseMaterial.setThumbnailLink(thumbnailLink);
        itemCourseMaterial.setIconLink(iconLink);
        itemCourseMaterial.setFileSize(fileSize);
        itemCourseMaterial.setFilePath(null);
        itemCourseMaterial.setDownloading(null);
        itemCourseMaterial.setWaiting(null);
        itemCourseMaterial.setProgress(0);
        itemCourseMaterial.setHashId(fileHash);
        itemCourseMaterial.setWebViewLink(jsonObject.get("webViewLink").toString());
        itemCourseMaterial.setLink(jsonObject.get("webContentLink").toString());
        itemCourseMaterial.setMimeType(jsonObject.get("mimeType").toString());
        materialNode.child(fileHash).setValue(itemCourseMaterial);
    }

    /**
     * Check if the file is duplicate
     *
     * @return null if no duplicate exists, otherwise original file.
     */
    private CourseMaterial findDuplicate() {
        return Database.getInstance(getApplicationContext()).getMaterialDao().checkHashId(fileHash);
    }

    private void notifyDuplicate(String originalFileName) {
        builder = sendNotification(getApplicationContext(), R.drawable.ic_cloud_done_black_24dp, fileName + " couldn't be uploaded",
                "A similar file already exists in this course with name " + originalFileName);
        builder.setProgress(0, 0, false);
        builder.setOngoing(false);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationManager.notify(notificationId, builder.build());
    }

    private void notifyInit() {
        builder = sendNotification(getApplicationContext(), R.drawable.ic_launcher_foreground, "Uploading " + fileName, "Please wait...");
        builder.setProgress(0, 0, true);
        builder.setOngoing(true);
        notificationManager.notify(notificationId, builder.build());
    }

    private void notifyFailure() {
        builder = sendNotification(getApplicationContext(), R.drawable.ic_launcher_foreground, "Comrades",
                "File Could not be uploaded.Please try again later.");
        builder.setProgress(0, 0, false);
        builder.setOngoing(false);
        notificationManager.notify(notificationId, builder.build());
    }

    private void notifySuccess() {
        builder = sendNotification(getApplicationContext(), R.drawable.ic_cloud_done_black_24dp, fileName + " uploaded",
                "Thanks for Contributing");
        builder.setProgress(0, 0, false);
        builder.setOngoing(false);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationManager.notify(notificationId, builder.build());
    }
}
