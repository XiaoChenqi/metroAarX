package com.facilityone.wireless.workorder.serviceimpl;

import android.os.Bundle;

import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.componentservice.workorder.WorkorderService;
import com.facilityone.wireless.workorder.fragment.WorkorderCreateFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderDispatchFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderInfoFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderMenuFragment;
import com.facilityone.wireless.workorder.fragment.WorkorderQueryFragment;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/7/3 下午4:14
 */
public class WorkorderServiceImpl implements WorkorderService {
    @Override
    public BaseFragment getFragment(Bundle bundle) {
        return WorkorderMenuFragment.getInstance(bundle);
    }

    @Override
    public BaseFragment getWorkorderInfoFragment(int workorderStatus, String code, Long woId) {
        return WorkorderInfoFragment.getInstance(workorderStatus, code, woId);
    }

    @Override
    public BaseFragment getWorkorderInfoFragment(int workorderStatus, String code, Long woId, boolean isMaintenance) {
        return WorkorderInfoFragment.getInstance(workorderStatus, code, woId,isMaintenance);
    }

    @Override
    public BaseFragment getWorkorderInfoFragment(int workorderStatus, String code, Long woId, boolean isMaintenance, boolean isFinish) {
        return WorkorderInfoFragment.getInstance(workorderStatus, code, woId,isMaintenance,isFinish);
    }




    @Override
    public BaseFragment getWorkorderQueryFragment(boolean my) {
        return WorkorderQueryFragment.getInstance(my);
    }

    @Override
    public BaseFragment getWorkorderCreateFragment(int fromType, long equipmentId) {
        return WorkorderCreateFragment.getInstance(fromType, equipmentId);
    }

    @Override
    public BaseFragment getWorkorderCreateFragment(int fromType, long equipmentId
            , String locationName, LocationBean locationBean
            , List<LocalMedia> localMedias, Long itemId
            , String desc, Long demandId, String phone, String people) {
        return WorkorderCreateFragment.getInstance(fromType,
                equipmentId, locationName,
                locationBean, localMedias, itemId,
                desc, demandId, phone, people);
    }
    
    @Override
    public BaseFragment getWorkorderCreateFragment(int fromType, long equipmentId
            , String locationName, LocationBean locationBean
            , List<LocalMedia> localMedias, Long itemId
            , String desc, Long demandId, String phone, String people,boolean waterMark) {
        return WorkorderCreateFragment.getInstance(fromType,
                equipmentId, locationName,
                locationBean, localMedias, itemId,
                desc, demandId, phone, people,waterMark);
    }

     /**
      * @Auther: karelie
      * @Date: 2021/8/23
      * @Infor: 维护管理中批量派单
      */
     @Override
     public BaseFragment getOrderDispatchFragment(ArrayList<String> woId, String code, String sendWorkContent, Long estimateStartTime, Long estimateEndTime,Long workTeamId) {
         return WorkorderDispatchFragment.getInstance(woId, code, sendWorkContent, estimateStartTime, estimateEndTime,workTeamId);
     }

}
