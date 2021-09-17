package com.facilityone.wireless.a.arch.xcq.net;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facilityone.wireless.a.arch.BaseApplication;
import com.facilityone.wireless.a.arch.utils.DeviceUtils;
import com.facilityone.wireless.a.arch.xcq.Constants.Constant;
import com.facilityone.wireless.a.arch.xcq.net.download.DownloadProgressListener;
import com.google.gson.Gson;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.SPCookieStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class NetWork_OkHttp3 implements DownloadProgressListener {

    private static NetWork_OkHttp3 instance;

    /**
     * 没有把返回类型构造进去的 Retrofit，
     * 构建了超时时间，baseurl，拦截器（第三方），头文件等的 Retrofit
     */
    private Retrofit partRetrofit;
    private DownloadProgressListener listener;

    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    public static NetWork_OkHttp3 getInstance(String url) {
        if (instance == null) {
            instance = new NetWork_OkHttp3(url);
        }
        return instance;
    }

    public static final int TIMEOUT = 30;

    /**
     * 标准的network
     */
    public NetWork_OkHttp3() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //不一定需要添加
                //添加统一的接口参数，例如：
                //100个接口都需要传userid和token的时候，可以在这里配置
                HttpUrl originalHttpUrl = chain.request().url();
                HttpUrl url = originalHttpUrl.newBuilder()
//                        .addQueryParameter("platformKey", "app_video_request") 每个接口的公共参数
                        .build();
                Request newRequest = chain.request().newBuilder()
                        //.addHeader("platformKey", "test_xcq") 每个接口的公共header
                        //.addHeader("Content-Type","application/json")
                        .url(url)
                        .build();
                Response response = chain.proceed(newRequest);
                return response;
            }
        };

        OkHttpClient okHttp3Client = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
//                SharedPreferences fm_cookie = SophixStubApplication.getInstance().getSharedPreferences("fm_cookie", Context.MODE_PRIVATE);
//                String cookie = fm_cookie.getString("cookie", "");
            }
        })
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(loggingInterceptor)//添加日志拦截器
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constant.BASE_URL)
                .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(
                        BaseApplication.getInstance().gson))
                .client(okHttp3Client)
                .build();
        partRetrofit = retrofit;

    }
    String TAG ="林晓旭";

    /**
     * 自定义的network
     * @param serviceUrl
     */
    public NetWork_OkHttp3(String serviceUrl) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        header.put("Device-Type", "android");
//        header.put("Device-Id", FMAPP.getDeviceId());
//        header.put("source", "android");
        /**
         * 此处有4个header参数
         */
        /**
         * 需要手动加入cookie，因为登录的时候cookie已经获取到了
         */
        SharedPreferences fm_cookie = BaseApplication.getInstance().getSharedPreferences("fm_cookie", Context.MODE_PRIVATE);
        String cookie = fm_cookie.getString("cookie", "");

        Log.d(TAG, "NetWork_OkHttp3: ");

       Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //不一定需要添加
                //添加统一的接口参数，例如：
                //100个接口都需要传userid和token的时候，可以在这里配置
                HttpUrl originalHttpUrl = chain.request().url();
                HttpUrl url = originalHttpUrl.newBuilder()
//                        .addQueryParameter("platformKey", "app_video_request") 每个接口的公共参数
                        .build();
                Request newRequest = chain.request().newBuilder()
                        //.addHeader("platformKey", "test_xcq") 每个接口的公共header
                        .addHeader("Content-Type","application/json")
                        .addHeader("Device-Type", "android")
                        .addHeader("Device-Id", DeviceUtils.getDeviceId())
                        .addHeader("source", "android")
                        .url(url)
                        .build();
                Response response = chain.proceed(newRequest);
                return response;
            }
        };

        OkHttpClient okHttp3Client = new OkHttpClient().newBuilder()
//                .cookieJar(new CookieJar() {
//                    @Override
//                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
//                        cookieStore.put(url.host(), cookies);
//                    }
//
//                    @Override
//                    public List<Cookie> loadForRequest(HttpUrl url) {
//                        List<Cookie> cookies = cookieStore.get(url.host());
//                        return cookies != null ? cookies : new ArrayList<Cookie>();
//
//                    }
//                })
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(loggingInterceptor)//添加日志拦截器
                .addInterceptor(interceptor)
                .cookieJar(new CookieJarImpl(new SPCookieStore(BaseApplication.getInstance().getApplicationContext())))
                .build();

        Gson temp = BaseApplication.getInstance().gson;

        Retrofit retrofit = new Retrofit.Builder().baseUrl(serviceUrl)
                .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(
                        BaseApplication.getInstance().gson))
                .client(okHttp3Client)
                .build();
        partRetrofit = retrofit;

    }

    public Retrofit getPartRetrofit() {
        return partRetrofit;
    }

    @Override
    public void update(long bytesRead, long contentLength, boolean done) {

    }
}
