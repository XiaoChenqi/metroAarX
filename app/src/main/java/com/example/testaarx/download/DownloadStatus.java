package com.example.testaarx.download;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:下载的状态
 * Date: 2018/10/24 11:23 AM
 */
public interface DownloadStatus {
    int DOWNLOAD_STATUS_NOT_DOWNLOAD = 0;//未下载
    int DOWNLOAD_STATUS_HAVE_DOWNLOAD = 1;//已下载
    int DOWNLOAD_STATUS_FAILED_DOWNLOAD = 2;//下载失败
    int DOWNLOAD_STATUS_HAVE_UPDATE = 3;//有更新
}
