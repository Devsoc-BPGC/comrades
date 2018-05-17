package com.macbitsgoa.student_companion;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author aayushSingla
 */

public class UploadFile extends AsyncTask<Void, Void, String> {
     private String path;
    private String accessToken;
     private ProgressDialog progressDialog;
     @SuppressLint("StaticFieldLeak")
     private Context mContext;
    JSONObject jsonObject = null;

     UploadFile(String path,String accessToken,Context mContext){
            progressDialog=new ProgressDialog(mContext);
            this.path=path;
            this.mContext=mContext;
            this.accessToken=accessToken;
     }

    @Override
    protected String doInBackground(Void... voids) {
        return uploadFile();
    }

    private String uploadFile() {

        try {
            File file = new File(path);
            OkHttpClient okHttpClient=new OkHttpClient();
            ///////////////////DON'T REMOVE THIS///////////////////////////
            /*JSONObject jsonObject=new JSONObject()
                    .put("name","Aayush.jpg");

            Headers header_metadata=new Headers.Builder().add("Content-Type","application/json; charset=UTF-8").build();
            Headers header_data=new Headers.Builder().add("Content-Type",getMimeType(path)).build();

            RequestBody metadata=RequestBody.create(MediaType.parse("application/json"),jsonObject.toString());

            RequestBody requestBody= new MultipartBuilder()
                    .addPart(header_metadata,metadata)
                    .addPart(header_data,RequestBody.create(MediaType.parse(getMimeType(path)),fileToBytes(file)))
                    .build();*/

            RequestBody requestBody= RequestBody.create(MediaType.parse(getMimeType(path)),fileToBytes(file));

            Request request=new Request.Builder()
                    .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=media")
                    .addHeader("Content-Type",getMimeType(path))
                    .addHeader("Content-Length", String.valueOf(file.length()))
                    .addHeader("Authorization","Bearer "+accessToken)
                    .post(requestBody)
                    .build();
            Log.e("Response:content-type",getMimeType(path));
            Log.e("Response:","authorization:"+accessToken);
            Log.e("Response:content-Length",file.length()+"");

            Response response = okHttpClient.newCall(request).execute();
            //Log.e("Response",response.body().string());

            return response.body().string();

        } catch (Exception e) {
            Log.e("Response:","Failed");
            e.printStackTrace();
        }
        return null;


    }


    private byte[] fileToBytes(File file){
        byte[] bytes = new byte[0];
        try(FileInputStream inputStream = new FileInputStream(file)) {
            bytes = new byte[inputStream.available()];
            //noinspection ResultOfMethodCallIgnored
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    private String getMimeType(String filePath) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Uploading your file");
        progressDialog.setMessage("Please wait.....");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.hide();
        Toast.makeText(mContext,"File Uploaded",Toast.LENGTH_LONG).show();
    }


}