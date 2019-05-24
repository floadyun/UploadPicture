package com.iwinad.uploadpicture.http.Entity;

import com.iwinad.uploadpicture.UploadImage;
import com.iwinad.uploadpicture.http.RetrofitManager;
import com.iwinad.uploadpicture.http.loading.ProgressUtil;

import java.io.File;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by yixiaofei on 2017/3/10 0010.
 */

public class UploadFile extends BaseLoader{

    private static UploadService mService = RetrofitManager.getRetrofit().create(UploadService.class);

    interface UploadService{
        @Multipart
        @POST("m/upload")
        Observable<UploadImage> uploadImage(@Part() MultipartBody.Part file);
    }
    private static MultipartBody.Part getFilePart(File file,String fileType){
        RequestBody requestBody= RequestBody.create(MediaType.parse(fileType),file);
        MultipartBody.Part part= MultipartBody.Part.createFormData("file_name", file.getName(), new ProgressRequestBody(requestBody,
                null));
        return part;
    }
    public static void uploadImage(File file,String fileType,Observer<UploadImage> observer){
        setSubscribe(mService.uploadImage(getFilePart(file,fileType)),observer);
    }
    public static <T> void setSubscribe(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(observer);
    }


}
