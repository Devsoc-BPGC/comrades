package com.macbitsgoa.comrades.ref;

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
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Request google drive permissions.
 * @author aayush singla
 */

@SuppressWarnings("WeakerAccess")
public class MetaDataAndPermissions extends AsyncTask<Void, Void, Void> {
    @SuppressWarnings("WeakerAccess")
    public static final String AUTHORIZATION_FIELD_KEY = "Authorization";
    @SuppressWarnings("WeakerAccess")
    public static final String AUTHORIZATION_FIELD_VALUE_PREFIX = "Bearer ";
    private static final String TAG = "MAC->" + MetaDataAndPermissions.class.getSimpleName();
    @NonNull
    private final String driveApiBaseUrl;
    private final String fileId;
    private final String accessToken;

    /**
     * Default constructor.
     * @param fileId id of the file.
     * @param accessToken obtained from sign in.
     * @param driveApiBaseUrl base url of google drive api.
     */
    @SuppressWarnings("WeakerAccess")
    public MetaDataAndPermissions(final String fileId, final String accessToken,
                                  @NonNull final String driveApiBaseUrl) {
        this.fileId = fileId;
        this.accessToken = accessToken;
        this.driveApiBaseUrl = driveApiBaseUrl;
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
                .addHeader(AUTHORIZATION_FIELD_KEY, AUTHORIZATION_FIELD_VALUE_PREFIX + accessToken)
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
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(fileId);
        final String webLink = (String) jsonObject.get(HomeActivity.WEB_CONTENT_LINK);
        final HashMap<String, String> hashMap = jsonToMap(jsonObject.toString());
        dbRef.child(HomeActivity.FILE_ID_KEY).setValue(fileId);
        dbRef.child("meta-data").setValue(hashMap);
        dbRef.child(HomeActivity.WEB_CONTENT_LINK).setValue(webLink);
    }


    private static HashMap<String, String> jsonToMap(final String t) throws JSONException {
        final HashMap<String, String> map = new HashMap<>(0);
        final JSONObject jObject = new JSONObject(t);
        final Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            final String key = (String) keys.next();
            final String value = jObject.getString(key);
            map.put(key, value);
        }
        return map;
    }
}