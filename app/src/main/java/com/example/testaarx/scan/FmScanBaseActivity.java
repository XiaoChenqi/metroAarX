package com.example.testaarx.scan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.testaarx.R;
import com.example.testaarx.download.APPUrl;
import com.example.testaarx.download.DownloadStatus;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.ec.logon.UserUrl;
import com.facilityone.wireless.a.arch.ec.module.LocationBean;
import com.facilityone.wireless.a.arch.ec.module.UserService;
import com.facilityone.wireless.a.arch.ec.utils.SPKey;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.a.arch.utils.MetroUtils;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.facilityone.wireless.basiclib.utils.StringUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.fm.tool.network.model.BaseResponse;
import com.fm.tool.scan.ScanActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

public class FmScanBaseActivity extends BaseFragmentActivity
        implements EmptyFragment.OnGoFragmentListener {
    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0L;
    public static int themeColor =0;
    private EmptyFragment mInstance;
    @Override
    protected int getContextViewId() {
        return R.id.scan_base_id;
    }

    @Override
    protected FMFragment setRootFragment() {
        mInstance = EmptyFragment.getInstance(CommonConstant.MESSAGE_SCAN);
        mInstance.setOnGoFragmentListener(this);
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        MetroUtils.getParamFromMetro(this);
    }

    @Override
    public Object createPresenter() {
        return null;
    }

    @Override
    public void goFragment(Bundle bundle) {

        mInstance.getActivity().finish();
        //mInstance.startWithPop(WorkorderCreateFragment.getInstance(CREATE_ORDER_BY_OTHER,equipId));
        //todo 登录成功以后，直接扫码
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);

        ScanActivity.setOnScanResultListener(new ScanActivity.OnScanResultListener() {
            @Override
            public void success(String QRCode) {
                LogUtils.d("TAG", "扫描结果==" + QRCode);
                if (TextUtils.isEmpty(QRCode)) {
                    ToastUtils.showShort("二维码异常");
                    return;
                }
                scanResult(QRCode);
            }
        });
    }
    @Override
    protected boolean isImmersionBarEnabled() {//fmfactivity中
        return true;
    }
    @Override
    public void onBackPressedSupport() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            //pop();
            finish();
        } else {
            this.finish();
//            if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
//                this.finish();
//            } else {
//                TOUCH_TIME = System.currentTimeMillis();
//                ToastUtils.showShort("再按一次退出");
//            }
        }
    }

    public void scanResult(String spotCode) {
//        spotCode = "STOCK|MATERIAL|001|1212|F-ONE";
        String [] result = spotCode.split("\\|");
        if (result != null){
            String resultType = result[0];
            switch (resultType){
//                case "PATROL": //巡检
//                    if (!result[4].equals("F-ONE")){
//                        ToastUtils.showShort("二维码错误");
//                        return;
//                    }
//                    Router router = Router.getInstance();
//                    PatrolService demandService = (PatrolService) router.getService(PatrolService.class.getSimpleName());
//                    if (demandService != null) {
//                        BaseFragment fragment = demandService.goToScanForInfor(spotCode);
//                        HomeFragment parentFragment = (HomeFragment) getParentFragment();
//                        parentFragment.start(fragment);
//                    }
//                    break;
                case "USERINFO"://签到
                    signOn(Long.parseLong(result[2]));
                    break;
                case "BASIC":
//                BASIC|EM|10|1|jon委外-|F-ONE
                //员工类型：0:总部；1:委外；2:线路；3:车站；4:站区

                /**
                 * 如下
                 * */
                Integer personType = Integer.parseInt(result[3]);
                if (personType == null || personType.equals("")){
                    ToastUtils.showShort("无权限信息");
                    return;
                }

                if (!personType.equals(DownloadStatus.OUT_SOURCING_CODE)){
                    ToastUtils.showShort("非委外人员，不需要签到");
                    return;
                }
                //被签到人ID
                Long personId = Long.parseLong(result[2]);
                //值班人员名称
                String contactName = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.EM_NAME);
                //值班人员ID
                Long contactId = SPUtils.getInstance(SPKey.SP_MODEL_USER).getLong(SPKey.EM_ID);
                String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
                UserService.UserInfoBean infoBean = com.blankj.utilcode.util.GsonUtils.fromJson(userInfo, UserService.UserInfoBean.class);
                //值班位置
                LocationBean mLocation = infoBean.location;
                getUserInfo();
                if (infoBean.type.equals(3)) {
                    operateAttendance(personId, contactId, contactName, System.currentTimeMillis(), mLocation);
                }else {
                    ToastUtils.showShort("非车站值班人员，无法进行签到");
                }
                //case "STOCK": //物资
//                    Router routerInventory = Router.getInstance();
//                    ProjectService.InventoryQRCodeBean inventoryQRCodeBean = getPresenter().getInventoryQRCodeBean(spotCode);
//                    if(inventoryQRCodeBean == null) {
//                        ToastUtils.showShort("二维码不存在或匹配出错");
//                        return;
//                    }
//                    try {
//                        long wareHouseId = Long.parseLong(inventoryQRCodeBean.wareHouseId);
//                        InventoryService inventoryService = (InventoryService) routerInventory.getService(InventoryService.class.getSimpleName());
//                        if (inventoryService != null) {
//                            BaseFragment fragment = inventoryService.ScanForInventoryInfor(inventoryQRCodeBean.code,wareHouseId,true);
//                            HomeFragment parentFragment = (HomeFragment) getParentFragment();
//                            parentFragment.start(fragment);
//                        }
//                    } catch (NumberFormatException e) {
//                        e.printStackTrace();
//                    }
                   // break;
                default:
                    ToastUtils.showShort("请扫描正确二维码");
                    break;
            }
        }
    }

    /**
     * @Auther: karelie
     * @Date: 2021/8/5
     * @Infor: 签到
     */
    public void signOn(Long personId){
        final ProjectService.SignOnReq request = new ProjectService.SignOnReq();
        request.personId = personId;
        OkGo.<BaseResponse<Object>>post(FM.getApiHost() + APPUrl.SCAN_FOR_SIGNON)
                .tag(this)
                .isSpliceUrl(true)
                .upJson(toJson(request))
                .execute(new FMJsonCallback<BaseResponse<Object>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<Object>> response) {
                        if (response.body().data != null){
                            SignOnSuccess();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<Object>> response) {
                        super.onError(response);

                    }
                });
    }
    protected String toJson(Object o) {
        try {
            return GsonUtils.toJson(o, false);
        } catch (Exception e) {
            return "";
        }
    }
    public void SignOnSuccess(){
        ToastUtils.showShort("签到成功");
    }


    /**
     * @Created by: kuuga
     * @Date: on 2021/8/26 10:48
     * @Description: 新扫一扫签到
     */

    public void operateAttendance(Long persionId, Long contactId, String contactName, Long createTime, LocationBean location){
//        showLoading();
        ProjectService.AttendanceReq request= new ProjectService.AttendanceReq();
        request.personId=persionId;
        request.contactId=contactId;
        request.contactName=contactName;
        request.createTime=createTime;
        request.location=location;
        String json = toJson(request);
        OkGo.<BaseResponse<String>>post(FM.getApiHost() +  APPUrl.SCAN_FOR_SIGNON)
                .tag(this)
                .isSpliceUrl(true)
                .upJson(json)
                .execute(new FMJsonCallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<String>> response) {
                        dismissLoading();
                        int code = response.body().code;
                        SignOnSuccess();

                    }

                    @Override
                    public void onError(Response<BaseResponse<String>> response) {
                        super.onError(response);
//                        dismissLoading();
                    }
                });
    }

    public void getUserInfo() {
        OkGo.<BaseResponse<UserService.UserInfoBean>>post(FM.getApiHost() + UserUrl.USER_INFO_URL)
                .tag(this)
                .isSpliceUrl(true)
                .upJson("{}")
                .execute(new FMJsonCallback<BaseResponse<UserService.UserInfoBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponse<UserService.UserInfoBean>> response) {
                        UserService.UserInfoBean data = response.body().data;
                        if (data != null) {
                            FM.getConfigurator().withEmId(data.emId);
                            FM.getConfigurator().withEmName(data.name);
                            if (data.emId != null) {
                                SPUtils.getInstance(SPKey.SP_MODEL_USER).put(SPKey.EM_ID, data.emId);
                            }
                            SPUtils.getInstance(SPKey.SP_MODEL_USER).put(SPKey.EM_NAME, StringUtils.formatString(data.name));
                            String toJson = toJson(data);
                            SPUtils.getInstance(SPKey.SP_MODEL_USER).put(SPKey.USER_INFO, toJson);
//                            getUserInfoSuccess(toJson);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponse<UserService.UserInfoBean>> response) {
                        super.onError(response);
                        String userInfo = SPUtils.getInstance(SPKey.SP_MODEL_USER).getString(SPKey.USER_INFO);
//                        getUserInfoSuccess(userInfo);
                    }
                });
    }
}
