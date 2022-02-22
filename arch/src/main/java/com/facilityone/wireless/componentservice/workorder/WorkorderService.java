package com.facilityone.wireless.componentservice.workorder;

import com.facilityone.wireless.a.arch.ec.module.IService;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：gary
 * Email: xuhaozv@163.com
 * description:获取工单的首页fragment
 * Date: 2018/7/3 下午4:13
 */
public interface WorkorderService extends IService {

    int CREATE_ORDER_BY_OTHER = 2001;//其他页面来创建工单
    int CREATE_ORDER_BY_PATROL_QUERY_REPAIR = 2002;//巡检查询报修

    BaseFragment getWorkorderInfoFragment(int workorderStatus, String code, Long woId);

    BaseFragment getWorkorderInfoFragment(Boolean fromMessage,int workorderStatus, String code, Long woId);
     /**
      * @Auther: karelie
      * @Date: 2021/8/19
      * @Infor: 四运 维护工单跳转
      */
    BaseFragment getWorkorderInfoFragment(int workorderStatus, String code, Long woId,boolean isMaintenance);

    BaseFragment getWorkorderInfoFragment(int workorderStatus, String code, boolean isExeption,Long woId);

    BaseFragment getWorkorderInfoFragment(int workorderStatus, String code, Long woId,boolean isMaintenance,boolean isFinish);

    BaseFragment getOrderDispatchFragment(ArrayList<String> woId, String code, String sendWorkContent, Long estimateStartTime, Long estimateEndTime,Long workTeamId);

    //待处理
    BaseFragment getWorkorderInfoPendingFragment(int workorderStatus, String code, Long woId, Integer isPending, boolean isMaintenance);

    BaseFragment getWorkorderQueryFragment(boolean my);

    BaseFragment getWorkorderCreateFragment(int fromType, long equipmentId);

    //综合巡检入口
    BaseFragment getWorkorderCreateFragment(int fromType, long equipmentId,String desc,LocationBean locationBean,String locationName,Long patrolDetailId);

    //巡检需求报障
    BaseFragment getWorkorderCreateFragment(int fromType,
                                            long equipmentId,
                                            String locationName,
                                            LocationBean locationBean,
                                            List<LocalMedia> localMedias,
                                            Long patrolTaskSpotResultId,
                                            String desc,
                                            Long demandId,
                                            String phone,
                                            String people);

    //图片是否加水印
    BaseFragment getWorkorderCreateFragment(int fromType, long equipmentId
            , String locationName, LocationBean locationBean
            , List<LocalMedia> localMedias, Long itemId
            , String desc, Long demandId, String phone, String people, boolean waterMark);
}
