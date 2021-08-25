package com.facilityone.wireless.a.arch.net;
import com.facilityone.wireless.a.arch.xcq.net.NetWork_OkHttp3;
import com.facilityone.wireless.basiclib.app.FM;

import retrofit2.Retrofit;

public class FmNetWork {
    private static FmNetWork instance;//不带header的请求
    private FmNetApi fmNetApi;

    public static FmNetWork getInstance() {
        if (instance == null)
            instance = new FmNetWork();
        return instance;
    }
    /**
     * 用于把 commoblib中的基础Retrofit，和 module的参数组成完成的 Retrofit
     */
    private FmNetWork(){
        //String tempUrl = FM.getApiHost()+ UserUrl.LOGON_URL;
        Retrofit partRetrofit = NetWork_OkHttp3.getInstance(FM.getApiHost()+"/").getPartRetrofit();
        fmNetApi = partRetrofit.create(FmNetApi.class);
    }
    public FmNetApi getFmNetApi(){

        return fmNetApi;
    }
}
