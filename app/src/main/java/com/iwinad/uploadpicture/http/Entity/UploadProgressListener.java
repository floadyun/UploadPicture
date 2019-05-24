package com.iwinad.uploadpicture.http.Entity;

import java.io.File;

/**
 * Created by yixiaofei on 2017/3/11 0011.
 */

public interface UploadProgressListener {
    /**
     * 上传进度
     * @param currentBytesCount
     * @param totalBytesCount
     */
    void onProgress(long currentBytesCount, long totalBytesCount);
    /**
     * 上传进度
     * @param currentBytesCount
     * @param totalBytesCount
     */
    void onUploadProgress(long currentBytesCount, long totalBytesCount,File file);
}
