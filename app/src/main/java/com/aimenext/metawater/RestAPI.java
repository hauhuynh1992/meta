package com.aimenext.metawater;

import com.aimenext.metawater.data.Response;

import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public class RestAPI {
    private static final String URL = BuildConfig.ENV_URL;
    private static final String add_image = "add_image";
    private static Retrofit retrofitHome;

    public static IRequestService getRetrofit() {
        if (retrofitHome == null) {
            OkHttpClient client;
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofitHome = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofitHome.create(IRequestService.class);
    }


    public interface IRequestService {
        @Multipart
        @POST(add_image)
        Observable<Response> addImage(@Part("code") RequestBody canCode,
                                      @Part("type") RequestBody type,
                                      @Part("upload_device") RequestBody device,
                                      @Part("picture_date") RequestBody date,
                                      @Part MultipartBody.Part[] listFile
        );
    }
}
