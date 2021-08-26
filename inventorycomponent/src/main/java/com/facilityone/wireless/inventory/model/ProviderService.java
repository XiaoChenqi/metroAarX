package com.facilityone.wireless.inventory.model;

import com.facilityone.wireless.a.arch.ec.module.Page;

import java.util.List;

/**
 * Created by peter.peng on 2018/11/27.
 */

public class ProviderService {

    /**
     * 联网请求供应商列表数据请求体
     */
    public static class ProviderListRequest {
        public Long inventoryId;
        public Page page;
    }

    /**
     * 联网请求供应商列表返回数据实例bean
     */
    public static class ProviderListBean{
        public Page page;
        public List<Provider> contents;
    }

    /**
     * 供应商实例bean
     */
    public static class Provider{
        public Long providerId;//供应商id
        public String name;//名称
        public String phone;//电话
        public String address;//地址
        public String contact;//联系人
    }
}
