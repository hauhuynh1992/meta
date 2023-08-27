package com.aimenext.metawater.activity.unsend;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.aimenext.metawater.R;
import com.aimenext.metawater.RestAPI;
import com.aimenext.metawater.activity.unsend.adapter.UnSendRVAdapter;
import com.aimenext.metawater.data.Job;
import com.aimenext.metawater.data.Response;
import com.aimenext.metawater.data.local.dao.ItemDAO;
import com.aimenext.metawater.data.local.db.AppDatabase;
import com.aimenext.metawater.data.local.entity.Item;
import com.aimenext.metawater.utils.DialogHandler;
import com.aimenext.metawater.utils.ImageUploader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.schedulers.Schedulers;

public class UnSendActivity extends AppCompatActivity {

    private AppDatabase appDatabase;
    private ItemDAO dao;
    private RecyclerView rvList;
    private TextView btnSend;
    private ImageButton btnBack;
    private UnSendRVAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unsend);
        rvList = findViewById(R.id.rvUnSend);
        btnBack = findViewById(R.id.img_back);
        btnSend = findViewById(R.id.btn_send);
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        dao = appDatabase.getItemDAO();

        btnSend.setOnClickListener(v -> {
            sendData();
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnBack.setImageResource(R.drawable.ic_baseline_arrow_back_24);

        mAdapter = new UnSendRVAdapter();
        mLayoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(mLayoutManager);
        rvList.setAdapter(mAdapter);
        generateDatabase();
    }

    private void generateDatabase() {
        Observable.just(appDatabase)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(postResult -> {
                    List<Item> items = dao.getItems();
                    ArrayList jobs = new ArrayList<Job>();
                    if (items != null) {
                        for (int i = 0; i < items.size(); i++) {
                            Log.d("AAAHAU", items.get(i).getId() + "/" + items.get(i).getImage() + "/" + items.get(i).getCode());
                            jobs.add(new Job(
                                    items.get(i).getId(),
                                    items.get(i).getImage(),
                                    items.get(i).getCode(),
                                    items.get(i).getType(),
                                    items.get(i).getUnique(),
                                    items.get(i).getDate()));
                        }
                    }
                    mAdapter.setJobs(jobs);
                }, throwable -> {

                });

    }

    @SuppressLint("CheckResult")
    private void sendData() {
        showLoadingDialog();
        List<Job> items = mAdapter.getAll();
        if (items != null) {
            ImageUploader imageUploader = new ImageUploader();
            List<String> images = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                images.add(items.get(i).getImageUri());
            }

            String canCode = items.get(0).getCanCode();
            String type = items.get(0).getType();
            String uniqueId = items.get(0).getUnique();
            String dateString = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(items.get(0).getDate()));
            int size = items.size();
            AtomicInteger index = new AtomicInteger();
            imageUploader.uploadImages(images, canCode, uniqueId, type, dateString).subscribe(postResult -> {
                dismissLoadingDialog();
                Log.d("AAAHAU", "Success");
                index.getAndIncrement();
                if (index.get() == size) {
                    dao.deleteAll();
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UnSendActivity.this, "正常に送信できました", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }

            }, throwable -> {
                dismissLoadingDialog();
                Log.d("AAAHAU", "error: " + throwable.getMessage().toString());
                finish();
            });
        }
    }

    synchronized private void sendPhoto(ItemDAO dao, Long id, String path, String code, String device, String type, String picture_date) {
        Log.i("AAAHAU", "sendPhoto: " + id.toString());
        showLoadingDialog();
        File file = new File(path);
        MultipartBody.Part[] listFileParts = new MultipartBody.Part[1];
        RequestBody requestImage = RequestBody.create(MediaType.parse("image/*"), file);
        listFileParts[0] = MultipartBody.Part.createFormData("file", file.getName(), requestImage);
        RequestBody requestPictureDate = RequestBody.create(MediaType.parse("multipart/form-data"), picture_date);
        RequestBody requestCanCode = RequestBody.create(MediaType.parse("multipart/form-data"), code);
        RequestBody requestType = RequestBody.create(MediaType.parse("multipart/form-data"), type);
        RequestBody requestDevice = RequestBody.create(MediaType.parse("multipart/form-data"), device);
        Observable<Response> cryptoObservable = RestAPI.getRetrofit().addImage(requestCanCode, requestType, requestDevice, requestPictureDate, listFileParts);
        cryptoObservable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(postResult -> {
                    Log.d("AAAHAU", "Success: " + id.toString());
                    dismissLoadingDialog();
                    dao.deleteJob(id);
                    File fdelete = new File(path);
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Log.d("AAAHAU", "file Deleted :" + path);
                        } else {
                            Log.d("AAAHAU", "file not Deleted :" + path);
                        }
                    }

                    int size = dao.getItems().size();
                    if (size == 0) {
                        this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(UnSendActivity.this, "正常に送信できました", Toast.LENGTH_SHORT).show();
                            }
                        });
                        finish();
                    }
                }, throwable -> {
                    dismissLoadingDialog();
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(UnSendActivity.this, "送信不可", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.d("AAAHAU", "error: " + throwable.getMessage().toString());
                    finish();
                });
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            loadingDialog = DialogHandler.createLoadingDialog(this);
            loadingDialog.show();
        } else {
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing() == true) {
            loadingDialog.dismiss();
        }
        loadingDialog = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
    }
}