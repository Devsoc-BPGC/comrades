package com.macbitsgoa.student_companion;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author AayushSingla
 */

public class UploadFile extends AsyncTask<Void,Void,Void> implements Callback {
     private String path;
     private String accessToken;
     private ProgressDialog progressDialog;
     @SuppressLint("StaticFieldLeak")
     private Context mContext;

     UploadFile(String path,String accessToken,Context mContext){
            progressDialog=new ProgressDialog(mContext);
            this.path=path;
            this.mContext=mContext;
            this.accessToken=accessToken;
     }

    @Override
    protected Void doInBackground(Void... voids) {
         uploadFile();
        return null;
    }

    private void uploadFile() {

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

            okHttpClient.newCall(request).enqueue(this);

            Log.e("Response:content-type",getMimeType(path));
            Log.e("Response:","authorization:"+accessToken);
            Log.e("Response:content-Length",file.length()+"");


        } catch (Exception e) {
            Log.e("Response:","Failed");
            e.printStackTrace();
        }


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
    public void onFailure(Request request, IOException e) {
        Log.e("Response:", e.getMessage());

    }

    @Override
    public void onResponse(Response response) throws IOException {
           Log.e("Response",response.body().string());
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setTitle("Uploading your file");
        progressDialog.setMessage("Please wait.....");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void result){
        progressDialog.hide();
        Toast.makeText(mContext,"File Uploaded",Toast.LENGTH_LONG).show();
    }


}