package com.example.testaarx.scan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.testaarx.MetroUtils;
import com.example.testaarx.R;
import com.example.testaarx.download.APPUrl;
import com.facilityone.wireless.a.arch.base.FMFragment;
import com.facilityone.wireless.a.arch.base.FMJsonCallback;
import com.facilityone.wireless.a.arch.mvp.BaseFragment;
import com.facilityone.wireless.a.arch.mvp.BaseFragmentActivity;
import com.facilityone.wireless.basiclib.app.FM;
import com.facilityone.wireless.basiclib.utils.GsonUtils;
import com.facilityone.wireless.componentservice.common.empty.EmptyFragment;
import com.facilityone.wireless.componentservice.common.permissions.CommonConstant;
import com.facilityone.wireless.componentservice.patrol.PatrolService;
import com.facilityone.wireless.workorder.fragment.WorkorderCreateFragment;
import com.fm.tool.network.model.BaseResponse;
import com.fm.tool.scan.ScanActivity;
import com.luojilab.component.componentlib.router.Router;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.PASSWORD;
import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.THEME_COLOR;
import static com.facilityone.wireless.a.arch.xcq.Constants.Constant.USERNAME;

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
            pop();
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
}
