package com.facilityone.wireless.demand.serviceimpl;

import android.os.Bundle;

import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.componentservice.demand.DemandService;
import com.facilityone.wireless.demand.fragment.DemandCreateFragment;
import com.facilityone.wireless.demand.fragment.DemandFragment;
import com.facilityone.wireless.demand.fragment.DemandInfoFragment;
import com.facilityone.wireless.demand.module.DemandConstant;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:
 * Date: 2018/3/29 上午10:09
 */
public class DemandServiceImpl implements DemandService {

    @Override
    public BaseFragment getFragment(Bundle bundle) {
        return DemandFragment.getInstance(bundle);
    }

    @Override
    public BaseFragment getDemandInfoByQuery(Long demandId) {
        return DemandInfoFragment.getInstance(DemandConstant.DEMAND_REQUES_QUERY, demandId);
    }

    @Override
    public BaseFragment getDemandInfoByMsg(Long demandId) {
        return DemandInfoFragment.getInstance(DemandConstant.DEMAND_REQUES_QUERY, demandId, true);
    }

    /**
     * Author:karelie
     * 快速报障
     * */
    @Override
    public BaseFragment goToQuickReport() {
        return DemandCreateFragment.getInstance("快速报障");
    }

    @Override
    public BaseFragment goToQuickReport(long equipmentId, String locationName, LocationBean locationBean, String desc, List<LocalMedia>imageIds,Boolean isPatrol,Long contentId,String deviceName,Long deviceId,String code) {

        /**
         * 目前取消完善，isComplete参数暂写死False
         * */
        return DemandCreateFragment.getInstance(equipmentId,"快速报障",locationName,locationBean,desc,false,imageIds,isPatrol,contentId,deviceName,deviceId,code);
    }


}
