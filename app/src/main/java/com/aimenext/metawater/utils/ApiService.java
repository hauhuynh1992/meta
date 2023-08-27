package com.aimenext.metawater.utils;

import com.aimenext.metawater.data.Response;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface ApiService {
    @Multipart
    @POST("add_image")
    Call<Response> addImage(@Part("code") RequestBody canCode,
                            @Part("type") RequestBody type,
                            @Part("upload_device") RequestBody device,
                            @Part("picture_date") RequestBody date,
                            @Part MultipartBody.Part[] listFile
    );
}
