package com.iwinad.uploadpicture.http.Entity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by yixiaofei on 2017/3/9 0009.
 */
public abstract class BaseObserver<T> implements Observer<T>{
    //请求成功回调
    public abstract void onSuccess(T value);

    @Override
    public void onSubscribe(Disposable d) {

    }
    @Override
    public void onNext(T value) {
        if(value!=null){
            onSuccess(value);
        }
    }
    @Override
    public void onError(Throwable e) {
       System.out.println("HTTP Request .."+e.toString());
    }
    @Override
    public void onComplete() {

    }
}
