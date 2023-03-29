package com.aimenext.metawater.activity.unsend;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aimenext.metawater.R;
import com.aimenext.metawater.RestAPI;
import com.aimenext.metawater.activity.unsend.adapter.UnSendRVAdapter;
import com.aimenext.metawater.data.Job;
import com.aimenext.metawater.data.Response;
import com.aimenext.metawater.data.local.dao.ItemDAO;
import com.aimenext.metawater.data.local.db.AppDatabase;
import com.aimenext.metawater.data.local.entity.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
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

    private void sendData() {
        List<Job> items = mAdapter.getAll();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Long id = items.get(i).getId();
                String imageUriInput = items.get(i).getImageUri();
                String canCode = items.get(i).getCanCode();
                String type = items.get(i).getType();
                String uniqueId = items.get(i).getUnique();
                sendPhoto(dao, id, imageUriInput, canCode, uniqueId, type);
            }
        }
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
                    Log.d("AAAHAU", "Success: " + id.toString());
                    dao.deleteJob(id);
                    File fdelete = new File(path);
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Log.d("AAAHAU", "file Deleted :" + path);
                        } else {
                            Log.d("AAAHAU", "file not Deleted :" + path);
                        }
                    }
                    Toast.makeText(this, "正常に送信できました", Toast.LENGTH_SHORT).show();
                    finish();
                }, throwable -> {
                    Log.d("AAAHAU", "error");
                    finish();
                });
    }
}