package com.aimenext.metawater.activity.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aimenext.metawater.R;
import com.aimenext.metawater.RestAPI;
import com.aimenext.metawater.data.Constants;
import com.aimenext.metawater.data.Response;
import com.aimenext.metawater.data.local.dao.ItemDAO;
import com.aimenext.metawater.data.local.db.AppDatabase;
import com.aimenext.metawater.data.local.entity.Item;
import com.aimenext.metawater.utils.UploadWorker;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.util.Hex;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CameraActivity extends AppCompatActivity {

    private String currentPhotoPath;
    private AppDatabase appDatabase;
    private ItemDAO dao;
    private Uri mCameraCaptureURI;
    private ImageView mImageView;
    private String code;
    private String type;
    private String uniqueID = null;
    private final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        code = getIntent().getStringExtra(Constants.EXTRA_CODE);
        type = getIntent().getStringExtra(Constants.EXTRA_TYPE);
        TextView header_code = findViewById(R.id.code_text);
        header_code.setText(code);
        mImageView = findViewById(R.id.image_view);
        TextView button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(v -> finish());
        TextView button_ok = findViewById(R.id.button_ok);
        button_ok.setOnClickListener(v -> sendPhoto());
        permissionsCheck(this, Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = this.getSharedPreferences(
                    PREF_UNIQUE_ID, this.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                UUID WIDEVINE_UUID = UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed");
                try {
                    MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID);
                    byte[] deviceUniqueIdArray = mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID);
                    uniqueID = Hex.bytesToStringLowercase(deviceUniqueIdArray);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(PREF_UNIQUE_ID, uniqueID);
                    editor.apply();
                } catch (UnsupportedSchemeException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.v("CameraActivity", "UUID: " + uniqueID);
        generateDatabase();
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
                        .addTag(code)
                        .build();
        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork(getPackageName(),
                        ExistingPeriodicWorkPolicy.KEEP, uploadWorkRequest);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MetaWater" + "_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        try {
            String intent;
            File dataFile;

            intent = MediaStore.ACTION_IMAGE_CAPTURE;
            dataFile = createImageFile();


            Intent cameraIntent = new Intent(intent);

            mCameraCaptureURI = FileProvider.getUriForFile(this,
                    this.getApplicationContext().getPackageName() + ".provider",
                    dataFile);


            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraCaptureURI);
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);


            if (cameraIntent.resolveActivity(getPackageManager()) == null) {
                return;
            }
            startActivityForResult(cameraIntent, Constants.REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            Log.e("CameraActivity", e.toString());
        }
    }

    private void permissionsCheck(final Activity activity, final List<String> requiredPermissions) {

        List<String> missingPermissions = new ArrayList<>();
        List<String> supportedPermissions = new ArrayList<>(requiredPermissions);

        // android 11 introduced scoped storage, and WRITE_EXTERNAL_STORAGE no longer works there
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            supportedPermissions.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        for (String permission : supportedPermissions) {
            int status = ContextCompat.checkSelfPermission(activity, permission);
            if (status != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (!missingPermissions.isEmpty()) {
            this.requestPermissions(missingPermissions.toArray(new String[missingPermissions.size()]), Constants.REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(currentPhotoPath).into(mImageView);
            } else {
                this.finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CAMERA_PERMISSION) {

            for (int permissionIndex = 0; permissionIndex < permissions.length; permissionIndex++) {
                int grantResult = grantResults[permissionIndex];
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    finish();
                }
            }

            try {
                dispatchTakePictureIntent();
            } catch (Exception e) {
                Log.v("CameraActivity", "Unknown error: " + e.toString());
            }
        }
    }

    private void generateDatabase() {
        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "mydb")
                .allowMainThreadQueries()
                .build();
        dao = appDatabase.getItemDAO();
    }

    private void sendPhoto() {
        File file = new File(currentPhotoPath);
        MultipartBody.Part[] listFileParts = new MultipartBody.Part[1];
        RequestBody requestImage = RequestBody.create(MediaType.parse("image/*"), file);
        listFileParts[0] = MultipartBody.Part.createFormData("file", file.getName(), requestImage);

        RequestBody requestCanCode = RequestBody.create(MediaType.parse("multipart/form-data"), code);
        RequestBody requestType = RequestBody.create(MediaType.parse("multipart/form-data"), type);
        RequestBody requestDevice = RequestBody.create(MediaType.parse("multipart/form-data"), uniqueID);
        Observable<Response> cryptoObservable = RestAPI.getRetrofit().addImage(requestCanCode, requestType, requestDevice, listFileParts);
        cryptoObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postResult -> {
                    Log.i("AAAHAU", "Success");
                    Toast.makeText(this, "Send success", Toast.LENGTH_SHORT).show();
                    finish();
                }, throwable -> {
                    Log.i("AAAHAU", "error");
                    Toast.makeText(this, "save database success", Toast.LENGTH_SHORT).show();
                    AppDatabase appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "mydb")
                            .allowMainThreadQueries()
                            .build();
                    ItemDAO dao = appDatabase.getItemDAO();
                    Item item = new Item(type, code, currentPhotoPath, uniqueID, new Date().getTime());
                    dao.insert(item);
                    finish();
                });
    }
}
