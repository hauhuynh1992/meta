package com.aimenext.metawater.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aimenext.metawater.data.Constants;
import com.aimenext.metawater.R;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.camera.CameraSourceConfig;
import com.google.mlkit.vision.camera.CameraXSource;
import com.google.mlkit.vision.camera.DetectionTaskCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QRActivity extends AppCompatActivity {
    PreviewView previewView;
    private BarcodeScanner barcodeDetector;
    private DetectionTaskCallback<List<Barcode>> detectionTaskCallback;
    private CameraXSource cameraSource;
    String intentData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        previewView = findViewById(R.id.surfaceView);
        detectionTaskCallback =
                detectionTask ->
                        detectionTask
                                .addOnSuccessListener(this::onDetectionTaskSuccess)
                                .addOnFailureListener(this::onDetectionTaskFailure);
        permissionsCheck(this, Arrays.asList(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }


    private void createThenStartCameraXSource() {

        barcodeDetector = BarcodeScanning.getClient();
        CameraSourceConfig cameraConfig = new CameraSourceConfig.Builder(this, barcodeDetector, detectionTaskCallback)
                .setFacing(CameraSourceConfig.CAMERA_FACING_BACK)
                .build();
        cameraSource = new CameraXSource(cameraConfig, previewView);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        cameraSource.start();
    }

    private void onDetectionTaskSuccess(List<Barcode> results) {
        for (Barcode barcode : results) {
            intentData = barcode.getRawValue();
        }
        if (intentData.length() != 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("qr_code", intentData);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    private void onDetectionTaskFailure(Exception e) {
        String error = "Failed to process. Error: " + e.getLocalizedMessage();
        Log.d("QRActivity", error);
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
            createThenStartCameraXSource();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CAMERA_PERMISSION) {

            for (int permissionIndex = 0; permissionIndex < permissions.length; permissionIndex++) {
                int grantResult = grantResults[permissionIndex];
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    return;
                }
            }

            try {
                createThenStartCameraXSource();
            } catch (Exception e) {
                Log.v("QRActivity", "Unknown error: " + e.toString());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (cameraSource != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraSource.start();
        } else {
            createThenStartCameraXSource();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.close();
        }
    }
}

