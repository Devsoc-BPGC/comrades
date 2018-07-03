package com.macbitsgoa.comrades.coursematerial;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.macbitsgoa.comrades.BuildConfig;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.Nullable;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;

/**
 * Request google drive permissions.
 *
 * @author aayush singla
 */

@SuppressWarnings("WeakerAccess")
public class MetaDataAndPermissions extends AsyncTask<Void, Void, Void> {
    @SuppressWarnings("WeakerAccess")
    public static final String AUTHORIZATION_FIELD_KEY = "Authorization";
    @SuppressWarnings("WeakerAccess")
    public static final String AUTHORIZATION_FIELD_VALUE_PREFIX = "Bearer ";
    private static final String TAG = TAG_PREFIX + MetaDataAndPermissions.class.getSimpleName();
    private final String driveApiBaseUrl = "https://www.googleapis.com/drive/v3/files/";
    private final String fileId;
    private final String accessToken;
    private final String fName;
    private final String extension;


    /**
     * Default constructor.
     *
     * @param fileId      id of the file.
     * @param accessToken obtained from sign in.
     */
    @SuppressWarnings("WeakerAccess")
    public MetaDataAndPermissions(final String fileId, final String accessToken, final String fName,
                                  final String extension) {
        this.fileId = fileId;
        this.accessToken = accessToken;
        this.fName = fName;
        this.extension = extension;
    }


    @Override
    protected Void doInBackground(final Void... voids) {
        try {
            getPermissions();
            final JSONObject metaData = getMetadata();
            if (metaData == null) {
                Log.e(TAG, "Received null metadata, returning");
                return null;
            }
            pushToFirebase(getMetadata());
        } catch (final JSONException e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Uploading file and granting permissions");
        }
    }

    @Override
    protected void onPostExecute(final Void result) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Permissions granted and pushed to firebase");
        }
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
                .url(driveApiBaseUrl + fileId + "/permissions")
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
                .url(driveApiBaseUrl + fileId + "?access_token=" + accessToken + "&fields=*")
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

        final ItemCourseMaterial itemCourseMaterial = new ItemCourseMaterial();
        itemCourseMaterial.setAddedBy(owner);
        itemCourseMaterial.setFileName(fName);
        itemCourseMaterial.setExtension(extension);
        itemCourseMaterial.setId(fileId);
        itemCourseMaterial.setLink(jsonObject.get("webContentLink").toString());
        itemCourseMaterial.setMimeType(jsonObject.get("mimeType").toString());
        dbRef.child(fileId).setValue(itemCourseMaterial);
    }

}
