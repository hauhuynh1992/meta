package com.aimenext.metawater.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aimenext.metawater.ParseJson;
import com.aimenext.metawater.R;
import com.aimenext.metawater.activity.camera.CameraActivity;
import com.aimenext.metawater.activity.unsend.UnSendActivity;
import com.aimenext.metawater.data.ArMarker;
import com.aimenext.metawater.data.Constants;
import com.aimenext.metawater.data.local.dao.ItemDAO;
import com.aimenext.metawater.data.local.db.AppDatabase;
import com.aimenext.metawater.data.local.entity.Item;
import com.aimenext.metawater.utils.UploadWorker;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {

    private EditText inputAR, inputQR, inputCAN;
    private TextView pendingWork, imageRemain, codeRemain, buttonCamera;
    private AppDatabase appDatabase;
    ConnectivityManager connectivityManager;
    ConstraintLayout remain_layout;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        remain_layout = findViewById(R.id.remain_layout);
        inputAR = findViewById(R.id.input_text_AR);
        inputCAN = findViewById(R.id.input_text_CAN);
        inputQR = findViewById(R.id.input_text_QR);
        pendingWork = findViewById(R.id.pending_work);
        codeRemain = findViewById(R.id.code_remain);
        imageRemain = findViewById(R.id.image_remain);
        buttonCamera = findViewById(R.id.button_camera);
        buttonCamera.setOnClickListener(v -> takePhoto());
        TextView button_qr = findViewById(R.id.button_QR);
        button_qr.setOnClickListener(v -> scanQR());
        TextView button_ar = findViewById(R.id.button_AR);
        button_ar.setOnClickListener(v -> scanAR());
        remain_layout.setOnClickListener(v -> gotoUnSend());
        connectivityManager = getSystemService(ConnectivityManager.class);
        if (connectivityManager.getActiveNetwork() == null) {
            onNetWorkChange(false);
        }
        connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                onNetWorkChange(true);
            }

            @Override
            public void onLost(Network network) {
                onNetWorkChange(false);
            }
        });
        inputAR.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    inputQR.setText("");
                    inputCAN.setText("");
                }
            }
        });
        inputCAN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    inputAR.setText("");
                    inputQR.setText("");
                }
            }
        });
        inputQR.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    inputAR.setText("");
                    inputCAN.setText("");
                }
            }
        });
//        getWorkInfo();
    }

    private void generateDatabase() {
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        ItemDAO dao = appDatabase.getItemDAO();
        List<Item> items = dao.getItems();
        if (items != null) {
            imageRemain.setText(String.valueOf(items.size()));
            codeRemain.setText(String.valueOf(items.size()));
        }
    }

    private void onNetWorkChange(boolean isOn) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (isOn) {
//                    pendingWork.setVisibility(View.GONE);
//                    slideDownView(pendingWork);
//                } else {
//                    pendingWork.setVisibility(View.VISIBLE);
//                    slideUpView(pendingWork);
//                }
//            }
//        });
    }

    public static void slideUpView(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, view.getHeight(), 0);
        animate.setDuration(500);
        view.startAnimation(animate);
    }

    public static void slideDownView(View view) {
        view.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        view.startAnimation(animate);
    }

    private void takePhoto() {
        if (inputAR.getText().toString().length() > 0) {
            Intent t = new Intent(this, CameraActivity.class);
            t.putExtra(Constants.EXTRA_CODE, inputAR.getText().toString());
            t.putExtra(Constants.EXTRA_TYPE, "AR");
            startActivityForResult(t, Constants.START_CAMERA);
        } else if (inputCAN.getText().toString().length() > 0) {
            Intent t = new Intent(this, CameraActivity.class);
            t.putExtra(Constants.EXTRA_CODE, inputCAN.getText().toString());
            t.putExtra(Constants.EXTRA_TYPE, "CAN");
            startActivityForResult(t, Constants.START_CAMERA);
        } else if (inputQR.getText().toString().length() > 0) {
            Intent t = new Intent(this, CameraActivity.class);
            t.putExtra(Constants.EXTRA_CODE, inputQR.getText().toString());
            t.putExtra(Constants.EXTRA_TYPE, "QR");
            startActivityForResult(t, Constants.START_CAMERA);
        } else {
            Toast.makeText(this, "コードをスキャンもしくは入力してください", Toast.LENGTH_LONG).show();
        }
    }

    private void scanAR() {
        // 最新版のARを起動する
        try {
            Intent intent = new Intent();
            intent.setClassName("com.sample.clientdev",
                    "com.fujitsu.interstage.ar.mobileclient.android.base.MtActivity");
            intent.putExtra("cid", "1234567890");
            intent.putExtra("uid", "0123456789");
            intent.putExtra("mid", "9876543210");
            startActivityForResult(intent, Constants.START_AR);
        } catch (ActivityNotFoundException activityNotFound) {
            Toast.makeText(this, "ARアプリが見つかりません", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotoUnSend() {
        Intent intent = new Intent(this, UnSendActivity.class);
        startActivity(intent);
    }

    private void scanQR() {
        Intent t = new Intent(this, QRActivity.class);
        startActivityForResult(t, Constants.START_QR);
    }

    private void onArResult(ArMarker arMarkers, File arPictureFile) {

    }

//    private void getWorkInfo() {
//        WorkQuery workQuery = WorkQuery.Builder
//                .fromStates(Arrays.asList(WorkInfo.State.ENQUEUED))
//                .build();
//
//        LiveData<List<WorkInfo>> workInfos = WorkManager.getInstance(this).getWorkInfosLiveData(workQuery);
//        final Observer<List<WorkInfo>> nameObserver = new Observer<List<WorkInfo>>() {
//            @Override
//            public void onChanged(@Nullable final List<WorkInfo> info) {
//                codeRemain.setText(String.valueOf(info.stream().map(i -> i.getTags()).distinct().count()));
//                imageRemain.setText(String.valueOf(info.size()));
//            }
//        };
//        workInfos.observe(this, nameObserver);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.START_QR && resultCode == RESULT_OK) {
            String s = data.getStringExtra("qr_code");
            inputQR.setText(s);
            inputQR.requestFocus();
        }
        if (requestCode == Constants.START_CAMERA) {
            inputAR.setText("");
            if (inputAR.isFocused()) {
                inputAR.clearFocus();
            }
            inputQR.setText("");
            if (inputQR.isFocused()) {
                inputQR.clearFocus();
            }
        }
        if (requestCode == Constants.START_AR && resultCode == RESULT_OK) {
            String strres = data.getStringExtra("strres");
            ParseJson parseJson = new ParseJson();
            ArMarker[] arMarkers = parseJson.parseArMarkerFromStr(strres);
            if (arMarkers[0] != null) {
                inputAR.setText(String.valueOf(arMarkers[0].getMarkerId()));
                inputAR.requestFocus();
            } else {
                Toast.makeText(this, "マーカーのIDを取得できません", Toast.LENGTH_LONG).show();
            }
            // キャプチャ画像をアプリ配下のmediaディレクトリにコピーする
//            String inDirStr = arMarkers.getDir() + '/';
//            String fname = arMarkers.getFile();
//
//            File iFile = new File(inDirStr + fname);

            //onArResult(arMarkers, iFile);

        } else if (requestCode == Constants.START_AR && resultCode != RESULT_OK) {
            Log.v("SystemError", "ARでエラーが発生しました。");
            onArResult(null, null);
        }
    }

    @Override protected void onResume() {
        super.onResume();
        generateDatabase();
        sendWork();
        inputAR.setText("");
        inputCAN.setText("");
    }

    private void sendWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest uploadWorkRequest =
                new PeriodicWorkRequest.Builder(UploadWorker.class, PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
                        TimeUnit.MILLISECONDS,
                        PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                        TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .build();
        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork(getPackageName(),
                        ExistingPeriodicWorkPolicy.KEEP, uploadWorkRequest);
    }
}