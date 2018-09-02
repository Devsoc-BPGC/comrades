package com.macbitsgoa.comrades.coursematerial;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;
import static com.macbitsgoa.comrades.CHCKt.getCourseMaterialRef;
import static com.macbitsgoa.comrades.coursematerial.UploadService.AUTHORIZATION_FIELD_KEY;
import static com.macbitsgoa.comrades.coursematerial.UploadService.AUTHORIZATION_FIELD_VALUE_PREFIX;
import static com.macbitsgoa.comrades.coursematerial.UploadService.DRIVE_API_BASE_URL;
import static com.macbitsgoa.comrades.coursematerial.UploadService.fileToBytes;
import static com.macbitsgoa.comrades.coursematerial.UploadService.getFileExtension;
import static com.macbitsgoa.comrades.coursematerial.UploadService.getMimeType;

/**
 * {@link Worker} for uploading files.
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

    @NonNull
    @Override
    public Result doWork() {
        Data input = getInputData();
        path = input.getString(KEY_PATH);
        accessToken = input.getString(KEY_ACCESS_TOKEN);
        fileName = input.getString(KEY_FILE_NAME);
        courseId = input.getString(KEY_COURSE_ID);

        fileHash = UploadService.calculateMD5(new File(path));
        String response = uploadFile();
        if (response == null) return Result.FAILURE;
        final JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            fileId = (String) jsonObject.get("id");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            return Result.FAILURE;
        }
        if (!makePublic(accessToken, fileId)) {
            return Result.FAILURE;
        }
        JSONObject metadata = obtainMetadata(fileId, accessToken);
        if (metadata == null) {
            return Result.FAILURE;
        }
        try {
            writeToDb(metadata);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }

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
}
