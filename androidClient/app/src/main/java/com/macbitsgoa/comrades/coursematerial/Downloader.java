package com.macbitsgoa.comrades.coursematerial;

import android.app.IntentService;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import static com.macbitsgoa.comrades.CHCKt.TAG_PREFIX;


/**
 * A {@link Worker} do download {@link CourseMaterial}.
 * Use {@link #download(CourseMaterial)} to access this class.
 */
public class Downloader extends Worker {

    public static final String KEY_MATERIAL = Downloader.class.getName() + "keys.material";
    public static final int BUF_SIZE_8K = 8192;
    public static final int BUF_SIZE_1K = 1024;
    private static final String TAG = TAG_PREFIX + Downloader.class.getSimpleName();
    public static final int BACKOFF_DELAY = 30;

    public static void download(CourseMaterial material) {
        Data downloaderData = new Data.Builder()
                .put(KEY_MATERIAL, new Gson().toJson(material))
                .build();
        Constraints downloadWorkConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest downloadRequest = new OneTimeWorkRequest.Builder(Downloader.class)
                .setConstraints(downloadWorkConstraints)
                .setInputData(downloaderData)
                .setBackoffCriteria(BackoffPolicy.LINEAR, BACKOFF_DELAY, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(downloadRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        CourseMaterial material = new Gson().fromJson(inputData.getString(KEY_MATERIAL), CourseMaterial.class);
        MaterialRepository repo = new MaterialRepository(getApplicationContext());
        material.downloadStatus = CourseMaterial.Status.WAIT_DOWNLOADING;
        DownloadProgress dlProgress = new DownloadProgress(material._id, 0);
        repo.update(material);
        repo.insert(dlProgress);

        int count;
        try {
            final URL url = new URL(material.link);
            final URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();

            File file = new File(material.filePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            InputStream input = new BufferedInputStream(url.openStream(), BUF_SIZE_8K);

            final OutputStream output = new FileOutputStream(material.filePath + material.fileName + material.extension);
            byte[] data = new byte[BUF_SIZE_1K];
            long total = 0;
            int progress;

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
                total += count;
                progress = (int) (total * 100 / material.fileSize);
                if (progress != dlProgress.progress) {
                    dlProgress.progress = progress;
                    Log.e(TAG, "publish " + progress);
                    repo.update(dlProgress);
                }
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();
        } catch (final Exception e) {
            Log.e(TAG, e.getMessage(), e.fillInStackTrace());
            return Result.RETRY;
        }
        material.downloadStatus = CourseMaterial.Status.CLICK_TO_OPEN;
        repo.update(material);
        return Result.SUCCESS;
    }
}
