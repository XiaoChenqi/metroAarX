package com.facilityone.wireless.a.arch.ec.module;

import com.facilityone.wireless.a.arch.offline.model.service.OnDownloadListener;

public class OutLineReq {
   public OnDownloadListener listener;
   public Long aLong;

    public OnDownloadListener getListener() {
        return listener;
    }

    public void setListener(OnDownloadListener listener) {
        this.listener = listener;
    }

    public Long getaLong() {
        return aLong;
    }

    public void setaLong(Long aLong) {
        this.aLong = aLong;
    }
}
