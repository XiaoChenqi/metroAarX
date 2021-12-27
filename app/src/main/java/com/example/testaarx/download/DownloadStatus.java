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



    String PATROL_CODE = "PATROL"; //巡检二维码标识
    String SIGIN_CODE = "USERINFO"; //人员签到二维码标识
    String STOCK_CODE = "STOCK" ;//物资二维码标识

    Integer HEADQUARTERS_CODE = 0; //总部二维码
    Integer OUT_SOURCING_CODE = 1; //委外二维码
    Integer LINE_CODE = 2; //线路二维码
    Integer STATION_CODE = 3; //车站二维码
    Integer STATION_AREA_CODE = 4; //站区二维码
}
