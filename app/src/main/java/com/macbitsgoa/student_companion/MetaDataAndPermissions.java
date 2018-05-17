package com.macbitsgoa.student_companion;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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


/**
 * @author aayush singla
 */

public class MetaDataAndPermissions extends AsyncTask<Void, Void, Void> {
    private ProgressDialog progressDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private String fileId;
    private String accessToken;


    MetaDataAndPermissions(Context mContext, String fileId, String accessToken) {
        progressDialog = new ProgressDialog(mContext);
        this.mContext = mContext;
        this.fileId = fileId;
        this.accessToken = accessToken;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            getPermissions();
            pushToFirebase(getMetadata());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Uploading your file");
        progressDialog.setMessage("Granting Permissions....");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        progressDialog.hide();
        Toast.makeText(mContext, "Permissions Granted", Toast.LENGTH_LONG).show();
        Toast.makeText(mContext, "Pushed to Firebase", Toast.LENGTH_LONG).show();
    }

    private void getPermissions() throws JSONException {

        JSONObject jsonPermission = new JSONObject()
                .put("role", "reader")
                .put("type", "anyone")
                .put("allowFileDiscovery", "true");

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonPermission.toString());

        Request permission = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url("https://www.googleapis.com/drive/v3/files/" + fileId + "/permissions")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(permission).execute();
            //JSONObject jsonObject=new JSONObject(response.body().string());
            Log.e("Response:permission", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            //} catch (JSONException e) {
            //    e.printStackTrace();
        }

    }


    private JSONObject getMetadata() {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/drive/v3/files/" + fileId + "?access_token=" + accessToken + "&fields=*")
                .get()
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            JSONObject jsonObject = new JSONObject(response.body().string());
            Log.e("Response:", jsonObject.toString());
            return jsonObject;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void pushToFirebase(JSONObject jsonObject) throws JSONException {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(fileId);
        HashMap<String, String> hashMap = jsonToMap(jsonObject.toString());
        databaseReference.child("fileId").setValue(fileId);
        databaseReference.child("meta-data").setValue(hashMap);

    }


    private HashMap<String, String> jsonToMap(String t) throws JSONException {

        HashMap<String, String> map = new HashMap<>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = jObject.getString(key);
            map.put(key, value);

        }
        return map;
    }
}