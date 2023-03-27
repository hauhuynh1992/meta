package com.aimenext.metawater.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.aimenext.metawater.RestAPI;
import com.aimenext.metawater.data.Response;
import com.aimenext.metawater.data.local.dao.ItemDAO;
import com.aimenext.metawater.data.local.db.AppDatabase;
import com.aimenext.metawater.data.local.entity.Item;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UploadWorker extends Worker {
    public UploadWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        Log.d("AAAHAU", "doWork");
        AppDatabase appDatabase = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        ItemDAO dao = appDatabase.getItemDAO();
        List<Item> items = dao.getItems();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Long id = items.get(i).getId();
                String imageUriInput = items.get(i).getImage();
                String canCode = items.get(i).getCode();
                String type = items.get(i).getType();
                String uniqueId = items.get(i).getUnique();
                sendPhoto(dao, id, imageUriInput, canCode, uniqueId, type);
            }
        }
        return Result.success();
    }

    private void sendPhoto(ItemDAO dao, Long id, String path, String code, String device, String type) {
        File file = new File(path);
        MultipartBody.Part[] listFileParts = new MultipartBody.Part[1];
        RequestBody requestImage = RequestBody.create(MediaType.parse("image/*"), file);
        listFileParts[0] = MultipartBody.Part.createFormData("file", file.getName(), requestImage);

        RequestBody requestCanCode = RequestBody.create(MediaType.parse("multipart/form-data"), code);
        RequestBody requestType = RequestBody.create(MediaType.parse("multipart/form-data"), type);
        RequestBody requestDevice = RequestBody.create(MediaType.parse("multipart/form-data"), device);
        Observable<Response> cryptoObservable = RestAPI.getRetrofit().addImage(requestCanCode, requestType, requestDevice, listFileParts);
        cryptoObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(postResult -> {
                    Log.i("AAAHAU", "Success");
                    dao.deleteJob(id);
                }, throwable -> {
                    Log.i("AAAHAU", "error");
                });
    }

    public void appendLog(String text) {
        File logFile = new File(Environment.getExternalStorageDirectory() + "/" + "log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
