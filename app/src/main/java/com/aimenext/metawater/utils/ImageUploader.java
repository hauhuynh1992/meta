package com.aimenext.metawater.utils;

import com.aimenext.metawater.BuildConfig;
import com.aimenext.metawater.data.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageUploader {

    private static final String URL = BuildConfig.ENV_URL;
    private ApiService apiService;

    public ImageUploader() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public Observable<Response> uploadImages(List<String> images, String code, String device, String type, String picture_date) {
        return Observable.fromIterable(images)
                .flatMap(image -> createUploadObservable(image, code, device, type, picture_date))
                .subscribeOn(Schedulers.io());
    }

    private Observable<Response> createUploadObservable(String imagePath, String code, String device, String type, String picture_date) {
        return Observable.create(emitter -> {
            try {
                File file = new File(imagePath);
                MultipartBody.Part[] listFileParts = new MultipartBody.Part[1];
                RequestBody requestImage = RequestBody.create(MediaType.parse("image/*"), file);
                listFileParts[0] = MultipartBody.Part.createFormData("file", file.getName(), requestImage);
                RequestBody requestPictureDate = RequestBody.create(MediaType.parse("multipart/form-data"), picture_date);
                RequestBody requestCanCode = RequestBody.create(MediaType.parse("multipart/form-data"), code);
                RequestBody requestType = RequestBody.create(MediaType.parse("multipart/form-data"), type);
                RequestBody requestDevice = RequestBody.create(MediaType.parse("multipart/form-data"), device);

                Call<com.aimenext.metawater.data.Response> call = apiService.addImage(requestCanCode, requestType, requestDevice, requestPictureDate, listFileParts);
                Response response = call.execute().body();

                if (response != null) {
                    emitter.onNext(response);
                } else {
                    emitter.onError(new IOException("No response received"));
                }

                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }
}