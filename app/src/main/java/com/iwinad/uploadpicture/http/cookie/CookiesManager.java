package com.iwinad.uploadpicture.http.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by yixiaofei on 2017/2/24 0024.
 */

public class CookiesManager implements CookieJar {

    private static CookiesManager cookieManager;

    private PersistentCookieStore cookieStore;

    public CookiesManager(){
        cookieStore = new PersistentCookieStore();
    }

    public static CookiesManager getInstance(){
        if(cookieManager==null){
            cookieManager = new CookiesManager();
        }
        return cookieManager;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (cookies != null && cookies.size() > 0) {
            for (Cookie item : cookies) {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }
}
