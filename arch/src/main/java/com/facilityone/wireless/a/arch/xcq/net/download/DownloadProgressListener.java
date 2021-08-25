package com.facilityone.wireless.a.arch.xcq.net.download;

/**
 * 下载监听接口
 * Created by MSI-PC on 2018/4/4.
 */

public interface DownloadProgressListener {
    /**
     * 上传文件
     * @param bytesRead
     * @param contentLength
     * @param done
     */
    void update(long bytesRead, long contentLength, boolean done);
}
