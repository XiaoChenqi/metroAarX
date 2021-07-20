package com.example.testaarx.download;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:下载进度
 * Date: 2018/10/24 11:00 AM
 */
public class DownloadProgressEntity {
    private int type;//有更新 下载失败 已更新 未下载
    private String name;//名称
    private int progress;//进度
    private int max;//最大进度

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
