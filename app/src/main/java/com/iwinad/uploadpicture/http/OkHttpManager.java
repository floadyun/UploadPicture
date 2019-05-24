package com.iwinad.uploadpicture.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yixiaofei on 2017/2/24 0024.
 */

public class OkHttpManager {

    private static OkHttpClient mOkHttpClient;
//    private static final boolean debug = true;
//    private static OkHttpClient mOkHttpClient = new OkHttpClient();
//    static {
//        if (debug) {
//            mOkHttpClient.networkInterceptors().add(new StethoInterceptor());
//        }
//    }


    //设置缓存目录
   // private static File cacheDirectory = new File(ARApplication.getInstance().getApplicationContext().getCacheDir().getAbsolutePath(), "MyCache");

   // private static Cache cache = new Cache(cacheDirectory, 10 * 1024 * 1024);

    /**
     * 获取OkHttpClient对象
     *
     * @return
     */
    public static OkHttpClient getOkHttpClient() {

//        if (null == mOkHttpClient) {
//            /****正式**/
//            mOkHttpClient = new OkHttpClient.Builder()
//                    //.cookieJar(CookiesManager.getInstance())
//                    //.addInterceptor(new MyIntercepter())
//                    //.addNetworkInterceptor(new CookiesInterceptor(MyApplication.getInstance().getApplicationContext()))
//                    //设置请求读写的超时时间
////                    .addInterceptor(new RequestInterceptor())
//                    .connectTimeout(10, TimeUnit.SECONDS)
//                    .writeTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(30, TimeUnit.SECONDS)
//                 //   .cache(cache)
//                    .build();
            /*****调试****/
//            mOkHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).addNetworkInterceptor(new StethoInterceptor()).addInterceptor(new RequestInterceptor()).build();
            /****正式**/
            mOkHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).addInterceptor(new RequestInterceptor()).readTimeout(30, TimeUnit.SECONDS).build();

//        }
        return mOkHttpClient;
    }
    private static class RequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "text/html; charset=UTF-8")
//                    .addHeader("Accept-Encoding", "*")
                    .addHeader("Content-Length","0")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Accept","application/json")
                    .addHeader("Access-Control-Allow-Origin", "*")
                    .addHeader("Access-Control-Allow-Headers", "X-Requested-With")
                    .addHeader("Vary", "Accept-Encoding")
//                    .addHeader("Cookie", "add cookies here")
                    .build();
            return chain.proceed(request);
        }
    }
}
